package com.lesports.crawler.processor.sina;

import java.util.Calendar;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.Content;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.crawler.processor.AbstractPageProcessor;
import com.lesports.utils.LeDateUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.model.annotation.TargetUrl;

/**
 * SINA直播抓取
 * 
 * @author denghui
 *
 */
@TargetUrl("http://match.sports.sina.com.cn/index.html$")
@Component
public class SinaMatchesProcessor extends AbstractPageProcessor<SourceMatch[]> {

    private static final String URL_TEMPLATE = "http://platform.sina.com.cn/sports_other/livecast_dateschedule?app_key=3633771828&date=%s&callback=getLivecastScheculeCallback";

    @Override
    protected List<String> getTargetRequests(Page page) {
        List<String> requests = Lists.newArrayList();
        // 后七天数据
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        for (int i = 0; i < 7; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1);
            String date = LeDateUtils.formatYYYY_MM_DD(calendar.getTime());
            requests.add(String.format(URL_TEMPLATE, date));
        }

        return requests;
    }

    @Override
    protected SourceMatch[] doProcess(Page page) {
        // nothing to do
        return null;
    }

    public static void main(String[] args) {
        Spider.create(new SinaMatchesProcessor()).addUrl("http://match.sports.sina.com.cn/index.html").run();

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
