# MediaSOLV
A digital signage software solution comprised of 
* Player App (i.e. scheduler)
* Dashboard App & Alerter App (console)
* Server App

Each component shares the same code base and configuration settings. Besides the major components listed , the JAUUS server app is used to publish jar level updates to all components.

## Player
------
This app is installed and ran on a standard computer that has an Alchemy GFX card installed on it. This "player" computer is streaming multi-channel content to many different screens. Besides exchanging data with the server directly, the player has a scheduler running executing specific tasks including playing list for all content.

## Dashboard
------
Can be installed and ran from a standard computer as the code is writting in Java Swing. This app provides all the controls a user may need to manage player devices. This includes things like pinging, startup/shutdown, content change, play schedule changes and more. Content is sent and received with player devices using an intermediary FTP site.

## Server
------
Any standard server installation will work. The only purpose of this piece is to continually check the health of each player and provide a communcation hub between the Player(s) and Dashboard(s) apps.
