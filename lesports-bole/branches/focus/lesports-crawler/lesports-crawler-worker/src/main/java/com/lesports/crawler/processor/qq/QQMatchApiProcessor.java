package com.lesports.crawler.processor.qq;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lesports.bole.model.LiveType;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceLive;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.crawler.utils.CrawlerUtils;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.Json;

@TargetUrl("http://matchweb.sports.qq.com/kbs/list\\?*")
@Component
public class QQMatchApiProcessor extends AbstractPageProcessor<SourceMatch[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(QQMatchApiProcessor.class);
    private static final String LIVE_URL_PREFIX = "http://kbs.sports.qq.com/kbsweb/game.htm?mid=%s";
    private Pattern columnIdPattern = Pattern.compile("columnId\\=([0-9]*)");

    @Override
    protected SourceMatch[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        List<SourceMatch> smes = Lists.newArrayList();

        // gameList({...})
        String text = CrawlerUtils.getJsonFromText(page.getRawText());
        Json json = new Json(text);
        List<String> dateMatches = json.jsonPath("$.data.*").all();
        if (requiredEmpty("$.data.*", dateMatches, pageUrl))
            return null;

        for (String item : dateMatches) {
            Json dmjson = new Json(item);
            List<String> matches = dmjson.jsonPath("$.[*]").all();
            if (requiredEmpty("$.[*]", matches, pageUrl))
                continue;

            for (String match : matches) {
                Json mjson = new Json(match);
                // columnId
                String competition = null;
                String gameFName = null;
                Matcher matcher = columnIdPattern.matcher(pageUrl);
                if (matcher.find()) {
                    String columnId = matcher.group(1);
                    String value = QQMatchesProcessor.ID2NAME.get(columnId);
                    String[] splited = value.split("\\|");
                    competition = splited[0];
                    if (!splited[1].equals("null")) {
                        gameFName = splited[1];
                    }
                }
                if (requiredEmpty("", competition, pageUrl))
                    continue;

                // startTime
                String startTime = mjson.jsonPath("$.startTime").get();
                if (requiredEmpty("$.startTime", startTime, pageUrl))
                    continue;

                // leftName
                String leftName = mjson.jsonPath("$.leftName").get();

                // rightName
                String rightName = mjson.jsonPath("$.rightName").get();

                // liveType: 0->数据,1->图文,3->视频直播,4->集锦
                String liveTypeStr = mjson.jsonPath("$.liveType").get();
                LiveType liveTypeEnum = null;
                if ("0".equals(liveTypeStr) || "1".equals(liveTypeStr)) {
                    // 不区分数据直播和图文直播
                    liveTypeEnum = LiveType.IMAGE_TEXT;
                } else if ("3".equals(liveTypeStr) || "4".equals(liveTypeStr)) {
                    // 不区分视频直播和集锦
                    liveTypeEnum = LiveType.VIDEO;
                } else {
                    LOGGER.error("unkown liveType {} on match {}", liveTypeStr, pageUrl);
                    continue;
                }

                // mid
                String mid = mjson.jsonPath("$.mid").get();
                if (requiredEmpty("$.mid", mid, pageUrl))
                    continue;

                // matchDesc
                String matchDesc = mjson.jsonPath("$.matchDesc").get();
                if (requiredEmpty("$.matchDesc", matchDesc, pageUrl))
                    continue;

                SourceMatch sm = new SourceMatch();
                sm.setSource(getSource());
                sm.setSourceId(mid);
                sm.setGameFName(gameFName);
                sm.setCompetition(competition);
                sm.setStartTime(LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.parseYMDHMS(startTime)));
                if (!Strings.isNullOrEmpty(leftName) && !Strings.isNullOrEmpty(rightName)) {
                    sm.setCompetitors(Lists.newArrayList(leftName, rightName));
                } else if (Strings.isNullOrEmpty(leftName) && Strings.isNullOrEmpty(rightName)) {
                    sm.setVs(Boolean.FALSE);
                } else {
                    LOGGER.error("only one competitor exists");
                    continue;
                }
                SourceLive live = new SourceLive("QQ", liveTypeEnum, matchDesc, String.format(LIVE_URL_PREFIX, mid));
                sm.setLives(Lists.newArrayList(live));
                sm.setSourceUrl(pageUrl);
                if (Boolean.TRUE.equals(sm.getVs())) {
                    sm.setName(String.format("%s %s VS %s", matchDesc, leftName, rightName));    
                } else {
                    sm.setName(matchDesc);
                }
                sm.setSourceData(JSON.parseObject(match));
                smes.add(sm);
            }
        }

        return smes.toArray(new SourceMatch[0]);
    }

    public static void main(String[] args) {
        Spider.create(new QQMatchApiProcessor())
                .addUrl("http://matchweb.sports.qq.com/kbs/list?callback=gameList&columnId=100000&startTime=2015-12-04&endTime=2015-12-10&_=1449457263826")
                .run();

    }

    @Override
    protected Source getSource() {
        return Source.QQ;
    }

    @Override
    protected Content getContent() {
        return Content.MATCH;
    }
}
