# SE308-winter2020-environmental-app

Competitive, gamified behavior tracker to encourage awareness and encourage sustainable practices. Ability to scan product bar codes to see their environmental impact.

# See UI Mockup Here:

https://www.figma.com/proto/bh5f84oIEU3nPKicuKOBUI/SnapGreen?node-id=1%3A10&scaling=min-zoom

# Analysis and Design Models

## Component Diagram
![Annotation 2020-03-08 141438](https://user-images.githubusercontent.com/38018381/76172520-25f23980-6154-11ea-9d1f-c3ae9858771d.png)

## Use Case Diagram

The app involves two actors, one being a player(user) and the other being a clock. The player can perform various activities. They can login to the app and if they don't have an account they can create a new account. A player can also access their settings, add friends, enter usage stats which will also lead to the system to calculate the stats. They can create a new game, which in turn will start the game clock countdown and this is managed by the clock. Finally the player can scan the product barcode and in return view the environmental impact of it.
<img width="605" alt="Screen Shot 2020-03-08 at 3 20 23 PM" src="https://user-images.githubusercontent.com/38018381/76172459-71581800-6153-11ea-88e2-5ae69b7becf9.png">

## Activity Diagram

This diagram displays the process of creating, playing, and ending a game. Reading from top to bottom you can see the different decisions at each step and what happens after the user makes a decision on whether or not to perform a certain action. The diagram is pretty self-explanatory and easy to follow.
<img width="626" alt="Screen Shot 2020-02-21 at 11 51 02 AM" src="https://user-images.githubusercontent.com/38018381/76172476-ab291e80-6153-11ea-8859-beb67f91f88d.png">

This diagram shows the basic workflow when adding usage data into SnapGreen. Several different statistics are updated including any games in progress.
![StatsActivityDiagram](https://user-images.githubusercontent.com/44537937/76588351-0a649700-64a4-11ea-8824-5d71447ea23f.png)

## Class Diagram

This diagram is a rough draft that shows the interaction between the different main classes of the game. It also shows the different methods that perform the various actions within the app. It also highlights the dependencies between one class and another.

<img width="680" alt="Screen Shot 2020-03-08 at 4 15 50 PM" src="https://user-images.githubusercontent.com/38018381/76172963-1d9bfd80-6158-11ea-8b19-47a29ac5901c.png">

## Sequence Diagram

This diagram shows the interaction between the app, server, and database when a user tries to login. The app sends the login attempt information to the server and the server queries the database and recieves a response. The server then sends a response to the app based on whether the login attempt was valid, whether the user doesn't exist, or whether the password doesn't match.
![Annotation 2020-03-02 212312](https://user-images.githubusercontent.com/38018381/76172505-f80cf500-6153-11ea-8d9c-cf0885f0c9ec.png)

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
