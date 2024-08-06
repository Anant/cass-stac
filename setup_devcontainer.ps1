#!/bin/bash

# Prompt for necessary environment variables
$SERVER_PORT = Read-Host "Enter SERVER_PORT"
$ASTRA_DB_USERNAME = Read-Host "Enter ASTRA_DB_USERNAME"
$ASTRA_DB_KEYSPACE = Read-Host "Enter ASTRA_DB_KEYSPACE"
$ASTRA_DB_ID = Read-Host "Enter ASTRA_DB_ID"
$DATASTAX_ASTRA_PASSWORD = Read-Host "Enter DATASTAX_ASTRA_PASSWORD"
$DATASTAX_ASTRA_SCB_NAME = Read-Host "Enter DATASTAX_ASTRA_SCB_NAME"

# Export the environment variables for the current shell session
[System.Environment]::SetEnvironmentVariable('SERVER_PORT', $SERVER_PORT, [System.EnvironmentVariableTarget]::Process)
[System.Environment]::SetEnvironmentVariable('ASTRA_DB_USERNAME', $ASTRA_DB_USERNAME, [System.EnvironmentVariableTarget]::Process)
[System.Environment]::SetEnvironmentVariable('ASTRA_DB_KEYSPACE', $ASTRA_DB_KEYSPACE, [System.EnvironmentVariableTarget]::Process)
[System.Environment]::SetEnvironmentVariable('ASTRA_DB_ID', $ASTRA_DB_ID, [System.EnvironmentVariableTarget]::Process)
[System.Environment]::SetEnvironmentVariable('DATASTAX_ASTRA_PASSWORD', $DATASTAX_ASTRA_PASSWORD, [System.EnvironmentVariableTarget]::Process)
[System.Environment]::SetEnvironmentVariable('DATASTAX_ASTRA_SCB_NAME', $DATASTAX_ASTRA_SCB_NAME, [System.EnvironmentVariableTarget]::Process)

# Build the Docker image
sudo docker build -t stac-app \
  --build-arg ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME \
  --build-arg ASTRA_DB_KEYSPACE=$ASTRA_DB_KEYSPACE \
  --build-arg ASTRA_DB_ID=$ASTRA_DB_ID \
  --build-arg DATASTAX_ASTRA_PASSWORD=$DATASTAX_ASTRA_PASSWORD \
  --build-arg DATASTAX_ASTRA_SCB_NAME=$DATASTAX_ASTRA_SCB_NAME \
  .

# Run the Docker container
sudo docker run -p $SERVER_PORT:$SERVER_PORT \
  -e SERVER_PORT=$SERVER_PORT \
  -e ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME \
  -e ASTRA_DB_KEYSPACE=$ASTRA_DB_KEYSPACE \
  -e ASTRA_DB_ID=$ASTRA_DB_ID \
  -e DATASTAX_ASTRA_PASSWORD=$DATASTAX_ASTRA_PASSWORD \
  -e DATASTAX_ASTRA_SCB_NAME=$DATASTAX_ASTRA_SCB_NAME \
  stac-app

