# CS190-Final-Project
This project is built in order to keep track with my playlist. It is designed to manage changes in the playlist, including methods creating a backup playlist, and overwriting the current playlist with it.

#The Problem
I hope this project could help me save time managing my playlist. A lot of times I accidentally delete songs from my playlist and I want to recover the changes, I currently create an actual backup playlist in the app, but with this project it would be much more time efficient to update information, as well as copying and pasting songs in a larger scale.

#How it works
0. Before your start, this project is designed exclusively for NetEase Cloud Music and does not support other music platforms. Please make sure you have your own NetEase Cloud Music account before using this application. You would also need the following: 
  Java 21, docker, Gson 2.15.0 (included in this project).

1. After you've setup you account, pull and run moefurina/ncm-api:latest in docker. Run these code in terminal:
   docker pull moefurina/ncm-api:latest
   docker run -d \
   --name ncm-api \
   -p 3000:3000 \
   moefurina/ncm-api:latest
3. Open this link in your browser http://localhost:3000/qrlogin.html. Scan the QR code with your mobile Netease app to login to the Netease API. To find the scanner, click the three lines at the top left corner, the scanner will appear at the top right. After scanning the code, check the login information. You should see "code: 200" appear in the lower section on the screen, find "nickname" to make sure it matches with your username in Netease.
4. Open up Chrome Developer Tools to obtain cookies. Go to Network, open up any request, find header request and copy the cookie. Run this command in terminal:
   export NCM_COOKIE='copy and paste your cookies here'
5. Download this project form Github, compile and run with Gson. Gson is in the file lib. Run these command in terminal:
   javac -cp "src/main/java/lib/gson-2.15.0.jar" src/main/java/*.java
   java -cp "src/main/java/lib/gson-2.15.0.jar" src/main/java/Main.java
6. The program asks for your playlist id. To obtain this, go to your Netease mobile app, find the playlist you want to use, click share, then copy the link it generates. Open the link with your browser, the address would change into some form like https://music.163.com/playlist?id=12234567890. Copy that id.
7. Navigate through the GUI in java.
Backup: creates a backup playlist using your current playlist.
Compare: Compare and display the differences between the backup and the actual paylist.
Refresh: Update the playlist to what is now in your mobile app. Not that it does not automatically update the backup.
Overwrite: Overwrite your playlist in the mobile app with the backup playlist in Main.


#MVP Scope:
1. Setup github repository and java project [x]
2. Create Song class and Playlist class [x]
3. Write Basic methods needed [x]
4. Write playlist comparison [x]
5. Connect with Netease using Netease API [x]
6. Test and finalize [x]

#Future Plans:
1. Build GUI for visual representation of the project. [x]
2. Being able to perform actual operations on actual playlists [x]
3. Allow users to scan the login QR code in the project.
4. Display the difference between the two playlist. [x]
5. Being able to undo accidental changes

#Demo
