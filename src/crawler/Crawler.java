package crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	
	private static String URL;
	
	
	
	public Crawler (String url) throws IOException{
		this.URL = url;
	}
	
	//get all category links
	public ArrayList<String> getAllCategoryLinks() throws IOException {
		Document doc = Jsoup.connect(URL)
				.timeout(100000)
				.get();
		
		ArrayList<String> categoryLinks = new ArrayList<>();
		Elements categoryElements = doc.select("div.storyblock-hdr a[href]");
		
		for (Element categoryElement : categoryElements) {
			String categoryLink = categoryElement.absUrl("href");
			if(categoryLink.contains("Home")) {
				categoryLinks.add(categoryLink);
			}
		}
		return categoryLinks;
	}
	
	//get all links from a category
	public ArrayList<String> getArticleLinks(String categoryLink) throws IOException {
	
		ArrayList<String> articleLinks = new ArrayList<>();
		
			for(Integer j = 1; j<=1;j++) {
				String pageLink = categoryLink.substring(0, (categoryLink.length()-4)) + "/p/" + j.toString() +".epi";
				System.out.println("loadding link in " +pageLink);
				Document doc = Jsoup.connect(pageLink).get();
				Elements articleElements = doc.select("a[href]");

				for (Element articleElement : articleElements) {
					String articleLink = articleElement.absUrl("href");
					if(!articleLink.contains("/Home/")
							&& !articleLink.contains("/Source/")
							&& !articleLink.contains("/Tag/")
							&& !articleLink.contains("/Group/")
							&&  articleLink.contains(".epi")
							&&  articleLink.contains("http://www.baomoi.com")
							&& (!articleLink.equals("http://www.baomoi.com")
							||!articleLink.equals("http://www.baomoi.com/")))
					{
						if (!articleLinks.contains(articleLink))
							articleLinks.add(articleLink);
					}
				}	
			}	
		return articleLinks;
	}
}
