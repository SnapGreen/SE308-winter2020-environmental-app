#!/bin/bash
FILENAME=$1
FULLPATH="${2}/${FILENAME}"

printf "dowloading from %s\n" "$FULLPATH"
curl -v -O $FULLPATH 
# for logging (needs testing):
#curl -vs -O $FULLPATH 2>&1 > curl.log

./distributeFiles.sh $FILENAME

