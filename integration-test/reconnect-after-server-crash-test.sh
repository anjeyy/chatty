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

# given established connection
createAndStartServer
createAndStartClient chatty-client
setUsernameForClient "automated user 1" chatty-client
verifyMessage "Have fun automated user 1, but don't go too wild." chatty-client
verifyMessage "~~ Connection to the Chatroom established. ~~" chatty-client
verifyMessage "~~ Say hi to the others. ~~" chatty-client

# when server suddenly crashes
docker stop chatty-server
sleep 2s
docker logs chatty-client
verifyMessage "~~ Connection to the Chatroom lost. ~~" chatty-client
verifyMessage "~~ Enter a message trying to reconnect and send that message. ~~" chatty-client
sendMessageViaClient "trigger reconnect" chatty-client
docker start chatty-server
sleep 10s

# then verify appropriate messages
docker logs chatty-client
echo "-------------------- Verification --------------------"
verifyMessage "Waiting 2 s for another retry.. (1/5)" chatty-client
verifyMessage "Waiting 2 s for another retry.. (2/5)" chatty-client
verifyMessage "~~ Connection to the Chatroom established. ~~" chatty-client
verifyMessage "~~ Say hi to the others. ~~" chatty-client

cleanup
