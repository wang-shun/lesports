package com.lesports.crawler.controller;

import com.lesports.crawler.component.priority.PriorityProcessor;
import org.junit.Test;
import us.codecraft.webmagic.Request;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/16
 */
public class ControllerTest extends AbstractIntegrationTest {

    @Test
    public void testRun() throws Exception {
        PriorityProcessor priorityProcessor = new PriorityProcessor();
        Controller controller = new Controller(5);
        controller.setPriorityProcessor(priorityProcessor);
        controller.addRequest(new Request("http://www.zhibo8.cc/"));
        controller.run();
    }
}