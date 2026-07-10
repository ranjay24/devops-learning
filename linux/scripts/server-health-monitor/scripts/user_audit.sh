#!/bin/bash
echo "=== User Audit Report ===" > ../logs/user_report.txt
echo "Users with UID >= 1000 (regular users):" >> ../logs/user_report.txt
awk -F: '$3 >= 1000 {print $1, $3}' /etc/passwd >> ../logs/user_report.txt
echo "" >> ../logs/user_report.txt
echo "Groups on this system:" >> ../logs/user_report.txt
cut -d: -f1 /etc/group >> ../logs/user_report.txt
cat ../logs/user_report.txt