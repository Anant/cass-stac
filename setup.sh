#!/bin/bash

echo "What is your Astra DB username? 🔒"
read -r ASTRA_DB_USERNAME
export ASTRA_DB_USERNAME=$ASTRA_DB_USERNAME
gp env ASTRA_DB_USERNAME="$ASTRA_DB_USERNAME" &>/dev/null

echo "What is your Astra DB keyspace? 🔒"
read -r ASTRA_DB_KEYSPACE
export ASTRA_DB_KEYSPACE=${ASTRA_DB_KEYSPACE}
gp env ASTRA_DB_KEYSPACE="${ASTRA_DB_KEYSPACE}" &>/dev/null

echo "What is your Astra DB ID? 🔒"
read -rs ASTRA_DB_ID
export ASTRA_DB_ID=${ASTRA_DB_ID}
gp env ASTRA_DB_ID="${ASTRA_DB_ID}" &>/dev/null

echo "What is your Astra DB password? 🔒"
read -rs DATASTAX_ASTRA_PASSWORD
export DATASTAX_ASTRA_PASSWORD=${DATASTAX_ASTRA_PASSWORD}
gp env DATASTAX_ASTRA_PASSWORD="${DATASTAX_ASTRA_PASSWORD}" &>/dev/null

. ~/.bashrc
astra login -t "${DATASTAX_ASTRA_PASSWORD}"
export ASTRA_DB_BUNDLE="astra-creds.zip"
gp env ASTRA_DB_BUNDLE="astra-creds.zip" &>/dev/null

astra db download-scb "$ASTRA_DB_ID" -f src/main/resources/${ASTRA_DB_BUNDLE}
echo "You're all set 👌"