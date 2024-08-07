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

# Save the environment variables to a .env file
echo "SERVER_PORT=$SERVER_PORT" > .env
echo "ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME" >> .env
echo "ASTRA_DB_KEYSPACE=$ASTRA_DB_KEYSPACE" >> .env
echo "ASTRA_DB_ID=$ASTRA_DB_ID" >> .env
echo "DATASTAX_ASTRA_PASSWORD=$DATASTAX_ASTRA_PASSWORD" >> .env
echo "DATASTAX_ASTRA_SCB_NAME=$DATASTAX_ASTRA_SCB_NAME" >> .env

# Build the Docker image
sudo docker build -t stac-app \
  --build-arg ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME \
  --build-arg ASTRA_DB_KEYSPACE=$ASTRA_DB_KEYSPACE \
  --build-arg ASTRA_DB_ID=$ASTRA_DB_ID \
  --build-arg DATASTAX_ASTRA_PASSWORD=$DATASTAX_ASTRA_PASSWORD \
  --build-arg DATASTAX_ASTRA_SCB_NAME=$DATASTAX_ASTRA_SCB_NAME \
  .
