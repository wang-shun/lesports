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

    public static String defaultDownloadPathPrex = "/letv/third_part_files/";
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
    //赛前对阵文件下载列表
    public static final MultiValueMap<Long, String> schedulePreview = new LinkedMultiValueMap<>();
    //比分直播文件下载列表
    public static final MultiValueMap<Long, String> lives = new LinkedMultiValueMap<>();
    //事件直播下载列表
    public static final MultiValueMap<Long, String> liveEvents = new LinkedMultiValueMap<>();
    //球队赛季技术统计文件下载列表
    public static final MultiValueMap<Long, String> teamStats = new LinkedMultiValueMap<>();
    //球队赛季技术统计文件下载列表
    public static final MultiValueMap<Long, String> teamSeasons = new LinkedMultiValueMap<>();
    //球队榜单文件下载列表
    public static final MultiValueMap<Long, String> teamStandings = new LinkedMultiValueMap<>();
    //球员赛季技术统计文件下载列表
    public static final MultiValueMap<Long, String> playerStats = new LinkedMultiValueMap<>();
    //球员榜单文件下载列表
    public static final MultiValueMap<Long, String> playerStandings = new LinkedMultiValueMap<>();

    static {
        lives.add(0L, "469-XPathSportrard|/livescore/livescore_letv_4505_delta.xml");
        lives.add(0L, "469-XPathSportrard|/livescore/livescore_letv_4505.xml");
        liveEvents.add(0L, "469-XPathSportrard|/livescore/livescore_letv_4505_delta.xml");//sportrard直播文件，涵盖足球，篮球等所有提供直播赛事
        liveEvents.add(0L, "469-XPathSportrard|/livescore/livescore_letv_4505.xml");

        //【足球--国际俱乐部--亚冠-2017】
        schedules.add(102294002L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.AFCChampionsLeagueGroupA.2017.AFCChampionsLeague2017GroupA.xml|schedulesandresult_Soccer.InternationalClubs.AFCChampionsLeagueGroupB.2017.AFCChampionsLeague2017GroupB.xml|schedulesandresult_Soccer.InternationalClubs.AFCChampionsLeagueGroupC.2017.AFCChampionsLeague2017GroupC.xml|schedulesandresult_Soccer.InternationalClubs.AFCChampionsLeagueGroupD.2017.AFCChampionsLeague2017GroupD.xml|schedulesandresult_Soccer.InternationalClubs.AFCChampionsLeagueGroupE.2017.AFCChampionsLeague2017GroupE.xml|schedulesandresult_Soccer.InternationalClubs.AFCChampionsLeagueGroupF.2017.AFCChampionsLeague2017GroupF.xml|schedulesandresult_Soccer.InternationalClubs.AFCChampionsLeagueGroupG.2017.AFCChampionsLeague2017GroupG.xml|schedulesandresult_Soccer.InternationalClubs.AFCChampionsLeagueGroupH.2017.AFCChampionsLeague2017GroupH.xml");
        teamStandings.add(102294002L, "469-XPathSportrard|leaguetable_Soccer.InternationalClubs.AFCChampionsLeagueGroupA.AFCChampionsLeague2017GroupA.xml|leaguetable_Soccer.InternationalClubs.AFCChampionsLeagueGroupB.AFCChampionsLeague2017GroupB.xml|leaguetable_Soccer.InternationalClubs.AFCChampionsLeagueGroupC.AFCChampionsLeague2017GroupC.xml|leaguetable_Soccer.InternationalClubs.AFCChampionsLeagueGroupD.AFCChampionsLeague2017GroupD.xml|leaguetable_Soccer.InternationalClubs.AFCChampionsLeagueGroupE.AFCChampionsLeague2017GroupE.xml|leaguetable_Soccer.InternationalClubs.AFCChampionsLeagueGroupF.AFCChampionsLeague2017GroupF.xml|leaguetable_Soccer.InternationalClubs.AFCChampionsLeagueGroupG.AFCChampionsLeague2017GroupG.xml|leaguetable_Soccer.InternationalClubs.AFCChampionsLeagueGroupH.AFCChampionsLeague2017GroupH.xml");
        teamSeasons.add(102294002L, "469-XPathSportrard|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALHIDD.45472.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALSADD.39733.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ALWEHDATJOR.40650.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.BENGALURUFC.118670.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ELJAISHSCQAT.56113.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.ESTEGHLALTEHRAN.34316.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.FCBUNYODKORTASHKENT.23481.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.HANOITT.33130.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.JOHORDARULTAKZIMFC.3327.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.KITCHEESC.42199.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.NASAFQARSHI.34017.xml|squad_Soccer.InternationalClubs.AFCChampionsLeague.AFCChampionsLeague2017.TAMPINESROVERS.3043.xml");
        playerStandings.add(102294002L, "469-XPathSportrard|goalscorers_Soccer.InternationalClubs.AFCChampionsLeague.2017.xml");
        playerStandings.add(102294002L, "469-XPathSportrard2|assists_Soccer.InternationalClubs.AFCChampionsLeague.2017.xml");
        //【足球--中国--中超-2017】
        schedules.add(102294002L, "469-XPathSportrard|schedulesandresult_Soccer.China.ChineseSuperLeague.2017.ChineseSuperLeague2017.xml");
        schedules.add(102294002L, "469-XPathSoda|/282/fixture/s1-282-2017-result");
        teamSeasons.add(102294002L, "469-XPathSoda|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.BEIJINGGUOAN.3376.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.CHANGCHUNYATAI.34694.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.CHONGQINGLIFAN.3362.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.GUANGZHOUEVERGRANDE.24156.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.GUANGZHOURFFC.3375.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.GUIZHOUZHICHENGFC.50017.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.HEBEICHINAFORTUNEFC.71564.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.HENANJIANYE.34692.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.JIANGSUFC.34693.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.LIAONINGWHOWINFC.41429.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.SHANDONGLUNENG.3371.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.SHANGHAISHENHUA.3373.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.SHANGHAISIPG.41537.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.TIANJINSONGJIANG.50018.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.TIANJINTEDA.3367.xml|squad_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.YANBIAN.41539.xml");
        teamStandings.add(102294002L, "469-XPathSportrard|leaguetable_Soccer.China.ChineseSuperLeague.ChineseSuperLeague2017.xml");
        teamSeasons.add(102294002L, "469-XPathSoda|/282/team/");
        playerStandings.add(102294002L, "469-XPathSoda|/282/s4-282-2017-player-assist.xml");
        playerStandings.add(102294002L, "469-XPathSoda2|/282/s4-282-2017-player-assist.xml");
        schedulePreview.add(100538002L, "53-XPathSoda|/282/preview/s6-282-2016-1063144-preview.xml");
        //【足球-瑞典-瑞超-2017】
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Sweden.Allsvenskan.2017.Allsvenskan2017.xml");
        //【足球-美国-美国职业足球大联盟-2017】
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.USA.MajorLeagueSoccer.2017.MLS207.xml|schedulesandresult_Soccer.USA.MajorLeagueSoccerPlayoffs.2017.MajorLeagueSoccer2017Playoffs.xml");
        // 【足球-日本-日本J联赛-2017】
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Japan.JLeague.2017.JLeague2017.xml");
// 【足球-韩国-韩国K联赛-2017】
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.SouthKorea.KLeagueClassic.2017.KLeagueClassic2017.xml");
        // 【英格兰足球超级联赛-英超】
        schedules.add(100848002L, "469-XPathSportrard|schedulesandresult_Soccer.England.PremierLeague.1617.PremierLeague1617.xml");
        teamSeasons.add(100848002L, "469-XPathSportrard|squad_Soccer.MANCHESTERCITY.17.xml|squad_Soccer.CHELSEAFC.38.xml|squad_Soccer.EVERTON.48.xml|squad_Soccer.MANCHESTERUNITED.35.xml|squad_Soccer.ARSENAL.42.xml|squad_Soccer.TOTTENHAMHOTSPUR.33.xml|squad_Soccer.LIVERPOOLFC.44.xml|squad_Soccer.HULLCITY.96.xml|squad_Soccer.CRYSTALPALACE.7.xml|squad_Soccer.WESTHAMUNITED.37.xml|squad_Soccer.SWANSEACITY.74.xml|squad_Soccer.BURNLEYFC.6.xml|squad_Soccer.LEICESTERCITY.31.xml|squad_Soccer.SOUTHAMPTONFC.45.xml|squad_Soccer.SUNDERLANDAFC.41.xml|squad_Soccer.STOKECITY.29.xml|squad_Soccer.AFCBOURNEMOUTH.60.xml|squad_Soccer.MIDDLESBROUGHFC.36.xml|squad_Soccer.WATFORDFC.24.xml|squad_Soccer.WESTBROMWICHALBION.8.xml");
        teamStandings.add(100848002L, "469-XPathSportrard|leaguetable_Soccer.England.PremierLeague.PremierLeague1617.xml");//英超
        playerStandings.add(100848002L, "469-XPathSportrard|goalscorers_Soccer.England.PremierLeague.1617.xml");
        playerStandings.add(100848002L, "469-XPathSportrard2|assists_Soccer.England.PremierLeague.1617.xml");

        //【2018世预亚12强】
        lives.add(101543002L, "53-XPathSoda|/412/matchresult/s9-412-2018-^ID:$-matchresult.xml");//soda 12强
        liveEvents.add(101543002L, "53-XPathSoda|/412/matchresult/s9-412-2018-^ID:$-timeline.xml");//soda 12强
        schedules.add(101543002L, "469-XPathSportrard|schedulesandresult_Soccer.International.WorldCupQualificationAFCRound3GrA.2018.WCQualification2018AFC3rdroundGroupA.xml|schedulesandresult_Soccer.International.WorldCupQualificationAFCRound3GrB.2018.WCQualification2018AFC3rdroundGroupB.xml");
        schedules.add(101543002L, "53-XPathSoda|/412/s1-412-2018-result.xml");
        teamSeasons.add(101543002L, "53-XPathSoda|/412/team/");
        teamStandings.add(101543002L, "469-XPathSportrard|leaguetable_Soccer.International.WorldCupQualificationAFCRound3GrA.WCQualification2018AFC3rdroundGroupA.xml|leaguetable_Soccer.International.WorldCupQualificationAFCRound3GrB.WCQualification2018AFC3rdroundGroupB.xml");
        playerStandings.add(101543002L, "53-XPathSoda|/412/team/s4-412-2018-player-assist.xml");
        playerStandings.add(101543002L, "53-XPathSoda|/412/team/s4-412-2018-player-goal.xml");
        teamStats.add(101543002L, "53-XPathSoda|/412/team/");
        playerStats.add(101543002L, "53-XPathSoda|/412/player/");

        //【2018世界杯欧洲区预选赛】
        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupA.2018.WorldCup2018QualificationUEFAGroupA.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupB.2018.WorldCup2018QualificationUEFAGroupB.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupC.2018.WorldCup2018QualificationUEFAGroupC.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupD.2018.WorldCup2018QualificationUEFAGroupD.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupE.2018.WorldCup2018QualificationUEFAGroupE.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupF.2018.WorldCup2018QualificationUEFAGroupF.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupG.2018.WorldCup2018QualificationUEFAGroupG.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupH.2018.WorldCup2018QualificationUEFAGroupH.xml|schedulesandresult_Soccer.International.WorldCupQualificationUEFAGroupI.2018.WorldCup2018QualificationUEFAGroupI.xml");
        //【2018世界杯南美区预选赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.International.WorldCupQualificationCONMEBOL.2018.WorldCup2018QualificationCONMEBOL.xml");
//        //五人制足球世界杯
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Futsal.International.FIFAFutsalWorldCupFinalround.2016.FIFAFutsalWorldCup2016FinalRound.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupA.2016.FIFAFutsalWorldCup2016GroupA.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupB.2016.FIFAFutsalWorldCup2016GroupB.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupC.2016.FIFAFutsalWorldCup2016GroupC.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupD.2016.FIFAFutsalWorldCup2016GroupD.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupE.2016.FIFAFutsalWorldCup2016GroupE.xml|schedulesandresult_Futsal.International.FIFAFutsalWorldCupGroupF.2016.FIFAFutsalWorldCup2016GroupF.xml");
//        //U17女足世界杯
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalYouth.U17WorldCupWomenKnockoutstage.2016.U17WorldCupWomen2016Knockoutstage.xml|schedulesandresult_Soccer.InternationalYouth.U17WomensWorldCupGroupA.2016.U17WorldCup2016WomenGroupA.xml|schedulesandresult_Soccer.InternationalYouth.U17WomensWorldCupGroupB.2016.U17WorldCup2016WomenGroupB.xml|schedulesandresult_Soccer.InternationalYouth.U17WomensWorldCupGroupC.2016.U17WorldCup2016WomenGroupC.xml|schedulesandresult_Soccer.InternationalYouth.U17WomensWorldCupGroupD.2016.U17WorldCup2016WomenGroupD.xml");
//        //U20女足世界杯
//        schedules.add(102060002L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupKnockoutstage.2016.U20WomensWorldCup2016Knockoutstage.xml|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupGroupA.2016.U20WomensWorldCup2016GroupA.xml|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupGroupB.2016.U20WomensWorldCup2016GroupB.xml|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupGroupC.2016.U20WomensWorldCup2016GroupC.xml|schedulesandresult_Soccer.InternationalYouth.U20WomensWorldCupGroupD.2016.U20WomensWorldCup2016GroupD.xml");
//        //国际冠军杯
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.InternationalChampionsCup.2016.InternationalChampionsCup2016");
        //【Sportrard】【国家类比赛】

        //英格兰冠军联赛
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.Championship.1617.Championship1617.xml");
//        //英格兰足球甲级联赛
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.LeagueOne.1617.LeagueOne1617.xml");
//        //英格兰足球乙级联赛
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.LeagueTwo.1617.LeagueTwo1617.xml");
//        //英格兰联赛杯
//        schedules.add(0L, "469-XPathSportrard|");
//        //英格兰社区盾
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.England.CommunityShield.2016.CommunityShield2016.xml");
//        //英格兰足总杯
//        schedules.add(0L, "469-XPathSportrard|");
        //苏格兰联赛杯
//        schedules.add(0L, "469-XPathSportrard|");
//        //苏格兰足球冠军联赛
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Scotland.Championship.1617.Championship1617.xml");
//        //苏格兰足球超级联赛
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Scotland.Premiership.1617.Premiership1617.xml");

        //德国足球甲级联赛
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Germany.Bundesliga.1617.Bundesliga1617.xml");
//        // 西班牙足球甲级联赛
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Spain.PrimeraDivision.1617.PrimeraDivision1617.xml");

//        //【法国足球甲级联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.France.Ligue1.1617.Ligue11617.xml");

        // 【意大利足球甲级联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Italy.SerieA.1617.SerieA1617.xml");
//        // 【意大利杯】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Italy.CoppaItalia.1617.CoppaItalia1617.xml");
//
//        // 【欧洲冠军联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueQualification.1617.UEFAChampionsLeague1617Qualification.xml");
//        // 【欧足联欧洲联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupL.1617.UEFAEuropaLeague1617GroupL.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupK.1617.UEFAEuropaLeague1617GroupK.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupJ.1617.UEFAEuropaLeague1617GroupJ.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupI.1617.UEFAEuropaLeague1617GroupI.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupH.1617.UEFAEuropaLeague1617GroupH.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupG.1617.UEFAEuropaLeague1617GroupG.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupF.1617.UEFAEuropaLeague1617GroupF.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupE.1617.UEFAEuropaLeague1617GroupE.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupD.1617.UEFAEuropaLeague1617GroupD.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupC.1617.UEFAEuropaLeague1617GroupC.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupB.1617.UEFAEuropaLeague1617GroupB.xml|schedulesandresult_Soccer.InternationalClubs.UEFAEuropaLeagueGroupA.1617.UEFAEuropaLeague1617GroupA.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupH.1617.UEFAChampionsLeague1617GroupH.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupG.1617.UEFAChampionsLeague1617GroupG.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupF.1617.UEFAChampionsLeague1617GroupF.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupE.1617.UEFAChampionsLeague1617GroupE.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupD.1617.UEFAChampionsLeague1617GroupD.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupC.1617.UEFAChampionsLeague1617GroupC.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupB.1617.UEFAChampionsLeague1617GroupB.xml|schedulesandresult_Soccer.InternationalClubs.UEFAChampionsLeagueGroupA.1617.UEFAChampionsLeague1617GroupA.xml\n");
//        //【欧洲超级杯】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalClubs.UEFASuperCup.2016.UEFASuperCup2016.xml");
//        //【欧洲青年冠军联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupC.1617.UEFAYouthLeague1617GroupC.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupD.1617.UEFAYouthLeague1617GroupD.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupE.1617.UEFAYouthLeague1617GroupE.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupF.1617.UEFAYouthLeague1617GroupF.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupG.1617.UEFAYouthLeague1617GroupG.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupH.1617.UEFAYouthLeague1617GroupH.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueKnockoutstage.1617.UEFAYouthLeague1617Knockoutstage.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupA.1617.UEFAYouthLeague1617GroupA.xml|schedulesandresult_Soccer.InternationalYouth.UEFAYouthLeagueGroupB.1617.UEFAYouthLeague1617GroupB.xml");
//【世界少年足球锦标赛】
//        schedules.add(0L, "469-XPathSportrard|");
//【荷兰杯】
//        schedules.add(0L, "469-XPathSportrard|");
        // 【丹麦足球超级联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Denmark.Superligaen.1617.Superligaen1617.xml");

//// 【比利时足球甲级联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Belgium.ProLeague.1617.ProLeague1617.xml");
////【比利时超级杯】
//        schedules.add(0L, "469-XPathSportrard|");
////【乌克兰足球超级联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Ukraine.PremierLeague.1617.PremierLeague1617.xml");
//// 【葡萄牙足球超级联赛】
//        schedules.add(0L, "469-XPathSportrard|schedulesandresult_Soccer.Portugal.PrimeiraLiga.1617.PrimeiraLiga1617.xml");


//【【篮球】】
        //   schedules.add(102059002L, "499-XPathStats2|\\cba\\CBACHN_SCHEDULE.XML");//CBA
//        schedules.add(102045002L, "499-XPathStats|NBA_SCHEDULE.XML");//NBA
//【【棒球】】


        //【PLAYER-STANDING】-2016球员榜单接入列表

//        playerStandings.add(102059002L, "499-XPathStats2|\\cba\\CBACHN_LEADERS.XML");//CBA
//        playerStandings.add(102045002L, "499-XPathStats|NBA_LEADERS.XML");//NBA


        //【TEAM-STANDING】-2016球队榜单接入列表
//        teamStandings.add(102045002L, "499-XPathStats|NBA_TEAM_STANDINGS.XML");//NBA-联盟
//        teamStandings.add(102045002L, "499-XPathStats_0|NBA_TEAM_STANDINGS.XML");//NBA-分区
//        teamStandings.add(102059002L, "499-XPathStats2|\\cba\\NBA_TEAM_STANDINGS.XML");//CBA

        //【球队阵容】
        //CBA
        //  teamSeasons.add(102059002L, "499|\\cba\\CBACHN_ALL_ROSTER.XML");


        //CBA
//        teamStats.add(102059002L, "499|\\cba\\CBACHN_TEAM_STATS.XML");
        //CBA
//        playerStats.add(102059002L, "499|\\cba\\CBACHN_PLAYER_STATS.XML");
    }
}
