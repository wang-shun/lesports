package com.lesports.sms.data.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/8/17.
 */
public class SodaConstants {


    public static final Long soccerID = 100015000L;
    public static final Map<String, Long> cidMap = new HashMap<String, Long>(); //soda技术统计映射表
    public static final Map<String, String> sodaPlayerStatPath = new HashMap<String, String>(); //soda技术统计映射表
    public static final Map<String, String> sodaPlayerCareerStatPath = new HashMap<String, String>(); //soda技术统计映射Strin表
    public static final Map<String, String> sodaPlayerGoalRankingStatPath = new HashMap<>();//soda 球员积分榜技术统计
    public static final Map<String, String> sodaPlayerAssistRankingStatPath = new HashMap<>();//soda球员射手榜
    public static final Map<String, String> sodaTeamStatPath = new HashMap<String, String>(); //soda球队赛季统计映射表
    public static final Map<String, String> sodaTeamAvgStatPath = new HashMap<String, String>(); //soda球队赛季统计映射表
    public static final Map<String, String> sodaTeamStandingStatPath = new HashMap<>();//soda球队积分榜
    public static final Map<String, String> sodaLiveTeamStatPath = new HashMap<>();//soda单场球队技术统计项
    public static final Map<String, String> sodaLivePlayerStatPath = new HashMap<>();//soda单场球员技术统计项


    static {
        cidMap.put("9", 100152001L);//欧冠
        cidMap.put("282", 47001L);//中超
        cidMap.put("412-2", 309001L);//40强
        cidMap.put("412", 101425001L);//12强
        cidMap.put("262", 100322001L);//友谊赛
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

        sodaTeamStatPath.put("total", "./@matchs");
        sodaTeamStatPath.put("win", "./@win");
        sodaTeamStatPath.put("draw", "./@draw");
        sodaTeamStatPath.put("lose", "./@lose");
        sodaTeamStatPath.put("goal", "./@goal");
        sodaTeamStatPath.put("concede", "./@concede");
        sodaTeamStatPath.put("goaldiffer", "./@draw");
        sodaTeamStatPath.put("points", "./@lose");
        sodaTeamStatPath.put("avgGoal", "./@avgGoal");
        sodaTeamStatPath.put("avgConcede", "./@avgConc");
        sodaTeamStatPath.put("avgPoints", "./@lose");
        //doda 球队排行榜
        sodaTeamStandingStatPath.put("matchNumber", "./@matches");
        sodaTeamStandingStatPath.put("teamScore", "./@score");
        sodaTeamStandingStatPath.put("winMatch", "./@win");
        sodaTeamStandingStatPath.put("lossMatch", "./@lose");
        sodaTeamStandingStatPath.put("flatMatch", "./@draw");
        sodaTeamStandingStatPath.put("goals", "./@goal");
        sodaTeamStandingStatPath.put("fumble", "./@concede");
        sodaTeamStandingStatPath.put("goalDiffer", "./@goaldiffer");


        //jishutongji
        sodaPlayerStatPath.put("goal", "./@goal");
        sodaPlayerStatPath.put("matchs", "./@matches");
        sodaPlayerStatPath.put("red", "./@red");
        sodaPlayerStatPath.put("yellow", "./@yellow");
        sodaPlayerStatPath.put("foul", "./@foul");
        sodaPlayerStatPath.put("clearance", "./@clearance");
        sodaPlayerStatPath.put("keypass", "./@keypass");
        //total stats
        sodaPlayerCareerStatPath.put("date", "./@date");
        sodaPlayerCareerStatPath.put("goal", "./@goal");
        sodaPlayerCareerStatPath.put("matchs", "./@matchs");
        sodaPlayerCareerStatPath.put("assist", "./@assist");
        sodaPlayerCareerStatPath.put("shoot", "./@shoot");
        sodaPlayerCareerStatPath.put("save", "./@save");
        //得分榜
        sodaPlayerGoalRankingStatPath.put("goal", "./@goal");
        sodaPlayerGoalRankingStatPath.put("penaltyNumber", "./@penalty");
        sodaPlayerGoalRankingStatPath.put("caps", "./@lineup");
        //soda 助攻榜
        sodaPlayerAssistRankingStatPath.put("assists", "./@assist");
        sodaPlayerAssistRankingStatPath.put("caps", "./@lineup");
        //单场直播技术统计
        sodaLiveTeamStatPath.put("ballPossession", "./BallPossession/Team?/text()");
        sodaLiveTeamStatPath.put("shotOnGoal", "./Stat[@type='shot_on_trger']/text()");
        sodaLiveTeamStatPath.put("shotOffGoal", "./Stat[@type='shot_off_target']/text()");
        sodaLiveTeamStatPath.put("freeKicks", "./Stat[@type='kick']/text()");
        sodaLiveTeamStatPath.put("cornerKicks", "./Stat[@type='corner']/text()");
        sodaLiveTeamStatPath.put("offsides", "./Stat[@type='offside']/text()");
        sodaLiveTeamStatPath.put("throwIns", "./Stat[@type='throw']/text()");
        sodaLiveTeamStatPath.put("goalkeeperSaves", "./Stat[@type='shot_blocked']/text()");
        sodaLiveTeamStatPath.put("goalKicks", "./Stat[@type='goal_kick']/text()");
        sodaLiveTeamStatPath.put("fouls", "./Stat[@type='foul']/text()");
        sodaLiveTeamStatPath.put("yellow", "./Stat[@type='yellow_card']/text()");
        sodaLiveTeamStatPath.put("red", "./Stat[@type='red_card']/text()");
    }
}
