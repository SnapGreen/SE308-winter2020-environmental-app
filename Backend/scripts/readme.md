# Script usage:

All of the following should be done from a linux command line.  Windows users might consider enabling wsl (Windows Subsystem for Linux), or opting for something like the Bash environment optionally provided when Git is installed.  All scripts are written for use on Ubuntu Server 18.04.04 LTS

Once on the Linux command line, verify that 'unzip' has been installed by typing:

   `unzip --version`

If it hasn't been installed, you'll get a message informing you of such, hopefully with information on how it can be installed.  On Ubuntu, the command to install is:

   `apt get update`
   `apt get ugrade`
   `apt install unzip`

This ensures that all of your other programs are up-to-date, and can be used for most other programs as well (troubleshooting notes at end for more information).

All of the scripts need to be made executable, if not already.  You can check by typing:

   'ls -la'

This will output a list of all files in the directory; on the far left you'll see a column that looks something this:

-rwxrwxrwx
-rwxrwxrwx
...

Basically, if you aren't seeing an "x" in the files that end with .sh, you will need to change the file to make it e'x'ecutable before you can run it using:
   `chmod u=rwx,g=rwx,u=r *filename*`

For a quick refresher, go here:
https://www.computerhope.com/unix/uchmod.htm

## 1. in a linux shell, enter:
   `./getFDAUpdate.sh [-f]|[-d]|[-h]`

The [-f]|[-d]|[-h] part represents optional flags--i.e., you can run it in one of four ways:
   `./getFDAUpdate.sh` or
   `./getFDAUpdate.sh -h` or
   `./getFDAUpdate.sh -d` or
   `./getFDAUpdate.sh -f`

If run without flags, the script will download a web page from the FDA which lists the contents of their data directory, parse that page for relevant data, then check if the newest data is different than our current data.  If so, it will ask if you would like to download the newer file.

The -h flag will output information on what the other flag options do.  There is more information here, but it's traditional to have such an option.

The -d flag will download if there is an update -without- prompting you.  It's designed to be automated.

The -f flag will download whether the latest version is newer or not.  Use this the first time you run the program, and it will download the source data from the FDA to your local directory.

More details can be found in comments of the script itself.

If you choose to download, the script will then automatically call another script:

## 2. <span>downloadData.sh</span> *filename*
This script will download the latest "FoodData_Central_....zip" file to this directory.  Once that's complete, the file will be passed to:

## 3. <span>distributeFiles.sh</span> *filename*
This script unzips the archive into this directory, then distributes them to where they need to go.  It then deletes the files we don't need, and keeps:

### branded_food.csv
This is our main source of data--it contains a plethora of information.  We only take four columns--the gtin-upc codes, the fdc_ids, the ingredients, and the date modified.  It is moved into the parent "FDA" folder

### food_update_log_entry.csv
This lists all of the changes that have been made since the last update.  Once our initial upload is finished, we will instead only upload/update the products listed in this file.  It stays in this folder

## 4. in a linux shell, enter:
   `./convertToJson.sh` or
   `./convertToJson.sh -f`

The default method will do everything and leave the temporary files behind for inspection (debugging) purposes.  The second one will remove temporary files as it goes.

This script does the heavy work, and can take quite awhile to complete.  Complete details can be found in the comments of the script itself, but basically it takes the FDA file and outputs: 

#### branded_food_xxxx.json
Expect hundreds of these, each placed in the parent directory.  They each contain a set number of products in json format:

products: [
   {
      "id": "*14 digit gtin*", 
      "info": {
         "ingredients": [
            "ingred1",
            "ingred2",
            ...,
            "ingredn"
         ],
         "score": "-999999999",
         "productModified": "*date from FDA*"
      }
   },
   etc....
]

The number of products per .json is set to 250, but can be changed by modifying the "SPLIT_FILE_LINES" constant in "settings.txt"

#### map_fdcid_gtin.txt

This file contains hundreds of thousands of pairs, mapping 6-digit fdc id numbers to 14 digit gtin upc codes.  When the FDA makes changes, they list them in a file included with our download; the changes are listed by fdc id, which we currently aren't storing in our database.  This file allows us to figure out which of our products need to be updated. 

## 5. <span>uploadToDB.sh</span>

This script will upload ~34 of the lowest-numbered "branded_food_xxxx.json" files to our database, then delete them.  It is meant to be run once daily, ensuring that we don't go over our 20k writes/per day limit.

# Other Files

There are several other scripts in this directory--here's a short description of their purpose:

### settings.txt
The various scripts get their variables from here before they run.  It saves the effort of having to change, for instance, filenames in every script.  If, for instance, you wanted to change how many products are listed per json, you would do that here.

### *something*.awk
These are scripts passed to 'awk', which is a program that specializes in processing table-like data quickly.

### removalpatterns.txt
This is a slew of regular expression patterns to help clean the "ingredients" provided by manufacturers.  This is by far the slowest part to run, and will be adjusted/optimized as time allows.

### currentlatest.txt and lastlatest.txt
These files store the date that the most recent FDA data was uploaded, as well as the last one that was updated.  Whenever 

### *something*.tmp
These files pop up from time to time--they are usually deleted by the scripts, so you'll see them coming and going as you run them.  They serve as temporary files for intermediate steps in the data processing stages.

### sedxxxxx
These files pop up while sed is running through the 'ingredients.tmp' file.  They'll disappear when it's done, unless the program is interrupted.  If you happen to see them lying around while the program isn't running, it's safe to remove them.

# Server Automation

In order to automate these processes on the server, you'll have to schedule them to be performed automatically.  On our server, this was done via cron as described here:  https://opensource.com/article/17/11/how-use-cron-linux
In the case of our server, two lines were added:
   - `01 23 * * * .../getFDAUpdate.sh -b`
   - `01 00 * * * .../uploadToDB.sh -b`
The first line tells the server to check for an update from the FDA at 11:01 PM every night (bypassing debug mode, which would prompt the user).  the second line tells the server to upload to Firebase at 12:01 AM every night (also bypassing debug mode).

# Troubleshooting

This script isn't blazingly fast--it takes almost two minutes to run on my relatively new computer.  You should expect hundreds of new files to pop up in your directory--over 600 at least.  It's doing a lot, and it doesn't need to be run very often.  I imagine the linux gurus looking over this code and grimacing--sorry, I've only been using it for about a year.  I will continue to optimize as time goes on, time permitting.

If you receive errors upon running the script re: "\r" in line xx:
   - install 'dos2unix' using the same process outlined at the top
   - `dos2unix convertToJson.sh`
   - then `./convertToJson.sh`

If you receive errors in the vein of "command on line xx not found", open the script that was running and look at that line--that will tell you which program you need to install using the process outlined at the top.

Finally, if you get errors mentioning "sudo", you'll need to either log in as the administrator or try running from a linux environment where you -are- the administrator.  If you've installed your own version of linux, odds are that you already have those rights and simply need to enter `sudo *command*` instead of just `*command*`

