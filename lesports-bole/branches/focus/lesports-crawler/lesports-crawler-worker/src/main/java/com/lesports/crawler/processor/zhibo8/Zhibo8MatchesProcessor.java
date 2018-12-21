package com.lesports.crawler.processor.zhibo8;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.SourceMatch;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.Selectable;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/17
 */
@TargetUrl("http://www.zhibo8.cc/?$")
@Component
public class Zhibo8MatchesProcessor extends AbstractZhibo8Processor<SourceMatch[]> {
    private Site site = Site.me();

    @Override
    protected SourceMatch[] doProcess(Page page) {
        Selectable selectable = page.getHtml().xpath("//div[@class='schedule_container']/div[@class='box']");
        List<Selectable> boxes = selectable.nodes();
        if (CollectionUtils.isEmpty(boxes)) {
            return null;
        }
        List<SourceMatch> sms = Lists.newArrayList();
        for (Selectable box : boxes) {
            String title = box.xpath("//div[@class=titlebar]/h2/text()").get();
            if (StringUtils.isEmpty(title)) {
                continue;
            }
            List<Selectable> ul = box.xpath("//div[@class=content]/ul/li").nodes();
            if (CollectionUtils.isEmpty(ul)) {
                continue;
            }
            for (Selectable li : ul) {
                String timeAndMatch = li.xpath("/li/allText()").get();
                List<Selectable> hrefs = li.xpath("/li/a").nodes();
                if (CollectionUtils.isEmpty(hrefs)) {
                    continue;
                }
                SourceMatch match = new SourceMatch();
                for (Selectable hh : hrefs) {
                    // 只取第一个链接,后续链接无效或重复
                    String href = hh.xpath("/a/@href").get();
                    match.setSourceUrl(href);
                    break;
                }

                match.setSource(getSource());
                match.setSourceId(getId(match.getSourceUrl()));
                match.setCompetition(getCompetition(timeAndMatch));
                match.setStartTime(getStartTime(title, timeAndMatch));
                match.setCompetitors(getCompetitors(timeAndMatch));
                if (match.getCompetitors().isEmpty()) {
                    match.setVs(Boolean.FALSE);
                }

                if (match.getCompetitors() != null) {
                    if (Boolean.TRUE.equals(match.getVs())) {
                        String name = String.format("%s %s VS %s", match.getCompetition(), match.getCompetitors().get(0), match.getCompetitors().get(1));
                        match.setName(name);
                    } else {
                        match.setName(match.getCompetition());
                    }
                }
                // 按url区分足球/篮球
                if (match.getSourceUrl().contains("zhibo8.cc/zhibo/nba")) {
                    match.setGameFName("篮球");
                } else if (match.getSourceUrl().contains("zhibo8.cc/zhibo/zuqiu")) {
                    match.setGameFName("足球");
                }
                if (check(match)) {
                    sms.add(match);
                    page.addTargetRequest(match.getSourceUrl());
                }
            }
        }

        return sms.toArray(new SourceMatch[0]);
    }

    private boolean check(SourceMatch match) {
        return !(Strings.isNullOrEmpty(match.getCompetition())
                || "福利竞猜".equals(match.getCompetition())
                || Strings.isNullOrEmpty(match.getStartTime()));
    }

    @Override
    public Site getSite() {
        return site;
    }

    private List<String> getCompetitors(String timeAndMatch) {
        if (StringUtils.isEmpty(timeAndMatch) || !StringUtils.contains(timeAndMatch, "-")) {
            return Collections.emptyList();
        }
        String[] splits = StringUtils.split(timeAndMatch, " ");
        if (null == splits || splits.length < 5) {
            return Collections.emptyList();
        }
        List<String> competitors = new ArrayList<>();
        competitors.add(splits[2]);
        competitors.add(splits[4]);
        return competitors;
    }

    private String getCompetition(String timeAndMatch) {
        if (StringUtils.isEmpty(timeAndMatch)) {
            return null;
        }
        String[] splits = StringUtils.split(timeAndMatch, " ");
        if (null == splits || splits.length < 5) {
            return null;
        }
        return splits[1];
    }

    private String getStartTime(String title, String timeAndMatch) {
        try {
            if (StringUtils.isEmpty(title) || StringUtils.isEmpty(timeAndMatch)) {
                return null;
            }
            String[] splits = StringUtils.split(timeAndMatch, " ");
            if (null == splits || splits.length < 2) {
                return null;
            }
            DateTime dateTime = new DateTime();
            String year = "" + dateTime.getYear();
            String[] strings = StringUtils.split(title, " ");
            String date = year + strings[0].replace("日", "").replace("月", "");
            String time = splits[0].replace(":", "") + "00";
            return date + time;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    protected Content getContent() {
        return Content.MATCH;
    }

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException {
        Spider.create(new Zhibo8MatchesProcessor())
                .addUrl("http://www.zhibo8.cc/")
                .run();
    }
}
