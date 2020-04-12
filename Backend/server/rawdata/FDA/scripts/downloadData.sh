#!/bin/bash
FILENAME=$1
FULLPATH="${2}/${FILENAME}"

printf "dowloading from %s\n" "$FULLPATH"
curl -v -O $FULLPATH > curl.log

./distributeFiles.sh $FILENAME

