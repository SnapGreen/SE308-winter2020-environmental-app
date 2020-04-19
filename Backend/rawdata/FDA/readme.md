#Data

This folder contains the latest data from the FDA "branded_food.csv", as well as
a fluctuating amount of "branded_food_xxxx.json"s to be uploaded to the
database.  It is populated and de-populated by scripts in the "scripts" folder,
which will be scheduled to run periodically.  More info can be found in the
readme.md found there.

The relevance of the data is as follows:

##Branded_Food.csv
This is the latest file from the FDA containing product information.  It may be
obtained by running a script in the folder.

##fdcid-gtin.txt
This file contains pairs mapping 6-digit fdcid's to 14 digit gtin-upc codes.
When the FDA updates their database, the information is provided in a .csv that
doesn't contain the gtin information--so this file will allow us to map that
information to the relevant products in the database and make our updates as
needed.  This is also produced by a script.

##Branded_Food_xxxx.json
These are produced by a script as well--they represent 500 products in json
format each from Branded_Food.csv.  Another script will periodically take the
lowest-numbered (that's the xxxx part), upload it to the database, then delete
them as it goes.  Upon first run, expect more than 700 of them to show up (as of
April 2020).
