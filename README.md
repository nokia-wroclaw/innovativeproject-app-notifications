# innovativeproject-app-notifications
### Notify Me

The goal of this project is to build an app, which will help most of peoples to handle the problem of overwhelming 
number of notifications.

Notify Me is quite simple app which gives You an oportunity to make whatever You want with notifications that comes to 
You from any source, You want. All You have to do is write an service that will handle You source, and send 
Notifications to our restApi, of course to show You how powerfull this tool is, we've developed two basic source 
(Twitter and any Website) that will show You, how easy and usefull it can be.

Notification format:
``` 
{ notificationID : ,
  userID : ,
  sourceID : ,
  flag : ,
  topic : ,
  message : ,
  timestamp : ,
  priority : }
```

# New Features!

  - Set any Website you want and be notified when it changes its content!
  - Set Your twitter account, and manage Notifications in a way you've never dreamed about.
  - Install our plugin in Your Google Chrome on any computer You want!
  - Install our android application and You will never miss any Notification!


# Installation

Set up any linux-based device by installing Docker and docker-compose
https://docs.docker.com/install/linux/docker-ce/ubuntu/#prerequisites
https://docs.docker.com/compose/install/#install-compose
Download Zip file from our repository, and just run our script 
```
$ ./build.sh 
```
And that's all, unfortunatelly all of our microservices, are using our own server, thats why You will have to change it 
to Your own.
Install the dependencies and devDependencies and start the server.
Of course if You would like to change anything we've prepared instruction that will propably help You handle all 
problems, if not, just contact any of our team members :)

# Contributing

Students
  - Arkadiusz Sokołowski
  - Bartosz Gardziejewski
  - Filip Baszak
  - Mateusz Wójcik
 
Nokia Supervisors
  - Dominik Markiewicz
  - Mateusz Sołtysik

