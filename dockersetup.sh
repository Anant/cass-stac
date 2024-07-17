#!/bin/bash

# Debugging: Print environment variables
echo "ASTRA_DB_USERNAME=${ASTRA_DB_USERNAME}"
echo "ASTRA_DB_KEYSPACE=${ASTRA_DB_KEYSPACE}"
echo "ASTRA_DB_ID=${ASTRA_DB_ID}"
echo "DATASTAX_ASTRA_PASSWORD=${DATASTAX_ASTRA_PASSWORD}"
echo "DATASTAX_ASTRA_SCB_NAME=${DATASTAX_ASTRA_SCB_NAME}"

# Check if environment variables are set
if [ -z "$ASTRA_DB_USERNAME" ]; then
  echo "Error: ASTRA_DB_USERNAME is not set."
  exit 1
fi

if [ -z "$ASTRA_DB_KEYSPACE" ]; then
  echo "Error: ASTRA_DB_KEYSPACE is not set."
  exit 1
fi

if [ -z "$ASTRA_DB_ID" ]; then
  echo "Error: ASTRA_DB_ID is not set."
  exit 1
fi

if [ -z "$DATASTAX_ASTRA_PASSWORD" ]; then
  echo "Error: DATASTAX_ASTRA_PASSWORD is not set."
  exit 1
fi

if [ -z "$DATASTAX_ASTRA_SCB_NAME" ]; then
  echo "DATASTAX_ASTRA_SCB_NAME is not set, using default value"
  DATASTAX_ASTRA_SCB_NAME="secure-connect-database.zip"
fi

# Send POST request to obtain the download link for SCB
RESPONSE=$(curl -s -X POST https://api.astra.datastax.com/v2/databases/$ASTRA_DB_ID/secureBundleURL \
    -H "Accept: application/json" \
    -H "Authorization: Bearer $DATASTAX_ASTRA_PASSWORD")

# Extract download URL from response
DOWNLOAD_URL=$(echo $RESPONSE | jq -r '.downloadURL')

if [ "$DOWNLOAD_URL" == "null" ]; then
  echo "Error: Unable to obtain download URL."
  echo "Response: $RESPONSE"
  exit 1
fi

# Download Secure Connect Bundle (SCB) using the obtained URL
SCB_DEST="/app/src/main/resources/${DATASTAX_ASTRA_SCB_NAME}"
curl -L -o $SCB_DEST $DOWNLOAD_URL

if [ $? -ne 0 ]; then
  echo "Error downloading SCB."
  exit 1
fi

echo "SCB downloaded and saved to $SCB_DEST"

