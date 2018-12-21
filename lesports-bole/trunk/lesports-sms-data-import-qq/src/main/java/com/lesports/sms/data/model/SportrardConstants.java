package com.lesports.sms.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/8/17.
 */
public class SportrardConstants {

    public static final Map<String, String> goalScorePlayerStandingStatPath = new HashMap<String, String>(); //球员射手榜
    public static final Map<String, String> assistPlayerStandingStatPath = new HashMap<String, String>(); //球员射手榜
    public static final Map<String, String> teamStandingStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> teamLiveStatPath = new HashMap<String, String>(); //soda技术统计映射表

    static {
        //得分榜
        goalScorePlayerStandingStatPath.put("rank", "./PlayerStatsEntry[@id='4']/@value");
        goalScorePlayerStandingStatPath.put("goal", "./PlayerStatsEntry[@id='2']/@value");
        goalScorePlayerStandingStatPath.put("penaltyNumber", "./PlayerStatsEntry[@id='36']/@value");
        goalScorePlayerStandingStatPath.put("caps", "./PlayerStatsEntry[@id='40']/@value");
        //助攻榜
        assistPlayerStandingStatPath.put("rank", "./PlayerStatsEntry[@id='27']/@value");
        assistPlayerStandingStatPath.put("assists", "./PlayerStatsEntry[@id='25']/@value");
        assistPlayerStandingStatPath.put("caps", "./PlayerStatsEntry[@id='40']/@value");
        //球队积分榜
        teamStandingStatPath.put("matchNumber", "./LeagueTableColumn[@key='matchesTotal']/@value");
        teamStandingStatPath.put("matchHome", "./LeagueTableColumn[@key='matchesHome']/@value");
        teamStandingStatPath.put("matchAway", "./LeagueTableColumn[@key='matchesAway']/@value");
        teamStandingStatPath.put("teamScore", "./LeagueTableColumn[@key='pointsTotal']/@value");
        teamStandingStatPath.put("winMatch", "./LeagueTableColumn[@key='winTotal']/@value");
        teamStandingStatPath.put("lossMatch", "./LeagueTableColumn[@key='lossTotal']/@value");
        teamStandingStatPath.put("flatMatch", "./LeagueTableColumn[@key='drawTotal']/@value");
        teamStandingStatPath.put("goals", "./LeagueTableColumn[@key='goalsForTotal']/@value");
        teamStandingStatPath.put("fumble", "./LeagueTableColumn[@key='goalsAgainstTotal']/@value");
        teamStandingStatPath.put("goalDiffer", "./LeagueTableColumn[@key='goalsForTotal']/@value|-|./LeagueTableColumn[@key='goalsAgainstTotal']/@value");

        //jishutongji
        teamLiveStatPath.put("ballPossession", "./BallPossession/Team?/text()");
        teamLiveStatPath.put("shotOnGoal", "./ShotsOnGoal/Team?/text()");
        teamLiveStatPath.put("shotOffGoal", "./ShotsOffGoal/Team?/text()");
        teamLiveStatPath.put("freeKicks", "./FreeKicks/Team?/text()");
        teamLiveStatPath.put("cornerKicks", "./CornerKicks/Team?/text()");
        teamLiveStatPath.put("offsides", "./Offsides/Team?/text()");
        teamLiveStatPath.put("throwIns", "./ThrowIns/Team?/text()");
        teamLiveStatPath.put("goalkeeperSaves", "./GoalkeeperSaves/Team?/text()");
        teamLiveStatPath.put("goalKicks", "./GoalKicks/Team?/text()");
        teamLiveStatPath.put("fouls", "./Fouls/Team?/text()");

    }
}
