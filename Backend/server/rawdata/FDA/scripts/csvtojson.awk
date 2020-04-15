# tells awk to use "," as a delimiter
BEGIN { FS = "|" } 

# https://stackoverflow.com/questions/29613863/awk-split-a-column-of-delimited-text-in-a-row-into-lines
{
   print("\t\t{")
   printf("\t\t\t\"id\": \"%014d\",\n", $1)
   print("\t\t\t\"info\": {")

   print("\t\t\t\t\"ingredients\": [")
   num_ingreds = split($3, ingreds, ",")
   for(i = 1; i < num_ingreds; ++i){
      printf("\t\t\t\t\t\"%s\",\n", ingreds[i])
   }
   printf("\t\t\t\t\t\"%s\"\n", ingreds[num_ingreds])
   print("\t\t\t\t],")

   print("\t\t\t\t\"score\": \"-999999999\",")
   printf("\t\t\t\t\"dateModified\": \"%s\"\n", $2)
   print("\t\t\t}")
   print("\t\t},")
   # clear the array, reset
   split("", ingreds)
   num_ingreds=0;
}
