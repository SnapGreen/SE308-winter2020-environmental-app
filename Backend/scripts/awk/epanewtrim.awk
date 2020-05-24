# tells awk to use "," as a delimiter
BEGIN { FS = "\",\""} 
# gsub = "global substitution":
# only "Gray [Square]" items are kept on this list
# the other items are in the "old data"
# trims to "chemical name|-2"
{ printf("%s|-2\n", $2) }
