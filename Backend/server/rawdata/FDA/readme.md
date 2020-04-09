#Script usage:

1. copy the "branded_food.csv" file to this directory
2. in a linux shell, enter:
   ./convertBranded_FoodsToJson.sh

This will output several new files, "branded_food_xxxx.json", each of which contain 500 products in the following format: 
products: [
   {
      "id": "<14 digit gtin>", 
      "info": {
         "ingredients": [
            "ingred1",
            "ingred2",
            ...,
            "ingredn"
         ],
         "score": "-999999999",
         "scoreModified": "0000-00-00",      //year.month.day
         "productModified": "<date from FDA>"
      }
   },
   etc....
]

Another script will upload these files to the database, in order of lowest
suffix to highest--deleting as it goes.  We will likely run once per day in
batches sufficient to keep us below the limit, yet still leave room for other
queries.

This script isn't blazingly fast--it takes almost two minutes to run on my
relatively new computer.  You should expect hundreds of new files to pop up in
your directory--over 600 at least.  It's doing a lot, and it doesn't need to be
run very often.

Note:  if you receive errors upon running the script re: "\r" in line xx:
   - install dos2unix:  sudo apt install dos2unix
   - "dos2unix convertBranded_FoodsToJson.sh"
   - then "./convertBranded_FoodsToJson.sh"

