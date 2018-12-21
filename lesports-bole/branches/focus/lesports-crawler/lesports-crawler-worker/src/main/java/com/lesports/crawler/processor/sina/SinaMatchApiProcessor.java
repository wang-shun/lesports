package com.lesports.crawler.processor.sina;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

/**
 * SINA直播页接口数据抓取
 * 
 * @author denghui
 *
 */
@TargetUrl("http://platform.sina.com.cn/sports_other/livecast_dateschedule\\?*")
@Component
public class SinaMatchApiProcessor extends AbstractPageProcessor<SourceMatch[]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SinaMatchApiProcessor.class);

    @Override
    protected SourceMatch[] doProcess(Page page) {
        String pageUrl = page.getRequest().getUrl();
        List<SourceMatch> smes = Lists.newArrayList();

        // getLivecastScheculeCallback({...});
        String text = CrawlerUtils.getJsonFromText(page.getRawText());
        Json json = new Json(text);
        List<String> matches = json.jsonPath("$.result.data.[*]").all();
        if (requiredEmpty("$.result.data.*", matches, pageUrl))
            return null;

        for (String match : matches) {
            Json mjson = new Json(match);
            // id
            String id = mjson.jsonPath("$.id").get();
            if (requiredEmpty("$.id", id, pageUrl))
                continue;

            // type
            String gameFName = null;
            String type = mjson.jsonPath("$.type").get();
            if (!Strings.isNullOrEmpty(type)) {
                for (String str : Arrays.asList("足球","篮球","网球")) {
                    if (type.contains(str)) {
                        gameFName = str;
                        break;
                    }
                }
            }

            // competition
            String competition = mjson.jsonPath("$.LeagueType_cn").get();
            if (competition == null) {
                competition = "综合";// 综合赛事
            }
            if (requiredEmpty("$.LeagueType_cn", competition, pageUrl))
                continue;

            // competitors
            String team1 = mjson.jsonPath("$.Team1").get();
            String team2 = mjson.jsonPath("$.Team2").get();

            // startTime
            String date = mjson.jsonPath("$.MatchDate").get();
            if (requiredEmpty("$.MatchDate", date, pageUrl))
                continue;
            String time = mjson.jsonPath("$.MatchTime").get();
            if (requiredEmpty("$.MatchTime", time, pageUrl))
                continue;

            // live
            // LiveStatus:0->未开始, 1->直播中?, 2->已结束
            String liveUrl = mjson.jsonPath("$.live_url").get();
            String videoLiveUrl = mjson.jsonPath("$.VideoLiveUrl").get();
            String title = mjson.jsonPath("$.Title").get();
            if (requiredEmpty("$.Title", title, pageUrl))
                continue;

            SourceMatch sm = new SourceMatch();
            sm.setSource(getSource());
            sm.setSourceId(id);
            sm.setGameFName(gameFName);
            sm.setCompetition(competition);
            if (!Strings.isNullOrEmpty(team1) && !Strings.isNullOrEmpty(team2)) {
                sm.setCompetitors(Lists.newArrayList(team1, team2));
            } else if (Strings.isNullOrEmpty(team1) && Strings.isNullOrEmpty(team2)) {
                sm.setVs(Boolean.FALSE);
            } else {
                LOGGER.error("only one competitor exists");
                continue;
            }
            String dateTime = date + " " + time + ":00";
            sm.setStartTime(LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.parseYMDHMS(dateTime)));
            // 添加图文和视频直播信息
            List<SourceLive> lives = new ArrayList<>();
            if (!Strings.isNullOrEmpty(videoLiveUrl)) {
                SourceLive live = new SourceLive("SINA", LiveType.VIDEO, title, videoLiveUrl);
                lives.add(live);
            }
            if (!Strings.isNullOrEmpty(liveUrl)) {
                SourceLive live = new SourceLive("SINA", LiveType.IMAGE_TEXT, title, liveUrl);
                lives.add(live);
            }
            sm.setLives(lives);
            sm.setSourceUrl(pageUrl);
            sm.setName(title);
            sm.setSourceData(JSON.parseObject(match));
            smes.add(sm);
        }

        return smes.toArray(new SourceMatch[0]);
    }

    public static void main(String[] args) {
        Spider.create(new SinaMatchApiProcessor())
                .addUrl("http://platform.sina.com.cn/sports_other/livecast_dateschedule?app_key=3633771828&date=2015-12-07&callback=getLivecastScheculeCallback")
                .run();
    }

    @Override
    protected Source getSource() {
        return Source.SINA;
    }

    @Override
    protected Content getContent() {
        return Content.MATCH;
    }
}
