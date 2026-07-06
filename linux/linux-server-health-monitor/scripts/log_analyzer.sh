#!/bin/bash
LOG_FILE="../logs/sample_app.log"
echo -e "ERROR: disk full\nINFO: backup done\nERROR: connection lost\nWARNING: low memory\nERROR: disk full" > $LOG_FILE

echo "=== Log Analysis Report ===" > ../logs/analysis_report.txt
echo "Total ERROR count:" >> ../logs/analysis_report.txt
grep -c "ERROR" $LOG_FILE >> ../logs/analysis_report.txt

echo "Unique error messages (sorted by frequency):" >> ../logs/analysis_report.txt
grep "ERROR" $LOG_FILE | sort | uniq -c | sort -rn >> ../logs/analysis_report.txt

cat ../logs/analysis_report.txt
