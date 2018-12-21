package com.lesports.crawler.processor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.util.Assert;

import com.lesports.crawler.repo.AbstractIntegrationTest;
import com.lesports.sms.api.vo.TTeam;
import com.lesports.sms.client.SmsApis;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/17
 */
public class ZhibobaListPageProcessorTest extends AbstractIntegrationTest {

    @Test
    public void testProcess() throws Exception {
//        Spider spider = Spider.create(new ProcessorDispatcher()
//                .addPageProcessor(new ZhibobaListPageProcessor())
//                .addPageProcessor(new ZhibobaLivePageProcessor()))
////                .setScheduler(new PriorityMsgQueueScheduler())
//                .addPipeline(new DataAttachPipeline()
//                        .addHandler(new ListDataAttachHandler())
//                        .addHandler(new LiveDataAttachHandler()))
//                .addPipeline(new FetchedPipeline())
//                .setExitWhenComplete(false)
//                .addUrl("http://www.zhibo8.cc")
//                        //开启5个线程抓取
//                .thread(1);
//        spider.setEmptySleepTime(10000);
//        spider.run();
    }

    @Test
    public void testPattern() {
        String exp = "(\\w+)\\.htm$";
        Pattern pattern = Pattern.compile(exp);
        Matcher matcher = pattern.matcher("http://www.zhibo8.cc/zhibo/nba/2015/1120qishivsxionglu.htm");
        matcher.find();
        matcher.group(1);
    }

    @Test
    public void testGetCompetitor() {
        List<TTeam> tTeams = SmsApis.getTeamsByRegexName("北控", 0, null);
        Assert.notEmpty(tTeams);
    }
}