#!/bin/bash
mvn clean install -DskipTests
cd auth-server
mvn clean install -DskipTests
cd ..
cd camwatch-server
mvn clean install -DskipTests
cd ..
docker-compose build

