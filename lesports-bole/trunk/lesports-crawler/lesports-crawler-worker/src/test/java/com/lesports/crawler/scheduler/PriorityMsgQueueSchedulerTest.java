package com.lesports.crawler.scheduler;

import com.lesports.crawler.controller.FakedTask;
import com.lesports.crawler.repo.AbstractIntegrationTest;
import org.junit.Test;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Task;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/24
 */
public class PriorityMsgQueueSchedulerTest extends AbstractIntegrationTest {

    @Test
    public void testPush() throws Exception {
        Request request = new Request();
        request.setUrl("http://www.zhibo8.cc/zhibo/nba/2015/112461727.htm");
        Task task = new FakedTask("", Site.me());
        PriorityMsgQueueScheduler priorityMsgQueueScheduler = new PriorityMsgQueueScheduler();
        priorityMsgQueueScheduler.push(request, task);
    }

    public void testPoll() throws Exception {

    }
}