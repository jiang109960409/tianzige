package com.qifang.tianzige.scraping;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;
import com.qifang.tianzige.domain.BasicExplainDO;
import com.qifang.tianzige.domain.CharacterDecompositionDO;
import com.qifang.tianzige.domain.CharacterEncodingDO;
import com.qifang.tianzige.domain.PointDO;
import com.qifang.tianzige.domain.PronunciationDO;
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
	private final static String	YUEYV_URL				= "http://www.yueyv.cn/";
	private final static String	DOWNLOAD_IMG_SRC		= "D:\\hanimg\\";
	private static WebDriver	driver					= null;
	private static Connection	conn;
	private final static String	JDBC_DRIVER				= "com.mysql.jdbc.Driver";
	private final static String	DB_URL					= "jdbc:mysql://localhost:3306/mdbg";
	private final static String	USERNAME				= "root";
	private final static String	PASSWORD				= "12345";

	/**
	 * 开始爬取
	 * @throws ClassNotFoundException 
	 * @throws SQLException 
	 */
	public static void start() {
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
		driver = new ChromeDriver();

		try {
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		try {
			setWordName(word, hanDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setStrokeNumber(word, hanDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setStrokeOrderSvg(word);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setPronunciation(word);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setCharacterEncoding(word, hanDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setCharacterDecomposition(word);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			setBasicExplain(word, hanDocument);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			saveWord(word);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 设置字名
	 * 如果字名为空，说明此字以图片的形式存在
	 * @param word
	 * @param document
	 */
	private static void setWordName(WordDO word, Document document) throws Exception {
		String nameImage = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]/div/span/img").evaluate(document)
				.get();
		if (StringUtils.isNullOrEmpty(nameImage)) {
			String name = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]/div/span[1]/text()").evaluate(document)
					.get();
			word.setWordName(name);
		} else {
			String nameImageUrl = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]/div/span/img/@src")
					.evaluate(document).get();
			File file = new File(DOWNLOAD_IMG_SRC);
			if (!file.exists()) {
				file.mkdirs();
			}
			HttpClientUtils.downloadImage(nameImageUrl, DOWNLOAD_IMG_SRC + word.getIdUTF8() + ".png");
			word.setWordName("");
		}
	}

	/**
	 * 设置笔画数
	 * @param word
	 * @param hanDocument
	 */
	private static boolean setStrokeNumber(WordDO word, Document hanDocument) throws Exception {
		StrokeNumberDO strokeNumber = new StrokeNumberDO();
		String infoHtml = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]").evaluate(hanDocument).get();
		Document document = Jsoup.parse(infoHtml);
		String info = document.text();
		/**
		 * 解 最常用字 常用字 现通表 标准字体 异体字: 觧 懈 拼音 jiě jiè xiè 注音jiě jiè xiè 注音ㄐ一ㄝˇ ㄐ一ㄝˋ ㄒ一ㄝˋ 部首角部部外笔画6画总笔画31画13画 五笔86QBVHQEVH五笔98QEVGQBVG仓颉JASHQNBSHQ郑码RLYIRLYM 四角3536327252结构左右电码60435031区位29663555 统一码65B189E3笔顺ノフノフ一一一フノノ一一一ノフノフ一一丨フノノ一一丨
		 */
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
	private static boolean setStrokeOrderSvg(WordDO word) throws Exception {
		driver.get(TIANZIGE_URL);
		WebElement wordsElement = driver.findElement(By.name("words"));
		if (!String.valueOf(word.getWordName()).equals(String.valueOf('0'))) {
			wordsElement.sendKeys(String.valueOf(word.getWordName()));
		} else {
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
				String d = Xsoup.compile("/html/body/div[1]/ul/li[1]/svg/path[" + i + "]/@d").evaluate(document).get();
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
		return true;
	}

	/**
	 * 设置字的发音
	 * @param word
	 */
	private static boolean setPronunciation(WordDO word) throws Exception {
		if (!StringUtils.isNullOrEmpty(word.getWordName())) {
			driver.get(YUEYV_URL);
			WebElement inputEle = driver.findElement(By.xpath("//*[@id=\"kw\"]"));
			inputEle.sendKeys("行");
			WebElement submitEle = driver.findElement(By.xpath("//*[@id=\"su\"]"));
			submitEle.click();

			WebElement mandarianMarkEle = driver.findElement(By.xpath(
					"/html/body/div[1]/div[2]/div/div/div/div[3]/div[2]/div/div/div/div[1]/div[1]/div/h2/table[1]/tbody/tr/td[4]"));
			// 普通话：háng hàng héng
			String mandarinEleMarkText = mandarianMarkEle.getText();
			List<WebElement> mandarianNumberEle = driver.findElements(By.xpath(
					"/html/body/div[1]/div[2]/div/div/div/div[3]/div[2]/div/div/div/div[1]/div[1]/div/h2/table[1]/tbody/tr/td[4]/a"));
			String[] mandarianMarkArr = mandarinEleMarkText.replaceAll("普通话：", "").split(" ");
			LinkedList<PronunciationDO> pronunciations = new LinkedList<>();
			for (int i = 0; i < mandarianMarkArr.length; i++) {
				pronunciations.add(new PronunciationDO(mandarianMarkArr[i],
						mandarianNumberEle.get(i).getAttribute("data-rel").replaceAll("mandarin", "")));
			}

			List<WebElement> trEles = driver.findElements(By.xpath(
					"/html/body/div[1]/div[2]/div/div/div/div[3]/div[2]/div/div/div/div[1]/div[1]/div/h2/table[2]/tbody/tr"));
			trEles.remove(0);
			for (int i = 0; i < pronunciations.size(); i++) {
				WebElement we = trEles.get(i);
				String yueYu = we.findElements(By.tagName("td")).get(0).findElement(By.tagName("span"))
						.findElement(By.tagName("font")).getText();
				pronunciations.get(i).setJyutpingCantonese(yueYu);
			}
			word.setPronunciation(pronunciations);
		}
		return true;
	}

	/**
	 * 设置字的编码集
	 * TODO 没有设置康熙字典字段的值
	 * @param word
	 */
	private static void setCharacterEncoding(WordDO word, Document hanDoc) throws Exception {
		String infoHtml = Xsoup.compile("/html/body/div[2]/div[1]/div[2]/div[3]").evaluate(hanDoc).get();
		Document document = Jsoup.parse(infoHtml);

		String showClass = null;
		CharacterEncodingDO ce = new CharacterEncodingDO();

		Elements siJiaoEle = document.getElementsMatchingOwnText("四角");
		if (!siJiaoEle.isEmpty() && !siJiaoEle.parents().isEmpty()) {
			Element siJiaoSpanEle = siJiaoEle.parents().get(0).nextElementSibling();
			Element siJiaoAEle = siJiaoSpanEle.child(0);
			String siJiaoHref = siJiaoAEle.attr("href").toString();
			if (siJiaoHref.endsWith(".html")) {
				showClass = siJiaoSpanEle.attr("class").split(" ")[1];
				ce.setSiJiao(siJiaoAEle.text());
			} else {
				Element siJiaoSpan2Ele = siJiaoSpanEle.nextElementSibling();
				showClass = siJiaoSpan2Ele.attr("class").split(" ")[1];
				ce.setSiJiao(siJiaoSpan2Ele.child(0).text());
			}
		} else {
			ce.setSiJiao("");
		}

		Elements cangJieEle = document.getElementsMatchingOwnText("仓颉");
		if (!cangJieEle.isEmpty() && !cangJieEle.parents().isEmpty()) {
			Element cangJieSpanEle = cangJieEle.parents().get(0).nextElementSibling();
			Element cangJieAEle = cangJieSpanEle.child(0);
			String cangJieHref = cangJieAEle.attr("href").toString();
			if (cangJieHref.endsWith(".html")) {
				if (StringUtils.isNullOrEmpty(showClass)) {
					showClass = cangJieSpanEle.attr("class").split(" ")[1];
				}
				ce.setCangJie(cangJieAEle.text());
			} else {
				if (StringUtils.isNullOrEmpty(showClass)) {
					Element cangJieSpan2Ele = cangJieSpanEle.nextElementSibling();
					showClass = cangJieSpan2Ele.attr("class").split(" ")[1];
				}
				ce.setCangJie(cangJieSpanEle.nextElementSibling().child(0).text());
			}
		} else {
			ce.setCangJie("");
		}

		Elements zhengMaEle = document.getElementsMatchingOwnText("郑码");
		if (!zhengMaEle.isEmpty() && !zhengMaEle.parents().isEmpty()) {
			Element zhengMaSpanEle = zhengMaEle.parents().get(0).nextElementSibling();
			Element zhengMaAEle = zhengMaSpanEle.child(0);
			String zhengMaHref = zhengMaAEle.attr("href").toString();
			if (zhengMaHref.endsWith(".html")) {
				if (StringUtils.isNullOrEmpty(showClass)) {
					showClass = zhengMaSpanEle.attr("class").split(" ")[1];
				}
				ce.setZhengMa(zhengMaAEle.text());
			} else {
				if (StringUtils.isNullOrEmpty(showClass)) {
					Element zhengMa2Ele = zhengMaSpanEle.nextElementSibling();
					showClass = zhengMa2Ele.attr("class").split(" ")[1];
				}
				ce.setZhengMa(zhengMaSpanEle.nextElementSibling().child(0).text());
			}
		} else {
			ce.setZhengMa("");
		}

		Elements quWeiEle = document.getElementsMatchingOwnText("区位");
		if (!quWeiEle.isEmpty() && !quWeiEle.parents().isEmpty()) {
			Element quWeiSpanEle = quWeiEle.parents().get(0).nextElementSibling();
			Element quWeiAEle = quWeiSpanEle.child(0);
			String quWeiHref = quWeiAEle.attr("href").toString();
			if (quWeiHref.endsWith(".html")) {
				if (StringUtils.isNullOrEmpty(showClass)) {
					showClass = quWeiSpanEle.attr("class").split(" ")[1];
				}
				ce.setQuWei(quWeiAEle.text());
			} else {
				if (StringUtils.isNullOrEmpty(showClass)) {
					Element quWei2Ele = quWeiSpanEle.nextElementSibling();
					showClass = quWei2Ele.attr("class").split(" ")[1];
				}
				ce.setQuWei(quWeiSpanEle.nextElementSibling().child(0).text());
			}
		} else {
			ce.setQuWei("");
		}

		Elements wuBiEle = document.getElementsMatchingOwnText("五笔98");
		if (wuBiEle.isEmpty()) {
			wuBiEle = document.getElementsMatchingOwnText("五笔");
		}
		if (!wuBiEle.isEmpty() && !wuBiEle.parents().isEmpty()) {
			Element wuBiSpanEle = wuBiEle.parents().first().nextElementSibling();
			String wuBiClass = wuBiSpanEle.attr("class").toString();
			if (!StringUtils.isNullOrEmpty(showClass) && wuBiClass.contains(showClass)) {
				ce.setWuBi(wuBiSpanEle.text());
			} else {
				ce.setWuBi(wuBiSpanEle.nextElementSibling().text());
			}
		} else {
			ce.setWuBi("");
		}

		Elements unicodeEle = document.getElementsMatchingOwnText("统一码");
		if (!unicodeEle.isEmpty()) {
			Element unicodeSpanEle = unicodeEle.first().nextElementSibling();
			String unicodeClass = unicodeSpanEle.attr("class").toString();
			if (!StringUtils.isNullOrEmpty(showClass) && unicodeClass.contains(showClass)) {
				ce.setUnicode(unicodeSpanEle.text());
			} else {
				ce.setUnicode(unicodeSpanEle.nextElementSibling().text());
			}
		} else {
			ce.setUnicode("");
		}

		Elements dianMaEle = document.getElementsMatchingOwnText("电码");
		if (!dianMaEle.isEmpty()) {
			Element dianMaSpanEle = dianMaEle.parents().first().nextElementSibling();
			String dianMaClass = dianMaSpanEle.attr("class").toString();
			if (!StringUtils.isNullOrEmpty(showClass) && dianMaClass.contains(showClass)) {
				ce.setDianMa(dianMaSpanEle.text());
			} else {
				ce.setDianMa(dianMaSpanEle.nextElementSibling().text());
			}
		} else {
			ce.setDianMa("");
		}

		Elements biShunEle = document.getElementsMatchingOwnText("笔顺");
		if (!biShunEle.isEmpty()) {
			Element biShunSpanEle = biShunEle.first().nextElementSibling();
			String biShunClass = biShunSpanEle.attr("class").toString();
			if (!StringUtils.isNullOrEmpty(showClass) && biShunClass.contains(showClass)) {
				ce.setPenOrderNo(convertBiShun(biShunSpanEle.text()));
			} else {
				ce.setPenOrderNo(convertBiShun(biShunSpanEle.nextElementSibling().text()));
			}
		} else {
			ce.setPenOrderNo("");
		}
		word.setCharacterEncoding(ce);
	}

	/**
	 * 设置字的结构
	 * @param word
	 */
	private static void setCharacterDecomposition(WordDO word) throws Exception {
		CharacterDecompositionDO cd = new CharacterDecompositionDO(word.getWordName());
		String decomUrl = "https://www.mdbg.net/chinese/dictionary-ajax?c=cdcd&i=";
		String html = HttpClientUtils.doGet(decomUrl + cd.getWord());
		Document doc = Jsoup.parse(html);

		String imgXpath = "/html/body/table/tbody/tr[2]/td/table/tbody/tr[3]/td[2]/div/img/@src";
		String imgXpathChild = "/html/body/table/tbody/tr[3]/td[2]/div/img/@src";
		String upXpath = "/html/body/table/tbody/tr[2]/td/table/tbody/tr[1]/td[2]/span/allText()";
		String upXpathChild = "/html/body/table/tbody/tr[1]/td[2]/span/allText()";
		String downXpath = "/html/body/table/tbody/tr[2]/td/table/tbody/tr[4]/td/span/allText()";
		String downXpathChild = "/html/body/table/tbody/tr[4]/td/span/allText()";
		String childWordNameXpath = "/html/body/table/tbody/tr[3]/td[1]/span/a/allText()";
		String upHtmlXpath = "/html/body/table/tbody/tr[2]/td/table/tbody/tr[1]/td[2]/table/outerHtml()";
		String downHtmlXpath = "/html/body/table/tbody/tr[2]/td/table/tbody/tr[4]/td/table/outerHtml()";

		String imgSrc = Xsoup.compile(imgXpath).evaluate(doc).get();
		String composition = analysisCompostion(imgSrc);
		cd.setComposition(composition);

		LinkedList<CharacterDecompositionDO> components = new LinkedList<>();
		String upSpan = Xsoup.compile(upXpath).evaluate(doc).get();
		if (StringUtils.isNullOrEmpty(upSpan)) { // 复合结构
			String upStr = Xsoup.compile(upHtmlXpath).evaluate(doc).get();
			Document upDoc = Jsoup.parse(upStr);

			String wordName = Xsoup.compile(childWordNameXpath).evaluate(upDoc).get();
			CharacterDecompositionDO cd2 = new CharacterDecompositionDO(wordName);
			String imgSrc2 = Xsoup.compile(imgXpathChild).evaluate(upDoc).get();
			String composition2 = analysisCompostion(imgSrc2);
			cd2.setComposition(composition2);

			LinkedList<CharacterDecompositionDO> components2 = new LinkedList<>();
			String upSpan2 = Xsoup.compile(upXpathChild).evaluate(upDoc).get();
			String downSpan2 = Xsoup.compile(downXpathChild).evaluate(upDoc).get();
			components2.add(new CharacterDecompositionDO(upSpan2, null, null));
			components2.add(new CharacterDecompositionDO(downSpan2, null, null));
			cd2.setComponents(components2);
			components.add(cd2);
		} else { // 简单结构
			components.add(new CharacterDecompositionDO(upSpan, null, null));
		}

		String downSpan = Xsoup.compile(downXpath).evaluate(doc).get();
		if (StringUtils.isNullOrEmpty(downSpan)) { // 复合结构
			String downStr = Xsoup.compile(downHtmlXpath).evaluate(doc).get();
			Document downDoc = Jsoup.parse(downStr);

			String wordName = Xsoup.compile(childWordNameXpath).evaluate(downDoc).get();
			CharacterDecompositionDO cd2 = new CharacterDecompositionDO(wordName);
			String imgSrc2 = Xsoup.compile(imgXpathChild).evaluate(downDoc).get();
			String composition2 = analysisCompostion(imgSrc2);
			cd2.setComposition(composition2);

			LinkedList<CharacterDecompositionDO> components2 = new LinkedList<>();
			String upSpan2 = Xsoup.compile(upXpathChild).evaluate(downDoc).get();
			String downSpan2 = Xsoup.compile(downXpathChild).evaluate(downDoc).get();
			components2.add(new CharacterDecompositionDO(upSpan2, null, null));
			components2.add(new CharacterDecompositionDO(downSpan2, null, null));
			cd2.setComponents(components2);
			components.add(cd2);
		} else { // 简单结构
			components.add(new CharacterDecompositionDO(downSpan, null, null));
		}
		cd.setComponents(components);
		word.setCharacterDecomposition(cd);
	}

	/**
	 * 设置字的基本解释
	 * @param word
	 * @param hanDocument
	 */
	private static void setBasicExplain(WordDO word, Document hanDocument) throws Exception {
		BasicExplainDO be = new BasicExplainDO();
		Element basicExpEle = hanDocument.getElementsMatchingOwnText("基本解释").first();
		String basicStr = basicExpEle.nextElementSibling().text().replaceAll("[0-9]、911查询·新华字典", "")
				.replaceAll("[0-9]、www.911cha.com", "");
		be.setBasic(basicStr);

		Element chineseEnglishEle = hanDocument.getElementsMatchingOwnText("汉英互译").first();
		String chineseEnglishStr = chineseEnglishEle.nextElementSibling().text();
		List<String> chineseEnglishWords = Arrays.asList(chineseEnglishStr.split("、"));
		be.setChineseEnglishTranslate(chineseEnglishWords);

		Elements replatedWordsEles = hanDocument.getElementsMatchingOwnText("相关字词");
		if (!replatedWordsEles.isEmpty()) {
			String replatedWordsStr = replatedWordsEles.first().nextElementSibling().text();
			List<String> replatedWords = Arrays.asList(replatedWordsStr.split("、"));
			be.setRelatedWords(replatedWords);
		}

		Element createWordEle = hanDocument.getElementsMatchingOwnText("造字法").first();
		String createWordStr = createWordEle.nextElementSibling().text();
		be.setFormationOfCharacter(createWordStr);

		Element englishEle = createWordEle.nextElementSibling().nextElementSibling().nextElementSibling();
		String engStr = englishEle.text();
		be.setEnglish(engStr);
		word.setBasicExplain(be);
	}

	/**
	 * 将汉字存入数据库
	 * @param word
	 */
	private static void saveWord(WordDO word) throws Exception {
		String strokeNumber = JSONObject.toJSONString(word.getStrokeNumber());
		String strokeOrderSvg = JSONObject.toJSONString(word.getStrokeOrderSvg());
		String pronunciation = JSONObject.toJSONString(word.getPronunciation());
		String chracterEncoding = JSONObject.toJSONString(word.getCharacterEncoding());
		String chracterDecomposition = JSONObject.toJSONString(word.getCharacterDecomposition());
		String basicExplain = JSONObject.toJSONString(word.getBasicExplain());
		System.out.println(basicExplain);
	}

	/**
	 * 
	 * 
	 * 将"フノフ一一丨フノノ一一" 转换成 "53511253311"
	 * 笔顺 横：1，竖：2，撇：3，捺：4，折：5
	 * 一: 1
	 * 丨: 2
	 * ノ: 3
	 * 丶: 4
	 * 其他：5
	 * 
	 * "解"：フノフ一一丨フノノ一一 (53511253311)
	 * @param bs
	 * @return
	 */
	private static String convertBiShun(String bs) throws Exception {
		char[] biShuns = bs.toCharArray();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < biShuns.length; i++) {
			switch (biShuns[i]) {
			case '一':
				sb.append("1");
				break;
			case '丨':
				sb.append("2");
				break;
			case 'ノ':
				sb.append("3");
				break;
			case '丶':
				sb.append("4");
				break;
			default:
				sb.append("5");
			}
		}
		return sb.toString();
	}

	/**
	 * 根据名称中含的单词来确定字的结构
	 * 例如，horizontal_12.gif，含horizontal单词，也就是左右结构
	 *
	 * ⿲（左中右）⿳（上中下）⿹（半包围）
	 * 	horizontal: 	⿰（左右）
	 *  vertical: 		⿱（上下）
	 *  inclusion: 		⿴（全包围）
	 *  overlay: 		⿻（镶嵌）
	 * @param composition
	 * @return
	 */
	private static String analysisCompostion(String composition) throws Exception {
		if (composition.contains("horizontal")) {
			return "左右";
		} else if (composition.contains("vertical")) {
			return "上下";
		} else if (composition.contains("inclusion")) {
			return "全包围";
		} else if (composition.contains("overlay")) {
			return "镶嵌";
		}
		return null;
	}
}
