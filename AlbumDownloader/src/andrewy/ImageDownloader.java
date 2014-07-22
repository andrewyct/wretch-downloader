package andrewy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ImageDownloader {
	public static void main(String[] args) {
		// specify user account here
		WretchAlbumDownloader downloader = new WretchAlbumDownloader("user_account");

		// single album: download(target_path, album_id)
		downloader.download("target_path", 1);
		
		// multiple albums: download(target path, album_start, album_end)
		// downloader.download("target_path", 2, 10);

		// all albums: download(target path)
		// downloader.download("target_path");

		System.out.println("[Info] End");
	}
}

class WretchAlbumDownloader {
	private String site = "http://www.wretch.cc/album/";
	private String account;
	private HashMap<String, ArrayList<String>> links = new HashMap<String, ArrayList<String>>();
	
	public WretchAlbumDownloader(String acc) {
		this.account = acc;
	}

	public HashMap<String, ArrayList<String>> getImageLinks() {
		return this.links;
	}
	
	// get image links for a specific album
	public void download(String path, int start) {
		int book = start, result;
		
		while ( book < (start+1) ) {
			result = lookUp(book);
			
			if (result < 0)
				break;
			else {
				book += 1;
			}
			getImages(links, path);
			
			try {
				System.out.println("[Info] Restart in 5 sec\n");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// get image links from album-a to album-b 
	public void download(String path, int start, int end) {
		int book = start, result;
		
		if(start >= end) {
			System.out.println("[Error] Check the album number again\n");
			return;
		}
		
		while ( book <= end ) {
			result = lookUp(book);
			
			if (result < 0)
				break;
			else {
				book += 1;
			}
			getImages(links, path);
			
			try {
				System.out.println("[Info] Restart in 5 sec\n");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	// get image links for all albums
	public void download(String path) {
		int book = 1, result;
		
		while ( true ) {
			result = lookUp(book);
			
			if (result < 0)
				break;
			else {
				book += 1;
			}
			getImages(links, path);
			
			try {
				System.out.println("[Info] Restart in 5 sec\n");
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	//
	public void printLinks(HashMap<String, ArrayList<String>> map) {
		System.out.println("[Info] Printing image links");
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			System.out.println(String.format("%" + 7 + "s" + "Listing album %s ... ", " ", entry.getKey()));
			for (String link : entry.getValue()) {
				System.out.println(String.format("%" + 9 + "s" + "%s", " ", link));
			}
		}
	}

	//
	public void getImages(HashMap<String, ArrayList<String>> map, String path){
		int imgCount = 0, img = 0;
		long startTime = System.currentTimeMillis();
		
		System.out.println("[Info] Start downloading images");
		
		for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
			File dir = new File(path + "/" + account + "/" + entry.getKey());
			if (!(dir.exists())) {
				dir.mkdirs();
				// if (result)
				// System.out.println(String.format("%" + 7 + "s" + "Created new folder", " "));
			}
			
			for (String link : entry.getValue()) {
				try {
					URL url = new URL(link);
					InputStream is = url.openStream();
					OutputStream os = new FileOutputStream(dir.getPath() + "/" + (img + 1) + ".jpg");
					byte[] b = new byte[is.available()];
					int length;
					
//					if(img == 1) {
//						img++;
//						continue;
//					}
					
					while ((length = is.read(b)) != -1) {
						os.write(b, 0, length);
					}

					is.close();
					os.close();
					img += 1;

				} catch (IOException e) {
					System.err.println("[Error] Cannot download picture "+ (img + 1));
				}
				imgCount +=1;
			}
		}
		System.out.println(String.format("%" + 7 + "s" + "Downloaded %d images in ... %s ms\n", " ",
				imgCount, System.currentTimeMillis() - startTime));
		
		this.links.clear();
	}

	//
	public boolean isRedirect(HttpURLConnection conn) {
		int response = 0;
		HttpURLConnection connection = null;
		try {
			connection = conn;
			response = connection.getResponseCode();

			System.out.print(String.format("%" + 9 + "s" + "Response Code ... %d", " ", response));

			if (response != HttpURLConnection.HTTP_OK) {
				if (response == HttpURLConnection.HTTP_MOVED_TEMP
						|| response == HttpURLConnection.HTTP_MOVED_PERM
						|| response == HttpURLConnection.HTTP_SEE_OTHER)
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	//
	public String getPageURL(String url) {
		HttpURLConnection connection = null;
		String redirectedPage;
		try {
			// Establish connection
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setReadTimeout(10000);
			connection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			connection.addRequestProperty("User-Agent", "Mozilla");
			connection.addRequestProperty("Referer", "google.com");
			redirectedPage = connection.getHeaderField("Location");
			connection = (HttpURLConnection) new URL(redirectedPage).openConnection();
			
		} catch (MalformedURLException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println("URL: "+connection.getURL().toString());
		return connection.getURL().toString();
	}

	//
	public String getRedirectedPageURL(HttpURLConnection conn) {
		String newUrl = "";
		
		// if the response code is 300
		
		return newUrl;
	}
	
	// find links and add to the map
	// return 0 when cannot open album, -1 when last album, 1 when album has been mapped
	public int lookUp(int book) {
		String albumUrl, currentUrl, lastUrl;
		Document doc;
		Element displayLink, displayImage;
		long startTime = System.currentTimeMillis();
		boolean hasPassword;
		
		System.out.println("[Info] Start mapping image links for album " + book);
		try {
			// Connect to target URL
			albumUrl = (site + "show.php?i=" + account + "&b=" + book);
			currentUrl = getPageURL(albumUrl);
			
			if (currentUrl.contains("AlbumNotOpen")) {
				// Album is hidden or password protected
				System.out.print(String.format("%" + 7 + "s" + "Hidden album\n", " "));
				return 0;

			} else if (currentUrl.contains("AlbumIsEmpty")) {
				// Album is empty
				System.out.print(String.format("%" + 7 + "s" + "Empty album\n", " "));
				return 0;
				
			} else if (currentUrl.contains("NoSuchAlbum")) {
				// There is no such album
				System.out.print(String.format("%" + 7 + "s" + "No such album\n", " "));
				return -1;

			} else {
				// Album exists and can be opened
				ArrayList<String> temp = new ArrayList<String>();
				doc = Jsoup.connect(currentUrl).userAgent("Mozilla").referrer("google.com").get();
				hasPassword = doc.getElementById("bigcontainer").text().contains("password");
				
				if (hasPassword) {
					System.out.print("[Error] Protected by password\n");
					return 0;
				} else {
					lastUrl = (site + doc.getElementById("last").attr("href").substring(2));
				}
				
				while(currentUrl.compareTo(lastUrl) != 0) {
					// find the URL for first photo, relocate if it returns null element
					displayLink = doc.getElementById("DisplayLink");
					
					if((displayLink != null) && (displayLink.attr("href").contains("referurl")))
						break;
					
					while ( (displayLink == null) && (!hasPassword) ) {
						// find next available image link
						String nextLink = doc.getElementById("next").attr("href");
						System.out.print(String.format("%" + 7 + "s" + "Getting next available photo ... ", " "));	
						
						// if there are no more next links
						if( nextLink.isEmpty() ) {
							System.out.print("Reached last file\n");
							break;
						}
						
						currentUrl = (site + nextLink.substring(2));
						doc = Jsoup.connect(currentUrl).userAgent("Mozilla").referrer("google.com").get();
						
						if((displayLink = doc.getElementById("DisplayLink")) != null )
							System.out.print("Done\n");
						else
							System.out.print("Relocated\n");
					}
					
					if (displayLink != null) {
						String albumName = doc.getElementsByAttributeValueContaining("href",("&book=" + book)).first().text();
						// find the URL for next photo
						while ( (displayImage = doc.getElementById("DisplayImage")) != null) {
							String nextLink =  doc.getElementById("next").attr("href");
							
							if (nextLink.isEmpty()) {
								temp.add(displayImage.attr("src"));
								break;
							} else {
								// store the pic's url from current page
								temp.add(displayImage.attr("src"));
								// connect to the next pic
								currentUrl = (site + nextLink.substring(2));
								doc = Jsoup.connect(currentUrl)	.userAgent("Mozilla").referrer("google.com").get();
							}
						}
						// add the whole list to map, indexed by album number
						links.put(new String(book + "_" + albumName), temp);
						
						if (links.isEmpty()) {
							System.out.print("[Error] Cannot add url to list\n");
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.print("[Error] Cannot connect to URL\n");
			e.printStackTrace();
		} 
		System.out.println(String.format("%" + 7 + "s" + "Mapping completed in ... %d ms", " ",
				System.currentTimeMillis() - startTime));
		return 1;
	}
}