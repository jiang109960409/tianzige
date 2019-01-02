package com.qifang.tianzige.scraping;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.mysql.jdbc.StringUtils;
import com.qifang.tianzige.domain.PointDO;
import com.qifang.tianzige.domain.StrokeNumberDO;
import com.qifang.tianzige.domain.WordDO;
import com.qifang.tianzige.util.BytesUtils;
import com.qifang.tianzige.util.HttpClientUtils;

import us.codecraft.xsoup.Xsoup;

/**
 * 爬取偏旁部首
 */
public class ScrapingPianPang {

	private final static String	PIAN_PANG_DOMAIN_NAME	= "https://zidian.911cha.com";
	private final static String	BI_HUA_DOMAIN_NAME		= "https://bihua.51240.com/";
	private final static String	CHROME_DRIVER_PATH		= "D:\\Devopment\\chromedriver\\chromedriver.exe";
	private final static String	TIANZIGE_URL			= "http://www.an2.net/zi/";
	private final static String	MDBG_URL				= "https://www.mdbg.net/chinese/dictionary?page=worddict&wdrst=0&wdqb=";
	private static WebDriver	driver					= null;

	/**
	 * 开始爬取
	 */
	public static void start() {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
		driver = new ChromeDriver();

		String indexHtml = HttpClientUtils.doGet("https://zidian.911cha.com/bushou.html");
		Document indexDocument = Jsoup.parse(indexHtml);
		Elements indexElements = indexDocument.getElementsByAttributeValueMatching("href",
				Pattern.compile("./bushou_[a-z]+.html"));

		Set<String> ppbsUrls = new TreeSet<>();
		indexElements.forEach(l -> ppbsUrls.add(PIAN_PANG_DOMAIN_NAME + l.attr("href").substring(1)));
		ppbsUrls.forEach(l -> scrapingHans(l));
	}

	/**
	 * 爬取偏旁部首下的汉字
	 * @param ppbsUrl
	 */
	private static void scrapingHans(String ppbsUrl) {
		Set<String> ziUrls = new TreeSet<>();
		String ppbsHtml = HttpClientUtils.doGet(ppbsUrl);
		Document ppbsDocument = Jsoup.parse(ppbsHtml);
		Elements ziElements = ppbsDocument.getElementsByClass("zi");
		ziElements.forEach(l -> ziUrls.add(PIAN_PANG_DOMAIN_NAME + "/" + l.child(0).child(0).attr("href")));
		ziUrls.forEach(l -> scrapingHan(l));
	}

	/**
	 * 爬取汉字
	 * @param hanUrl
	 */
	private static void scrapingHan(String hanUrl) {
		WordDO word = new WordDO();
		String hanHtml = HttpClientUtils.doGet(hanUrl);
		Document hanDocument = Jsoup.parse(hanHtml);
		int id = Integer.parseInt(hanUrl.split("/")[3].replaceAll("zi", "").replaceAll(".html", ""), 16);
		String idUTF8 = BytesUtils.bytes2HexString(
				new String(Character.toChars(Integer.parseInt(Integer.toHexString(id), 16))).getBytes());
		word.setId(id);
		word.setIdUTF8(idUTF8);
		setWordName(word, hanDocument);
		setStrokeNumber(word, hanDocument);
		setStrokeOrderSvg(word);
		setPronunciation(word);
	}

	/**
	 * 设置字的发音
	 * @param word
	 */
	private static boolean setPronunciation(WordDO word) {
		if (!StringUtils.isNullOrEmpty(word.getWordName())) {
			driver.get(MDBG_URL + word.getWordName());
			WebElement wordEle = driver.findElement(By.xpath("//*[@id=\"contentarea\"]/table/tbody/tr/td/table/tbody/tr[1]/td[1]/div[1]/a/span"));
			wordEle.click();
			List<WebElement> mandarinPinYins = driver.findElements(By.xpath("//*[@id=\"aj152446_0\"]/table/tbody/tr/td[1]/div/table/tbody/tr/td/table/tbody/tr[1]/td[4]/div/a/span"));
			for (WebElement m : mandarinPinYins) {
				String pinYin = m.getText();
			}
		
		
		
		
		}
		return true;
	}

	/**
	 * 获取字的动画svg
	 * 
	 * 请求地址：http://www.an2.net/zi/zi.php
	 * 请求参数：words=入&typez=bs&types=9&typec=2&typem=7&isfill=2&typef=1&diyziti=
	 * 
	 * 提示消息：
	 * 			1、同一IP20秒内限制只能请求1次，请见谅！
	 * 			2、非正规渠道请求！请认准本站网址http://www.an2.net/zi/
	 * @param word
	 */
	private static boolean setStrokeOrderSvg(WordDO word) {
		try {
			driver.get(TIANZIGE_URL);
			WebElement wordsElement = driver.findElement(By.name("words"));
			if (!String.valueOf(word.getWordName()).equals(String.valueOf('0'))) {
				wordsElement.sendKeys(String.valueOf(word.getWordName()));
			} else {
				System.out.println(new String(Character.toChars(word.getId())));
				wordsElement.sendKeys(new String(Character.toChars(word.getId())));
			}
			WebElement bsElement = driver.findElement(By.xpath("/html/body/form/div[4]/input[3]"));
			bsElement.click();
			WebElement typeElement = driver.findElement(By.xpath("/html/body/form/div[5]/input[5]"));
			typeElement.click();
			WebElement submitElement = driver.findElement(By.xpath("/html/body/form/div[11]/input"));
			submitElement.submit();
			String currentHandle = driver.getWindowHandle();
			Set<String> handles = driver.getWindowHandles();
			for (String handle : handles) {
				if (!handle.equals(currentHandle)) {
					driver.switchTo().window(handle);
					break;
				}
			}
			Document document = Jsoup.parse(driver.getPageSource());
			int size = Xsoup.compile("/html/body/div[1]/ul/li[1]/svg/path").evaluate(document).list().size();
			if (size != 0) {
				LinkedList<LinkedList<PointDO>> pointsAll = new LinkedList<>();
				for (int i = 1; i <= size; i++) {
					String d = Xsoup.compile("/html/body/div[1]/ul/li[1]/svg/path[" + i + "]/@d").evaluate(document)
							.get();
					String[] pointsArr = d.split(" ");
					LinkedList<PointDO> points = new LinkedList<>();
					for (int j = 0; j < pointsArr.length - 1; j += 2) {
						double x = Double.parseDouble(pointsArr[j].substring(1));
						double y = Double.parseDouble(pointsArr[j + 1]);
						PointDO p = new PointDO(x, y);
						points.add(p);
					}
					pointsAll.add(points);
				}
				word.setStrokeOrderSvg(pointsAll);
			} else {
				word.setStrokeOrderSvg(null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return true;
		}
		return true;
	}

	/**
	 * 设置笔画数
	 * @param word
	 * @param hanDocument
	 */
	private static boolean setStrokeNumber(WordDO word, Document hanDocument) {
		StrokeNumberDO strokeNumber = new StrokeNumberDO();
		String infoHtml = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]").evaluate(hanDocument).get();
		Document document = Jsoup.parse(infoHtml);
		String info = document.text();

		Pattern p = Pattern.compile("部外笔画[0-9]+画");
		Matcher m = p.matcher(info);
		if (m.find())
			strokeNumber.setExceptRadicalNumber(
					Integer.parseInt(info.substring(m.start(), m.end()).replaceAll("部外笔画", "").replaceAll("画", "")));
		else
			strokeNumber.setExceptRadicalNumber(0);

		Pattern p2 = Pattern.compile("总笔画[0-9]+画");
		Matcher m2 = p2.matcher(info);
		if (m2.find())
			strokeNumber.setTotalNumber(
					Integer.parseInt(info.substring(m2.start(), m2.end()).replaceAll("总笔画", "").replaceAll("画", "")));
		else
			strokeNumber.setTotalNumber(0);

		Pattern p3 = Pattern.compile("简体部首.+?部");
		Matcher m3 = p3.matcher(info);
		if (m3.find()) {
			strokeNumber.setRadical(info.substring(m3.start(), m3.end()).replaceAll("简体部首", "").replaceAll("部", ""));
		} else {
			Pattern p4 = Pattern.compile("部首.+?部");
			Matcher m4 = p4.matcher(info);
			if (m4.find()) {
				strokeNumber.setRadical(info.substring(m4.start(), m4.end()).replaceAll("部首", "").replaceAll("部", ""));
			} else {
				strokeNumber.setRadical("");
			}
		}

		Pattern p5 = Pattern.compile("结构(单一|左右|上下|左中右|上中下|右上包围|左上包围|左下包围|上三包围|下三包围|左三包围|全包围|镶嵌|品字|田字)");
		Matcher m5 = p5.matcher(info);
		if (m5.find())
			strokeNumber.setComposition(info.substring(m5.start(), m5.end()).replaceAll("结构", ""));
		else
			strokeNumber.setComposition("");
		try {
			String strokeUrl = BI_HUA_DOMAIN_NAME + word.getIdUTF8() + "__bihuachaxun/";
			String strokeHtml = HttpClientUtils.doGet(strokeUrl);

			Document strokeDocument = Jsoup.parse(strokeHtml);
			Elements nameElements = strokeDocument.getElementsMatchingText("名称");
			if (nameElements.size() == 0 || nameElements.get(0) == null
					|| nameElements.get(0).nextElementSibling() == null
					|| nameElements.get(0).nextElementSibling().child(0) == null) {
				strokeNumber.setStrokes(null);
				word.setStrokeNumber(strokeNumber);
				return true;
			} else {
				String strokesStr = nameElements.get(0).nextElementSibling().child(0).ownText();
				String[] strokesArr = strokesStr.split("、");
				LinkedList<String> strokes = new LinkedList<>();
				for (int i = 0; i < strokesArr.length; i++) {
					strokes.add(strokesArr[i]);
				}
				strokeNumber.setStrokes(strokes);
			}
		} catch (Exception e) {
			e.printStackTrace();
			strokeNumber.setStrokes(null);
			word.setStrokeNumber(strokeNumber);
			return true;
		}
		word.setStrokeNumber(strokeNumber);
		return true;
	}

	/**
	 * 设置字名
	 * @param word
	 * @param document
	 */
	private static void setWordName(WordDO word, Document document) {
		String nameImage = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]/div/span/img").evaluate(document)
				.get();
		if (StringUtils.isNullOrEmpty(nameImage)) {
			String name = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]/div/span[1]/text()").evaluate(document)
					.get();
			word.setWordName(name);
		} else {
			String nameImageUrl = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]/div/span/img/@src")
					.evaluate(document).get();
			HttpClientUtils.downloadImage(nameImageUrl, "D:/" + word.getId() + ".png");
			word.setWordName("");
		}
	}

}
