#!/usr/bin/env bash

set -e


### connect two clients and display message ###
# create server
docker run -d -p 8080:8080 --name chatty-server anjeyy/chatty:server-latest
sleep 10s
docker logs chatty-server
# create first client
docker run -d --name chatty-client-one --network="host" -it anjeyy/chatty:client-latest
sleep 10s
docker logs chatty-client-one
# create second client
docker run -d --name chatty-client-two --network="host" -it anjeyy/chatty:client-latest
sleep 10s
docker logs chatty-client-two

# simulate user input
docker exec chatty-client-one echo "automated user 1" | socat EXEC:"docker attach chatty-client-one",pty STDIN
docker logs chatty-client-one

docker exec chatty-client-two echo "automated user 2" | socat EXEC:"docker attach chatty-client-two",pty STDIN
docker logs chatty-client-two

sleep 5s

docker exec chatty-client-one echo "automated message - yeah" | socat EXEC:"docker attach chatty-client-one",pty STDIN
docker logs chatty-client-one

sleep 1s

docker logs chatty-client-two