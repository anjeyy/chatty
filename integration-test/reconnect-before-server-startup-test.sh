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
  docker run -d -e websocket.retry_timeout=2500 --name "$DOCKER_NAME" --network="host" -it anjeyy/chatty:client-ci-latest
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
      echo "~~~ Success - message '$MESSAGE_TO_VERIFY' was verified for '$DOCKER_IMAGE' ! ~~~"
    else
      echo "~~~ ERROR: An error occurred during '$DOCKER_IMAGE' verification , for details please see in the logs. ~~~"
      exit 1
    fi
}

function cleanup() {
    docker rm -f chatty-server
    docker rm -f chatty-client
}


############
### main ###
############

# create and try to connect with client - reconnect triggered
createAndStartClient chatty-client
setUsernameForClient "automated user 1" chatty-client
sleep 1s

# start server - client can connect to server now
createAndStartServer
docker logs chatty-client

echo "-------------------- Verification --------------------"
verifyMessage "Waiting 2 s for another retry.. (1/5)" chatty-client
verifyMessage "Waiting 2 s for another retry.. (2/5)" chatty-client
verifyMessage "Waiting 2 s for another retry.. (3/5)" chatty-client
verifyMessage "~~ Connection to the Chatroom established. ~~" chatty-client
verifyMessage "~~ Say hi to the others. ~~" chatty-client

cleanup
