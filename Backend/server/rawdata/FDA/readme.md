#Script usage:

1. copy the "branded_food.csv" file to this directory
2. in a linux shell, enter:
   ./convertBranded_FoodsToJson.sh

This will output several new files, "branded_food_xx.json", which contain 15,000 products each in the following format: 
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

Another script will upload these files to the database, in order of lowest "xx"
to highest, by seeking out the lowest available "branded_food_xx.json",
uploading it, then deleting.  It will check once per day.

Note:  if you receive errors upon running the script re: "\r" in line xx:
   - install dos2unix:  sudo apt install dos2unix
   - "dos2unix convertBranded_FoodsToJson.sh"
   - then "./convertBranded_FoodsToJson.sh"

