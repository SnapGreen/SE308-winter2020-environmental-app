#Script usage:

<<<<<<< HEAD
1. make sure that jq is installed
   - on ubuntu, the commands are:
      - sudo apt install jq
2. copy the "branded_food.csv" file to this directory
3. in a linux shell, enter:
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
         "scoreModified": "0000.00.00",      //year.month.day
         "productModified": "0000.00.00"
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
=======
1. copy the "branded_food.csv" file to this directory
2. in a linux shell, enter:
   ./convertBranded_FoodsToJson.sh

This will output a new file, "branded_food.json", which consists of a json key "gtin14ToIngredients" paired to a list of key-value pairs.

The format for those pairs is:
 {"<14 digit gtin number>": "<list of ingredients>"}

>>>>>>> ebfb21a0737a49cfa88dc05c6051a8eb35846c9c

