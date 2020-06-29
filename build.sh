#!/usr/bin/env bash

set -e
echo "Build the system"
./mvnw clean package -U -Dmaven.test.skip=true





