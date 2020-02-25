# SE308-winter2020-environmental-app

Competitive, gamified behavior tracker to encourage awareness and encourage sustainable practices. Ability to scan product bar codes to see their environmental impact.

# See UI Mockup Here:

https://www.figma.com/proto/bh5f84oIEU3nPKicuKOBUI/SnapGreen?node-id=1%3A10&scaling=min-zoom

# Environmental App Server

## Requirements

### Installation:

#### Windows

We recommend enabling WSL (Windows Subsystem for Linux) first--while it is
possible to install these programs on Windows without doing so, the server will
ultimately be hosted in a Linux environment and therefore will be expressed with
Linux commands. When choosing a "flavor" of Linux to install, choose "Ubuntu
18.04 LTS" from the Microsoft store--certain commands vary depending on which
variety of Linux you choose, and for this we are going with Ubuntu (for now).

[How to install/enable WSL on Windows 10](https://docs.microsoft.com/en-us/windows/wsl/install-win10)

Once WSL has been installed/enabled, you can start it by going to any folder in
explorer, clicking in the box showing your location (e.g. "This PC > Local Disk
(C:) > Users...," just above the folder contents), then typing "wsl" and hitting
_enter_. That will put you into the Linux command line.

#### Linux

##### Installing node.js (from the command line)

> `sudo apt update`

> `sudo apt install nodejs`

> `nodejs -v`

##### Installing npm (node package manager)

> `sudo apt install npm`

> `npm -v`

##### Installing express.js

> `npm install express`

#### Mac

First, you need to install XCode (from the Apple App Store), and Homebrew
(Apple's package manager for Mac). All following commands should be entered
into the terminal:

##### Installing Homebrew

> `ruby -e "$(curl -fsSl https://raw.githubusercontent.com/Homebrew/install/master/install)"`

##### Installing node and npm

> `brew install node`

> `node -v`

> `npm -v`

##### updating node and npm

> `brew upgrade`

> `brew upgrade node`

> `node -v`

> `npm -v`

### Uninstall:

#### Windows

If you've installed via WSL, follow the Linux instructions below from the linux
command line. Otherwise, uninstall programs as you normally would.

#### Linux

> `sudo apt remove nodejs`

> `sudo apt purge nodejs`

> `sudo apt autoremove`

#### Mac

> `brew uninstall node`

## Key Dependencies

### Dev Dependencies

nodemon: Utilized to have the server refresh automatically with every change

## Running the Server

Running the server is as simple as two commands

> `npm install`

> `npm run serve`

You may get a warning from your firewall--go ahead and let it slide.
Your terminal should announce that the server is running. Open up a browser
window, and go to "localhost:8080". You should see a blank page with a button
at the bottom; you should also see a message in the terminal that states "user
connected." Try clicking on the button--you will see repeated messages.
