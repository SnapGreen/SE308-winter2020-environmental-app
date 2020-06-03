# SnapGreen | Cal Poly Software Engineering I & II 2020
[![Build Status](https://travis-ci.com/SnapGreen/SE308-winter2020-environmental-app.svg?branch=master)](https://travis-ci.com/SnapGreen/SE308-winter2020-environmental-app)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=SnapGreen_SE308-winter2020-environmental-app&metric=alert_status)](https://sonarcloud.io/dashboard?id=SnapGreen_SE308-winter2020-environmental-app)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=SnapGreen_SE308-winter2020-environmental-app&metric=sqale_index)](https://sonarcloud.io/dashboard?id=SnapGreen_SE308-winter2020-environmental-app)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=SnapGreen_SE308-winter2020-environmental-app&metric=code_smells)](https://sonarcloud.io/dashboard?id=SnapGreen_SE308-winter2020-environmental-app)

<em>Providing users with a measurable awareness of their environmental impact. Users can track daily habits and scan products to increase and improve their environmental score.</em>

<a href="https://github.com/SnapGreen/SE308-winter2020-environmental-app/tree/master/Backend">Understanding Our Backend</a>

# Coding Style
<a href="https://kotlinlang.org/docs/reference/coding-conventions.html">Kotlin Style Convention</a>

<a href="https://prettier.io/docs/en/">Javascript Style Convention: Prettier</a>

# Static Code Analysis
<a href="https://sonarcloud.io/dashboard?id=SnapGreen_SE308-winter2020-environmental-app">View SonarCloud Project Dashboard</a>

# Continuous Integration Software
<a href="https://travis-ci.com/github/SnapGreen/SE308-winter2020-environmental-app">View Travis CI Dashboard</a>

# Design Diagrams and Prototypes
<details>
  <summary>View Diagrams Here</summary>
<br>
 
## UI Prototypes
<a href="https://www.figma.com/proto/bh5f84oIEU3nPKicuKOBUI/SnapGreen?node-id=1%3A10&scaling=min-zoom">View Figma Mockup</a>
 
## Component Diagram
![Screen Shot 2020-06-01 at 2 31 37 PM](https://user-images.githubusercontent.com/38018381/83457144-e61ab580-a415-11ea-837f-5249a253b3e5.png)

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
</details>

# Testing
Our team is utilizing Espresso and JUnit for Kotlin and Jest for JS testing.

<a href="https://docs.google.com/document/d/127FFINRSePh865mnvbP_oG0q_9rLcbGXoqgU5NzB5AQ/edit?usp=sharing">View Acceptance Test Specification</a>

<a href="https://github.com/SnapGreen/SE308-winter2020-environmental-app/blob/master/Client/app/src/androidTest/java/com/acme/snapgreen/ui/login/AcceptanceTests.kt">View Acceptance Test Code</a>

# Setting up the Developer Environment
Our app utilizes Android Studio for front-end development and Node.js for the backend. View both 

<details>
  <summary>Android Studio Setup</summary>
<br>
  
  ### Installation:
  
  Install latest version of [Android Studio](https://developer.android.com/studio). 
  
  Navigate to Tools > SDK Manager
  
  Download and Install Android 9.0 (Pie)
  
  Navigate to SDK Tools
  
  Download and Install Google Play Services 
  
  Clone the repository
  
  Go to File > Open and select the "Client" folder from the repository.
  
  Wait for import and gradle sync to complete.
  
  If prompted, download and install the latest versions of both gradle and kotlin (may not be neccessary) 
  
  Connect an android phone with developer mode activated and USB debugging turned on
  
  OR Navigate to Tools > AVD Manager
    Select Create Virtual Device 
    Select Pixel 3 > Pie > Finish
   
  Click on the play button on the top of Android Studio to build and run the app!

</details>
<details>
  <summary>Node Server Setup</summary>
<br>
  
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

  ### Some Important Dev Dependencies

  husky: Allows for pre-commits hooks (Used to run prettier styling for every JS commit)
  jest: Testing framework for JS
  nodemon: Utilized to have the server refresh automatically with every change
  prettier: Automatic code formatting for every JS commit

  ## Running the Server

  Running the server is as simple as two commands

  > `npm install`

  > `npm run serve`

  You may get a warning from your firewall--go ahead and let it slide.
  Your terminal should announce that the server is running. Open up a browser
  window, and go to "localhost:8080". You should see a blank page with a button
  at the bottom; you should also see a message in the terminal that states "user
  connected." Try clicking on the button--you will see repeated messages.

 </details>
