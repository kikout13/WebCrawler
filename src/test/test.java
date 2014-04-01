package test;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import Crawler.Article;
import Crawler.Trends;

public class test {	
	public static void main (String[] args) throws IOException {
		Article article = new Article();
		String url = "http://www.baomoi.com/Dau-an-Viet-Nam-tai-Hoi-nghi-thuong-dinh-an-ninh-hat-nhan/122/13412416.epi";
		Document doc = Jsoup.connect(url).get();
		//System.out.println(doc);
		//set url
		article.setUrl(url);
		//set date crawl
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		Date date = new Date();
		String dateStr = dateFormat.format(date);
		article.setTime(dateStr);
		//set title
		String title = doc.select("h1.title").text();
		article.setTitle(title);
		//set category
		String category = doc.select("div.breadcrums").text();
		category = category.substring(0, category.length()-3);
		article.setCategory(category);
		//set description
		String description = doc.select("h2.summary").text();
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
		for(int i=0;i<keyList.length;i++) {
			keyList[i] = Jsoup.parse(keyList[i]).text();
			//System.out.println(listKeys[i]);
		}
		article.setKey(keyList);
		//set trends
			//get word
		Trends[] trends;
		String word = doc.select("div.trending ul.itemlisting a[href]").html();
		String[] wordList = word.split("\n");
		trends = new Trends[wordList.length];
		for(int i=0;i<wordList.length;i++) {
			wordList[i] = Jsoup.parse(wordList[i]).text();
			//System.out.println(wordList[i]);
			//trends[i].setWord(wordList[i]);
		}	
			//get weight
		String[] weightList = new String[wordList.length];
		Elements weights = doc.select("div.trending ul.itemlisting li[class]");
		int i=0;
		for(Element trend : weights) {
			String weight = trend.attr("class").substring(trend.attr("class").length()-1);
			weightList[i] = weight;
			//System.out.println(weight);
//			trends[i].setWeight(trend.attr("class").substring(trend.attr("class").length()-1));
			i++;
		}
		for(int j=0;j<trends.length;j++) {
			Trends trend = new Trends();
			trend.setWord(wordList[j]);
			trend.setWeight(weightList[j]);
			trends[j] = trend;
//			System.out.println(trends[j].word + " " +trends[j].weight);
		}
		article.setTrends(trends);
//		System.out.println(article.get);
		Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
        String str = gson.toJson(article);
        PrintWriter pw = new PrintWriter("000001.json");
        str = str.replace("\\u003c", "<");
        str = str.replace("\\u003e", ">");
        str = str.replace("\\u003d", "=");
        str = str.replace("\\u0027", "'");
        pw.print(str);pw.flush();pw.close();
		System.out.println(str);
	}
}
