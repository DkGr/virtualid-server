# Virtual iD

Virtual iD is a project prototype to create an open-source and encrypted social network with no personal data violations. Uploaded files (photos, videos,...) are stored into a private ownCloud space syncable with your computers, tablets and smartphones. Encryption/decryption features are powered by [OpenPGP.js](https://github.com/openpgpjs/openpgpjs). File storage and sync are powered by [NextCloud](https://nextcloud.com/).

## Working features

- Basic account Creation
- JWT Token authentication
- OpenPGP keys generation at account creation
- Public stream post creation

## TODO

- Photo/Video link embeded into posts
- User search
- Comment system
- Like system
- Notification system
- Friendship system
- Instant Messaging
- Stream posts visibility system
- OpenPGP encryption/Decryption for posts
- Photo/Video upload into NextCloud server
- Account information page
- Privacy settings
- Encryption settings
- Email system
- OpenPGP encryption/Decryption for emails
- Localization system and translation
- Events system (creation, invites,...)
- Personal uploaded files sync (webdav) provided by NextCloud
- Personal Virtual iD Calendar sync (caldav) provided by NextCloud
- Virtual iD Contacts sync (carddav) provided by NextCloud
- Geolocation sharing
- Virtual Community pages
- Free and Premium account system (More cloud storage for premium)

## Installation on Ubuntu 20.04

Follow [Offical mongodb instructions](https://docs.mongodb.com/manual/tutorial/install-mongodb-on-ubuntu/) to install mongodb server


Next, start mongodb server :

```sudo service mongod start```

Install Java 14 :

```apt install openjdk-14-jdk-headless```

Download the latest release :

```wget https://github.com/eline-technologies/virtualid-server/releases/download/0.1.0-alpha/virtualid-server-0.1.0.jar```

Now you can run the Virtual iD server :

```java -jar virtualid-server-0.1.0.jar```


## How to contribute or build from source

Install [IntelliJ Idea](https://www.jetbrains.com/idea/)

Download or clone sources from this repository

Choose "Open or Import" from the IntelliJ Idea welcome pane