# tells awk to use "|" as a delimiter
BEGIN { FS = "|" } 

{ printf("\t\"%s\":%s,\n", $1, $2) }
