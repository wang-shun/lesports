package com.lesports.crawler.processor.zhibo8;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.lesports.bole.model.LiveType;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.SourceLive;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.utils.CrawlerUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/17
 */
@TargetUrl("http://www.zhibo8.cc/zhibo/*/*/*.htm$")
@Component
public class Zhibo8MatchProcessor extends AbstractZhibo8Processor<SourceMatch[]> implements PageProcessor {
    private Site site = Site.me();
    @SuppressWarnings("serial")
    private Set<String> invalidLiveUrl = new HashSet<String>() {{
      add("http://www.zhibo8.cc/shouji.htm");
    }};

    @Override
    protected SourceMatch[] doProcess(Page page) {
        Selectable selectable = page.getHtml().xpath("//div[@class='ft_video']/a");
        List<Selectable> urls = selectable.nodes();
        if (CollectionUtils.isEmpty(urls)) {
            // sv_list
            selectable = page.getHtml().xpath("//div[@id='sv_list']");
            if (selectable == null) {
                return null;
            }
            String div = selectable.get();
            Pattern pattern = Pattern.compile("'C0ha0ne0l(.)'");
            Matcher matcher = pattern.matcher(div);
            StringBuilder video = new StringBuilder();
            while(matcher.find()) {
                video.append(matcher.group(1));
            }
            Html videoHtml = new Html(video.toString());
            urls = videoHtml.xpath("//a").nodes();
            if (CollectionUtils.isEmpty(urls)) {
                return null;
            }
        }
        SourceMatch match = new SourceMatch();
        boolean selfAdded = false;
        for (Selectable url : urls) {
            String title = url.xpath("/a/text()").get();
            if (StringUtils.isEmpty(title)) {
                continue;
            }
            String href = url.xpath("/a/@href").get();
            if (StringUtils.isEmpty(href)) {
                continue;
            }
            // 过滤非http协议(播放器协议)和无效链接
            if (href.startsWith("http://") && !invalidLiveUrl.contains(href)) {
              String site = CrawlerUtils.getSite(href).toUpperCase();
              //其他都是视频吗?
              LiveType type = (site.equals("ZHIBO8")||site.equals("188BIFEN"))?LiveType.IMAGE_TEXT:LiveType.VIDEO;
              match.addLive(new SourceLive(site, type, title, href));
              if (href.contains("zhibo8.cc")) {
                  selfAdded = true;
              }
            }
        }
        if (!selfAdded) {
            // 如直播信号里没有本页，则添加
            match.addLive(new SourceLive("ZHIBO8", LiveType.IMAGE_TEXT, "互动图文直播", page.getRequest().getUrl()));
        }

        match.setSourceUrl(page.getRequest().getUrl());
        String id = getId(page.getRequest().getUrl());
        match.setSourceId(id);
        match.setSource(getSource());
        // 按url区分足球/篮球
        if (page.getRequest().getUrl().contains("zhibo8.cc/zhibo/nba")) {
            match.setGameFName("篮球");
        } else if (page.getRequest().getUrl().contains("zhibo8.cc/zhibo/zuqiu")) {
            match.setGameFName("足球");
        }
        return new SourceMatch[]{match};
    }

    @Override
    public Site getSite() {
        return site;
    }
    
    @Override
    protected Content getContent() {
        return Content.MATCH;
    }
    
    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
      Spider.create(new Zhibo8MatchProcessor())
          //.addUrl("http://www.zhibo8.cc/zhibo/nba/2016/0205huosaivsnikesi.htm")
          //.addUrl("http://www.zhibo8.cc/zhibo/zuqiu/2016/012264991.htm")
          //.addUrl("http://www.zhibo8.cc/zhibo/other/2016/012165158.htm")
          .addUrl("http://www.zhibo8.cc/zhibo/zuqiu/2016/0124leienvsayakexiao.htm")
          .run();
    }

}
