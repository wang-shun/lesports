package com.lesports.crawler.processor.qq;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * QQ直播数据抓取
 * 
 * @author denghui
 *
 */
@TargetUrl("http://kbs.sports.qq.com/?$")
@Component
public class QQMatchesProcessor extends AbstractPageProcessor<SourceMatch[]> {

    private static final String URL_TEMPLATE = "http://matchweb.sports.qq.com/kbs/list?callback=gameList&columnId=%s&startTime=%s&endTime=%s&_=1449457263826";
    @SuppressWarnings("serial")
    static final Map<String, String> ID2NAME = new HashMap<String, String>() {
        {
            put("100000", "NBA|篮球");
            put("208", "中超|足球");
            put("605", "亚冠|足球");
            put("5", "欧冠|足球");
            put("8", "英超|足球");
            put("23", "西甲|足球");
            put("21", "意甲|足球");
            put("22", "德甲|足球");
            put("24", "法甲|足球");
            put("100008", "CBA|篮球");
            put("100002", "综合|null");
            put("100005", "NFL|null");
        }
    };

    @Override
    protected List<String> getTargetRequests(Page page) {
        List<String> requests = Lists.newArrayList();
        // 后七天数据, 需要按赛程吗?
        Calendar calendar = Calendar.getInstance();
        String startTime = LeDateUtils.formatYYYY_MM_DD(calendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 6);
        String endTime = LeDateUtils.formatYYYY_MM_DD(calendar.getTime());

        for (Map.Entry<String, String> entry : ID2NAME.entrySet()) {
            requests.add(String.format(URL_TEMPLATE, entry.getKey(), startTime, endTime));
        }
        return requests;
    }

    @Override
    protected SourceMatch[] doProcess(Page page) {
        // Nothing to do
        return null;
    }

    public static void main(String[] args) {
        Spider.create(new QQMatchesProcessor()).addUrl("http://kbs.sports.qq.com/").run();
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
