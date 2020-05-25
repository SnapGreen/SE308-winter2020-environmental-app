# tells awk to use "|" as a delimiter
BEGIN { FS = "|" } 

# https://stackoverflow.com/questions/29613863/awk-split-a-column-of-delimited-text-in-a-row-into-lines
{
   print("\t\t{")
   printf("\t\t\t\"chemical\": \"%s\",\n", $1)
   printf("\t\t\t\"score\": %s\n", $2)
   print("\t\t},")
}
