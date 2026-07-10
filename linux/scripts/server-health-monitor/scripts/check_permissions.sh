#!/bin/bash
echo "Files with permissions wider than 644 in current directory:" > ../logs/permission_report.txt
ls -l | awk '$1 !~ /^-rw-r--r--/ {print $0}' >> ../logs/permission_report.txt
cat ../logs/permission_report.txt
