package com.qifang.tianzige.scraping;

import java.util.LinkedList;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import com.qifang.tianzige.domain.PointDO;

import us.codecraft.xsoup.Xsoup;

public class ScrapingHanController {

	private final static String	CHROME_DRIVER_PATH	= "D:\\Devopment\\chromedriver\\chromedriver.exe";
	private final static String	TIANZIGE_URL		= "http://www.an2.net/zi/";

	public static void start() {
		// 同一IP20秒内限制只能请求1次，请见谅！
		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);

		WebDriver driver = new ChromeDriver();
		driver.get(TIANZIGE_URL);
		WebElement wordsElement = driver.findElement(By.name("words"));
		wordsElement.sendKeys("江");
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
	}
}
