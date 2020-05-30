# tells awk to use "|" as a delimiter
BEGIN { FS = "|" } 

# https://stackoverflow.com/questions/29613863/awk-split-a-column-of-delimited-text-in-a-row-into-lines
{ printf("\t\"%s\":%s,\n", $1, $2) }
