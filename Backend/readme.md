# Backend Overview:

The server consist of the following subcomponents:

## index.js

This is the actual script that defines the server.  It is run detached, and is only shut down when index.js has been modified.

## script directory

This directory contains all of the scripts that run on the server, which are in the form of Bash scripts.  A more detailed readme.md is provided there.

Upon cloning our repo onto a server running Ubuntu, the "settings.txt" file should be updated to reflect local paths.  Afterward, `populateDB.sh` should be run to adust settings for the initial population of the database.

## data directories

data/ and dataraw/ respectively contain data that has been prepared for uploading, and data that has been downloaded from external sources.  Data that is in the process of being uploaded is stored in data/, while subdirectories contain updates that will be moved to the parent directory upon completion of the current upload batch.

## test directory

This directory stores the various tests for both javascript and bash scripts. It also stores test output, which is compared (diff'd) against expected output.

## log directory

This directory stores logs produced by the Bash scripts, which helps in diagnosing issues.  These logs are also periodically sent to an email specified in the settings as described below.

## temp directory

This directory stores the temporary files produced in the intermediary steps of each script.  When scripts are run in "debugging" mode, the files will be left intact for inspection; otherwise, they are deleted.

# Automation

Scripts are automated via crontab as described here:  https://opensource.com/article/17/11/how-use-cron-linux

`cron` is the default scheduling software on Ubuntu server 18.04, and should come installed.

While it isn't explicitly mentioned in that article, it is important to use full paths when invoking commands in a crontab. It is also noteworthy that cron will attempt to send the output of its operation to the email specified in the MAILTO variable; if email has not been set up, the logs produced by cron in syslog are insufficient to diagnose problems beyond telling whether or not cron actually attempted to run your script.  You can bypass this by explicitly redirectiong the output in the crontab entry.

For example:
`59 23 * * * /home/jtwedt/dosomething.sh > /home/myname/logs/dosomething.log &1>2`
...would run "dosomething.sh" at 11:59 every day, then send the output to "dosomething.log".  Iterestingly, as the output has been redirected to the log, it is no longer captured for emailing.  There are ways around this, but suffice it to say that setting up server email should be a priority before attempting to automate via cron, if one intends to have email anyway.

# Email

Email on the server is run through Postfix, using gmail as a relay as described here:  https://www.linode.com/docs/email/postfix/configure-postfix-to-send-mail-using-gmail-and-google-apps-on-debian-or-ubuntu/

Postfix is the default mail service for Ubuntu server, and should come installed.

It is important to note that there are many tutorials to do this, some of them fail to mention that, to the best of my knowledge, 2-factor authentication is required on your google email account before it can be used (and even then your email may raise flags).

In my first attempts, I tried to use one of my existing gmail accounts.  As I did not want to set up 2-FA on an account that I logged into multiple times daily, I attempted to set up Postfix using that email's login info, which did not work (but was supposed to).  By starting a new email account and setting up 2-fa, I was able to generate a separate password for this purpose (the process is described in the linked article)

# Testing, Debugging, and Logging

Tests can be found in tests/ and can be carried out at will, scheduled through cron, and are automatically appllied whenever code is pushed to the repository (along with coverage measurements).  Most testing is done via diff comparisons, although shunit2 is being integrated.  They are largely component tests, as the scripts are run in debug mode by default and errors both logged and emailed.  The intention is to refactor most of the functions into POSIX C programs, then more properly integrate unit testing.  However, this code will likely never achieve full coverage, as many of the scripts either draw data from, or send data to, third parties.  Automating tests for this on a large scale would likely result in a loss of access on our end.

As mentioned, debugging mode is applied whenever the more complicated scripts are run without flags.  This leaves the intermediary artifacts of each operation in temp/ for inspection.  Scripts are also verbose, with their output captured and logged if being run in automation mode--if a function fails in a script, the log will capture where it happened and the script can be re-run in debug mode to inspect where the error lies.

# Troubleshooting

Depending on the host operating system, you may run into a variety of issues.  For instance, if you are running a server from the wsl (Windows Subsystem for Linux), you might come across errors mentioning "\r" in line xx.  If that is the
case:
   - install 'dos2unix' using the same process outlined at the top
   - `dos2unix convertToJson.sh`
   - then `./convertToJson.sh`

If you receive errors in the vein of "command on line xx not found", open the script that was running and look at that line--that will tell you which program you need to install using the process outlined at the top.

Finally, if you get errors mentioning "sudo", you'll need to either log in as the administrator or try running from a linux environment where you -are- the administrator.  If you've installed your own version of linux, odds are that you already have those rights and simply need to enter `sudo *command*` instead of just `*command*`

In general, while it can seem overwhelming to work with a linux OS if you've never done so before, you will likely find that it is usually easy to find a solution online, and they tend to be easy to implement. 
