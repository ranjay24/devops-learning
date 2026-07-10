#!/bin/bash

# ============================================
# backup.sh - Folder Backup Script
# Author: Ranjay
# Description: Creates a compressed tar.gz
#              backup of a specified folder
# Usage: ./backup.sh <folder-name>
# ============================================

# Check if user provided a folder name
if [ -z "$1" ]; then
    echo "❌ Error: No folder provided."
    echo "Usage: ./backup.sh <folder-name>"
    exit 1
fi

# Store the folder name from argument
FOLDER=$1

# Check if the folder actually exists
if [ ! -d "$FOLDER" ]; then
    echo "❌ Error: Folder '$FOLDER' does not exist."
    exit 1
fi

# Create backup name with current date
DATE=$(date +%Y-%m-%d)
BACKUP_NAME="${FOLDER}_backup_${DATE}.tar.gz"

# Create the compressed backup
tar -czvf "$BACKUP_NAME" "$FOLDER"

# Check if backup was created successfully
if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Backup created successfully!"
    echo "📦 Backup file: $BACKUP_NAME"
    echo "📅 Date: $DATE"
else
    echo "❌ Backup failed."
    exit 1
fi