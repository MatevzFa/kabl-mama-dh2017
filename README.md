# KABL MAMA's Dragonhack 2017 Project

*About Dragonhack: [http://dragonhack.si/](http://dragonhack.si/)*

We created a set of applications which try to prevent bad habits people (and particularly students) have by forcing them to leave their PC and go for a walk to a location nearby.

## Google Chrome Extension
Found in [/extension](extension), this extension listens for browser tab updates. Whenever they happen it posts all of the urls in the tabs to [the Server](#the-server)

## The Server
The Server is built with [NodeJS](https://nodejs.org/). It's a very simple ExpressJS server with GET and POST methods for handling requests such as:
* GET for obtaining info about opened tabs,
* GET for checking whether the task has been completed,
* POST for sending data about opened tabs and
* POST for signaling that the given task has been completed.

When it recieves a POST request from the (Extension)[#google-chrome-extension] it checks for tabs which suggest that the user is wasting time (by e.g. browsing 9gag, reddit, facebook etc.). Domains that are considered waste of time are stored in [bad-urls.json](server/bad-urls.json)

## The Android App
When the user gets locked out of his PC they use this app to regain access to the PC. This is done by visiting one of the locations nearby (which were set by the user). Once you get close enough to the chosen location the user taps the 'Check In' button in the app and thus informs the server that the task has been completed.

## The PC Application
This is a Daemon-like application which runs continuously in the background. It's checking for any apps which could mean that the user is wasting time, and also communicates with the Chrome Extension via [the Server](#the-server). With obtained information it decides whether the user should be locked out of their PC.  
Before the lockout, the user is given a 15 minute notice, so they have some time to finish whatever they are doing. After the time runs out PC access is blocked, and remains so until [the Android App](#the-android-app) is used to unlock it again.