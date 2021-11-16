#!/usr/bin/env bash

set -e

#################
### functions ###
#################

function createAndStartServer() {
    docker run -d -p 8080:8080 --name chatty-server anjeyy/chatty:server-ci-latest
    sleep 10s
    docker logs chatty-server
}

function createAndStartClient() {
  DOCKER_NAME=$1
  docker run -d --name "$DOCKER_NAME" --network="host" -it anjeyy/chatty:client-ci-latest
  sleep 10s
  docker logs "$DOCKER_NAME"
}

function setUsernameForClient() {
  USER_NAME=$1
  DOCKER_IMAGE=$2
  docker exec "$DOCKER_IMAGE" echo "$USER_NAME" | socat EXEC:"docker attach $DOCKER_IMAGE",pty STDIN
  docker logs "$DOCKER_IMAGE"
}

function sendMessageViaClient() {
  MESSAGE=$1
  DOCKER_IMAGE=$2
  docker exec "$DOCKER_IMAGE" echo "$MESSAGE" | socat EXEC:"docker attach $DOCKER_IMAGE",pty STDIN
  docker logs "$DOCKER_IMAGE"
}

function verifyMessage() {
    MESSAGE_TO_VERIFY=$1
    DOCKER_IMAGE=$2
    if docker logs "$DOCKER_IMAGE" | grep -q "$MESSAGE_TO_VERIFY";
    then
      echo "~~~ Success - message was verified for '$DOCKER_IMAGE' ! ~~~"
    else
      echo "~~~ ERROR: An error occurred during '$DOCKER_IMAGE' verification , for details please see in the logs. ~~~"
      exit 1
    fi
}

function cleanup() {
    docker rm -f chatty-server
    docker rm -f chatty-client-one
    docker rm -f chatty-client-two
}

############
### main ###
############

# create server and client
createAndStartServer
createAndStartClient chatty-client-one
createAndStartClient chatty-client-two

# set username for both clients
setUsernameForClient "automated user 1" chatty-client-one
setUsernameForClient "automated user 2" chatty-client-two
verifyMessage "Have fun automated user 1, but don't go too wild." chatty-client-one
verifyMessage "Have fun automated user 2, but don't go too wild." chatty-client-two
sleep 5s

# send message voa both clients (to receive them on the other client)
sendMessageViaClient "automated message from first client - yeah" chatty-client-one
sendMessageViaClient "automated message from second client - double yeah" chatty-client-two
verifyMessage "automated message from first client - yeah" chatty-client-one
verifyMessage "automated message from second client - double yeah" chatty-client-two
sleep 1s

# verify received message from other client
verifyMessage " automated user 1~ automated message from first client - yeah" chatty-client-two
verifyMessage " automated user 2~ automated message from second client - double yeah" chatty-client-one

cleanup
