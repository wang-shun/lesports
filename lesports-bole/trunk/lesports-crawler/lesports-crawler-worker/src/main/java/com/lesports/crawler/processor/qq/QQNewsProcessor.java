package com.lesports.crawler.processor.qq;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

@TargetUrl("http://sports.qq.com/*.htm(#p=[0-9]*)?$")
@Component
public class QQNewsProcessor extends AbstractPageProcessor<SourceNews[]> {
    private static final Logger logger = LoggerFactory.getLogger(QQNewsProcessor.class);

    @Override
    protected SourceNews[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        SourceNews news = new SourceNews();
        Document doc = page.getHtml().getDocument();
        if (doc.select(".galleryPic").size() > 0) {
            // TODO: 图集新闻
            return null;
        }

        /**
         * http://sports.qq.com/a/20151202/018554.htm
         * http://sports.qq.com/a/20151125/008729.htm 文章ID不同
         */
        Elements articleE = doc.select("#Main-Article-QQ,#Article");

        String title = articleE.select("h1").text();
        if (requiredEmpty("h1", title, pageUrl))
            return null;
        news.setTitle(title);

        Elements refE = articleE.select("span[bosszone=jgname] a");
        String refName = null, refUrl = null;
        if (refE.size() > 0) {
            refName = refE.text();
            refUrl = refE.attr("href");
        } else {
            refName = articleE.select("span[bosszone=jgname]").text();
        }
        news.setReferenceName(refName);
        news.setReferenceUrl(refUrl);

        String publishAt = articleE.select("span[class$=time]").text().trim();
        if (requiredEmpty("span[class$=time]", publishAt, pageUrl))
            return null;
        publishAt += ":00";
        news.setPublishAt(LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.parseYMDHMS(publishAt)));

        Elements contentE = null;
        if (articleE.select(".rv-root").size() > 0) {
            // NBA
            contentE = articleE.select(".rv-root~p[style=TEXT-INDENT: 2em]");
        } else {
            // Other
            contentE = articleE.select("#invideocon~p[style=TEXT-INDENT: 2em]");
            if (contentE.size() == 0) {
                contentE = articleE.select("p[style=TEXT-INDENT: 2em]");
            }
        }

        if (requiredEmpty("content", contentE.text(), pageUrl))
            return null;

        short paragraphOrder = 1;
        for (Element paragraph : contentE) {
            String content = paragraph.text().trim();
            if (!Strings.isNullOrEmpty(content)) {
                news.addParagraph(new SourceNewsParagraph(paragraphOrder++, content));
            }
        }

        for (Element ele : articleE.select("#videokg span a,#tagsWord_1 span a")) {
            news.addTag(ele.text());
        }

        short imageOrder = 1;
        for (Element ele : articleE.select("p[align=center]")) {
            for (Element imgE : ele.select("img")) {
                String src = imgE.attr("src");
                if (requiredEmpty("p[align=center] img", src, pageUrl))
                    return null;

                if (!src.endsWith("erweima.png")) {
                    // 过滤二维码 腾讯体育APP
                    SourceNewsImage img = new SourceNewsImage();
                    img.setImageUrl(src);
                    String imgName = null;
                    Element nes = ele.nextElementSibling();
                    if (nes != null && "center".equals(nes.attr("align"))) {
                        imgName = nes.text();
                    } else {
                        imgName = imgE.attr("alt");
                    }
                    if (!Strings.isNullOrEmpty(imgName)) {
                        if (imgName.contains("腾讯体育APP")) {
                            continue;
                        }
                        img.setImageName(imgName);
                    }
                    img.setOrder(imageOrder++);
                    news.addImage(img);
                }
            }
        }

        news.setSource(getSource());
        news.setSourceId(CrawlerUtils.getIdFromUrl(pageUrl));
        news.setSourceUrl(pageUrl);
        putCmtId(news, page);
        return new SourceNews[] { news };
    }

    private void putCmtId(SourceNews news, Page page) {
        // var cmt_id='1290946841';
        String exp = "cmt_id\\s*=\\s*['\"]?([0-9]+)['\"]?;";
        Pattern pattern = Pattern.compile(exp);
        Matcher matcher = pattern.matcher(page.getRawText());
        if (matcher.find()) {
            news.putExtra("cmt_id", matcher.group(1));
        } else {
            logger.warn("can not parse cmt_id from: " + page.getRequest().getUrl());
        }
        return;
    }

    public static void main(String[] args) {
        Spider.create(new QQNewsProcessor()).addUrl("http://sports.qq.com/a/20151201/022570.htm")
                .addUrl("http://sports.qq.com/a/20151201/013358.htm")
                .addUrl("http://sports.qq.com/a/20151130/035822.htm")
                .addUrl("http://sports.qq.com/a/20151201/015373.htm")
                .addUrl("http://sports.qq.com/a/20151130/003105.htm")
                .addUrl("http://sports.qq.com/a/20151130/025105.htm")
                .addUrl("http://sports.qq.com/a/20151202/018554.htm").run();
    }

    @Override
    protected Source getSource() {
        return Source.QQ;
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }
}
