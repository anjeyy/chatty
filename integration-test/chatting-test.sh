#!/usr/bin/env bash

set -e

#todo reconnect feature testing (both ways!!)

#################
### functions ###
#################

function createAndStartServer() {
    docker run -d -p 8080:8080 --name chatty-server anjeyy/chatty:server-latest
    sleep 10s
    docker logs chatty-server
}

function createAndStartClient() {
  DOCKER_NAME=$1
  docker run -d --name "$DOCKER_NAME" --network="host" -it anjeyy/chatty:client-latest
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

function verifySentMessage() {
    MESSAGE_TO_VERIFY=$1
    DOCKER_IMAGE=$2
    if docker logs chatty-client | grep -q "$MESSAGE_TO_VERIFY";
    then
      echo "~~~ Success - message was verified! ~~~"
    else
      echo "~~~ ERROR: An error occurred during verification, for details please see in the logs. ~~~"
      exit 1
    fi
}

############
### main ###
############

createAndStartServer
createAndStartClient chatty-client-one
createAndStartClient chatty-client-two

setUsernameForClient "automated user 1" chatty-client-one
setUsernameForClient "automated user 2" chatty-client-two
sleep 5s

sendMessageViaClient "automated message from first client - yeah" chatty-client-one
sendMessageViaClient "automated message from second client - double yeah" chatty-client-two
sleep 1s

verifySentMessage " automated user 1~ automated message from first client - yeah" chatty-client-two
verifySentMessage " automated user 2~ automated message from second client - double yeah" chatty-client-one