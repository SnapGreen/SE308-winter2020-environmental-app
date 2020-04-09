# tells awk to use "," as a delimiter
BEGIN { FS = "\",\""} 
# gsub = "global substitution":
#    replaces inner extraneous quotes with _,
{gsub(/"/, "_", $4)}
#    removes \t in ingredients column
{gsub(/\t/, "", $4)} 
#    removes ascii "horizontal tab" in ingredients column
{gsub(/_x000D_/, "", $4)}
#    removes asterisks from ingredients column
{gsub(/\*/, "", $4)}
#    replaces open parens with ', ' in ingredients column
{gsub(/ \(/, ", ", $4)}
#    replaces close parens with '' in ingredients column
{gsub(/\)/, "", $4)}
# replaces . with ' ' in ingredients column
{gsub(/\./, ",", $4)}
# deletes the phrase "contains 2% or less of:" from ingredients
{gsub(/CONTAINS.*: /, "", $4)}
# deletes left brackets, replaces with ','
{gsub(/\[/, " ,", $4)}
# deletes right brackets, replaces with ','
{gsub(/\]/, ", ", $4)}
# deletes "VITAMIN --"
{gsub(/ VITAMIN.*,/, "", $4)}
# changes "SOMETHING CURED WITH SOMETHING" to "SOMETHING, SOMETHING" 
{gsub(/ CURED WITH:?/, ",", $4)}
# removes "FROM SOMETHING"
{gsub(/FROM .*,/, "", $4)}

# https://stackoverflow.com/questions/29613863/awk-split-a-column-of-delimited-text-in-a-row-into-lines
{
   print("\t{")
   printf("\t\t\"id\": \"%014d\",\n", $3)
   print("\t\t\"info\": {")
   print("\t\t\t\"ingredients\": [")
   num_ingreds = split($4, ingreds, ", ")
   for (i = 1; i <= num_ingreds; ++i){
      # remove the statement "bla bla ingredients: "
      if(ingreds[i]~/INGREDIENTS: /){
         sub(/.*INGREDIENTS: /, "", ingreds[i])
      }
      if (!(ingreds[i] in seen) && !(ingreds[i]~/FOR .*/)){
         seen[ingreds[i]] = 1
         words[++count] = ingreds[i]
      }
   }
   for(i = 1; i < count; ++i){
      printf("\t\t\t\t\"%s\",\n", tolower(words[i]))
   }
   sub(/,/, "", words[count])
   printf("\t\t\t\t\"%s\"\n", tolower(words[count]))
   print("\t\t\t],")
   print("\t\t\t\"score\": \"-999999999\",")
   printf("\t\t\t\"dateModified\": \"%s\"\n", $10)
   print("\t\t}")
   print("\t},")
   # clear both arrays
   split("", seen)
   split("", words)
   split("", ingreds)
   count=0;
}
