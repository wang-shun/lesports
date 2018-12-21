package com.lesports.msg.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/15
 */
public class LiveMessageTest extends TestCase {
    @Test
    public void testPurse(){
        String messageBody = "{\"dateType\":3,\"param\":{\"liveId\":\"1020150329182236\",\"liveHallType\":\"1\",\"type\":\"update\"}}";
        LiveMessage liveMessage = JSONObject.parseObject(messageBody, LiveMessage.class);
        Assert.isTrue(liveMessage.getDateType() > 0);
    }
}