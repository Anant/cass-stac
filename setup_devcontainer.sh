#!/bin/bash

# Prompt for necessary environment variables
read -p "Enter SERVER_PORT: " SERVER_PORT
read -p "Enter ASTRA_DB_USERNAME: " ASTRA_DB_USERNAME
read -p "Enter ASTRA_DB_KEYSPACE: " ASTRA_DB_KEYSPACE
read -p "Enter ASTRA_DB_ID: " ASTRA_DB_ID
read -p "Enter DATASTAX_ASTRA_PASSWORD: " DATASTAX_ASTRA_PASSWORD
read -p "Enter DATASTAX_ASTRA_SCB_NAME: " DATASTAX_ASTRA_SCB_NAME

# Export the environment variables for the current shell session
export SERVER_PORT=$SERVER_PORT
export ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME
export ASTRA_DB_KEYSPACE=$ASTRA_DB_KEYSPACE
export ASTRA_DB_ID=$ASTRA_DB_ID
export DATASTAX_ASTRA_PASSWORD=$DATASTAX_ASTRA_PASSWORD
export DATASTAX_ASTRA_SCB_NAME=$DATASTAX_ASTRA_SCB_NAME

# Save the environment variables to a .env file
echo "SERVER_PORT=$SERVER_PORT" > .env
echo "ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME" >> .env
echo "ASTRA_DB_KEYSPACE=$ASTRA_DB_KEYSPACE" >> .env
echo "ASTRA_DB_ID=$ASTRA_DB_ID" >> .env
echo "DATASTAX_ASTRA_PASSWORD=$DATASTAX_ASTRA_PASSWORD" >> .env
echo "DATASTAX_ASTRA_SCB_NAME=$DATASTAX_ASTRA_SCB_NAME" >> .env

# Build the Docker image without using cache
docker build -t stac-app \
  --build-arg ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME \
  --build-arg ASTRA_DB_KEYSPACE=$ASTRA_DB_KEYSPACE \
  --build-arg ASTRA_DB_ID=$ASTRA_DB_ID \
  --build-arg DATASTAX_ASTRA_PASSWORD=$DATASTAX_ASTRA_PASSWORD \
  --build-arg DATASTAX_ASTRA_SCB_NAME=$DATASTAX_ASTRA_SCB_NAME \
  .


