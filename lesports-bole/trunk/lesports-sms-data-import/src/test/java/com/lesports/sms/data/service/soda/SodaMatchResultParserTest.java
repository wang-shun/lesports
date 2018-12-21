package com.lesports.sms.data.service.soda;

import com.lesports.AbstractIntegrationTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/3/24.
 */
public class SodaMatchResultParserTest  extends AbstractIntegrationTest {

    @Resource
    private SodaMatchResultParser sodaMatchResultParser;

    @Test
    public void testSodaMatchResultParser() {
        boolean result = sodaMatchResultParser.parseData("E:\\soda\\s9-282-2016-1063000-matchresult.xml");
        System.out.println("result: " + result);
    }
}
