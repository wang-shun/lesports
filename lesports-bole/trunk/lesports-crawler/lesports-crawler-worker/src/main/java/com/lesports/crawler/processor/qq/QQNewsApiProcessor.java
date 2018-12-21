package com.lesports.crawler.processor.qq;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;

/**
 * QQNewsApiProcessor
 * 
 * @author denghui
 *
 */
@TargetUrl("http://roll.sports.qq.com/interface/roll.php\\?*")
@Component
public class QQNewsApiProcessor extends AbstractPageProcessor<SourceNews[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QQNewsApiProcessor.class);
    private long lastTime = 0; // in seconds

    @Override
    protected synchronized SourceNews[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        if ("Access denied".equals(page.getRawText())) {
            LOGGER.error("Access denied for url: {}", pageUrl);
            return null;
        }

        Json json = new Json(page.getRawText());
        String code = json.jsonPath("$.response.code").get();
        if ("2".equals(code)) {
            // no more articles
            return null;
        } else if (!"0".equals(code)) {
            LOGGER.warn("page error: {}", pageUrl);
            return null;
        }

        long maxThisTime = 0;
        String articleInfo = json.jsonPath("$.data.article_info").get();
        Document doc = new Html(articleInfo).getDocument();
        Elements liElements = doc.select("li");
        for (Element element : liElements) {
            String time = element.select(".t-time").text();
            if (requiredEmpty(".t-time", time, pageUrl)) {
                continue;
            }
            int year = Calendar.getInstance().get(Calendar.YEAR);
            time = String.format("%d-%s:00", year, time);
            long thisTime = TimeUnit.MILLISECONDS.toSeconds(LeDateUtils.parseYMDHMS(time).getTime());
            // 保证第一次都抓取
            if (lastTime > thisTime && !isFirstTime(page)) {
                continue;
            }

            String href = element.select("a").attr("href");
            if (requiredEmpty("href", href, pageUrl)) {
                continue;
            }

            if (thisTime > maxThisTime) {
                maxThisTime = thisTime;
            }
            page.addTargetRequest(href);
        }

        if (maxThisTime > lastTime) {
            lastTime = maxThisTime;
        }
        return null;
    }

    private boolean isFirstTime(Page page) {
        Object firstTime = page.getRequest().getExtra("firstTime");
        if (firstTime == null) {
            return false;
        } else if ((boolean)firstTime == true) {
            return true;
        }
        return false;
    }

    @Override
    protected Source getSource() {
        return Source.QQ;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

    @Override
    public Site getSite() {
        // Referer: http://roll.sports.qq.com/
        site.addHeader("Referer", "http://roll.sports.qq.com/");
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new QQNewsApiProcessor()).addUrl("http://roll.sports.qq.com/interface/roll.php?0.7182201971299946&cata=&site=sports&date=&page=4&mode=1&of=json").run();
    }
}
