package com.spring.estest.http;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class HtmlParse {
    public static void main(String[] args) throws IOException {
        String url = "https://search.jd.com/Search?keyword=csdn&enc=utf-8";
        Document parse = Jsoup.parse(new URL(url), 10000);
        Element element = parse.getElementById("J_goodsList");
        Elements lis = element.getElementsByTag("li");
        for (Element li : lis) {
            String src = li.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = li.getElementsByClass("p-price").eq(0).text();
            String context = li.getElementsByClass("p-name").eq(0).text();
            System.out.println("<------------------------------------------------->");
            System.out.println(src);
            System.out.println(price);
            System.out.println(context);
        }
    }
}
