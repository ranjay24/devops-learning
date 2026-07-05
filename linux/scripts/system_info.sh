#!/bin/bash

# ============================================
# system_info.sh - System Information Script
# Author: Ranjay
# Description: Displays key system information
# ============================================

echo "============================================"
echo "          SYSTEM INFORMATION REPORT         "
echo "============================================"
echo ""

# Date and Time
echo "📅 Date and Time:"
date
echo ""

# Current User
echo "👤 Current User:"
whoami
echo ""

# System Uptime
echo "⏱️  System Uptime:"
uptime
echo ""

# Disk Usage
echo "💾 Disk Usage:"
df -h
echo ""

# Memory Usage
echo "🧠 Memory Usage:"
free -h
echo ""

# Total Running Processes
echo "⚙️  Total Running Processes:"
ps -ef | wc -l
echo ""

# Top 5 CPU Consuming Processes
echo "🔥 Top 5 CPU Consuming Processes:"
ps -eo pid,comm,%cpu --sort=-%cpu | head -6
echo ""

echo "============================================"
echo "         END OF SYSTEM REPORT               "
echo "============================================"