#/bin/bash

echo "Starting server"
mvn clean install -DskipTests=true
mvn ninja:run
