#!/bin/bash

# 1. Check if both arguments are provided
if [ -z "$1" ] || [ -z "$2" ]; then
    echo "❌ Error: Missing arguments."
    echo "Usage: ./make_note.sh <filename> <your-note>"
    exit 1
fi

# 2. Store arguments in clean variables
FILENAME=$1
NOTE=$2

# 3. Append the note to the file
echo "$NOTE" >> "$FILENAME"

# 4. Check if the append command worked
if [ $? -eq 0 ]; then
    echo "✅ Note successfully added to $FILENAME!"
else
    echo "❌ Failed to write note."
    exit 1
fi
