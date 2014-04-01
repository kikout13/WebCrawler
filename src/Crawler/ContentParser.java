package Crawler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ContentParser {
	ArrayList<String> articleLinks = new ArrayList<>();
	Hashtable<String, Integer> articleCount = new Hashtable<>();
	
	public ContentParser (ArrayList<String> articleLinks) {
		this.articleLinks = articleLinks;
	}
	
	public void writeJsonToFile () throws IOException {
		for(int i=0;i<articleLinks.size();i++) {
		System.out.println("getting content from "+ articleLinks.get(i));	
		Document doc = Jsoup.connect(articleLinks.get(i))
				.timeout(100000)
				.get();
		
		//Lay category cua article
		Element categoryElement = doc.select("div.breadcrums a[href]").last();
		String path = categoryElement.absUrl("href");			//vd: path = "http://www.baomoi.com/Home/TheGioi.rss"
		path = path.substring("http://wwww.baomoi.com/Home".length()-1, path.length()-4).toLowerCase();
		if(articleCount.containsKey(path)) {
			articleCount.put(path, articleCount.get(path)+1);
		} else {
			articleCount.put(path, 1);
		}
		
		//Lay du lieu cua article tu doc
		Article article = new Article();
		article.setUrl(articleLinks.get(i));
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date = new Date();
		String dateStr = dateFormat.format(date);
		article.setTime(dateStr);
		
		//set title
		String title = doc.select("h1.title").text();
		title = title.replace("\\u003c", "<");
		title = title.replace("\\u003e", ">");
		title = title.replace("\\u003d", "=");
		title = title.replace("\"", "'");
		article.setTitle(title);
		
		//set category
		String category = doc.select("div.breadcrums").text();
		category = category.substring(0, category.length()-3);
		article.setCategory(category);
		
		//set description
		String description = doc.select("h2.summary").text();
		description = description.replace("\\u003c", "<");
		description = description.replace("\\u003e", ">");
		description = description.replace("\\u003d", "=");
		description = description.replace("\"", "'");
		article.setDesciption(description);
		
		//set content
		String content = doc.select("table[width] tbody").html().toString();
		content = content.replace("<", "&lt");
		content = Jsoup.parse(content).text();
		content = content.replace("&lt", "<");
		content = content.replace("\"", "'");
		article.setContent(content);
		
		//set key
		String key = doc.select("div.keywords ul.itemlisting a[href]").html();
		String[] keyList = key.split("\n");
		for(int j=0;j<keyList.length;j++) {
			keyList[j] = Jsoup.parse(keyList[j]).text();
		}
		article.setKey(keyList);
		
		//set trends
			//get word
		Trends[] trends;
		String word = doc.select("div.trending ul.itemlisting a[href]").html();
		String[] wordList = word.split("\n");
		trends = new Trends[wordList.length];
		for(int j=0;j<wordList.length;j++) {
			wordList[j] = Jsoup.parse(wordList[j]).text();
		}	
			//get weight
		String[] weightList = new String[wordList.length];
		Elements weights = doc.select("div.trending ul.itemlisting li[class]");
		int count=0;
		for(Element trend : weights) {
			String weight = trend.attr("class").substring(trend.attr("class").length()-1);
			weightList[count] = weight;
			count++;
		}
		for(int j=0;j<trends.length;j++) {
			Trends trend = new Trends();
			trend.setWord(wordList[j]);
			trend.setWeight(weightList[j]);
			trends[j] = trend;
		}
		article.setTrends(trends);
		
		//chuyen article sang dang .json
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String jsonContent = gson.toJson(article);
        jsonContent = jsonContent.replace("\\u003c", "<");
        jsonContent = jsonContent.replace("\\u003e", ">");
        jsonContent = jsonContent.replace("\\u003d", "=");
        jsonContent = jsonContent.replace("\\u0027", "'");
		
		//luu vao file .json
		String fileName = String.format("%06d", articleCount.get(path));
		File file = new File("/home/tintuc"+path+"/"+fileName+".json");
		file.getParentFile().mkdirs();
		
		PrintWriter pw = new PrintWriter(file);
		pw.println(jsonContent);
		pw.flush();
		pw.close();
		}
	}
	
	public static void main (String[] args) throws IOException {
		ListLinks list = new ListLinks("http://www.baomoi.com");
		list.getAllCategoryLinks();
		ArrayList<String> listLinks = list.getAllArticleLinks();

		ContentParser contentParser = new ContentParser(listLinks);
		contentParser.writeJsonToFile();
	}
}
