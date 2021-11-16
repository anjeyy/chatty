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

### connect two clients and display message ###
## create server
#
## create first client
#docker run -d --name chatty-client-one --network="host" -it anjeyy/chatty:client-latest
#sleep 10s
#docker logs chatty-client-one
## create second client
#docker run -d --name chatty-client-two --network="host" -it anjeyy/chatty:client-latest
#sleep 10s
#docker logs chatty-client-two

# simulate user input - setting username
#docker exec chatty-client-one echo "automated user 1" | socat EXEC:"docker attach chatty-client-one",pty STDIN
#docker logs chatty-client-one
#
#docker exec chatty-client-two echo "automated user 2" | socat EXEC:"docker attach chatty-client-two",pty STDIN
#docker logs chatty-client-two
#
#sleep 5s

## simulate chatting with each other and receiving messages
#docker exec chatty-client-one echo "automated message - yeah" | socat EXEC:"docker attach chatty-client-one",pty STDIN
#docker logs chatty-client-one

#exit 1 -> Fehler - also keine assertions

#sleep 1s
#
#docker logs chatty-client-two

#if docker logs chatty-client | grep -q ' andi~ test';
#then
#  echo "matched"
#else
#  echo "no match"
#  exit 1
#fi

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

verifySentMessage " automated message from first client - yeah" chatty-client-two
verifySentMessage " automated message from second client - double yeah" chatty-client-one