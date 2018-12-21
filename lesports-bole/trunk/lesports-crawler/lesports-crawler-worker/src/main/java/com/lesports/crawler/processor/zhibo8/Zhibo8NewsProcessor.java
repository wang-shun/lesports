package com.lesports.crawler.processor.zhibo8;

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
import com.lesports.crawler.utils.CrawlerUtils;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

@TargetUrl("http://news.zhibo8.cc/*.htm$")
@Component
public class Zhibo8NewsProcessor extends AbstractZhibo8Processor<SourceNews[]> {
    private static final Logger logger = LoggerFactory.getLogger(Zhibo8NewsProcessor.class);
    private Site site = Site.me();

    @Override
    public SourceNews[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        SourceNews sn = new SourceNews();
        Elements mainE = page.getHtml().getDocument().select("#main");
        // 标题,发布时间,来源,评论数,引用
        Elements titleE = mainE.select(".title");
        String title = titleE.select("h1").text();
        if (Strings.isNullOrEmpty(title)) {
            logger.warn("parse title empty on page {}", pageUrl);
            return null;
        }
        sn.setTitle(title);
        Elements pubE = titleE.select("span");
        if (pubE.size() <= 0 || pubE.get(0).childNodeSize() <= 1) {
            logger.warn("parse publish time empty on page {}", pageUrl);
            return null;
        }
        String publishAt = pubE.get(0).childNode(0).toString();
        if (Strings.isNullOrEmpty(publishAt)) {
            logger.warn("parse publish time empty on page {}", pageUrl);
            return null;
        }
        // 2015-11-22 13:26:58
        sn.setPublishAt(LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.parseYMDHMS(publishAt.trim())));
        Elements refE = titleE.select("span a:nth-child(1)");
        String refUrl = refE.attr("href");;
        String refName = refE.text();
        if (!Strings.isNullOrEmpty(refUrl) && !Strings.isNullOrEmpty(refName)) {
            sn.setReferenceName(refName);
            sn.setReferenceUrl(refUrl);
        }
        sn.setSource(Source.ZHIBO8);

        // 图片
        Elements imagesE = mainE.select(".content img");
        short imageOrder = 1;
        for (Element imgE : imagesE) {
            SourceNewsImage img = new SourceNewsImage();
            String src = imgE.attr("src");
            if (Strings.isNullOrEmpty(src)) {
                logger.warn("parse image src empty on page {}", pageUrl);
                return null;
            }
            img.setImageUrl(src);
            if (title.startsWith("今日趣图")) {
                Element nes = imgE.parent().nextElementSibling();
                if (nes != null && "text-indent:2em;".equals(nes.attr("style"))) {
                    String name = nes.text();
                    if (!Strings.isNullOrEmpty(name)) {
                        img.setImageName(name);
                    }
                }
            } else {
                String alt = imgE.attr("alt");
                if (!Strings.isNullOrEmpty(alt)) {
                    img.setImageName(alt);
                }
            }
            img.setOrder(imageOrder++);
            sn.addImage(img);
        }

        // 正文
        Elements paragraphs = mainE.select(".content p[style^=text-indent:2em]");
        if (requiredEmpty(".content p[style^=text-indent:2em]", paragraphs.text(), pageUrl))
            return null;

        short paragraphOrder = 1;
        for (Element paragraph : paragraphs) {
            String content = paragraph.text().trim();
            if (!Strings.isNullOrEmpty(content)) {
                sn.addParagraph(new SourceNewsParagraph(paragraphOrder++, content));
            }
        }
        sn.setSourceId(CrawlerUtils.getIdFromUrl(pageUrl));
        sn.setSourceUrl(pageUrl);
        return new SourceNews[] { sn };
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new Zhibo8NewsProcessor())
                .addUrl("http://news.zhibo8.cc/nba/2016-01-09/5690c89be4c9f.htm")
                .addUrl("http://news.zhibo8.cc/nba/2015-11-23/5652db4186d85.htm")
                .addUrl("http://news.zhibo8.cc/zuqiu/2015-11-28/565969f734835.htm").addUrl("http://news.zhibo8.cc/nba/2015-11-28/56595ea23af07.htm").run();
    }

    @Override
    protected Content getContent() {
        return Content.NEWS;
    }

}
