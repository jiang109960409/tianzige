package com.qifang.tianzige.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.qifang.tianzige.scraping.ScrapingHanController;

public class JavaTesting {

	public static void main(String[] args) throws Exception {
		HttpURLConnection connection = null;
		InputStream is = null;
		BufferedReader br = null;
		String result = null;// 返回结果字符串
		try {
			// 创建远程url连接对象
			URL url = new URL("https://www.mdbg.net/chinese/dictionary-ajax?c=cdqchi&i=%E5%85%A5");
			// 通过远程url连接对象打开一个连接，强转成httpURLConnection类
			connection = (HttpURLConnection) url.openConnection();
			// 设置连接方式：get
			connection.setRequestMethod("GET");
			// 设置连接主机服务器的超时时间：15000毫秒
			connection.setConnectTimeout(15000);
			// 设置读取远程返回的数据时间：60000毫秒
			connection.setReadTimeout(60000);
			connection.setRequestProperty("Host", "www.mdbg.net");
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Accept", "text/html, */*; q=0.01");
			connection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
			connection.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.110 Safari/537.36");
			connection.setRequestProperty("Referer",
					"https://www.mdbg.net/chinese/dictionary?page=worddict&wdrst=0&wdqb=%E5%85%A5&dmtm=1");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
			connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
			connection.setRequestProperty("Cookie",
					"chardictsettings=p0p0pp0p0p0p0p0p1pp0p0; worddictsettings=ppp0p0p0ppppp0pppp1pppp1pp0p0; flashcardsettings=p0p1; charquizsettings=p0p0p15p1; translatesettings=p0p0; toolssettings=p0p0p0p0p2; chindictsession=1; chindictsettings=p0p0p0p1p0p1p1p0p1p1p1p0p1p0; chindictlastvisit=1546046664; _ga=GA1.2.1966346087.1544667103; cookieconsent_status=dismiss; PHPSESSID=kuitaufiuha31ib7kps7nhf7l3; _gid=GA1.2.838276464.1545980213; _gat=1\r\n"
							+ "			If-Modified-Since: Sat, 29 Dec 2018 01:24:58 GMT");
			// 发送请求
			connection.connect();
			// 通过connection连接，获取输入流
			if (connection.getResponseCode() == 200) {
				is = connection.getInputStream();
				// 封装输入流is，并指定字符集
				br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				// 存放数据
				StringBuffer sbf = new StringBuffer();
				String temp = null;
				while ((temp = br.readLine()) != null) {
					sbf.append(temp);
					sbf.append("\r\n");
				}
				result = sbf.toString();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			connection.disconnect();// 关闭远程连接
		}
		System.out.println(result);
	}

}
