package com.lesports.crawler.processor.sina;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.model.source.SourceNewsImage;
import com.lesports.crawler.model.source.SourceNewsParagraph;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.crawler.utils.CrawlerUtils;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl({ "http://sports.sina.com.cn/*.shtml$" })
@Component
public class SinaNewsProcessor extends AbstractPageProcessor<SourceNews[]> {
    private static final Logger logger = LoggerFactory.getLogger(SinaNewsProcessor.class);

    @Override
    protected SourceNews[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        SourceNews news = new SourceNews();
        Document doc = page.getHtml().getDocument();
        Elements articleE = doc.select("#J_Article_Wrap");

        String title = articleE.select("#artibodyTitle").text();
        if (requiredEmpty("#artibodyTitle", title, pageUrl))
            return null;
        news.setTitle(title);

        String publishAt = articleE.select("#pub_date").text();
        if (requiredEmpty("#pub_date", publishAt, pageUrl))
            return null;
        news.setPublishAt(parseDate(publishAt));

        Elements refE = articleE.select("#media_name a:nth-child(1)");
        if (!Strings.isNullOrEmpty(refE.text()) && !Strings.isNullOrEmpty(refE.attr("href"))) {
            news.setReferenceName(refE.text());
            news.setReferenceUrl(refE.attr("href"));
        }

        Elements paragraphs = articleE.select("#artibody p");
        if (requiredEmpty("#artibody p", paragraphs.text(), pageUrl))
            return null;

        short paragraphOrder = 1;
        for (Element paragraph : paragraphs) {
            String content = paragraph.text().trim();
            //TODO: check trim
            if (!Strings.isNullOrEmpty(content)) {
                news.addParagraph(new SourceNewsParagraph(paragraphOrder++, content));
            }
        }

        for (Element ele : articleE.select(".art_keywords a")) {
            news.addTag(ele.text());
        }

        short imageOrder = 1;
        for (Element ele : articleE.select(".img_wrapper img")) {
            String imgUrl = ele.attr("src");
            if (requiredEmpty(".img_wrapper img", imgUrl, pageUrl))
                return null;
            SourceNewsImage img = new SourceNewsImage();
            img.setImageUrl(imgUrl);
            String imgName = ele.attr("alt");
            if (!Strings.isNullOrEmpty(imgName)) {
                img.setImageName(imgName);
            }
            img.setOrder(imageOrder++);
            news.addImage(img);
        }

        news.setSource(getSource());
        news.setSourceId(CrawlerUtils.getIdFromUrl(pageUrl));
        news.setSourceUrl(pageUrl);
        return new SourceNews[] { news };
    }

    private String parseDate(String publishAt) {
        final String format = "yyyy年MM月dd日HH:mm";
        try {
            return LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.parseDate(publishAt.trim(), format));
        } catch (Exception e) {
            throw new RuntimeException("format sina news publish date failed", e);
        }
    }

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        Spider.create(new SinaNewsProcessor())
                .addUrl("http://sports.sina.com.cn/basketball/nba/2015-11-29/doc-ifxmazmz8989999.shtml")
                .addUrl("http://sports.sina.com.cn/basketball/nba/2015-12-01/doc-ifxmazmy2307055.shtml")
                .addUrl("http://sports.sina.com.cn/basketball/nba/2015-12-01/doc-ifxmazmy2304896.shtml")
                .run();
    }

    @Override
    protected Source getSource() {
        return Source.SINA;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }
}
