#!/bin/bash

LOG_FILE=/workspace/docker_build.log

# Capture both stdout and stderr to the log file
exec > >(tee -i $LOG_FILE)
exec 2>&1

# Source the .env file and clean any carriage returns
if [ -f /workspace/.env ]; then
  set -a
  source /workspace/.env
  set +a

  # Clean carriage returns from the variables
  SERVER_PORT=$(echo "$SERVER_PORT" | tr -d '\r')
  ASTRA_DB_USERNAME=$(echo "$ASTRA_DB_USERNAME" | tr -d '\r')
  ASTRA_DB_KEYSPACE=$(echo "$ASTRA_DB_KEYSPACE" | tr -d '\r')
  ASTRA_DB_ID=$(echo "$ASTRA_DB_ID" | tr -d '\r')
  DATASTAX_ASTRA_PASSWORD=$(echo "$DATASTAX_ASTRA_PASSWORD" | tr -d '\r')
  DATASTAX_ASTRA_SCB_NAME=$(echo "$DATASTAX_ASTRA_SCB_NAME" | tr -d '\r')
else
  echo "Error: .env file not found at /workspace/.env"
  exit 1
fi

# Save all arguments in a single string
ARGS="--server.port=\"$SERVER_PORT\" \
  --datastax.astra.secure-connect-bundle=\"$DATASTAX_ASTRA_SCB_NAME\" \
  --datastax.astra.username=\"$ASTRA_DB_USERNAME\" \
  --datastax.astra.password=\"$DATASTAX_ASTRA_PASSWORD\" \
  --datastax.astra.keyspace=\"$ASTRA_DB_KEYSPACE\""

# Display the argument string for debugging
echo "Maven Arguments: $ARGS"

# Navigate to the app directory and run the Maven command with the arguments string
cd /app
mvn spring-boot:run -Dspring-boot.run.arguments="$ARGS"
