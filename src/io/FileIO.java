package io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Hashtable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class FileIO {
	private String url;
	private String path;
	private String fileName;
	public FileIO() {
		//no args constructor
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void getFileName(Hashtable<String, Integer> articleCount) throws IOException{
		Document doc = Jsoup.connect(url)
				.timeout(100000)
				.get();
		//Lay category cua article
		Element categoryElement = doc.select("div.breadcrums a[href]").last();
		path = categoryElement.absUrl("href");			//vd: path = "http://www.baomoi.com/Home/TheGioi.rss"
		path = path.substring("http://wwww.baomoi.com/Home".length()-1, path.length()-4).toLowerCase();
		path = path.replace("-", "");
		if(articleCount.containsKey(path)) {
			articleCount.put(path, articleCount.get(path)+1);
		} else {
			articleCount.put(path, 1);
		}
		fileName = String.format("%06d", articleCount.get(path));
	}
	
	public void writeJsonFile(String jsonContent) throws IOException {
		File file = new File("tintuc"+path+"/"+fileName+".json");
		file.getParentFile().mkdirs();
		PrintWriter pw = new PrintWriter(file);
		pw.println(jsonContent);
		pw.flush();
		pw.close();
	}
}
