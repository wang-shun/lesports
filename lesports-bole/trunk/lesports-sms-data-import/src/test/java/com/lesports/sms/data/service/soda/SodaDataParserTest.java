package com.lesports.sms.data.service.soda;

import com.lesports.AbstractIntegrationTest;
import org.junit.Test;

import javax.annotation.Resource;

/**
 * Created by zhonglin on 2016/4/27.
 */
public class SodaDataParserTest   extends AbstractIntegrationTest {

    @Resource
    private SodaAssistParser sodaAssistParser;
    @Resource
    private SodaHistoryMatchParser sodaHistoryMatchParser;
    @Resource
    private  SodaMatchParser sodaMatchParser;
    @Resource
    private SodaRankingParser sodaRankingParser;

    @Resource
    private SodaTopPlayerGoalParser sodaTopPlayerGoalParser;

    //助攻榜
    @Test
    public void testSodaAssistParser() {
        boolean result = sodaAssistParser.parseData("E:\\soda\\t324-top-player-assist-282.xml");
        System.out.println("result: " + result);
    }

    //导入历史数据
    @Test
    public void testSodaHistoryMatchParser() {
        boolean result = sodaHistoryMatchParser.parseData("E:\\soda\\t202-match-stat-1063192.xml");
        System.out.println("result: " + result);
    }

    //导入赛程
    @Test
    public void testsodaMatchParser() {
        boolean result = sodaMatchParser.parseData("E:\\soda\\t201-fixtures-282.xml");
        System.out.println("result: " + result);
    }

    //积分榜
    @Test
    public void testSodaRankingParser() {
        boolean result = sodaRankingParser.parseData("E:\\soda\\t301-ranking-282.xml");
        System.out.println("result: " + result);
    }

    //射手榜
    @Test
    public void testSodaTopPlayerGoalParser() {
        boolean result = sodaTopPlayerGoalParser.parseData("E:\\soda\\t304-top-player-goal-282.xml");
        System.out.println("result: " + result);
    }

}
