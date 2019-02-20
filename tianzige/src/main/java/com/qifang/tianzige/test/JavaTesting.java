package com.qifang.tianzige.test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.mysql.jdbc.StringUtils;
import com.qifang.tianzige.domain.BasicExplainDO;
import com.qifang.tianzige.domain.CharacterDecompositionDO;
import com.qifang.tianzige.domain.CharacterEncodingDO;
import com.qifang.tianzige.domain.PronunciationDO;
import com.qifang.tianzige.util.HttpClientUtils;

import us.codecraft.xsoup.XPathEvaluator;
import us.codecraft.xsoup.Xsoup;

public class JavaTesting {
	private final static String	CHROME_DRIVER_PATH	= "D:\\Devopment\\chromedriver\\chromedriver.exe";
	private final static String	YUEYV_URL			= "http://www.yueyv.cn/";

	public static void main(String[] args) throws Exception {
		testDriver();
	}

	public static void testDriver() {
		//		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
		//		WebDriver driver = new ChromeDriver();
		//		driver.get("https://www.mdbg.net/chinese/dictionary?page=worddict&wdrst=0&wdqb=" + "浙");
		//		//*[@id="contentarea"]/table/tbody/tr/td/table/tbody/tr[1]/td[1]/div[1]/a/span

		String hanHtml = HttpClientUtils.doGet("https://zidian.911cha.com/zi6c5f.html");
		Document hanDocument = Jsoup.parse(hanHtml);

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

		System.out.println(be);
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
	private static String analysisCompostion(String composition) {
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
