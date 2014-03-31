package Crawler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class ContentParser {
	ArrayList<String> articleLinks = new ArrayList<>();
	Hashtable<String, Integer> articleCount = new Hashtable<>();
	
	public ContentParser (ArrayList<String> articleLinks) {
		this.articleLinks = articleLinks;
	}
	
	public void writeContentToFile () throws IOException {
		for(int i=0;i<articleLinks.size();i++) {
		System.out.println("getting content from "+ articleLinks.get(i));	
		Document doc = Jsoup.connect(articleLinks.get(i)).get();
		//Lay category cua article
		Element categoryElement = doc.select("div.breadcrums a[href]").last();
		String path = categoryElement.absUrl("href");			//vd: path = "http://www.baomoi.com/Home/TheGioi.rss"
		path = path.substring("http://wwww.baomoi.com/Home".length()-1, path.length()-4).toLowerCase();
		if(articleCount.containsKey(path)) {
			articleCount.put(path, articleCount.get(path)+1);
		} else {
			articleCount.put(path, 1);
		}
		
		//Lay summary
		Element summaryElement = doc.select("h2.summary").first();
		String summary = summaryElement.text();
		
		//Lay content
		Element contentElement = doc.select("div[itemprop]").first();
		String content = contentElement.toString();
		content = content.replace("<p>", "/n");			// /n = new line
		content = content.replace("<br />", "/n");
		content = content.replace("<span>", "/n");
		content = Jsoup.parse(content).text();
		String[] token = content.split("/n");
		
		//luu content vao file
		String fileName = String.format("%06d", articleCount.get(path));
		File file = new File("/home/manhvu/workspace/WebCrawler/tintuc"+path+"/"+fileName+".txt");
		file.getParentFile().mkdirs();
		PrintWriter pw = new PrintWriter(file);
		pw.println(summary);
		pw.flush();
		for(int j=0;j<token.length;j++) {
			if(token[j].length()>1) {
				pw.println(token[j]);
				pw.flush();
				}
			}
		}
	}
	
	public static void main (String[] args) throws IOException {
		ListLinks list = new ListLinks("http://www.baomoi.com");
		list.getAllCategoryLinks();
		ArrayList<String> listLinks = list.getAllArticleLinks();

		ContentParser contentParser = new ContentParser(listLinks);
		contentParser.writeContentToFile();
	}
}
