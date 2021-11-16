#!/usr/bin/env bash

set -e


### connect two clients and display message

docker run -d -p 8080:8080 --name chatty-server anjeyy/chatty:server-latest
sleep 10s
docker logs chatty-server
docker run -d --name chatty-client --network="host" -it anjeyy/chatty:client-latest
sleep 10s
docker logs chatty-client