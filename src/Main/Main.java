package Main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale.Category;

import io.FileIO;
import parser.Parser;
import crawler.Crawler;

public class Main {
		
	public static void main(String[] args) throws IOException {
		
		Crawler crawler = new Crawler("http://www.baomoi.com");
		Parser parser = new Parser();
		FileIO io = new FileIO();
		
		ArrayList<String> categoryLinks = crawler.getAllCategoryLinks();
		
		switch (args[0]) {
		case "all":
			for(int i=0;i<categoryLinks.size();i++) {
				
				ArrayList<String> articleLinks = crawler.getArticleLinks(categoryLinks.get(i));
				Hashtable<String, Integer> articleCount = new Hashtable<>();
				
				for(int j=0;j<articleLinks.size();j++) {
					String jsonContent = parser.ContentParser(articleLinks.get(j));
					io.setUrl(articleLinks.get(j));
					io.getFileName(articleCount);
					io.writeJsonFile(jsonContent);
				}
			}
			break;
		case "thegioi":
		case "xahoi":
		case "vanhoa":
		case "kinhte":
		case "khcn":
		case "thethao":
		case "giaitri":
		case "phapluat":
		case "giaoduc":
		case "suckhoe":
		case "otoxemay":
		case "nhadat":
			for(int i=0;i<categoryLinks.size();i++) {
				if(categoryLinks.get(i).toLowerCase().contains(args[0])) 
					{
						ArrayList<String> articleLinks = crawler.getArticleLinks(categoryLinks.get(i));
						Hashtable<String, Integer> articleCount = new Hashtable<>();
					
						for(int j=0;j<articleLinks.size();j++) {
							String jsonContent = parser.ContentParser(articleLinks.get(j));
							io.setUrl(articleLinks.get(j));
							io.getFileName(articleCount);
							io.writeJsonFile(jsonContent);
						}
					}
			}
			break;
		default:
			System.out.println("No such category");
			break;
		}	
	}
}
