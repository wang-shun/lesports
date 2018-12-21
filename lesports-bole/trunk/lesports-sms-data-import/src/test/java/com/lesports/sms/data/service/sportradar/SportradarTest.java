package com.lesports.sms.data.service.sportradar;

import com.lesports.AbstractIntegrationTest;
import com.lesports.sms.data.service.LiveScoreParser;
import com.lesports.sms.data.service.MatchFbParser;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/12/27.
 */
public class SportradarTest    extends AbstractIntegrationTest {

    @Resource
    private LiveScoreParser liveScoreParser;
    @Resource
    private MatchFbParser matchFbParser;


    //助攻榜
    @Test
    public void testLiveScoreParser() {
        boolean result = liveScoreParser.parseData("E:\\livescore_letv_4505_delta.xml");
        System.out.println("result: " + result);
    }

    //赛程
    @Test
    public void testMatchParser() {
        boolean result = matchFbParser.parseData("E:\\soda\\schedulesandresult_Soccer.Italy.SerieA.1617.SerieA1617.xml");
        System.out.println("result: " + result);
    }
}
