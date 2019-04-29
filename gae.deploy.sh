#!/usr/bin/env bash

gcloud config set project euro2ether
gcloud app versions list
# (max. 15 versions)
# gcloud app versions delete ...
mvn clean package verify
mvn endpoints-framework:openApiDocs
gcloud endpoints services deploy target/openapi-docs/openapi.json

mvn appengine:deploy

echo "Test request to API:"

curl \
    -H "Content-Type: application/json" \
    -X POST \
    -d '{"message":"test message"}' \
    https://euro2ether.appspot.com/_ah/api/testAPI/v1/returnPostRequestData

echo # empty line
echo $(date "+%FT%T%Z") : $(whoami)
gcloud app versions list
mvn clean