# tells awk to use "," as a delimiter
BEGIN { FS = "\",\""}

# tells awk the format to print with
{ printf("\t{\"%014d\": \"%s\"},\n", $3, $4) }

