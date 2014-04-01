package Crawler;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ListLinks {
	private static String URL;
	ArrayList<String> categoryLinks = new ArrayList<>();
	ArrayList<String> articleLinks = new ArrayList<>();
	public ListLinks (String url) throws IOException{
		this.URL = url;
	}
	
	public void getAllCategoryLinks() throws IOException {
		Document doc = Jsoup.connect(URL)
				.timeout(100000)
				.get();
		Elements categoryElements = doc.select("div.storyblock-hdr a[href]");
		for (Element categoryElement : categoryElements) {
			String categoryLink = categoryElement.absUrl("href");
			if(categoryLink.contains("TheGioi")
					|| categoryLink.contains("XaHoi")
					|| categoryLink.contains("VanHoa")
					|| categoryLink.contains("KinhTe")
					|| categoryLink.contains("KHCN"))
			{
				categoryLinks.add(categoryLink);
				//articleLinks.add(categoryLink);
			}
		}	
	}
	
	public ArrayList<String> getAllArticleLinks() throws IOException {
		//Lay link trong cac category theo page. Moi category co 10000 pages.
		for(int i=0; i<categoryLinks.size();i++) {
			String categoryLink = categoryLinks.get(i);
			for(Integer j = 1; j<=10000;j++) {
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
							&& (!articleLink.equals("http://www.baomoi.com")||!articleLink.equals("http://www.baomoi.com/")))
					{
						if (!articleLinks.contains(articleLink))
							articleLinks.add(articleLink);
					}
				}	
			}	
		}
		return articleLinks;
	}
	
}