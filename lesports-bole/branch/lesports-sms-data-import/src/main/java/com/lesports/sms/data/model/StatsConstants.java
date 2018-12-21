package com.lesports.sms.data.model;

import java.util.HashMap;
import java.util.Map;


import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/8/17.
 */
public class StatsConstants {


    public static final Long soccerID = 100015000L;
    public static final Map<String, Long> cidMap = new HashMap<String, Long>(); //soda技术统计映射表
    public static final Map<String, String> sodaPlayerStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> sodaPlayerTotalStatPath = new HashMap<String, String>(); //soda技术统计映射表
public static final String conference="CONFERENCE";
    public static final String division="DIVISION";
    public static final String group="GROUP";
    public static final Map<String, Long> nbaConferenceMap = new HashMap<String, Long>();//NBA地域映射关系
    public static final Map<String, String> NBALiveStatPath = new HashMap<String, String>(); //nba单场比赛实时技术统计
    public static final Map<String, String> NBAConferenceStandingStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBADivisionStandingStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> CBADivisionStandingStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBAPlayerTotalStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBAPlayerAVGStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBAPlayerTopStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> NBAPlayerStandingStatPath = new HashMap<String, String>(); //soda技术统计映射表
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
        //cidyingshe
        cidMap.put("282", 47001L);//中超
        cidMap.put("412-2", 309001L);//40强
        cidMap.put("412", 101425001L);//12强
        // cidMap.put("262", 100322001L);//友谊赛
        cidMap.put("206", 521001L);//东亚杯
        cidMap.put("427", 100352001L);//奥预亚
        cidMap.put("5", 42001L);//亚洲杯
        cidMap.put("26", 100234001L);//亚冠100373001L
        cidMap.put("311", 177001L);//足协杯
        cidMap.put("312", 100395001L);//中超级杯
        cidMap.put("242", 100283001L);//世俱杯
        cidMap.put("234", 67001L);//日甲
        cidMap.put("217", 70001L);//韩甲
        cidMap.put("32", 100235001L);//联合会杯
        cidMap.put("12", 100100001L);//欧联杯
        cidMap.put("115", 32001L);//德甲
        cidMap.put("116", 120001L);//德杯
        cidMap.put("117", 101422001L);//德超级杯
        cidMap.put("452", 393001L);//国际杯
        cidMap.put("9", 192001L);//欧冠
        cidMap.put("133", 20001L);//英超
        cidMap.put("134", 100228001L);//足总杯
        cidMap.put("135", 100040001L);//联赛杯
        cidMap.put("258", 101121001L);// 社区盾
        cidMap.put("153", 509001L);//比甲
        cidMap.put("55", 503001L);//苏超
        cidMap.put(" 57", 100042001L);//苏联杯
        cidMap.put("259", 100037001L);//英冠
        cidMap.put("289", 100170001L);// 澳超
        cidMap.put("36", 500001L);// 乌克超
        cidMap.put("46", 100041001L);//国王杯
        cidMap.put("45", 26001L);// 西甲
        cidMap.put("69", 172001L);// 葡超
        cidMap.put("449", 223001L);//土伦杯
        cidMap.put("122", 35001L);// 法甲
        cidMap.put("100", 29001L);// 意甲
        cidMap.put("101", 100266001L);// 意杯
        cidMap.put("47", 100061001L);// 西超级杯
        cidMap.put("348", 72001L);// 中甲
        cidMap.put("393", 604001L);// 奥迪杯
        cidMap.put("260", 100038001L);//英甲
        cidMap.put("295", 235001L);// 世青赛
        cidMap.put("293", 62001L);//美职
        cidMap.put("179", 100619001L);//  意乙
        //jishutongji
        sodaPlayerStatPath.put("goal", "./@goal");
        sodaPlayerStatPath.put("matchs", "./@matches");
        sodaPlayerStatPath.put("red", "./@red");
        sodaPlayerStatPath.put("yellow", "./@yellow");
        sodaPlayerStatPath.put("foul", "./@foul");
        sodaPlayerStatPath.put("clearance", "./@clearance");
        sodaPlayerStatPath.put("keypass", "./@keypass");
        //total stats
        sodaPlayerTotalStatPath.put("date", "./@date");
        sodaPlayerTotalStatPath.put("goal", "./@goal");
        sodaPlayerTotalStatPath.put("matchs", "./@matchs");
        sodaPlayerTotalStatPath.put("assist", "./@assist");
        sodaPlayerTotalStatPath.put("shoot", "./@shoot");
        sodaPlayerTotalStatPath.put("save", "./@save");
        //sats 单场比赛技术统计
        NBALiveStatPath.put("minutes", "./minutes/@minutes");
        NBALiveStatPath.put("points", "./points/@points");  //总得分
        NBALiveStatPath.put("fieldgoals_made", "./field-goals/@made");//投篮命中次数
        NBALiveStatPath.put("fieldgoals_attempted", "./field-goals/@attempted"); //投篮尝试次数
        NBALiveStatPath.put("fieldgoal_percentage", "./field-goals/@percentage");//命中率
        NBALiveStatPath.put("threepoint_made", "./three-point-field-goals/@made"); //三分球命中次数
        NBALiveStatPath.put("threepoint_attempted", "./three-point-field-goals/@attempted");//三分球投篮次数
        NBALiveStatPath.put("threepoint_percentage", "./three-point-field-goals/@percentage");//三分球命中率
        NBALiveStatPath.put("freethrows_made", "./free-throws/@made");  //罚球命中次数
        NBALiveStatPath.put("freethrows_attempted", "./free-throws/@attempted"); //罚球次数
        NBALiveStatPath.put("freethrow_percentage", "./free-throws/@percentage");  //罚球命中率
        NBALiveStatPath.put("offensive_rebounds", "./rebounds/@offensive"); //前场篮板
        NBALiveStatPath.put("defensive_rebounds", "./rebounds/@defensive");//后场篮板
        NBALiveStatPath.put("total_rebounds", "./rebounds/@total");//总篮板球次数
        NBALiveStatPath.put("assists", "./assists/@assists");//助攻次数
        NBALiveStatPath.put("personalfouls", "./personal-fouls/@fouls");// 犯规次数
        NBALiveStatPath.put("disqualification", "./personal-fouls/@disqualifications");//被罚下场次数
        NBALiveStatPath.put("steals", "./steals/@steals");//抢断次数
        NBALiveStatPath.put("turnovers", "./turnovers/@turnovers");//失误次数
        NBALiveStatPath.put("blockedshots", "./blocked-shots/@blocked-shots ");//盖帽次数
        NBALiveStatPath.put("jumpshots", "./jumpshots/@jumpshots");//跳投次数
        NBALiveStatPath.put("dunks", "./dunks/@dunks");//扣篮次数
        NBALiveStatPath.put("layups", "./layups/@layups");//上篮次数
        //nba 东西部技术统计排行榜
        NBAConferenceStandingStatPath.put("rank", "./conference-seed/@seed");
        NBAConferenceStandingStatPath.put("showOrder", "./conference-seed/@seed");
        NBAConferenceStandingStatPath.put("wins", "./wins/@number");
        NBAConferenceStandingStatPath.put("losses", "./losses/@number");
        NBAConferenceStandingStatPath.put("pointWinPic", "./points-for-per-game/@points");
        NBAConferenceStandingStatPath.put("pointLossPic", "./points-against-per-game/@points");
        NBAConferenceStandingStatPath.put("divisionGameBack", "./games-back/@number");
        NBAConferenceStandingStatPath.put("conferenceGameBack", "./conference-games-back/@games");
        NBAConferenceStandingStatPath.put("winPic", "./winning-percentage/@percentage");
        NBAConferenceStandingStatPath.put("homeWin", "./win-loss-record[@type='Home']/@wins");
        NBAConferenceStandingStatPath.put("homeLoss", "./win-loss-record[@type='Home']/@losses");
        NBAConferenceStandingStatPath.put("AwayWin", "./win-loss-record[@type='Away']/@wins");
        NBAConferenceStandingStatPath.put("AwayLoss", "./win-loss-record[@type='Away']/@losses");
        NBAConferenceStandingStatPath.put("conferenceWin", "./win-loss-record[@type='Conference']/@wins");
        NBAConferenceStandingStatPath.put("conferenceLoss", "./win-loss-record[@type='Conference']/@losses");
        NBAConferenceStandingStatPath.put("divisionWin", "./win-loss-record[@type='Division']/@wins");
        NBAConferenceStandingStatPath.put("divisionLoss", "./win-loss-record[@type='Division']/@losses");
        NBAConferenceStandingStatPath.put("streakWin", "./streak[@kind='Winning']/@games");
        NBAConferenceStandingStatPath.put("streakLoss", "./streak[@kind='Losing']/@games");
        NBAConferenceStandingStatPath.put("gt10Win", "./win-loss-record[@type='Games Decided by >10']/@wins");
        NBAConferenceStandingStatPath.put("gt10Loss", "./win-loss-record[@type='Games Decided by >10']/@losses");
        NBAConferenceStandingStatPath.put("lt3Win", "./win-loss-record[@type='Games Decided by <3']/@wins");
        NBAConferenceStandingStatPath.put("lt3Loss", "./win-loss-record[@type='Games Decided by <3']/@losses");
        NBAConferenceStandingStatPath.put("ScoreGt100Win", "./win-loss-record[@type='Games Scoring 100 or More']/@wins");
        NBAConferenceStandingStatPath.put("ScoreGt100Loss", "./win-loss-record[@type='Games Scoring 100 or More']/@losses");
        NBAConferenceStandingStatPath.put("Scorelt100Win", "./win-loss-record[@type='Games Scoring <100']/@wins");
        NBAConferenceStandingStatPath.put("Scorelt100Loss", "./win-loss-record[@type='Games Scoring <100']/@losses");
        NBAConferenceStandingStatPath.put("overTimeWin", "./win-loss-record[@type='Overtime Games']/@wins");
        NBAConferenceStandingStatPath.put("overTimeLoss", "./win-loss-record[@type='Overtime Games']/@losses");
        NBAConferenceStandingStatPath.put("last10Win", "./win-loss-record[@type='Last 10']/@wins");
        NBAConferenceStandingStatPath.put("last10Loss", "./win-loss-record[@type='Last 10']/@losses");
        //分区排行榜技术项
        NBADivisionStandingStatPath.put("rank", "./place/@place");
        NBADivisionStandingStatPath.put("showOrder", "./place/@place");
        NBADivisionStandingStatPath.put("wins", "./wins/@number");
        NBADivisionStandingStatPath.put("losses", "./losses/@number");
        NBADivisionStandingStatPath.put("pointWinPic", "./points-for-per-game/@points");
        NBADivisionStandingStatPath.put("pointLossPic", "./points-against-per-game/@points");
        NBADivisionStandingStatPath.put("divisionGameBack", "./games-back/@number");
        NBADivisionStandingStatPath.put("conferenceGameBack", "./conference-games-back/@games");
        NBADivisionStandingStatPath.put("winPic", "./winning-percentage/@percentage");
        NBADivisionStandingStatPath.put("homeWin", "./win-loss-record[@type='Home']/@wins");
        NBADivisionStandingStatPath.put("homeLoss", "./win-loss-record[@type='Home']/@losses");
        NBADivisionStandingStatPath.put("AwayWin", "./win-loss-record[@type='Away']/@wins");
        NBADivisionStandingStatPath.put("AwayLoss", "./win-loss-record[@type='Away']/@losses");
        NBADivisionStandingStatPath.put("conferenceWin", "./win-loss-record[@type='Conference']/@wins");
        NBADivisionStandingStatPath.put("conferenceLoss", "./win-loss-record[@type='Conference']/@losses");
        NBADivisionStandingStatPath.put("divisionWin", "./win-loss-record[@type='Division']/@wins");
        NBADivisionStandingStatPath.put("divisionLoss", "./win-loss-record[@type='Division']/@losses");
        NBADivisionStandingStatPath.put("streakWin", "./streak[@kind='Winning']/@games");
        NBADivisionStandingStatPath.put("streakLoss", "./streak[@kind='Losing']/@games");
        NBADivisionStandingStatPath.put("gt10Win", "./win-loss-record[@type='Games Decided by >10']/@wins");
        NBADivisionStandingStatPath.put("gt10Loss", "./win-loss-record[@type='Games Decided by >10']/@losses");
        NBADivisionStandingStatPath.put("lt3Win", "./win-loss-record[@type='Games Decided by <3']/@wins");
        NBADivisionStandingStatPath.put("lt3Loss", "./win-loss-record[@type='Games Decided by <3']/@losses");
        NBADivisionStandingStatPath.put("ScoreGt100Win", "./win-loss-record[@type='Games Scoring 100 or More']/@wins");
        NBADivisionStandingStatPath.put("ScoreGt100Loss", "./win-loss-record[@type='Games Scoring 100 or More']/@losses");
        NBADivisionStandingStatPath.put("Scorelt100Win", "./win-loss-record[@type='Games Scoring <100']/@wins");
        NBADivisionStandingStatPath.put("Scorelt100Loss", "./win-loss-record[@type='Games Scoring <100']/@losses");
        NBADivisionStandingStatPath.put("overTimeWin", "./win-loss-record[@type='Overtime Games']/@wins");
        NBADivisionStandingStatPath.put("overTimeLoss", "./win-loss-record[@type='Overtime Games']/@losses");
        NBADivisionStandingStatPath.put("last10Win", "./win-loss-record[@type='Last 10']/@wins");
        NBADivisionStandingStatPath.put("last10Loss", "./win-loss-record[@type='Last 10']/@losses");
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
        //球员本赛季最佳
        NBAPlayerTopStatPath.put("points", "./high-game-points/@high-game-points");
        //球员榜单技术统计项
        NBAPlayerStandingStatPath.put("stats", "./stat/@stat");
        //NBA 球员赛季技术统计汇总
        NBAPlayerTotalStatPath.put("games", "./games/@games");
        NBAPlayerTotalStatPath.put("games_started", "./games-started/@games-started");
        NBAPlayerTotalStatPath.put("minute", "./minutes/@minutes");
        NBAPlayerTotalStatPath.put("points", "./points/@points");
        NBAPlayerTotalStatPath.put("fieldgoals_made", "./field-goals/@made");
        NBAPlayerTotalStatPath.put("fieldgoals_attempted", "./field-goals/@attempted");
        NBAPlayerTotalStatPath.put("fieldgoal_percentage", "./field-goals/@percentage");
        NBAPlayerTotalStatPath.put("threepoint_made", "./three-point-field-goals/@made");
        NBAPlayerTotalStatPath.put("threepoint_attempted", "./three-point-field-goals/@attempted");
        NBAPlayerTotalStatPath.put("threepoint_percentage", "./three-point-field-goals/@percentage");
        NBAPlayerTotalStatPath.put("freethrows_made", "./free-throws/@made");
        NBAPlayerTotalStatPath.put("freethrows_attempted", "./free-throws/@attempted");
        NBAPlayerTotalStatPath.put("freethrow_percentage", "./free-throws/@percentage");
        NBAPlayerTotalStatPath.put("offensive_rebounds", "./rebounds/@offensive");
        NBAPlayerTotalStatPath.put("defensive_rebounds", "./rebounds/@defensive");
        NBAPlayerTotalStatPath.put("rebounds", "./rebounds/@total");
        NBAPlayerTotalStatPath.put("assists", "./assists/@assists");
        NBAPlayerTotalStatPath.put("personalfouls", "./personal-fouls/@fouls");
        NBAPlayerTotalStatPath.put("steals", "./steals/@steals");
        NBAPlayerTotalStatPath.put("turnovers", "./turnovers/@turnovers");
        NBAPlayerTotalStatPath.put("blockedshots", "./blocked-shots/@blocked-shots ");
        //NBA 球员赛季技术统计均值
        NBAPlayerAVGStatPath.put("games", "./games/@games");
        NBAPlayerAVGStatPath.put("games_started", "./games-started/@games-started");
        NBAPlayerAVGStatPath.put("minute", "./minutes/@minutes");
        NBAPlayerAVGStatPath.put("points", "./points/@average");
        NBAPlayerAVGStatPath.put("fieldgoals_made", "./field-goals/@made");
        NBAPlayerAVGStatPath.put("fieldgoals_attempted", "./field-goals/@attempted");
        NBAPlayerAVGStatPath.put("fieldgoal_percentage", "./field-goals/@percentage");
        NBAPlayerAVGStatPath.put("threepoint_made", "./three-point-field-goals/@made");
        NBAPlayerAVGStatPath.put("threepoint_attempted", "./three-point-field-goals/@attempted");
        NBAPlayerAVGStatPath.put("threepoint_percentage", "./three-point-field-goals/@percentage");
        NBAPlayerAVGStatPath.put("freethrows_made", "./free-throws/@made");
        NBAPlayerAVGStatPath.put("freethrows_attempted", "./free-throws/@attempted");
        NBAPlayerAVGStatPath.put("freethrow_percentage", "./free-throws/@percentage");
        NBAPlayerAVGStatPath.put("offensive_rebounds", "./rebounds/@offensive-average");
        NBAPlayerAVGStatPath.put("defensive_rebounds", "./rebounds/@defensive-average");
        NBAPlayerAVGStatPath.put("rebounds", "./rebounds/@total-average");
        NBAPlayerAVGStatPath.put("assists", "./assists/@average");
        NBAPlayerAVGStatPath.put("personalfouls", "./personal-fouls/@personal-fouls-average");
        NBAPlayerAVGStatPath.put("steals", "./steals/@average");
        NBAPlayerAVGStatPath.put("turnovers", "./turnovers/@average");
        NBAPlayerAVGStatPath.put("blockedshots", "./blocked-shots/@average");
        //NBA 球队赛季技术统计汇总
        NBATeamTotalStatPath.put("win_games", "./record/@wins");
        NBATeamTotalStatPath.put("loss_games", "./record/@losses");
        NBATeamTotalStatPath.put("fieldgoal_attempted", "./field-goals/@attempted");
        NBATeamTotalStatPath.put("fieldgoal_made", "./field-goals/@made");
        NBATeamTotalStatPath.put("fieldgoal_percentage", "./field-goals/@percentage");
        NBATeamTotalStatPath.put("threepoint_made", "./three-point-field-goals/@made");
        NBATeamTotalStatPath.put("threepoint_attempted", "./three-point-field-goals/@attempted");
        NBATeamTotalStatPath.put("threepoint_percentage", "./three-point-field-goals/@percentage");
        NBATeamTotalStatPath.put("freethrows_made", "./free-throws/@made");
        NBATeamTotalStatPath.put("freethrows_attempted", "./free-throws/@attempted");
        NBATeamTotalStatPath.put("freethrow_percentage", "./free-throws/@percentage");
        NBATeamTotalStatPath.put("points", "./points/@points");
        NBATeamTotalStatPath.put("rebounds", "./rebounds/@total");
        NBATeamTotalStatPath.put("assists", "./assists/@assists");
        NBATeamTotalStatPath.put("personalfouls", "./personal-fouls/@fouls");
        NBATeamTotalStatPath.put("steals", "./steals/@steals");
        NBATeamTotalStatPath.put("turnovers", "./turnovers/@turnovers");
        NBATeamTotalStatPath.put("blockedshots", "./blocked-shots/@blocked-shots ");
        //NBA 球队赛季技术统计均值
        NBATeamAVGStatPath.put("win_games", "./record/@wins");
        NBATeamAVGStatPath.put("loss_games", "./record/@losses");
        NBATeamAVGStatPath.put("fieldgoal_made", "./field-goals/@made");
        NBATeamAVGStatPath.put("fieldgoal_attempted", "./field-goals/@attempted");
        NBATeamAVGStatPath.put("fieldgoal_percentage", "./field-goals/@percentage");
        NBATeamAVGStatPath.put("threepoint_made", "./three-point-field-goals/@made");
        NBATeamAVGStatPath.put("threepoint_attempted", "./three-point-field-goals/@attempted");
        NBATeamAVGStatPath.put("threepoint_percentage", "./three-point-field-goals/@percentage");
        NBATeamAVGStatPath.put("freethrows_made", "./free-throws/@made");
        NBATeamAVGStatPath.put("freethrows_attempted", "./free-throws/@attempted");
        NBATeamAVGStatPath.put("freethrow_percentage", "./free-throws/@percentage");
        NBATeamAVGStatPath.put("points", "./points/@points-average");
        NBATeamAVGStatPath.put("rebounds", "./rebounds/@total-average");
        NBATeamAVGStatPath.put("assists", "./assists/@average");
        NBATeamAVGStatPath.put("personalfouls", "./personal-fouls/@personal-foul-average");
        NBATeamAVGStatPath.put("steals", "./steals/@average");
        NBATeamAVGStatPath.put("turnovers", "./turnovers/@turnover-average");
        NBATeamAVGStatPath.put("blockedshots", "./blocked-shots/@average ");

    }
}
