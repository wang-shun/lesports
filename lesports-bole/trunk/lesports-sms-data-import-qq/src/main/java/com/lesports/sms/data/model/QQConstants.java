package com.lesports.sms.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/8/17.
 */
public class QQConstants {

    public static final Map<String, Long> nbaConferenceMap = new HashMap<String, Long>();//NBA地域映射关系
    public static final Map<Long, Long> nbaTeamRankingDivisionMap = new HashMap<Long, Long>();//NBA地域和榜单映射关系
    public static final Map<String, String> NBALiveStatPath = new HashMap<String, String>(); //nba单场比赛实时技术统计
    public static final Map<String, String> NBAConferenceStandingStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> CBADivisionStandingStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBAPlayerTotalStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBAPlayerAVGStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBATeamTotalStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBATeamAVGStatPath = new HashMap<String, String>(); //soda技术统计映射表

    static {

        nbaConferenceMap.put("Eastern", 100130000L);
        nbaConferenceMap.put("Western", 100131000L);
        nbaConferenceMap.put("Pacific", 100137000L);
        nbaConferenceMap.put("Southwest", 100135000L);
        nbaConferenceMap.put("Northwest", 100136000L);
        nbaConferenceMap.put("Atlantic", 100133000L);
        nbaConferenceMap.put("Central", 100134000L);
        nbaConferenceMap.put("Southeast", 100132000L);


        nbaTeamRankingDivisionMap.put(100130000L, 100205000L);//东部联盟
        nbaTeamRankingDivisionMap.put(100131000L, 100204000L);//西部联盟
        nbaTeamRankingDivisionMap.put(100137000L, 104651000L);//太平洋区域榜单
        nbaTeamRankingDivisionMap.put(100135000L, 104652000L);//Southwest
        nbaTeamRankingDivisionMap.put(100136000L, 104653000L);//Northwest
        nbaTeamRankingDivisionMap.put(100133000L, 104648000L);//Atlantic
        nbaTeamRankingDivisionMap.put(100134000L, 104649000L);//Central
        nbaTeamRankingDivisionMap.put(100132000L, 104650000L);//Southeast
        //stats 单场球员比赛技术统计
        // NBALiveStatPath.put("points", "./points/@points");  //总得分
        NBALiveStatPath.put("fieldgoals_made", "fieldGoals");//投篮命中次数
        NBALiveStatPath.put("fieldgoals_attempted", "fieldGoalsAttempted"); //投篮尝试次数
        NBALiveStatPath.put("fieldgoal_percentage", "fieldGoals|/|fieldGoalsAttempted");//命中率
        NBALiveStatPath.put("threepoint_made", "threePointGoals"); //三分球命中次数
        NBALiveStatPath.put("threepoint_attempted", "threePointAttempted");//三分球投篮次数
        NBALiveStatPath.put("threepoint_percentage", "threePointGoals|/|threePointAttempted");//三分球命中率
        NBALiveStatPath.put("freethrows_made", "freeThrows");  //罚球命中次数
        NBALiveStatPath.put("freethrows_attempted", "freeThrowsAttempted"); //罚球次数
        NBALiveStatPath.put("freethrow_percentage", "freeThrows|/|freeThrowsAttempted");  //罚球命中率
        NBALiveStatPath.put("offensive_rebounds", "offensiveRebounds"); //前场篮板
        NBALiveStatPath.put("defensive_rebounds", "defensiveRebounds");//后场篮板
        NBALiveStatPath.put("total_rebounds", "rebounds");//总篮板球次数
        NBALiveStatPath.put("assists", "assists");//助攻次数
        NBALiveStatPath.put("personalfouls", "personalFouls");// 犯规次数
        //  NBALiveStatPath.put("disqualification", "./personal-fouls/@disqualifications");//被罚下场次数
        NBALiveStatPath.put("steals", "steals");//抢断次数
        NBALiveStatPath.put("turnovers", "turnovers");//失误次数
        NBALiveStatPath.put("blockedshots", "blocked");//盖帽次数
//        NBALiveStatPath.put("jumpshots", "./jumpshots/@jumpshots");//跳投次数
//        NBALiveStatPath.put("dunks", "./dunks/@dunks");//扣篮次数
//        NBALiveStatPath.put("layups", "./layups/@layups");//上篮次数
        //nba 东西部技术统计排行榜
        NBAConferenceStandingStatPath.put("rank", "serial");
        NBAConferenceStandingStatPath.put("showOrder", "serial");
        NBAConferenceStandingStatPath.put("wins", "wins");
        NBAConferenceStandingStatPath.put("losses", "losses");
        NBAConferenceStandingStatPath.put("pointWinPic", "points");
        NBAConferenceStandingStatPath.put("pointLossPic", "lossPoints");
        NBAConferenceStandingStatPath.put("divisionGameBack", "divGameBehind");
        NBAConferenceStandingStatPath.put("conferenceGameBack", "games-back");
        NBAConferenceStandingStatPath.put("winPic", "wining-percentage");
        NBAConferenceStandingStatPath.put("homeWin", "homeWins");
        NBAConferenceStandingStatPath.put("homeLoss", "homeLosses");
        NBAConferenceStandingStatPath.put("AwayWin", "awayWins");
        NBAConferenceStandingStatPath.put("AwayLoss", "awayLosses");
        NBAConferenceStandingStatPath.put("conferenceWin", "conferenceWins");
        NBAConferenceStandingStatPath.put("conferenceLoss", "conferenceLosses");
        NBAConferenceStandingStatPath.put("divisionWin", "divisionWins");
        NBAConferenceStandingStatPath.put("divisionLoss", "divisionLosses");
        NBAConferenceStandingStatPath.put("streakWin", "streak|ABS:+");
        NBAConferenceStandingStatPath.put("streakLoss", "streak|ABS:-");
        NBAConferenceStandingStatPath.put("gt10Win", "tenPtsOrMoreWins");
        NBAConferenceStandingStatPath.put("gt10Loss", "tenPtsOrMoreLosses");
        NBAConferenceStandingStatPath.put("lt3Win", "threePtsOrLessWins");
        NBAConferenceStandingStatPath.put("lt3Loss", "threePtsOrLessLosses");
        NBAConferenceStandingStatPath.put("ScoreGt100Win", "score100PlusWins");
        NBAConferenceStandingStatPath.put("ScoreGt100Loss", "score100PlusLosses");
        NBAConferenceStandingStatPath.put("Scorelt100Win", "score100MinusWins");
        NBAConferenceStandingStatPath.put("Scorelt100Loss", "score100MinusWins");
        NBAConferenceStandingStatPath.put("overTimeWin", "otWins");
        NBAConferenceStandingStatPath.put("overTimeLoss", "otLosses");
        NBAConferenceStandingStatPath.put("last10Win", "last10|SPLIT:-|0");
        NBAConferenceStandingStatPath.put("last10Loss", "last10|SPLIT:-|1");
        //
        CBADivisionStandingStatPath.put("rank", "./place/@place");
        CBADivisionStandingStatPath.put("showOrder", "./place/@place");
        CBADivisionStandingStatPath.put("winMatch", "./wins/@number");
        CBADivisionStandingStatPath.put("lossMatch", "./losses/@number");
        CBADivisionStandingStatPath.put("pointWinPic", "./points-for-per-game/@points");
        CBADivisionStandingStatPath.put("pointLossPic", "./points-against-per-game/@points");
        CBADivisionStandingStatPath.put("divisionGameBack", "./games-back/@number");
        CBADivisionStandingStatPath.put("conferenceGameBack", "./conference-games-back/@games");
        CBADivisionStandingStatPath.put("winPic", "./winning-percentage/@percentage");
        CBADivisionStandingStatPath.put("homeWin", "./win-loss-record[@type='Home']/@wins");
        CBADivisionStandingStatPath.put("homeLoss", "./win-loss-record[@type='Home']/@losses");
        CBADivisionStandingStatPath.put("AwayWin", "./win-loss-record[@type='Away']/@wins");
        CBADivisionStandingStatPath.put("AwayLoss", "./win-loss-record[@type='Away']/@losses");
        CBADivisionStandingStatPath.put("conferenceWin", "./win-loss-record[@type='Conference']/@wins");
        CBADivisionStandingStatPath.put("conferenceLoss", "./win-loss-record[@type='Conference']/@losses");
        CBADivisionStandingStatPath.put("divisionWin", "./win-loss-record[@type='Division']/@wins");
        CBADivisionStandingStatPath.put("divisionLoss", "./win-loss-record[@type='Division']/@losses");
        CBADivisionStandingStatPath.put("streakWin", "./streak[@kind='Winning']/@games");
        CBADivisionStandingStatPath.put("streakLoss", "./streak[@kind='Losing']/@games");
        CBADivisionStandingStatPath.put("gt10Win", "./win-loss-record[@type='Games Decided by >10']/@wins");
        CBADivisionStandingStatPath.put("gt10Loss", "./win-loss-record[@type='Games Decided by >10']/@losses");
        CBADivisionStandingStatPath.put("lt3Win", "./win-loss-record[@type='Games Decided by <3']/@wins");
        CBADivisionStandingStatPath.put("lt3Loss", "./win-loss-record[@type='Games Decided by <3']/@losses");
        CBADivisionStandingStatPath.put("ScoreGt100Win", "./win-loss-record[@type='Games Scoring 100 or More']/@wins");
        CBADivisionStandingStatPath.put("ScoreGt100Loss", "./win-loss-record[@type='Games Scoring 100 or More']/@losses");
        CBADivisionStandingStatPath.put("Scorelt100Win", "./win-loss-record[@type='Games Scoring <100']/@wins");
        CBADivisionStandingStatPath.put("Scorelt100Loss", "./win-loss-record[@type='Games Scoring <100']/@losses");
        CBADivisionStandingStatPath.put("overTimeWin", "./win-loss-record[@type='Overtime Games']/@wins");
        CBADivisionStandingStatPath.put("overTimeLoss", "./win-loss-record[@type='Overtime Games']/@losses");
        CBADivisionStandingStatPath.put("last10Win", "./win-loss-record[@type='Last 10']/@wins");
        CBADivisionStandingStatPath.put("last10Loss", "./win-loss-record[@type='Last 10']/@losses");
        //NBA 球员赛季技术统计汇总
        NBAPlayerTotalStatPath.put("games", "games");
        NBAPlayerTotalStatPath.put("games_started", "gamesStarted");
        NBAPlayerTotalStatPath.put("minute", "minutes");
        NBAPlayerTotalStatPath.put("points", "points");
        NBAPlayerTotalStatPath.put("fieldgoals_made", "fgMade");
        NBAPlayerTotalStatPath.put("fieldgoals_attempted", "fgAttempted");
        NBAPlayerTotalStatPath.put("fieldgoal_percentage", "fgPCT");
        NBAPlayerTotalStatPath.put("threepoint_made", "threesMade");
        NBAPlayerTotalStatPath.put("threepoint_attempted", "threesAttempted");
        NBAPlayerTotalStatPath.put("threepoint_percentage", "threesPCT");
        NBAPlayerTotalStatPath.put("freethrows_made", "ftMade");
        NBAPlayerTotalStatPath.put("freethrows_attempted", "ftAttempted");
        NBAPlayerTotalStatPath.put("freethrow_percentage", "ftPCT");
        NBAPlayerTotalStatPath.put("offensive_rebounds", "offensiveRebounds");
        NBAPlayerTotalStatPath.put("defensive_rebounds", "defensiveRebounds");
        NBAPlayerTotalStatPath.put("rebounds", "rebounds");
        NBAPlayerTotalStatPath.put("assists", "assists");
        NBAPlayerTotalStatPath.put("personalfouls", "fouls");
        NBAPlayerTotalStatPath.put("steals", "steals");
        NBAPlayerTotalStatPath.put("turnovers", "turnovers");
        NBAPlayerTotalStatPath.put("blockedshots", "blocks");
        //NBA 球员赛季技术统计均值
        NBAPlayerAVGStatPath.put("games", "games");
        NBAPlayerAVGStatPath.put("games_started", "gamesStarted");
        NBAPlayerAVGStatPath.put("minute", "minutesPG");
        NBAPlayerAVGStatPath.put("points", "pointsPG");
        NBAPlayerAVGStatPath.put("fieldgoals_made", "fgMade");
        NBAPlayerAVGStatPath.put("fieldgoals_attempted", "fgAttempted");
        NBAPlayerAVGStatPath.put("fieldgoal_percentage", "fgPCT");
        NBAPlayerAVGStatPath.put("threepoint_made", "threesMade");
        NBAPlayerAVGStatPath.put("threepoint_attempted", "threesAttempted");
        NBAPlayerAVGStatPath.put("threepoint_percentage", "threesPCT");
        NBAPlayerAVGStatPath.put("freethrows_made", "ftMade");
        NBAPlayerAVGStatPath.put("freethrows_attempted", "ftAttempted");
        NBAPlayerAVGStatPath.put("freethrow_percentage", "ftPCT");
        NBAPlayerAVGStatPath.put("offensive_rebounds", "offensiveReboundsPG");
        NBAPlayerAVGStatPath.put("defensive_rebounds", "defensiveReboundsPG");
        NBAPlayerAVGStatPath.put("rebounds", "reboundsPG");
        NBAPlayerAVGStatPath.put("assists", "assistsPG");
        NBAPlayerAVGStatPath.put("personalfouls", "foulsPG");
        NBAPlayerAVGStatPath.put("steals", "stealsPG");
        NBAPlayerAVGStatPath.put("turnovers", "turnoversPG");
        NBAPlayerAVGStatPath.put("blockedshots", "blocksPG");
        //NBA 球队赛季技术统计汇总
        //  NBATeamTotalStatPath.put("win_games", "./record/@wins");
        // NBATeamTotalStatPath.put("loss_games", "./record/@wins");
        NBATeamTotalStatPath.put("games", "games");
        NBATeamTotalStatPath.put("fieldgoal_attempted", "fgAttempted");
        NBATeamTotalStatPath.put("fieldgoal_made", "fgMade");
        NBATeamTotalStatPath.put("fieldgoal_percentage", "fgPCT");
        NBATeamTotalStatPath.put("threepoint_made", "threesMade");
        NBATeamTotalStatPath.put("threepoint_attempted", "threesAttempted");
        NBATeamTotalStatPath.put("threepoint_percentage", "threesPCT");
        NBATeamTotalStatPath.put("freethrows_made", "ftMade");
        NBATeamTotalStatPath.put("freethrows_attempted", "ftAttempted");
        NBATeamTotalStatPath.put("freethrow_percentage", "ftPCT");
        NBATeamTotalStatPath.put("points", "points");
        NBATeamTotalStatPath.put("rebounds", "rebounds");
        NBATeamTotalStatPath.put("assists", "assists");
        NBATeamTotalStatPath.put("personalfouls", "tFouls");
        NBATeamTotalStatPath.put("steals", "steals");
        NBATeamTotalStatPath.put("turnovers", "turnovers");
        NBATeamTotalStatPath.put("blockedshots", "blocks");
        //NBA 球队赛季技术统计均值
        //  NBATeamTotalStatPath.put("win_games", "./record/@wins"); MIsses
        // NBATeamTotalStatPath.put("loss_games", "./record/@wins");   NBATeamAVGStatPath.put("games", "games");//MISSED
        NBATeamAVGStatPath.put("fieldgoal_attempted", "fgAttempted");//ADD
        NBATeamAVGStatPath.put("fieldgoal_made", "fgMade");
        NBATeamAVGStatPath.put("fieldgoal_percentage", "fgPCT");
        NBATeamAVGStatPath.put("threepoint_made", "threesMade");
        NBATeamAVGStatPath.put("threepoint_attempted", "threesAttempted");
        NBATeamAVGStatPath.put("threepoint_percentage", "threesPCT");
        NBATeamAVGStatPath.put("freethrows_made", "ftMade");
        NBATeamAVGStatPath.put("freethrows_attempted", "ftAttempted");
        NBATeamAVGStatPath.put("freethrow_percentage", "ftPCT");
        NBATeamAVGStatPath.put("points", "avgPointsPG");
        NBATeamAVGStatPath.put("rebounds", "avgReboundsPG");
        NBATeamAVGStatPath.put("assists", "avgAssistsPG");
        NBATeamAVGStatPath.put("personalfouls", "avgFoulsPG");
        NBATeamAVGStatPath.put("steals", "avgStealsPG");
        // NBATeamAVGStatPath.put("turnovers", "./turnovers/@turnover-average");
        NBATeamAVGStatPath.put("blockedshots", "avgBlocksPG");

    }
}
