BEGIN { FS = "|" }
{
   num_ingreds = split($1, ingreds, ";")
   for (i = 1; i <= num_ingreds; ++i){
      if (!(ingreds[i] in seen)){
         seen[ingreds[i]] = 1
         words[++count] = ingreds[i]
      }
   }
   for(i = 1; i < count; ++i){
      printf("%s,", words[i])
   }
   printf("%s\n", words[count])
   split("", seen)
   split("", words)
   split("", ingreds)
   count=0;
}
