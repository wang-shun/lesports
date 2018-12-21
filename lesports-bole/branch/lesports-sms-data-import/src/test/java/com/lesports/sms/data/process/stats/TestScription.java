package com.lesports.sms.data.process.stats;

import com.lesports.AbstractIntegrationTest;
import com.lesports.sms.data.Generator.impl.*;
import com.lesports.sms.data.model.commonImpl.DefaultLiveScore;
import com.lesports.sms.data.model.commonImpl.DefaultPlayerStanding;
import com.lesports.sms.data.model.commonImpl.DefaultTeamSeason;
import org.junit.Test;

import javax.annotation.Resource;

//import org.im4java.core.GMOperation;

/**
 * Created by qiaohongxin on 2016/7/26.
 */
public class TestScription extends AbstractIntegrationTest {

    @Resource
    private DefaultScheduleGenerator scheduleGenerator;
    @Resource
    private DefaultTeamStatsGenerator teamStatsGenerator;
    @Resource
    private DefaultPlayerStatsGenerator playerStatsGenerator;
    @Resource
    private DefaultTeamSeasonGenerator teamSeasonGenerator;
    @Resource
    private DefaultPlayerStandingGenerator playerStandingGenerator;
    @Resource
    private DefaultTeamStandingGenerator teamStandingGenerator;
    @Resource
    private DefaultLiveScoreGenerator defaultLiveScoreGenerator;

    @Test
    public void testSchedule() {
        scheduleGenerator.nextProcessor();
    }

    @Test
    public void testTeamStats() {
        teamStatsGenerator.nextProcessor();
    }

    @Test
    public void testTeamStandings() {
        teamStandingGenerator.nextProcessor();
    }

    @Test
    public void testPlayerStats() {
        playerStatsGenerator.nextProcessor();
    }

    @Test
    public void testTeamSeason() {
        teamSeasonGenerator.nextProcessor();
    }

    @Test
    public void testPlayerStanding() {
        playerStandingGenerator.nextProcessor();
    }
    @Test
    public void testLiveScore() {
        defaultLiveScoreGenerator.nextProcessor();
    }
}






