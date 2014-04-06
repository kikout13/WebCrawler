package parser;

import java.io.IOException;

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

import article.Article;
import article.Trends;

public class Parser {
	
	public Parser () {
		//no constructor
	}
	
	//Parse content from an article
	public String ContentParser (String articleLink) throws IOException {
		
		Article article = new Article();
		
		System.out.println("getting content from "+ articleLink);	
		Document doc = Jsoup.connect(articleLink)
				.timeout(100000)
				.get();

		
		//set article url
		article.setUrl(articleLink);
		
		//parse and set crawl date
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date = new Date();
		String dateStr = dateFormat.format(date);
		article.setTime(dateStr);

		//parse and set title
		String title = doc.select("h1.title").text();
		title = title.replace("\"", "'");
		article.setTitle(title);

		//parse and set category
		String category = doc.select("div.breadcrums").text();
		category = category.substring(0, category.length()-3);
		article.setCategory(category);

		//parse and set description
		String description = doc.select("h2.summary").text();
		description = description.replace("\"", "'");
		article.setDesciption(description);

		//parse and set content
		String content = doc.select("table[width] tbody").html().toString();
		content = content.replace("<", "&lt");
		content = Jsoup.parse(content).text();
		content = content.replace("&lt", "<");
		content = content.replace("\"", "'");
		article.setContent(content);

		//parse and set key
		String key = doc.select("div.keywords ul.itemlisting a[href]").html();
		String[] keyList = key.split("\n");
		for(int j=0;j<keyList.length;j++) {
			keyList[j] = Jsoup.parse(keyList[j]).text();
		}
		article.setKey(keyList);

		//parse and set trends
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
			//set trends
		for(int j=0;j<trends.length;j++) {
			Trends trend = new Trends();
			trend.setWord(wordList[j]);
			trend.setWeight(weightList[j]);
			trends[j] = trend;
		}
		article.setTrends(trends);
		
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES).create();
        String jsonContent = gson.toJson(article);
        
		return jsonContent;
	}
}
