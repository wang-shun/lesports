package com.lesports.sms.data.model;


import com.google.common.collect.Maps;
import com.lesports.utils.LeProperties;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qiaohongxin on 2016/11/05.
 */
public class Constants {

    public static String statsDownloadUrl = "http://beitai-LeTV:letv2015@downloads.stats.com/beitai-LeTV/";//LeProperties.getString("stats.download.url");
    public static String localDownloadPath = "/letv/third_part_files/stats/";//LeProperties.getString("nba.stats.file.path");
    public static String statsUserName = "beitai-LeTV";//LeProperties.getString("stats.http.userName");
    public static String statsPassWord = "letv2015";//LeProperties.getString("stats.http.password");
    //local ftp to get stats nba data
    public static String LocalFtpHost = "220.181.153.69";
    public static int LocalFtpPort = 60000;
    public static String LocalFtpUserName = "Sport";
    public static String LocalFtpPassword = "7gLIbjs5{";
    //sportrard data
    public static String srHost = "10.120.56.176";
    public static int srPort = 65000;
    public static String srUserName = "Sport";
    public static String srPassword = "40a38cb71";


    //赛程文件下载列表
    public static final MultiValueMap<Long, String> schedules = new LinkedMultiValueMap<>();

    //赛程文件下载列表
    public static final MultiValueMap<Long, String> lives = new LinkedMultiValueMap<>();
    //事件直播下载列表
    public static final MultiValueMap<Long, String> liveEvents = new LinkedMultiValueMap<>();

    //球队赛季技术统计文件下载列表
    public static final MultiValueMap<Long, String> playerStats = new LinkedMultiValueMap<>();
    //球员榜单文件下载列表
    public static final MultiValueMap<Long, String> playerStandings = new LinkedMultiValueMap<>();
    //球队赛季技术统计文件下载列表
    public static final MultiValueMap<Long, String> teamStats = new LinkedMultiValueMap<>();
    //球队赛季技术统计文件下载列表
    public static final MultiValueMap<Long, String> teamSeasonStats = new LinkedMultiValueMap<>();
    //球队榜单文件下载列表
    public static final MultiValueMap<Long, String> teamStandings = new LinkedMultiValueMap<>();

    static {

        //SCHEDULE【2016】【足球】
        schedules.add(102059002L, "499-XPathStats2|\\cba\\CBACHN_SCHEDULE.XML");//CBA
        schedules.add(102045002L, "499-XPathStats|NBA_SCHEDULE.XML");//NBA
        //【Sportrard】【国际类比赛】
        //世界杯亚洲区预选赛12强赛
        schedules.add(0L, "469-XPathSportrard|");
        //世界杯欧洲区预选赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupA.2018.WorldCup2018QualificationUEFAGroupA.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupB.2018.WorldCup2018QualificationUEFAGroupB.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupC.2018.WorldCup2018QualificationUEFAGroupC.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupD.2018.WorldCup2018QualificationUEFAGroupD.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupE.2018.WorldCup2018QualificationUEFAGroupE.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupF.2018.WorldCup2018QualificationUEFAGroupF.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupG.2018.WorldCup2018QualificationUEFAGroupG.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupH.2018.WorldCup2018QualificationUEFAGroupH.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupI.2018.WorldCup2018QualificationUEFAGroupI.xml");
        //世界杯南美区预选赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.International.WorldCupQualificationCONMEBOL.2018.WorldCup2018QualificationCONMEBOL.xml");
        //五人制足球世界杯
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Futsal.International.FIFAFutsalWorldCupFinalround.2016.FIFAFutsalWorldCup2016FinalRound.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupA.2016.FIFAFutsalWorldCup2016GroupA.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupB.2016.FIFAFutsalWorldCup2016GroupB.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupC.2016.FIFAFutsalWorldCup2016GroupC.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupD.2016.FIFAFutsalWorldCup2016GroupD.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupE.2016.FIFAFutsalWorldCup2016GroupE.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupF.2016.FIFAFutsalWorldCup2016GroupF.xml");
        //U17女足世界杯
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalYouth.U17WorldCupWomenKnockoutstage.2016.U17WorldCupWomen2016Knockoutstage.xml|schedulesandresult_Soccer.InternationalYouth.U17WomensWorldCupGroupA.2016.U17WorldCup2016WomenGroupA.xml|schedulesandresult_Soccer.InternationalYouth.U17WomensWorldCupGroupB.2016.U17WorldCup2016WomenGroupB.xml|schedulesandresult_Soccer.InternationalYouth.U17WomensWorldCupGroupC.2016.U17WorldCup2016WomenGroupC.xml|schedulesandresult_Soccer.InternationalYouth.U17WomensWorldCupGroupD.2016.U17WorldCup2016WomenGroupD.xml");
        //U20女足世界杯
        schedules.add(102060002L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupKnockoutstage.2016.U20WomensWorldCup2016Knockoutstage.xml|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupGroupA.2016.U20WomensWorldCup2016GroupA.xml|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupGroupB.2016.U20WomensWorldCup2016GroupB.xml|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupGroupC.2016.U20WomensWorldCup2016GroupC.xml|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupGroupD.2016.U20WomensWorldCup2016GroupD.xml");
        //国际冠军杯
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.InternationalChampionsCup.2016.InternationalChampionsCup2016");
        //【Sportrard】【国家类比赛】
        // 英格兰足球超级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.PremierLeague.1617.PremierLeague1617.xml");
        //英格兰冠军联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.Championship.1617.Championship1617.xml");
        //英格兰足球甲级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.LeagueOne.1617.LeagueOne1617.xml");
        //英格兰足球乙级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.LeagueTwo.1617.LeagueTwo1617.xml");
        //英格兰联赛杯
        schedules.add(0L, "469-XPathSportrard|");
        //英格兰社区盾
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.CommunityShield.2016.CommunityShield2016.xml");
        //英格兰足总杯
        schedules.add(0L, "469-XPathSportrard|");

        //苏格兰联赛杯
        schedules.add(0L, "469-XPathSportrard|");
        //苏格兰足球冠军联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Scotland.Championship.1617.Championship1617.xml");
        //苏格兰足球超级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Scotland.Premiership.1617.Premiership1617.xml");

        //德国足球甲级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Germany.Bundesliga.1617.Bundesliga1617.xml");
        //德国杯;
        schedules.add(0L, "469-XPathSportrard|");
        // 西班牙足球甲级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Spain.PrimeraDivision.1617.PrimeraDivision1617.xml");
        // 西班牙国王杯;
        schedules.add(0L, "469-XPathSportrard|");

        //法国足球甲级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.France.Ligue1.1617.Ligue11617.xml");

        // 意大利足球甲级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Italy.SerieA.1617.SerieA1617.xml");
        // 意大利杯
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Italy.CoppaItalia.1617.CoppaItalia1617.xml");

        // 欧洲冠军联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueQualification.1617.UEFAChampionsLeague1617Qualification.xml");
        // 欧足联欧洲联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupL.1617.UEFAEuropaLeague1617GroupL.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupK.1617.UEFAEuropaLeague1617GroupK.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupJ.1617.UEFAEuropaLeague1617GroupJ.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupI.1617.UEFAEuropaLeague1617GroupI.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupH.1617.UEFAEuropaLeague1617GroupH.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupG.1617.UEFAEuropaLeague1617GroupG.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupF.1617.UEFAEuropaLeague1617GroupF.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupE.1617.UEFAEuropaLeague1617GroupE.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupD.1617.UEFAEuropaLeague1617GroupD.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupC.1617.UEFAEuropaLeague1617GroupC.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupB.1617.UEFAEuropaLeague1617GroupB.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupA.1617.UEFAEuropaLeague1617GroupA.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupH.1617.UEFAChampionsLeague1617GroupH.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupG.1617.UEFAChampionsLeague1617GroupG.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupF.1617.UEFAChampionsLeague1617GroupF.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupE.1617.UEFAChampionsLeague1617GroupE.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupD.1617.UEFAChampionsLeague1617GroupD.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupC.1617.UEFAChampionsLeague1617GroupC.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupB.1617.UEFAChampionsLeague1617GroupB.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupA.1617.UEFAChampionsLeague1617GroupA.xml\n");
        //欧洲超级杯
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.UEFASuperCup.2016.UEFASuperCup2016.xml");
        //欧洲青年冠军联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupC.1617.UEFAYouthLeague1617GroupC.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupD.1617.UEFAYouthLeague1617GroupD.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupE.1617.UEFAYouthLeague1617GroupE.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupF.1617.UEFAYouthLeague1617GroupF.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupG.1617.UEFAYouthLeague1617GroupG.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupH.1617.UEFAYouthLeague1617GroupH.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueKnockoutstage.1617.UEFAYouthLeague1617Knockoutstage.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupA.1617.UEFAYouthLeague1617GroupA.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupB.1617.UEFAYouthLeague1617GroupB.xml");
// 世界少年足球锦标赛
        schedules.add(0L, "469-XPathSportrard|");
//  荷兰杯
        schedules.add(0L, "469-XPathSportrard|");


        // 丹麦足球超级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Denmark.Superligaen.1617.Superligaen1617.xml");
// 瑞典足球超级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Sweden.Allsvenskan.2016.Allsvenskan2016.xml");
// 比利时足球甲级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Belgium.ProLeague.1617.ProLeague1617.xml");
//比利时超级杯
        schedules.add(0L, "469-XPathSportrard|");
//乌克兰足球超级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Ukraine.PremierLeague.1617.PremierLeague1617.xml");
// "葡萄牙足球超级联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Portugal.PrimeiraLiga.1617.PrimeiraLiga1617.xml");

//  亚洲冠军联赛
        schedules.add(0L, "469-XPathSportrard|");
        //中超
        schedules.add(0L, "469-XPathSportrard|");
        //中甲
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.China.ChinaLeague.2016.ChinaLeague2016.xml");
        //日本J联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Japan.JLeague.2016.JLeague2016.xml");
        //韩国K联赛
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.SouthKorea.KLeagueClassic.2016.KLeagueClassic2016.xml");
        //巴西杯
        schedules.add(0L, "469-XPathSportrard|");

        //美国职业足球大联盟
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.USA.MajorLeagueSoccer.2016.MLS2016.xml|schedulesandresult_Soccer.USA.MajorLeagueSoccerPlayoffs.2016.MajorLeagueSoccer2016Playoffs.xml");
        //南美杯
        schedules.add(0L, "469-XPathSportrard|");
// 南美解放者杯
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.CopaLibertadoresGroup1.2016.CopaLibertadores2016Group1.xml|schedulesandresult_Soccer.InternationalClubs.CopaLibertadoresGroup2.2016.CopaLibertadores2016Group2.xml|schedulesandresult_Soccer.InternationalClubs.CopaLibertadoresGroup3.2016.CopaLibertadores2016Group3.xml|schedulesandresult_Soccer.InternationalClubs.CopaLibertadoresGroup4.2016.CopaLibertadores2016Group4.xmlschedulesandresult_Soccer.InternationalClubs.CopaLibertadoresGroup5.2016.CopaLibertadores2016Group5.xml|schedulesandresult_Soccer.InternationalClubs.CopaLibertadoresGroup6.2016.CopaLibertadores2016Group6.xml|schedulesandresult_Soccer.InternationalClubs.CopaLibertadoresGroup7.2016.CopaLibertadores2016Group7.xml|schedulesandresult_Soccer.InternationalClubs.CopaLibertadoresGroup8.2016.CopaLibertadores2016Group8.xml|schedulesandresult_Soccer.InternationalClubs.CopaLibertadoresQualification.2016.CopaLibertadores2016Qualificationround.xml");
// 美洲杯
        schedules.add(0L, "469-XPathSportrard|");

//SCHEDULE【2016】【篮球】
        //       nameMap.put("933", "女篮亚锦赛");
//        nameMap.put("935", "女篮亚锦赛");
//        nameMap.put("943", "男篮亚锦赛");
//        nameMap.put("681", "美洲男篮锦标赛");
//        nameMap.put("285", "欧洲男篮锦标赛");
//        nameMap.put("285", "欧洲男篮锦标赛");
//        nameMap.put("138", "欧洲篮球冠军联赛");
//SCHEDULE【2016】【棒球】
        //        nameMap.put("109", "MLB");
//SCHEDULE【2016】【橄榄球】
        //        nameMap.put("31", "NFL|schedulesandresult_Football.USA.NFL.1617.NFL1617.xm");
        //SCHEDULE【2016】【赛车】
        //        f1NameMap.put("36", "F1世锦赛");


//        //比分直播文件
        lives.add(0L, "469-XPathSportrard|livescore_letv_4505_delta.xml");//sportrard
        lives.add(102045002L, "499-XPathStats|\\412\\matchresult\\s9-412-2018-^ID:$-matchresult.xml");//soda 12qiang
        lives.add(102059002L, "499-XPathStats2|\\cba\\CBACHN_BOXSCORE_GAME$^CODE$.XML");//CBA
        lives.add(102045002L, "499-XPathStats|NBA_BOXSCORE_GAME$^CODE:$.XML");//NBA

        //比分直播文件
        liveEvents.add(0L, "469-XPathSportrard|livescore_letv_4505_delta.xml");//sportrard
        lives.add(102045002L, "499-XPathStats|\\412\\timeline\\s8-412-2018-^ID:$-timeline.xml");//soda 12qiang
        liveEvents.add(102045002L, "499-XPathStats|NBA_PBP_GAME$^CODE$.XML");//NBA
        //PLAYER-STANDING-2016球员榜单接入列表
        //英超
        playerStats.add(100848002L, "469-XPathSportrard|goalscorers_Soccer.England.PremierLeague.1617.xml");
        playerStats.add(100848002L, "469-XPathSportrard2|assists_Soccer.England.PremierLeague.1617.xml");
        playerStandings.add(102059002L, "499-XPathStats2|\\cba\\CBACHN_LEADERS.XML");//CBA
        playerStandings.add(102045002L, "499-XPathStats|NBA_LEADERS.XML");//NBA


        //TEAM-STANDING-2016球队榜单接入列表
        teamStandings.add(102045002L, "499-XPathStats|NBA_TEAM_STANDINGS.XML");//NBA-联盟
        teamStandings.add(102045002L, "499-XPathStats_0|NBA_TEAM_STANDINGS.XML");//NBA-分区
        teamStandings.add(102059002L, "499-XPathStats2|\\cba\\NBA_TEAM_STANDINGS.XML");//CBA
        teamStandings.add(100848002L, "469-XPathSportrard|leaguetable_Soccer.England.PremierLeague.PremierLeague1617.xml");//英超
        //CBA
        teamSeasonStats.add(102059002L, "499|\\cba\\CBACHN_ALL_ROSTER.XML");
        //CBA
        teamStats.add(102059002L, "499|\\cba\\CBACHN_TEAM_STATS.XML");
        //CBA
        playerStats.add(102059002L, "499|\\cba\\CBACHN_PLAYER_STATS.XML");
    }
}
