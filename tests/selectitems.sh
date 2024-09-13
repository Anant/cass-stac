#!/bin/bash

# Check if at least one argument (filename) is provided
if [ "$#" -lt 1 ]; then
    echo "Usage: $0 <file1> <file2> ... <fileN>"
    exit 1
fi

# Loop through all provided filenames
for file in "$@"; do
    if [ -f "$file" ]; then
        # Run the curl command and capture the time total
        time_total=$(curl -s -w "%{time_total}\n" -X POST -H 'accept: */*' -H 'Content-Type: application/json' http://3.140.246.16:8080/items --data @"$file" -o /dev/null)
        echo "File: $file | Time total: ${time_total}s"
    else
        echo "File: $file does not exist."
    fi
done
