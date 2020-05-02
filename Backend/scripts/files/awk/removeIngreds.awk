BEGIN { FS = "|" }
{ printf("%s|%s\n", $2, $4) }

