package com.lesports.sms.data.model;


import com.google.common.collect.Maps;
import com.lesports.utils.LeProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lufei1 on 2014/11/25.
 */
public class Constants {

    public static String partnerSourceId = LeProperties.getString("partner.source.id");
    public static String sportradarRootPath = LeProperties.getString("sportradar.root.Path");
    public static String sportradarDownloadPath = LeProperties.getString("sportradar.download.Path");
    //Top12
    public static Long CHINASUPERLIGA_COMPETITION_ID = 101425001L;
    public static Long TOP12_COMPETITION_ID = 101425001L;
    public static Long TOP12_COMPETITION_SEASON_ID = 101543002L;
    public static final Map<String, String> sodaLiveSquadFilePath = new HashMap<String, String>(); //soda 不同赛事阵容文件路径
    public static final Map<String, String> sodaLiveEventFilePath = new HashMap<String, String>(); //soda 不同赛事阵容文件路径

    public static String SODA_LIVE_PATH = "/letv/third_part_files/soda/412/live/";
    public static String SODA_PLAYER_PATH = "/letv/third_part_files/soda/412/player/";
    public static String SODA_TEAM_PATH = "/letv/third_part_files/soda/412/team/";
    public static String SODA_PLAYER_RANKING_PATH = "/letv/third_part_files/soda/412/ranking/";
    public static String SODA_SCHEDULE_PATH = "/letv/third_part_files/soda/412/fixtures/";

    public static String SODA_LEAGUE_TEAM_PATH = "/letv/third_part_files/soda/s%/team/";
    public static String SODA_UPLOAD_TEAM_PATH = "//soda//s%//team//";

    //pac12
    public static String pac12DownloadUrl = "";
    public static String pacBasketballId = "";
    //stats data download url
    public static String statsDownloadUrl = "http://downloads.stats.com/beitai-LeTV/";//LeProperties.getString("stats.download.url");
    public static String localDownloadPath = "/letv/third_part_files/stats/";//LeProperties.getString("nba.stats.file.path");
    public static String statsUserName = "beitai-LeTV";//LeProperties.getString("stats.http.userName");
    public static String statsPassWord = "letv2015";//LeProperties.getString("stats.http.password");
    //local ftp to get stats nba data
    public static String LocalFtpHost = "36.110.225.187"; //LeProperties.getString("local.ftp.host");
    public static int LocalFtpPort = 65000;// LeProperties.getInt("local.ftp.port", 0);
    public static String LocalFtpUserName = "Sport";//LeProperties.getString("local.ftp.userName");
    public static String LocalFtpPassword = "7gLIbjs5{";//LeProperties.getString("local.ftp.password");
    //sportrard data
//    public static String srHost = "10.120.56.176";
//    public static int srPort = 65000;
//    public static String srUserName = "Sport";
//    public static String srPassword = "40a38cb71";
    public static String srHost = LeProperties.getString("sportradar.ftp.host");
    public static int srPort = LeProperties.getInt("sportradar.ftp.port", 0);
    public static String srUserName = LeProperties.getString("sportradar.ftp.userName");
    public static String srPassword = LeProperties.getString("sportradar.ftp.password");
    //    public static final Map<String, Integer> gameTypeMap = new HashMap<String, Integer>(); //大项小项map
    public static final Map<String, Integer> f1GameTypeMap = new HashMap<String, Integer>(); //大项小项map
    public static final Map<String, String> sportsTypeMap = new HashMap<String, String>(); //运动类型
    public static final Map<String, String> nameMap = new HashMap<String, String>(); //soccer
    public static final Map<Long, String> seasonSquadFileMap = new HashMap<Long, String>(); //soccer
    public static final Map<String, String> f1NameMap = new HashMap<String, String>(); //F1
    public static final Map<String, Long> statsNameMap = new HashMap<String, Long>(); //NBA
    public static final Map<String, String> positionMap = new HashMap<String, String>(); //足球位置
    public static final Map<String, Long> goalType = new HashMap<String, Long>(); //篮球位置
    public static final Map<String, Long> sodaGoalType = new HashMap<String, Long>(); //soda进球类型

    public static final Map<String, String> basketBallPositionMap = new HashMap<String, String>(); //篮球位置
    public static final Map<String, String> basketBallPositionMap2 = new HashMap<String, String>(); //篮球位置
    public static final Map<String, String> groupsMap = new HashMap<String, String>();    //小组赛分组对应

    public static final Map<String, String> tennisRoundMap = new HashMap<String, String>();    //小组赛分组对应

    public static final Map<String, String> nbaGameType = new HashMap<String, String>();    //nba game type starics
    public static final Map<String, String> euroTypeMap = new HashMap<String, String>();    //欧洲杯比赛类型
    public static final Map<String, String> euroGroupMap = new HashMap<String, String>();    //欧洲杯分组
    public static final Map<String, String> nationalityMap = new HashMap<String, String>();  //欧洲杯国家名字

    public static final Map<String, Long> nbaConferenceId = new HashMap<String, Long>();    //nba game type starics
    public static final Map<String, Long> nbaDivisionId = new HashMap<String, Long>();//分区对应关系

    public static final Map<String, Long> Top20rankingTypeId = new HashMap<String, Long>();//球员榜单对应关系
    public static final Map<String, Long> rankingTypeId = new HashMap<String, Long>();//榜单对应关系
    public static final Map<String, Long> footballRankingTypeId = new HashMap<String, Long>();//足球榜单对应关系

    public static final Map<String, Long> nbaConferenceMap = new HashMap<String, Long>();//NBA地域映射关系
    public static final List<String> statsPlayersFiles = new ArrayList<>();//赛程文件列表
    public static final Map<String, Long> TeamrankingType = new HashMap<String, Long>();//榜单对应关系
    public static final List<String> statsTeamRankingFiles = new ArrayList<String>();//榜单文件列表
    public static final List<String> statsTop20PlayerRankingFiles = new ArrayList<String>();//榜单文件列表
    public static final List<String> stasTeamStatFiles = new ArrayList<String>();//榜单文件列表
    public static final List<String> statsScheduleFiles = new ArrayList<String>();//赛程文件列表
    public static final List<String> statsPlayerStatFiles = new ArrayList<String>();
    //球员数统计文件列表
    public static final String nbaPlayerRankingFullFiles = "NBA_LEADERS_FULL.XML";//全部球员榜单文件


    public static final String euroPlayerRankingFiles = "EURO_LEADERS.XML";//球员榜单
    public static final String euroTeamRankingFiles = "EURO_TEAM_STANDINGS.XML";//球队榜单
    public static final String euroRosterFiles = "EURO_ROSTER.XML";//球队和球员
    public static final String euroScheduleFiles = "EURO_SCHEDULE.XML";//赛程

    public static String nbaFtpHost = "chinastatsftp.nba.com";

    public static int MAX_TRY_COUNT = 2;

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
    public static String FUSTAL = "29";
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
        //NBA 文件名
        statsPlayersFiles.add("NBA_ALL_ROSTER.XML");
        statsPlayersFiles.add("cba//CBACHN_ALL_ROSTER.XML");
        statsScheduleFiles.add("NBA_SCHEDULE_PRE.XML");
        statsScheduleFiles.add("NBA_SCHEDULE.XML");
        statsScheduleFiles.add("cba//CBACHN_SCHEDULE.XML");
        statsTeamRankingFiles.add("NBA_TEAM_STANDINGS.XML");
        // statsTeamRankingFiles.add("cba//CBACHN_TEAM_STANDINGS.XML");
        stasTeamStatFiles.add("NBA_TEAM_STATS.XML");
        stasTeamStatFiles.add("cba//CBACHN_TEAM_STATS.XML");
        statsTop20PlayerRankingFiles.add("NBA_LEADERS.XML");
        //  statsTop20PlayerRankingFiles.add("cba//CBACHN_LEADERS.XML");
        statsPlayerStatFiles.add("NBA_PLAYER_STATS.XML");
        statsPlayerStatFiles.add("cba//CBACHN_PLAYER_STATS.XML");
        sportsTypeMap.put("1", "足球");
        sportsTypeMap.put("29", "足球");
        sportsTypeMap.put("2", "篮球");
        sportsTypeMap.put("3", "棒球");
        sportsTypeMap.put("16", "橄榄球");

        f1GameTypeMap.put("11", 59);//赛车
        f1GameTypeMap.put("36", 60);//F1

        nbaGameType.put("1", "常规赛");//nba 常规赛
        nbaGameType.put("2", "季后赛");//nba 常规赛
        nbaGameType.put("3", "季前赛");//nba 常规赛
        nbaGameType.put("4", "全明星赛");
        nbaGameType.put("12", "1/4决赛");

        nbaConferenceId.put("Eastern", 100130000L);
        nbaConferenceId.put("Western", 100131000L);
        nbaDivisionId.put("Pacific", 100137000L);

        Top20rankingTypeId.put("ReboundsperGame", 104662000L);//篮板榜
        Top20rankingTypeId.put("PointsperGame", 104661000L);//得分榜
        Top20rankingTypeId.put("AssistsperGame", 104666000L);//助攻榜
        Top20rankingTypeId.put("StealsperGame", 104663000L);//抢断榜
        Top20rankingTypeId.put("BlockedShotsperGame", 104664000L);//盖帽榜
        Top20rankingTypeId.put("ThreePointersMadeperGame", 116903000L);//三分榜

        rankingTypeId.put("Reb/G", 116902000L);//篮板榜
        rankingTypeId.put("Pts/G", 116805000L);//得分榜
        rankingTypeId.put("Ast/G", 116703000L);//助攻榜
        rankingTypeId.put("Stl/G", 116806000L);//抢断榜
        rankingTypeId.put("Blk/G", 116807000L);//盖帽榜
        rankingTypeId.put("FG%", 116808000L);//投篮命中率
        rankingTypeId.put("3G%", 116704000L);//三分命中率

        footballRankingTypeId.put("Goals", 100160000L);//射手榜
        footballRankingTypeId.put("Assists", 100161000L);//助攻榜
        //nba 地域字典映射

        nbaConferenceMap.put("Eastern", 100130000L);
        nbaConferenceMap.put("Western", 100131000L);
        nbaConferenceMap.put("Pacific", 100137000L);
        nbaConferenceMap.put("Southwest", 100135000L);
        nbaConferenceMap.put("Northwest", 100136000L);
        nbaConferenceMap.put("Atlantic", 100133000L);
        nbaConferenceMap.put("Central", 100134000L);
        nbaConferenceMap.put("Southeast", 100132000L);

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
        //赛季球队阵容文件
        seasonSquadFileMap.put(20001L, "squad_Soccer.MANCHESTERCITY.17.xml|squad_Soccer.CHELSEAFC.38.xml|squad_Soccer.EVERTON.48.xml|squad_Soccer.MANCHESTERUNITED.35.xml|squad_Soccer.ARSENAL.42.xml|squad_Soccer.TOTTENHAMHOTSPUR.33.xml|squad_Soccer.LIVERPOOLFC.44.xml|squad_Soccer.HULLCITY.96.xml|squad_Soccer.CRYSTALPALACE.7.xml|squad_Soccer.WESTHAMUNITED.37.xml|squad_Soccer.SWANSEACITY.74.xml|squad_Soccer.BURNLEYFC.6.xml|squad_Soccer.LEICESTERCITY.31.xml|squad_Soccer.SOUTHAMPTONFC.45.xml|squad_Soccer.SUNDERLANDAFC.41.xml|squad_Soccer.STOKECITY.29.xml|squad_Soccer.AFCBOURNEMOUTH.60.xml|squad_Soccer.MIDDLESBROUGHFC.36.xml|squad_Soccer.WATFORDFC.24.xml|squad_Soccer.WESTBROMWICHALBION.8.xml");//英超阵容
        seasonSquadFileMap.put(32001L, "squad_Soccer.Germany.Bundesliga.Bundesliga1617.VFLWOLFSBURG.2524.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.BORUSSIADORTMUND.2673.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.RBLEIPZIG.36360.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.HAMBURGERSV.2676.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.FCINGOLSTADT04.5880.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.FCAUGSBURG.2600.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.WERDERBREMEN.2534.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.HERTHABSCBERLIN.2528.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.BAYER04LEVERKUSEN.2681.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.FCBAYERNMUNICH.2672.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.SCFREIBURG.2538.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.TSG1899HOFFENHEIM.2569.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.FCSCHALKE04.2530.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.EINTRACHTFRANKFURT.2674.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.SVDARMSTADT98.2576.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.1FCCOLOGNE.2671.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.BORUSSIAMONCHENGLADBACH.2527.xml|squad_Soccer.Germany.Bundesliga.Bundesliga1617.FSVMAINZ05.2556.xml");//德甲阵容
        seasonSquadFileMap.put(26001L, "squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.OSASUNA.2820.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.ATHLETICDEBILBAO.2825.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.RCDESPANYOLBARCELONA.2814.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.GRANADACF.33779.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.DEPORTIVOLACORUNA.2832.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.REALMADRID.2829.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.REALSOCIEDAD.2824.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.REALBETISBALOMPIE.2816.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.SPORTINGDEGIJON.2852.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.FCSEVILLA.2833.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.SDEIBAR.2839.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.CFVALENCIA.2828.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.RCCELTADEVIGO.2821.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.UNIONDEPORTIVALASPALMAS.6577.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.CDLEGANES.2845.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.ATLETICOMADRID.2836.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.CFMALAGA.2830.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.CDALAVES.2885.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.FCBARCELONA.2817.xml|squad_Soccer.Spain.PrimeraDivision.PrimeraDivision1617.CFVILLARREAL.2819.xml");//西甲阵容
        seasonSquadFileMap.put(29001L, "squad_Soccer.Italy.SerieA.SerieA1617.ACCHIEVOVERONA.2694.xml|squad_Soccer.Italy.SerieA.SerieA1617.ACFFIORENTINA.2693.xml|squad_Soccer.Italy.SerieA.SerieA1617.ACMILAN.2692.xml|squad_Soccer.Italy.SerieA.SerieA1617.ASROMA.2702.xml|squad_Soccer.Italy.SerieA.SerieA1617.ATALANTABERGAMASCACALCIO.2686.xml|squad_Soccer.Italy.SerieA.SerieA1617.BOLOGNAFC.2685.xml|squad_Soccer.Italy.SerieA.SerieA1617.CAGLIARI.2719.xml|squad_Soccer.Italy.SerieA.SerieA1617.EMPOLI.2705.xml|squad_Soccer.Italy.SerieA.SerieA1617.FCCROTONE.2718.xml|squad_Soccer.Italy.SerieA.SerieA1617.GENOACFC.2713.xml|squad_Soccer.Italy.SerieA.SerieA1617.INTERMILANO.2697.xml|squad_Soccer.Italy.SerieA.SerieA1617.JUVENTUS.2687.xml|squad_Soccer.Italy.SerieA.SerieA1617.LAZIOROMA.2699.xml|squad_Soccer.Italy.SerieA.SerieA1617.PALERMO.2715.xml|squad_Soccer.Italy.SerieA.SerieA1617.PESCARACALCIO1936.7935.xml|squad_Soccer.Italy.SerieA.SerieA1617.SAMPDORIA.2711.xml|squad_Soccer.Italy.SerieA.SerieA1617.SASSUOLO.2793.xml|squad_Soccer.Italy.SerieA.SerieA1617.SSCNAPOLI.2714.xml|squad_Soccer.Italy.SerieA.SerieA1617.TORINO.2696.xml|squad_Soccer.Italy.SerieA.SerieA1617.UDINESECALCIO.2695.xml");//意甲阵容
        seasonSquadFileMap.put(35001L, "squad_Soccer.France.Ligue1.Ligue11617.FCNANTES.1647.xml|squad_Soccer.France.Ligue1.Ligue11617.SMCAEN.1667.xml|squad_Soccer.France.Ligue1.Ligue11617.OLYMPIQUELILLE.1643.xml|squad_Soccer.France.Ligue1.Ligue11617.OLYMPICDEMARSEILLE.1641.xml|squad_Soccer.France.Ligue1.Ligue11617.ASMONACO.1653.xml|squad_Soccer.France.Ligue1.Ligue11617.ASNANCYLORRAINE.1675.xml|squad_Soccer.France.Ligue1.Ligue11617.SPORTINGCLUBDEBASTIA.1655.xml|squad_Soccer.France.Ligue1.Ligue11617.FCLORIENT.1656.xml|squad_Soccer.France.Ligue1.Ligue11617.EAGUINGAMP.1654.xml|squad_Soccer.France.Ligue1.Ligue11617.GIRONDINSDEBORDEAUX.1645.xml|squad_Soccer.France.Ligue1.Ligue11617.OLYMPIQUELYONNAIS.1649.xml|squad_Soccer.France.Ligue1.Ligue11617.FCMETZ.1651.xml|squad_Soccer.France.Ligue1.Ligue11617.DIJON.1686.xml|squad_Soccer.France.Ligue1.Ligue11617.SCOANGERS.1684.xml|squad_Soccer.France.Ligue1.Ligue11617.SAINTETIENNE.1678.xml|squad_Soccer.France.Ligue1.Ligue11617.FCSTADERENNES.1658.xml|squad_Soccer.France.Ligue1.Ligue11617.TOULOUSE.1681.xml|squad_Soccer.France.Ligue1.Ligue11617.PARISSAINTGERMAIN.1644.xml|squad_Soccer.France.Ligue1.Ligue11617.NICE.1661.xml|squad_Soccer.France.Ligue1.Ligue11617.SCMONTPELLIER.1642.xml");//法甲阵容
        seasonSquadFileMap.put(100234001L, "squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ADELAIDEUNITEDFC.2946.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALAHLIKSA.34469.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALAHLIUAE.34309.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALAIN.37082.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALFATEHSC.56023.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALHIDD.45472.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALHILAL.21895.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALJAZEERA.8090.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALRAYYAN.40660.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALSADD.39733.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALTAAWON.56021.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALWAHDAFCABUDHABI.34470.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALWEHDATJOR.40650.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.BENGALURUFC.118670.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.BRISBANEROARFC.5968.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.EASTERNSPORTSCLUB.115157.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ELJAISHSCQAT.56113.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ESTEGHLALKHUZESTAN.109573.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ESTEGHLALTEHRAN.34316.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.FCBUNYODKORTASHKENT.23481.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.FCSEOUL.7646.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.GAMBAOSAKA.3138.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.GLOBALFC.95623.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.GUANGZHOUEVERGRANDE.24156.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.HANOITT.33130.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.JEJUUTD.7649.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.JIANGSUFC.34693.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.JOHORDARULTAKZIMFC.3327.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.KASHIMAANTLERS.3134.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.KAWASAKIFRONTALE.5127.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.KITCHEESC.42199.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.LEKHWIYA.44441.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.LOKOMOTIVTASHKENT.40261.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.MUANGTHONGUNITED.39946.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.NASAFQARSHI.34017.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.PERSEPOLISFC.34314.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.SHANGHAISHENHUA.3373.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.SHANGHAISIPG.41537.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.SUWONSAMSUNGBLUEWINGS.7652.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.TAMPINESROVERS.3043.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ULSANHYUNDAIHORANGI.7653.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.URAWAREDDIAMONDS.3145.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.WESTERNSYDNEY.72926.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.YADANARBON.186789.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ZOBAHAN.39328.xml");
        seasonSquadFileMap.put(47001L, "squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.BEIJINGGUOAN.3376.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.CHANGCHUNYATAI.34694.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.CHONGQINGLIFAN.3362.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.GUANGZHOUEVERGRANDE.24156.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.GUANGZHOURFFC.3375.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.GUIZHOUZHICHENGFC.50017.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.HEBEICHINAFORTUNEFC.71564.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.HENANJIANYE.34692.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.JIANGSUFC.34693.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.LIAONINGWHOWINFC.41429.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.SHANDONGLUNENG.3371.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.SHANGHAISHENHUA.3373.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.SHANGHAISIPG.41537.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.TIANJINSONGJIANG.50018.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.TIANJINTEDA.3367.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.YANBIAN.41539.xml");
        nameMap.put("17", "英格兰足球超级联赛");
        nameMap.put("35", "德国足球甲级联赛");
        nameMap.put("8", "西班牙足球甲级联赛");
        nameMap.put("34", "法国足球甲级联赛");
        nameMap.put("23", "意大利足球甲级联赛");
        nameMap.put("649", "中超");
        nameMap.put("196", "日本J联赛");
        nameMap.put("410", "韩国经典K联赛");
        nameMap.put("242", "美国职业足球大联盟");
        nameMap.put("384", "南美解放者杯");
        nameMap.put("133", "美洲杯");
        nameMap.put("290", "2015加拿大女足世界杯");
        nameMap.put("782", "中甲联赛");
        nameMap.put("140", "2015中北美及加勒比地区金杯赛");
        nameMap.put("1337", "国际冠军杯");
        nameMap.put("39", "丹麦足球超级联赛");
        nameMap.put("40", "瑞典足球超级联赛");
        nameMap.put("36", "苏格兰足球超级联赛");
        nameMap.put("38", "比利时足球甲级联赛");
        nameMap.put("338", "比利时超级杯");
        nameMap.put("218", "乌克兰足球超级联赛");
        nameMap.put("238", "葡萄牙足球超级联赛");
        nameMap.put("18", "英格兰足球冠军联赛");
        nameMap.put("24", "英格兰足球甲级联赛");
        nameMap.put("25", "英格兰足球乙级联赛");
        nameMap.put("217", "德国杯");
        nameMap.put("329", "西班牙国王杯");
        nameMap.put("21", "英格兰联赛杯");
        nameMap.put("346", "英格兰社区盾");
        nameMap.put("332", "苏格兰联赛杯");
        nameMap.put("206", "苏格兰足球冠军联赛");
        nameMap.put("480", "南美杯");
        nameMap.put("7", "欧洲冠军联赛");
        nameMap.put("679", "欧足联欧洲联赛");
        nameMap.put("465", "欧洲超级杯");
        nameMap.put("933", "女篮亚锦赛");
        nameMap.put("935", "女篮亚锦赛");
        nameMap.put("943", "男篮亚锦赛");
        nameMap.put("681", "美洲男篮锦标赛");
        nameMap.put("285", "欧洲男篮锦标赛");
        nameMap.put("285", "欧洲男篮锦标赛");
        nameMap.put("138", "欧洲篮球冠军联赛");
        nameMap.put("330", "荷兰杯");
        nameMap.put("2324", "15/16欧洲青年冠军联赛");
        nameMap.put("279", "世界少年足球锦标赛");
        nameMap.put("19", "英格兰足总杯");
        nameMap.put("463", "亚洲冠军联赛");
        nameMap.put("668", "亚足联杯赛");
        nameMap.put("1566", "CBA联赛");
        nameMap.put("328", "意大利杯");
        nameMap.put("373", "巴西杯");
        nameMap.put("109", "MLB");
        nameMap.put("308", "2018世预赛亚洲区12强赛");
        nameMap.put("295", "2018世预赛南美区");
        nameMap.put("11", "2018世预赛欧洲区");
        nameMap.put("1203", "2016五人制足球世界杯");
        nameMap.put("827", "U17女足世界杯");
        nameMap.put("804", "U20女足世界杯");
        nameMap.put("31", "NFL");
        f1NameMap.put("36", "F1世锦赛");
        statsNameMap.put("NBA", 44001L);
        statsNameMap.put("CBACHN", 39001L);
        positionMap.put("G", "100125000");
        positionMap.put("D", "100126000");
        positionMap.put("M", "100127000");
        positionMap.put("F", "100128000");

        goalType.put("penalty", 100156000L);
        goalType.put("heading", 104660000L);
        goalType.put("owngoal", 104658000L);


        sodaGoalType.put("点球", 100156000L);
        sodaGoalType.put("头球", 104660000L);
        sodaGoalType.put("乌龙球", 104658000L);


        nationalityMap.put("Albania", "阿尔巴尼亚");
        nationalityMap.put("Iceland", "冰岛");
        nationalityMap.put("Ukraine", "乌克兰");
        nationalityMap.put("Slovakia", "斯洛伐克");
        nationalityMap.put("Czech Republic", "捷克");
        nationalityMap.put("Wales", "威尔士");
        nationalityMap.put("Turkey", "土耳其");
        nationalityMap.put("Switzerland", "瑞士");
        nationalityMap.put("Sweden", "瑞典");
        nationalityMap.put("Spain", "西班牙");
        nationalityMap.put("Russia", "俄罗斯");
        nationalityMap.put("Romania", "罗马尼亚");
        nationalityMap.put("Portugal", "葡萄牙");
        nationalityMap.put("Poland", "波兰");
        nationalityMap.put("Northern Ireland", "北爱尔兰");
        nationalityMap.put("Italy", "意大利");
        nationalityMap.put("Republic of Ireland", "爱尔兰");
        nationalityMap.put("Hungary", "匈牙利");
        nationalityMap.put("Germany", "德国");
        nationalityMap.put("France", "法国");
        nationalityMap.put("England", "英格兰");
        nationalityMap.put("Croatia", "克罗地亚");
        nationalityMap.put("Belgium", "比利时");
        nationalityMap.put("Austria", "奥地利");

        basketBallPositionMap.put("G", "后卫");
        basketBallPositionMap.put("C", "中锋");
        basketBallPositionMap.put("F", "前锋");
        basketBallPositionMap.put("SG", "后卫");
        basketBallPositionMap.put("G/F", "前锋/后卫");
        basketBallPositionMap.put("F-G", "前锋/后卫");
        basketBallPositionMap.put("F/C", "前锋/中锋");
        basketBallPositionMap.put("F-C", "前锋/中锋");
        basketBallPositionMap.put("C-F", "中锋/前锋");
        basketBallPositionMap.put("PF", "大前锋");
        basketBallPositionMap.put("SF", "小前锋");
        basketBallPositionMap.put("SG", "得分后卫");
        basketBallPositionMap.put("PG", "控球后卫");
        basketBallPositionMap.put("Guard", "后卫");
        basketBallPositionMap.put("Forward", "前锋");
        basketBallPositionMap.put("Shooting Guard", "得分后卫");
        basketBallPositionMap.put("Point Guard", "控球后卫");
        basketBallPositionMap.put("Center", "中锋");
        basketBallPositionMap.put("Small Forward", "小前锋");
        basketBallPositionMap.put("Forward-Center", "前锋/中锋");
        basketBallPositionMap.put("Guard-Forward", "前锋/后卫");
        basketBallPositionMap.put("Power Forward", "大前锋");

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
        groupsMap.put("Match for 3rd place", "^三四名决赛$");
        groupsMap.put("Semifinals", "^半决赛$");
        groupsMap.put("Quarterfinals", "^1/4决赛$");
        groupsMap.put("Placement Qualification Match", "^排位赛$");

        euroTypeMap.put("7", "^小组赛$");
        euroTypeMap.put("8", "^1/8决赛$");
        euroTypeMap.put("2", "^1/4决赛$");
        euroTypeMap.put("3", "^半决赛$");
        euroTypeMap.put("4", "^决赛$");

        euroGroupMap.put("11", "^A组$");
        euroGroupMap.put("12", "^B组$");
        euroGroupMap.put("13", "^C组$");
        euroGroupMap.put("14", "^D组$");
        euroGroupMap.put("15", "^E组$");
        euroGroupMap.put("16", "^F组$");

        tennisRoundMap.put("1", "ext_match_wq_draw_1");
        tennisRoundMap.put("2", "ext_match_wq_draw_2");
        tennisRoundMap.put("3", "ext_match_wq_draw_3");
        tennisRoundMap.put("25", "ext_match_wq_1_16");
        tennisRoundMap.put("26", "ext_match_wq_1_8");
        tennisRoundMap.put("27", "ext_match_wq_1_4");
        tennisRoundMap.put("28", "ext_match_wq_1_2");
        tennisRoundMap.put("29", "ext_match_wq_final");

        filebak = file1 + "|" + file10 + "|" + file11 + "|" + file12 + "|" + file13 + "|" + file14 + "|" + file15;


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
