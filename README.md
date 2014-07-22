wretch-downloader
=================
Online photo album downloader for [www.wretch.cc](http://en.wikipedia.org/wiki/Wretch_(website)) (a blogging website owned by Yahoo, but it has been [shut down](http://thenextweb.com/asia/2013/08/30/yahoo-owned-taiwanese-blogging-platform-wretch-cc-is-shutting-down-on-december-26/) since December 2013). The program allows user to automatically download open albums (publicly visible) of a specific account. It can be configured to manual or auto mode using different input parameters. 

*wretch-downloader* will parse the HTML information found on the given account, continuously search for the next available album, get the image url (the Mapping), and download the images automatically until it reaches the end of the last album.

###Download the Images
Specify user account:
```java
WretchAlbumDownloader d = new WretchAlbumDownloader("user_account");
```
To download single album:
```java
d.download(target_path, album_id)
```

To download multiple albums: 
```java
d.download(target path, album_start, album_end)
```

To download all albums:
```java
d.download(target_path)
```
