<<<<<<< HEAD
# tells awk to use "," as a delimiter
BEGIN { FS = "\",\""} 
# gsub = "global substitution":
#    replaces inner extraneous quotes with _,
{gsub(/"/, "_", $4)}
#    removes \t in ingredients column
{gsub(/\t/, "", $4)} 
#    removes ascii "horizontal tab" in ingredients column
{gsub(/_x000D_/, "", $4)}
#    replaces open parens with ', ' in ingredients column
{gsub(/ \(/, ", ", $4)}
#    replaces close parens with '' in ingredients column
{gsub(/\)/, "", $4)}
# replaces . with ' ' in ingredients column
{gsub(/\./, ",", $4)}
# deletes the phrase "contains 2% or less of:" from ingredients
{gsub(/CONTAINS.*: /, "", $4)}

# https://stackoverflow.com/questions/29613863/awk-split-a-column-of-delimited-text-in-a-row-into-lines
{
   printf("\t{\n\t\t\"id\": \"%014d\",\n", $3)
   # deletes explanations such as "FOR COLOR" 
   #sub(/FOR.*,/, ",", $4)
   print("\t\t\"ingredients\": [")
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
      printf("\t\t\t\"%s\",\n", words[i])
   }
   printf("\t\t\t\"%s\"\n", words[count])
   print("\t\t],")
   print("\t\t\"score\": \"-999999999\",")
   print("\t\t\"scoreModified\": \"0000-00-00\",")
   printf("\t\t\"dateModified\": \"%s\"\n", $10)
   print("\t},")
   # clear both arrays
   split("", seen)
   split("", words)
   split("", ingreds)
   count=0;
}

=======
# tells awk to use "," as a delimiter
BEGIN { FS = "\",\""}

# tells awk the format to print with
{ printf("\t{\"%014d\": \"%s\"},\n", $3, $4) }

>>>>>>> ebfb21a0737a49cfa88dc05c6051a8eb35846c9c
