#!/bin/bash

# Find all running containers related to 'stac-app'
containers=$(docker ps | grep 'stac-app' | awk '{print $1}')

# Stop the containers
if [ -n "$containers" ]; then
  echo "Stopping containers..."
  echo "$containers" | xargs docker stop
else
  echo "No running containers found for 'stac-app'."
fi

# Find all stopped containers related to 'stac-app'
stopped_containers=$(docker ps -a | grep 'stac-app' | grep 'Exited' | awk '{print $1}')

# Remove the stopped containers
if [ -n "$stopped_containers" ]; then
  echo "Removing stopped containers..."
  echo "$stopped_containers" | xargs docker rm
else
  echo "No stopped containers to remove for 'stac-app'."
fi

