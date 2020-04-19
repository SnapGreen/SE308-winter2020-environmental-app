# tells awk to use "," as a delimiter
BEGIN { FS = "\",\""} 
# gsub = "global substitution":
# deletes inner extraneous quotes from ingredients column
{gsub(/"/, "", $4)}
# removes ascii "horizontal tab", etc. from ingredients column
{gsub(/[::cntrl:]/, "", $4)}
# replaces ." with "
{gsub(/\.\"/, "\"", $4)}
# removes asterisks from ingredients column
{gsub(/\*/, "", $4)}
# removes any blank ingredient entries
{gsub(/,[, ]*,/, ",", $4)}

# trims to "fdcid|gtin|;ingred1;ingred2...|datemodified"
# first ; in ingreds helps for parsing in next step
{
   num_ingreds = split($4, ingreds, ", ")
   if(num_ingreds > 0){
      printf("%06d|%014d|", substr($1,2), $3)
      for (i = 1; i < num_ingreds; ++i){
         printf(";%s", tolower(ingreds[i]))
      }
      sub(/\./, "", ingreds[num_ingreds])
      printf(";%s|%s\n", tolower(ingreds[num_ingreds]), $10)
      # clear array
      split("", ingreds)
      num_ingreds=0
   }
}
