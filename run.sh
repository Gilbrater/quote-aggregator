#!/usr/bin/env bash

set -e

echo "Build all project"
mvn clean package -U -Dmaven.test.skip=true
echo "Down all"
docker-compose down
echo "Start Full System "
docker-compose  up -d --build

java -jar ./partner-service/partner-service-1.0-all.jar &

java -jar ./target/quotes-aggregator-1.0.0.jar &