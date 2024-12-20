#!/bin/bash

# Base URL for the API
BASE_URL="http://localhost:8080/api/v1/users"

# Loop through user IDs 1 to 1000
for id in {1..1000}
do
  # Make the HTTP GET request
  RESPONSE=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/$id")

  # Print the result
  if [ "$RESPONSE" -eq 200 ]; then
    echo "User $id: Success (HTTP 200)"
  else
    echo "User $id: Failed (HTTP $RESPONSE)"
  fi
done
