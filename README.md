wretch-downloader
=================
Online photo album downloader for [www.wretch.cc](http://en.wikipedia.org/wiki/Wretch_(website)) (a blogging website owned by Yahoo!). The program allows user to automatically download open albums (publicly visible) of a specific account. It can be configured to manual or auto mode using different input parameters. 

*wretch-downloader* will parse the HTML information found on the given account, continuously search for the next available album, get the image url (the Mapping), and download the images automatically until it reaches the end of the last album. The code can be used as a sample of parsing web page information using Java and jsoup.

*** *UPDATE* *** 
The website has been [shut down](http://thenextweb.com/asia/2013/08/30/yahoo-owned-taiwanese-blogging-platform-wretch-cc-is-shutting-down-on-december-26/) since December 2013.

<br />  
###Getting Started
The jsoup library ([download here](http://jsoup.org)) is required.

<br />  
###Download the Images

####Specify user account:
```java
WretchAlbumDownloader d = new WretchAlbumDownloader("user_account");
```
<br />  
####To download single album:
```java
d.download(target_path, album_id)
```
<br />  
####To download multiple albums: 
```java
d.download(target path, album_start, album_end)
```
<br />  
####To download all albums:
```java
d.download(target_path)
```
<br />  
