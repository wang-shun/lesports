package com.lesports.sms.data.model.sportrard;


import com.google.common.collect.Maps;
import com.lesports.utils.LeProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/04/15.
 */
public class SportrardConstants {

    public static String partnerSourceId = LeProperties.getString("partner.source.id");
    public static String sportradarRootPath = LeProperties.getString("sportradar.root.Path");
    public static String sportradarDownloadPath = LeProperties.getString("sportradar.download.Path");
    //pac12
    public static String pac12DownloadUrl = "";
    public static String pacBasketballId = "";
    //stats data download url
    public static String statsDownloadUrl = "http://downloads.stats.com/beitai-LeTV/";//LeProperties.getString("stats.download.url");
    public static String localDownloadPath = "/letv/third_part_files/stats/";//LeProperties.getString("nba.stats.file.path");
    public static String statsUserName = "beitai-LeTV";//LeProperties.getString("stats.http.userName");
    public static String statsPassWord = "letv2015";//LeProperties.getString("stats.http.password");
    //local ftp to get stats nba data
    public static String LocalFtpHost = "220.181.153.69"; //LeProperties.getString("local.ftp.host");
    public static int LocalFtpPort = 60000;// LeProperties.getInt("local.ftp.port", 0);
    public static String LocalFtpUserName = "Sport";//LeProperties.getString("local.ftp.userName");
    public static String LocalFtpPassword = "7gLIbjs5{";//LeProperties.getString("local.ftp.password");
    //sportrard data
    public static String srHost = LeProperties.getString("sportradar.ftp.host");
    public static int srPort = LeProperties.getInt("sportradar.ftp.port", 0);
    public static String srUserName = LeProperties.getString("sportradar.ftp.userName");
    public static String srPassword = LeProperties.getString("sportradar.ftp.password");
    //    public static final Map<String, Integer> gameTypeMap = new HashMap<String, Integer>(); //大项小项map
    public static final Map<String, Integer> f1GameTypeMap = new HashMap<String, Integer>(); //大项小项map
    public static final Map<String, String> sportsTypeMap = new HashMap<String, String>(); //运动类型
    public static final Map<String, Long> stageMap = new HashMap<String, Long>();//f1jieduan
    public static final Map<String, Long> substationMap = new HashMap<String, Long>(); //f1站点
    public static final Map<String, Long> nameMap = new HashMap<String, Long>(); //soccer
    public static final Map<String, String> scorrerStatPath = new HashMap<String, String>(); //soccer
    public static final Map<String, String> f1StatPath = new HashMap<String, String>(); //f1技术统计映射表
    public static final Map<String, String> FBTeamStandingStatPath = new HashMap<String, String>(); //soccer 榜单映射表
    public static final Map<String, String> BKTeamStandingStatPath = new HashMap<String, String>(); //篮球 榜单映射表
    public static final Map<String, String> F1StandingStatPath = new HashMap<String, String>(); //F1车手榜单映射表
    public static final Map<String, String> AssistPlayerStandingStatPath = new HashMap<String, String>(); //soccer 榜单映射表
    public static final Map<String, String> ScorePlayerStandingStatPath = new HashMap<String, String>();
    public static final Map<String, Long> f1NameMap = new HashMap<String, Long>(); //F1
    public static final Map<String, String> nbaNameMap = new HashMap<String, String>(); //NBA
    public static final Map<String, String> positionMap = new HashMap<String, String>(); //足球位置
    public static final Map<String, Long> goalType = new HashMap<String, Long>(); //篮球位置
    public static final Map<String, Long> sodaGoalType = new HashMap<String, Long>(); //soda进球类型


    public static final Map<String, String> basketBallPositionMap = new HashMap<String, String>(); //篮球位置
    public static final Map<String, String> basketBallPositionMap2 = new HashMap<String, String>(); //篮球位置
    public static final Map<String, String> groupsMap = new HashMap<String, String>();    //小组赛分组对应

    public static final Map<String, String> tennisRoundMap = new HashMap<String, String>();    //小组赛分组对应

    public static final Map<String, String> nbaGameType = new HashMap<String, String>();    //nba game type starics

    public static final Map<String, Long> nbaConferenceId = new HashMap<String, Long>();    //nba game type starics
    public static final Map<String, Long> nbaDivisionId = new HashMap<String, Long>();//分区对应关系

    public static final Map<String, Long> rankingTypeId = new HashMap<String, Long>();//榜单对应关系

    public static final Map<String, Long> TeamrankingType = new HashMap<String, Long>();//榜单对应关系
    public static final List<String> nbaTeamRankingFiles = new ArrayList<String>();//榜单文件列表
    public static final List<String> nbaScheduleFiles = new ArrayList<String>();//赛程文件列表
    public static final String nbaPlayerRankingFiles = "NBA_LEADERS.XML";//赛程文件列表
    public static final String nbaPlayersFiles = "NBA_ALL_ROSTER.XML";//赛程文件列表
    public static String nbaFtpHost = "chinastatsftp.nba.com";

    public static int MAX_TRY_COUNT = 3;

    public static int nbaFtpPort = 21;
    public static Integer nbaPartnerSourceId = 56;
    public static Integer PartnerSourceStaticId = 499;

    public static String nbaFtpUserName = "letv";

    public static String nbaFtpPassword = "R@ngEr6";

    public static Integer NBAPartnerSource = 37;

    public static Integer SODAOartnerSource = 40;

    public static Map<String, Long> sodaSMSCompetittionMap = Maps.newHashMap();//soda和sms赛事对应关系


    public static String F1Postion = "52";

    public static String F1Points = "11";

    public static String Formula1 = "36";

    public static String tennis = "5";

    public static String SOCCERID = "1";
    public static String BASKETBALLID = "2";
    public static String BASEBALL = "3";
    public static String AMERICAFOOTBALLID = "16";
    public static Long regularSeasonId = 100084000L;
    public static Long POSITION = 100008000L;

    private static String file1 = LeProperties.getString("liveScore.files.delta");
    private static String file2 = LeProperties.getString("england.team.arr");
    private static String file3 = LeProperties.getString("spain.team.arr");
    private static String file4 = LeProperties.getString("france.team.arr");
    private static String file5 = LeProperties.getString("germany.team.arr");
    private static String file6 = LeProperties.getString("italy.team.arr");
    private static String file7 = LeProperties.getString("japan.team.arr");
    private static String file8 = LeProperties.getString("usa.team.arr");
    private static String file9 = LeProperties.getString("copaLibertadores.team.arr");
    private static String file10 = LeProperties.getString("matchFb.details.files.1415");
    private static String file11 = LeProperties.getString("matchFb.files.1415");
    private static String file12 = LeProperties.getString("teamRankFb.files.1415");
    private static String file13 = LeProperties.getString("playerFandFb.files.1415");
    private static String file14 = LeProperties.getString("playerRankAssistsFb.files.1415");
    private static String file15 = LeProperties.getString("cupFb.files.1415");
    public static String filebak = null;

    static {
        nbaScheduleFiles.add("NBA_SCHEDULE_PRE.XML");
        nbaScheduleFiles.add("NBA_SCHEDULE.XML");
        nbaTeamRankingFiles.add("NBA_TEAM_STANDINGS_PRE.XML");
        nbaTeamRankingFiles.add("NBA_TEAM_STANDINGS.XML");
        //f1
        f1StatPath.put("points", "./TeamValue[@typeId='11']/@value");
        f1StatPath.put("position", "./TeamValue[@typeId='52']/@value");
        f1StatPath.put("fastestLapsTime", "./TeamValue[@typeId='22']/@value");
        f1StatPath.put("totalTime", "./TeamValue[@typeId='71']/@value");
        //f1TeamStandign
        F1StandingStatPath.put("carNumber", "./TeamValue[@typeId='51']/@value");
        F1StandingStatPath.put("fastestLapsTime", "./TeamValue[@typeId='58']/@value");
        F1StandingStatPath.put("perfectRaces", "./TeamValue[@typeId='59']/@value");
        F1StandingStatPath.put("podiums", "./TeamValue[@typeId='57']/@value");
        F1StandingStatPath.put("points", "./TeamValue[@typeId='11']/@value");
        F1StandingStatPath.put("polePositions", "./TeamValue[@typeId='56']/@value");
        F1StandingStatPath.put("racesStarted", "./TeamValue[@typeId='54']/@value");
        F1StandingStatPath.put("racesWithPoint", "./TeamValue[@typeId='55']/@value");
        F1StandingStatPath.put("victories", "./TeamValue[@typeId='53']/@value");
        F1StandingStatPath.put("position", "./TeamValue[@typeId='52']/@value");


        //
        scorrerStatPath.put("ballPossession", "./BallPossession/Team?/text()");
        scorrerStatPath.put("shotsOnGoal", "./ShotsOnGoal/Team?/text()");
        scorrerStatPath.put("shotsOffGoal", "./ShotOffGoal/Team?/text()");
        scorrerStatPath.put("freeKicks", "./FreeKicks/Team?/text()");
        scorrerStatPath.put("cornerKicks", "./CornerKicks/Team?/text()");
        scorrerStatPath.put("offsides", "./Offsides/Team?/text()");
        scorrerStatPath.put("throwIns", "./ThrowIns/Team?/text()");
        scorrerStatPath.put("goalKeeperSaves", "./GoalKeeperSaves/Team?/text()");
        scorrerStatPath.put("goalKicks", "./GoalKicks/Team?/text()");
        scorrerStatPath.put("foul", "./Foul/Team?/text()");
        //path end
        FBTeamStandingStatPath.put("matchesTotal", "matchNumbe");
        FBTeamStandingStatPath.put("pointsTotal", "teamScore");
        FBTeamStandingStatPath.put("winTotal", "successMatch");
        FBTeamStandingStatPath.put("lossTotal", "faildMatch");
        FBTeamStandingStatPath.put("drawTotal", "flatMatch");
        FBTeamStandingStatPath.put("goalsForTotal", "goal");
        FBTeamStandingStatPath.put("goalsAgainstTotal", "fumble");
        //
        AssistPlayerStandingStatPath.put("25", "assists");
        //
        ScorePlayerStandingStatPath.put("2", "goals");
        ScorePlayerStandingStatPath.put("36", "penaltyNumber");

//        gameTypeMap.put("Soccer", 4);
//        gameTypeMap.put("Basketball", 3);
//        gameTypeMap.put("Tennis", 5);
//        gameTypeMap.put("17", 13);//英超
//        gameTypeMap.put("35", 9);//德甲
//        gameTypeMap.put("8", 11);//西甲
//        gameTypeMap.put("34", 10);//法甲
//        gameTypeMap.put("23", 12);//意甲
//        gameTypeMap.put("649", 14);//中超
//		gameTypeMap.put("196", 716);//j联赛
//		gameTypeMap.put("410", 717);//k联赛
//		gameTypeMap.put("242", 718);//美国大联盟
//		gameTypeMap.put("384", 710);//南美解放者杯
//        gameTypeMap.put("133", 762);//美洲杯
//        gameTypeMap.put("290", 788);//女足世界杯
//        gameTypeMap.put("782", 712);//中甲
//        gameTypeMap.put("453", 786);//世青赛
//        gameTypeMap.put("40499",465058);//ATP巴塞罗那站
//        gameTypeMap.put("140",706);//金杯赛
//        gameTypeMap.put("1337",769);//国际冠军杯
//        gameTypeMap.put("39",465143);//丹超
//        gameTypeMap.put("40",465140);//瑞超
//        gameTypeMap.put("36",465120);//苏超
//        gameTypeMap.put("332",465123);//苏格兰联赛杯
//        gameTypeMap.put("38",465126);//比利时甲级联赛
//        gameTypeMap.put("218",465146);//乌克兰超级联赛

        sportsTypeMap.put("1", "足球");
        sportsTypeMap.put("2", "篮球");
        sportsTypeMap.put("3", "棒球");
        sportsTypeMap.put("16", "橄榄球");

        f1GameTypeMap.put("11", 59);//赛车
        f1GameTypeMap.put("36", 60);//F1

        nbaGameType.put("1", "常规赛");//nba 常规赛
        nbaGameType.put("2", "季后赛");//nba 常规赛
        nbaGameType.put("3", "季前赛");//nba 常规赛

        nbaConferenceId.put("Eastern", 100130000L);
        nbaConferenceId.put("Western", 100131000L);
        nbaDivisionId.put("Pacific", 100137000L);

        rankingTypeId.put("ReboundsperGame", 104662000L);//篮板榜
        rankingTypeId.put("PointsperGame", 104661000L);//得分榜
        rankingTypeId.put("AssistsperGame", 104666000L);//助攻榜
        rankingTypeId.put("StealsperGame", 104663000L);//抢断榜
        rankingTypeId.put("BlockedShotsperGame", 104664000L);//盖帽榜

        TeamrankingType.put("Eastern", 100205000L);
        TeamrankingType.put("Western", 100204000L);
        TeamrankingType.put("Pacific", 104651000L);
        TeamrankingType.put("Southwest", 104652000L);
        TeamrankingType.put("Northwest", 104653000L);
        TeamrankingType.put("Atlantic", 104648000L);
        TeamrankingType.put("Central", 104649000L);
        TeamrankingType.put("Southeast", 104650000L);
//        gameTypeMap.put("3",20);//ATP
//        gameTypeMap.put("6",21);//WTA


        f1NameMap.put("36", 50001L);
        nbaNameMap.put("7", "NBA");

        positionMap.put("G", "门将");
        positionMap.put("D", "后卫");
        positionMap.put("M", "中场");
        positionMap.put("F", "前锋");

        goalType.put("penalty", 100156000L);
        goalType.put("heading", 104660000L);
        goalType.put("owngoal", 104658000L);


        sodaGoalType.put("点球", 100156000L);
        sodaGoalType.put("头球", 104660000L);
        sodaGoalType.put("乌龙球", 104658000L);

        basketBallPositionMap.put("G", "后卫");
        basketBallPositionMap.put("C", "中锋");
        basketBallPositionMap.put("F", "前锋");
        basketBallPositionMap.put("SG", "后卫");
        basketBallPositionMap.put("G/F", "前锋/后卫");
        basketBallPositionMap.put("F/C", "前锋/中锋");
        basketBallPositionMap.put("PF", "前锋");
        basketBallPositionMap.put("SF", "前锋");
        basketBallPositionMap.put("PG", "后卫");

        basketBallPositionMap.put("F-G", "");
        basketBallPositionMap.put("F-C", "前锋/中锋");
        basketBallPositionMap.put("C-F", "中锋/前锋");

        basketBallPositionMap2.put("G", "后卫(G)");
        basketBallPositionMap2.put("C", "中锋(C)");
        basketBallPositionMap2.put("F", "前锋(F)");
        basketBallPositionMap2.put("F-G", "前锋/后卫(F-G)");
        basketBallPositionMap2.put("F-C", "前锋/中锋(F-C)");
        basketBallPositionMap2.put("C-F", "中锋/前锋(C-F)");


        groupsMap.put("1", "^A组$");
        groupsMap.put("2", "^B组$");
        groupsMap.put("3", "^C组$");
        groupsMap.put("4", "^D组$");
        groupsMap.put("5", "^E组$");
        groupsMap.put("6", "^F组$");
        groupsMap.put("7", "^G组$");
        groupsMap.put("8", "^H组$");
        groupsMap.put("A", "^A组$");
        groupsMap.put("B", "^B组$");
        groupsMap.put("C", "^C组$");
        groupsMap.put("D", "^D组$");
        groupsMap.put("E", "^E组$");
        groupsMap.put("F", "^F组$");
        groupsMap.put("G", "^G组$");
        groupsMap.put("H", "^H组$");
        groupsMap.put("I", "^I组$");
        groupsMap.put("J", "^J组$");
        groupsMap.put("K", "^K组$");
        groupsMap.put("L", "^L组$");

        groupsMap.put("Qualification round 1", "^资格赛1$");
        groupsMap.put("Qualification round 2", "^资格赛2$");
        groupsMap.put("Qualification round 3", "^资格赛3$");
        groupsMap.put("Qualification round 4", "^资格赛4$");
        groupsMap.put("Round 1", "^第1轮$");
        groupsMap.put("Round 2", "^第2轮$");
        groupsMap.put("Round 3", "^第3轮$");
        groupsMap.put("Round 4", "^第4轮$");
        groupsMap.put("Round 5", "^第5轮$");
        groupsMap.put("1/32", "^1/32决赛$");
        groupsMap.put("1/16", "^1/16决赛$");
        groupsMap.put("1/8", "^1/8决赛$");
        groupsMap.put("Final", "^决赛$");
        groupsMap.put("3rd Place Final", "^三四名决赛$");
        groupsMap.put("Semifinals", "^半决赛$");
        groupsMap.put("Quarterfinals", "^1/4决赛$");
        //f1jieduan
        stageMap.put("Practice 1",100086000L );
        stageMap.put("Practice 2",100086000L );
        stageMap.put("Practice 3",100086000L );
        stageMap.put("Qualification",100087000L );
        stageMap.put("Race",100075000L);
        //substation Map
        substationMap.put("243238_AustralianGrandPrix", 100089000L);//澳大利亚
        substationMap.put("243458_BahrainGrandPrix", 100091000L);//巴林
        substationMap.put("243678_ChineseGrandPrix", 100107000L);//中国
        substationMap.put("243898_RussianGrandPrix", 104687000L);//俄罗斯
        substationMap.put("244118_GranPremiodeEspana", 100093000L);//西班牙
        substationMap.put("244338_GrandPrixdMonaco", 100094000L);//摩纳哥
        substationMap.put("244558_GrandPrixduCanada", 100096000L);//加拿大
        substationMap.put("244778_GrandPrixofEurope", 100095000L);//欧洲站
        substationMap.put("244998_Grosser Preis von Osterreich", 104688000L);//奥地利
        substationMap.put("245218_BritishGrandPrix", 100099000L);//英国
        substationMap.put("245438_MagyarNagydijGrandPrix", 100101000L);// 匈牙利
        substationMap.put("245658_GermanGrandPrix", 100100000L);//德国
        substationMap.put("245878_BelgianGrandPrix", 100104000L); //比利时
        substationMap.put("247198_GranPremiodeMexico ", 104690000L);// 墨西哥
        substationMap.put("246318_SingaporeGrandPrix", 104689000L);//新加坡
        substationMap.put("246538_MalaysianGrandPrix", 100090000L);//马来西亚
        substationMap.put("246758_JapaneseGrandPrix", 100106000L);// 日本
        substationMap.put("246978_UnitedStatesGrandPrix", 100097000L);//美国
        substationMap.put("247418_GrandePremiodoBrasil", 100105000L);//巴西
        substationMap.put("247638_AbuDhabiGrandPrix", 104691000L);// 阿布扎比
        substationMap.put("246098_GranPremioDItalia", 100103000L);// 意大利
        substationMap.put("", 100092000L);//圣马力诺
        substationMap.put("", 100098000L);//发国
        substationMap.put("", 100102000L);//土耳其


        tennisRoundMap.put("1", "ext_match_wq_draw_1");
        tennisRoundMap.put("2", "ext_match_wq_draw_2");
        tennisRoundMap.put("3", "ext_match_wq_draw_3");
        tennisRoundMap.put("25", "ext_match_wq_1_16");
        tennisRoundMap.put("26", "ext_match_wq_1_8");
        tennisRoundMap.put("27", "ext_match_wq_1_4");
        tennisRoundMap.put("28", "ext_match_wq_1_2");
        tennisRoundMap.put("29", "ext_match_wq_final");

        filebak = file1 + "|" + file2 + "|" + file3 + "|" + file4 + "|" + file5 + "|" + file6 + "|" + file7 + "|" + file8 + "|" + file9 + "|" + file10 + "|" + file11 + "|" + file12 + "|" + file13 + "|" + file14 + "|" + file15;

        sodaSMSCompetittionMap.put("282", 47001L);

    }

    /**
     * 赛事类型
     */
    public static final Integer GAME_F_TYPE_LQ = 3;
    public static final Integer GAME_F_TYPE_ZQ = 4;
    public static final Integer GAME_F_TYPE_WQ = 5;
    public static final Integer GAME_F_TYPE_GEF = 6;
    public static final Integer GAME_F_TYPE_SC = 59;
    public static final Integer GAME_F_TYPE_ZH = 650;

}
