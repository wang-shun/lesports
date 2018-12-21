package com.lesports.sms.cooperation.util;

import com.lesports.utils.LeProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhonglin on 2016/3/28.
 */
public class Constants {
    //是否执行定时任务
    public static final boolean quarzflag = LeProperties.getBoolean("quarz.flag", true);
    //文件名后缀
    public static final String fileextraname = LeProperties.getString("file.extra.name");
    //文件本地路径
    public static final String filelocalpath = LeProperties.getString("file.local.path");
    //文件上传路径
    public static final String fileuploadpath = LeProperties.getString("file.upload.path");
    //是否上传文件
    public static final boolean fileuploadflag = LeProperties.getBoolean("file.upload.flag", true);
    //百度搜索本地文件
    public static final String filebaidurootpath = LeProperties.getString("file.baidu.root.path");
    //百度搜索上传路径
    public static final String filebaiduuploadpath = LeProperties.getString("file.baidu.upload.path");
    //奥运会本地文件
    public static final String fileolyrootpath = LeProperties.getString("file.oly.root.path");
    //奥运会上传路径
    public static final String fileolyuploadpath = LeProperties.getString("file.oly.upload.path");
    //cms 版块数据获取路径
    public static final String CMS_DATA_URL = "http://www.lesports.com/cmsdata/block/";


    //搜狗奥运统计参数
    public static final String OLY_SOGOU_CH = "sogou_olympic";
    //360奥运统计参数
    public static final String OLY_360_NEW_CH = "360_olympic_news";
    //百度视频奥运统计参数
    public static final String OLY_BAIDU_VIDEO_CH = "v_baidu_olympic";
    //神马搜索奥运统计参数
    public static final String OLY_SM_VIDEO_CH = "sm_olympic";
    //百度搜索统计参数
    public static final String SEARCH_BAIDU_VIDEO_CH = "baidu_search";
    //百度中超统计参数
    public static final String CSL_BAIDU_CH = "zchao_baiduald";

    //百度阿拉丁比赛id和mid对应关系
    public static final Map<String, String> aladdinMap = new HashMap<String, String>();
    //360阿拉丁星期几  全称和简称对应关系
    public static final Map<String, String> weekMap = new HashMap<String, String>();

    //360阿拉丁球队 简称对应关系
    public static final Map<Long, String> shortNameMap = new HashMap<Long, String>();
    //360阿拉丁球队 介绍页对应关系
    public static final Map<Long, String> mlbTeamUrlMap = new HashMap<Long, String>();
    //360阿拉丁球队logo图片url对应关系
    public static final Map<Long, String> mlbLogoUrlMap = new HashMap<Long, String>();
    //百度12强赛logo图片url对应关系
    public static final Map<Long, String> top12LogoUrlMap = new HashMap<Long, String>();
    //百度12强赛球队和id对应关系
    public static final Map<Long, String> top12TeamMap = new HashMap<Long, String>();
    //360 赛事和文件名对应关系
    public static final Map<String, String> oneboxParamNameMap = new HashMap<String, String>();


    //中超赛事id
    public static final Long CSL_COMPETITION_ID = 47001L;
    //中国国家队id
    public static final Long CHINA_FOOTBALL_TEAM_ID = 1440006L;
    //世预赛12强赛事id
    public static final Long TOP12_COMPETITION_ID = 101425001L;
    //英超赛事id
    public static final Long EPL_COMPETITION_ID = 20001L;
    //意甲赛事id
    public static final Long SERIEA_COMPETITION_ID = 29001L;
    //中超2016赛季id
    public static final Long CSL_COMPETITION_SEASON_ID = 100538002l;
    //mlb赛事id
    public static final Long MLB_COMPETITION_ID = 100424001L;
    //nfl赛事id
    public static final Long NFL_COMPETITION_ID = 100050001L;
    //奥运会赛事id
    public static final Long OLYMPIC_COMPETITION_ID = 3001l;
    //积分榜类型
    public static final Long TOPLIST_SCORE_ID = 100162000L;
    //射手榜类型
    public static final Long TOPLIST_SCORER_ID = 100160000L;
    //轮次类型
    public static final Long ROUND_PARENT_KD = 100004000L;
    //战报标签id
    public static final Long REPORT_TAG_ID = 100491008L;

    static {
        aladdinMap.put("121672003", "847717");
        aladdinMap.put("121673003", "847719");
        aladdinMap.put("121674003", "847720");
        aladdinMap.put("121675003", "847721");
        aladdinMap.put("307425003", "847723");
        aladdinMap.put("121676003", "847718");
        aladdinMap.put("307424003", "847722");
        aladdinMap.put("121677003", "847724");
        aladdinMap.put("121679003", "847725");
        aladdinMap.put("121680003", "847726");
        aladdinMap.put("121681003", "847730");
        aladdinMap.put("121682003", "847729");
        aladdinMap.put("307426003", "847848");
        aladdinMap.put("121683003", "847732");
        aladdinMap.put("307427003", "847728");
        aladdinMap.put("121685003", "847734");
        aladdinMap.put("121684003", "847733");
        aladdinMap.put("307428003", "847739");
        aladdinMap.put("121688003", "847737");
        aladdinMap.put("121687003", "847735");
        aladdinMap.put("121686003", "847736");
        aladdinMap.put("121689003", "847738");
        aladdinMap.put("307429003", "847740");
        aladdinMap.put("121690003", "847741");
        aladdinMap.put("121692003", "847745");
        aladdinMap.put("121691003", "847742");
        aladdinMap.put("307430003", "847743");
        aladdinMap.put("121694003", "847747");
        aladdinMap.put("121696003", "847748");
        aladdinMap.put("121695003", "847744");
        aladdinMap.put("121693003", "847746");
        aladdinMap.put("121698003", "847749");
        aladdinMap.put("121699003", "847752");
        aladdinMap.put("121697003", "847751");
        aladdinMap.put("121701003", "847753");
        aladdinMap.put("121700003", "847750");
        aladdinMap.put("307432003", "847754");
        aladdinMap.put("121702003", "847755");
        aladdinMap.put("307431003", "847756");
        aladdinMap.put("307433003", "847757");
        aladdinMap.put("121705003", "847759");
        aladdinMap.put("121704003", "847760");
        aladdinMap.put("121706003", "847764");
        aladdinMap.put("121703003", "847758");
        aladdinMap.put("121707003", "847762");
        aladdinMap.put("121708003", "847763");
        aladdinMap.put("307434003", "847761");
        aladdinMap.put("121709003", "847765");
        aladdinMap.put("121710003", "847766");
        aladdinMap.put("121711003", "847767");
        aladdinMap.put("307435003", "847771");
        aladdinMap.put("121714003", "847770");
        aladdinMap.put("121713003", "847768");
        aladdinMap.put("121712003", "847769");
        aladdinMap.put("307436003", "847772");
        aladdinMap.put("121715003", "847773");
        aladdinMap.put("307437003", "847775");
        aladdinMap.put("121716003", "847774");
        aladdinMap.put("121718003", "847778");
        aladdinMap.put("121719003", "847779");
        aladdinMap.put("121720003", "847776");
        aladdinMap.put("307438003", "847780");
        aladdinMap.put("121717003", "847777");
        aladdinMap.put("121721003", "847781");
        aladdinMap.put("121722003", "847782");
        aladdinMap.put("121723003", "847783");
        aladdinMap.put("307478003", "847784");
        aladdinMap.put("121724003", "847785");
        aladdinMap.put("121726003", "847788");
        aladdinMap.put("121725003", "847787");
        aladdinMap.put("307439003", "847786");
        aladdinMap.put("307440003", "847789");
        aladdinMap.put("121727003", "847790");
        aladdinMap.put("121728003", "847791");
        aladdinMap.put("121729003", "847792");
        aladdinMap.put("121730003", "847793");
        aladdinMap.put("307441003", "847795");
        aladdinMap.put("121731003", "847794");
        aladdinMap.put("121732003", "847796");
        aladdinMap.put("121733003", "847797");
        aladdinMap.put("121735003", "847798");
        aladdinMap.put("121734003", "847799");
        aladdinMap.put("307442003", "847801");
        aladdinMap.put("307443003", "847803");
        aladdinMap.put("121737003", "847800");
        aladdinMap.put("121736003", "847802");
        aladdinMap.put("121738003", "847804");
        aladdinMap.put("307444003", "847806");
        aladdinMap.put("121739003", "847805");
        aladdinMap.put("121740003", "847807");
        aladdinMap.put("121741003", "847809");
        aladdinMap.put("121742003", "847810");
        aladdinMap.put("121743003", "847811");
        aladdinMap.put("307445003", "847808");
        aladdinMap.put("121744003", "847812");
        aladdinMap.put("307446003", "847813");
        aladdinMap.put("121746003", "847815");
        aladdinMap.put("307479003", "847817");
        aladdinMap.put("121747003", "847816");
        aladdinMap.put("121745003", "847814");
        aladdinMap.put("121749003", "847818");
        aladdinMap.put("121748003", "847819");
        aladdinMap.put("121750003", "847820");
        aladdinMap.put("307447003", "847821");
        aladdinMap.put("121751003", "847822");
        aladdinMap.put("121753003", "847824");
        aladdinMap.put("121752003", "847823");
        aladdinMap.put("121754003", "847825");
        aladdinMap.put("121756003", "847828");
        aladdinMap.put("121755003", "847826");
        aladdinMap.put("307448003", "847827");
        aladdinMap.put("307449003", "847829");
        aladdinMap.put("307450003", "847831");
        aladdinMap.put("121757003", "847830");
        aladdinMap.put("121758003", "847832");
        aladdinMap.put("121759003", "847833");
        aladdinMap.put("121761003", "847835");
        aladdinMap.put("121762003", "847836");
        aladdinMap.put("121760003", "847834");
        aladdinMap.put("307451003", "847837");
        aladdinMap.put("121764003", "847839");
        aladdinMap.put("121765003", "847840");
        aladdinMap.put("121763003", "847838");
        aladdinMap.put("121768003", "847844");
        aladdinMap.put("121766003", "847841");
        aladdinMap.put("307452003", "847843");
        aladdinMap.put("121767003", "847842");
        aladdinMap.put("121769003", "847845");
        aladdinMap.put("307453003", "847727");
        aladdinMap.put("121771003", "847847");
        aladdinMap.put("121772003", "847849");
        aladdinMap.put("121770003", "847846");
        aladdinMap.put("121773003", "847850");
        aladdinMap.put("307454003", "847851");
        aladdinMap.put("121774003", "847852");
        aladdinMap.put("121778003", "847856");
        aladdinMap.put("121780003", "847859");
        aladdinMap.put("121777003", "847855");
        aladdinMap.put("121776003", "847854");
        aladdinMap.put("121775003", "847853");
        aladdinMap.put("307455003", "847858");
        aladdinMap.put("121779003", "847857");
        aladdinMap.put("307456003", "847860");
        aladdinMap.put("121783003", "847863");
        aladdinMap.put("121784003", "847864");
        aladdinMap.put("121782003", "847862");
        aladdinMap.put("121781003", "847861");
        aladdinMap.put("307457003", "847868");
        aladdinMap.put("121785003", "847866");
        aladdinMap.put("121787003", "847867");
        aladdinMap.put("121788003", "847869");
        aladdinMap.put("121790003", "847871");
        aladdinMap.put("121791003", "847872");
        aladdinMap.put("121789003", "847870");
        aladdinMap.put("307458003", "847873");
        aladdinMap.put("121792003", "847875");
        aladdinMap.put("307459003", "847876");
        aladdinMap.put("121793003", "847874");
        aladdinMap.put("307460003", "847878");
        aladdinMap.put("121796003", "847880");
        aladdinMap.put("121795003", "847879");
        aladdinMap.put("121794003", "847877");
        aladdinMap.put("121798003", "847883");
        aladdinMap.put("121797003", "847881");
        aladdinMap.put("307461003", "847882");
        aladdinMap.put("121799003", "847884");
        aladdinMap.put("307462003", "847885");
        aladdinMap.put("121801003", "847887");
        aladdinMap.put("121802003", "847888");
        aladdinMap.put("307463003", "847889");
        aladdinMap.put("121800003", "847886");
        aladdinMap.put("121804003", "847890");
        aladdinMap.put("121803003", "847892");
        aladdinMap.put("121805003", "847891");
        aladdinMap.put("121806003", "847893");
        aladdinMap.put("307464003", "847894");
        aladdinMap.put("121807003", "847896");
        aladdinMap.put("121808003", "847897");
        aladdinMap.put("121811003", "847900");
        aladdinMap.put("307465003", "847898");
        aladdinMap.put("121810003", "847899");
        aladdinMap.put("307466003", "847901");
        aladdinMap.put("121814003", "847904");
        aladdinMap.put("121812003", "847902");
        aladdinMap.put("121815003", "847905");
        aladdinMap.put("121816003", "847906");
        aladdinMap.put("121813003", "847903");
        aladdinMap.put("121817003", "847908");
        aladdinMap.put("307480003", "847907");
        aladdinMap.put("307467003", "847909");
        aladdinMap.put("121819003", "847910");
        aladdinMap.put("121820003", "847912");
        aladdinMap.put("121818003", "847911");
        aladdinMap.put("307468003", "847916");
        aladdinMap.put("121821003", "847913");
        aladdinMap.put("121822003", "847914");
        aladdinMap.put("121823003", "847915");
        aladdinMap.put("307469003", "847917");
        aladdinMap.put("121824003", "847918");
        aladdinMap.put("121825003", "847919");
        aladdinMap.put("121829003", "847924");
        aladdinMap.put("121826003", "847921");
        aladdinMap.put("121827003", "847922");
        aladdinMap.put("307470003", "847920");
        aladdinMap.put("121828003", "847923");
        aladdinMap.put("121831003", "847927");
        aladdinMap.put("307471003", "847926");
        aladdinMap.put("121830003", "847925");
        aladdinMap.put("121832003", "847928");
        aladdinMap.put("307472003", "847932");
        aladdinMap.put("121833003", "847929");
        aladdinMap.put("121834003", "847930");
        aladdinMap.put("121835003", "847931");
        aladdinMap.put("121836003", "847933");
        aladdinMap.put("307481003", "847936");
        aladdinMap.put("121839003", "847937");
        aladdinMap.put("121838003", "847935");
        aladdinMap.put("121837003", "847934");
        aladdinMap.put("121840003", "847939");
        aladdinMap.put("121841003", "847940");
        aladdinMap.put("307473003", "847938");
        aladdinMap.put("121844003", "847943");
        aladdinMap.put("121842003", "847941");
        aladdinMap.put("121846003", "847946");
        aladdinMap.put("121843003", "847942");
        aladdinMap.put("307475003", "847947");
        aladdinMap.put("121847003", "847948");
        aladdinMap.put("121845003", "847944");
        aladdinMap.put("307474003", "847945");
        aladdinMap.put("121849003", "847951");
        aladdinMap.put("121851003", "847953");
        aladdinMap.put("307477003", "847954");
        aladdinMap.put("121853003", "847956");
        aladdinMap.put("121852003", "847955");
        aladdinMap.put("121850003", "847952");
        aladdinMap.put("121848003", "847950");
        aladdinMap.put("307476003", "847949");
        aladdinMap.put("121809003", "847895");
        aladdinMap.put("121786003", "847865");
        aladdinMap.put("121678003", "847731");


        weekMap.put("星期一", "周一");
        weekMap.put("星期二", "周二");
        weekMap.put("星期三", "周三");
        weekMap.put("星期四", "周四");
        weekMap.put("星期五", "周五");
        weekMap.put("星期六", "周六");
        weekMap.put("星期日", "周日");

        //球队简称
        shortNameMap.put(112104006L, "扬基");
        shortNameMap.put(112111006L, "红袜");
        shortNameMap.put(112118006L, "金莺");
        shortNameMap.put(112120006L, "光芒");
        shortNameMap.put(112116006L, "蓝鸟");
        shortNameMap.put(112101006L, "皇家");
        shortNameMap.put(112126006L, "老虎");
        shortNameMap.put(112125006L, "白袜");
        shortNameMap.put(112123006L, "印第安人");
        shortNameMap.put(112121006L, "双城");
        shortNameMap.put(112108006L, "天使");
        shortNameMap.put(112130006L, "水手");
        shortNameMap.put(112128006L, "运动家");
        shortNameMap.put(112103006L, "太空人");
        shortNameMap.put(112107006L, "游骑兵");
        shortNameMap.put(112099006L, "大都会");
        shortNameMap.put(112112006L, "国民");
        shortNameMap.put(112122006L, "勇士");
        shortNameMap.put(112119006L, "费城人");
        shortNameMap.put(112117006L, "马林鱼");
        shortNameMap.put(112109006L, "小熊");
        shortNameMap.put(112124006L, "海盗");
        shortNameMap.put(112127006L, "酿酒人");
        shortNameMap.put(112129006L, "红人");
        shortNameMap.put(112110006L, "红雀");
        shortNameMap.put(112133006L, "洛基");
        shortNameMap.put(112132006L, "教士");
        shortNameMap.put(112131006L, "响尾蛇");
        shortNameMap.put(112106006L, "巨人");
        shortNameMap.put(112105006L, "道奇");

        mlbTeamUrlMap.put(112104006L, "http://www.lesports.com/topic/s/nyyankees/");
        mlbTeamUrlMap.put(112111006L, "http://www.lesports.com/topic/s/redsox/");
        mlbTeamUrlMap.put(112118006L, "http://www.lesports.com/topic/s/orioles/");
        mlbTeamUrlMap.put(112120006L, "http://www.lesports.com/topic/s/rays/");
        mlbTeamUrlMap.put(112116006L, "http://www.lesports.com/topic/s/bluejays/");
        mlbTeamUrlMap.put(112101006L, "http://www.lesports.com/topic/s/royals/");
        mlbTeamUrlMap.put(112126006L, "http://www.lesports.com/topic/s/tigers/");
        mlbTeamUrlMap.put(112125006L, "http://www.lesports.com/topic/s/whitesox/");
        mlbTeamUrlMap.put(112123006L, "http://www.lesports.com/topic/s/indians/");
        mlbTeamUrlMap.put(112121006L, "http://www.lesports.com/topic/s/twins/");
        mlbTeamUrlMap.put(112108006L, "http://www.lesports.com/topic/s/laangels/");
        mlbTeamUrlMap.put(112130006L, "http://www.lesports.com/topic/s/mariners/");
        mlbTeamUrlMap.put(112128006L, "http://www.lesports.com/topic/s/athletics/");
        mlbTeamUrlMap.put(112103006L, "http://www.lesports.com/topic/s/astros/");
        mlbTeamUrlMap.put(112107006L, "http://www.lesports.com/topic/s/rangers/");
        mlbTeamUrlMap.put(112099006L, "http://www.lesports.com/topic/s/nymets/");
        mlbTeamUrlMap.put(112112006L, "http://www.lesports.com/topic/s/nationals/");
        mlbTeamUrlMap.put(112122006L, "http://www.lesports.com/topic/s/braves/");
        mlbTeamUrlMap.put(112119006L, "http://www.lesports.com/topic/s/phillies/");
        mlbTeamUrlMap.put(112117006L, "http://www.lesports.com/topic/s/marlins/");
        mlbTeamUrlMap.put(112109006L, "http://www.lesports.com/topic/s/cubs/");
        mlbTeamUrlMap.put(112124006L, "http://www.lesports.com/topic/s/pirates/");
        mlbTeamUrlMap.put(112127006L, "http://www.lesports.com/topic/s/brewers/");
        mlbTeamUrlMap.put(112129006L, "http://www.lesports.com/topic/s/reds/");
        mlbTeamUrlMap.put(112110006L, "http://www.lesports.com/topic/s/cardinals/");
        mlbTeamUrlMap.put(112133006L, "http://www.lesports.com/topic/s/rockies/");
        mlbTeamUrlMap.put(112132006L, "http://www.lesports.com/topic/s/padres/");
        mlbTeamUrlMap.put(112131006L, "http://www.lesports.com/topic/s/diamondbacks/");
        mlbTeamUrlMap.put(112106006L, "http://www.lesports.com/topic/s/sfgiants/");
        mlbTeamUrlMap.put(112105006L, "http://www.lesports.com/topic/s/ladodgers/");


        mlbLogoUrlMap.put(112104006L, "http://i2.letvimg.com/lc04_iscms/201604/26/11/34/d27b61800d8f47c49633dbcaef8cce4a.png");
        mlbLogoUrlMap.put(112111006L, "http://i3.letvimg.com/lc05_iscms/201604/11/11/40/d4416f1f3d164592a7c0734c800b63c2.png");
        mlbLogoUrlMap.put(112118006L, "http://i0.letvimg.com/lc05_iscms/201604/11/11/43/fa4f634bb1284485806d045341b7f4e6.PNG");
        mlbLogoUrlMap.put(112120006L, "http://i3.letvimg.com/lc04_iscms/201605/05/18/37/f74e9c7f936b4bbfb71e28de563ab1d1.png");
        mlbLogoUrlMap.put(112116006L, "http://i0.letvimg.com/lc04_iscms/201604/11/11/42/02d9d6d574784d6395cb195ef9686008.png");
        mlbLogoUrlMap.put(112101006L, "http://i3.letvimg.com/lc05_iscms/201605/03/11/28/c52a43adbbaf4ca5a646d4f7109ec00d.png");
        mlbLogoUrlMap.put(112126006L, "http://i1.letvimg.com/lc04_iscms/201605/05/16/44/93e608b6fffe47c18d5efdc28b14eeda.png");
        mlbLogoUrlMap.put(112125006L, "http://i1.letvimg.com/lc05_iscms/201605/25/15/02/a3c8193d97724a55834219ae9e95f264.png");
        mlbLogoUrlMap.put(112123006L, "http://i2.letvimg.com/lc06_iscms/201605/05/18/35/5e02e9dbae4b44efb523a1a2501a2bf4.png");
        mlbLogoUrlMap.put(112121006L, "http://i0.letvimg.com/lc04_iscms/201605/05/16/37/2cc8317c8c004d64ab8a5117899a1dbb.png");
        mlbLogoUrlMap.put(112108006L, "http://i0.letvimg.com/lc04_iscms/201605/25/15/05/fc105e813d78416ca9c111ec40604c4e.png");
        mlbLogoUrlMap.put(112130006L, "http://i2.letvimg.com/lc05_iscms/201604/19/12/41/0fb76f4e36d047bda5e39a6b6ac7f305.png");
        mlbLogoUrlMap.put(112128006L, "http://i0.letvimg.com/lc05_iscms/201605/05/18/38/bbdd44bfc8fe4933b3834f11420c2e63.png");
        mlbLogoUrlMap.put(112103006L, "http://i3.letvimg.com/lc07_iscms/201604/26/11/34/5ced72a4c46744cea4cfca8c513a9f14.png");
        mlbLogoUrlMap.put(112107006L, "http://i1.letvimg.com/lc06_iscms/201604/19/12/28/d5799ed1500a45ab8a8dc5c9a317342f.png");
        mlbLogoUrlMap.put(112099006L, "http://i3.letvimg.com/lc03_iscms/201510/27/10/27/38c4a435b96c4f6c905adfa7bdd9b13a.png");
        mlbLogoUrlMap.put(112112006L, "http://i0.letvimg.com/lc07_iscms/201604/19/11/34/0838a4a8d463410d9893ffa8e81f4286.png");
        mlbLogoUrlMap.put(112122006L, "http://i3.letvimg.com/lc02_iscms/201605/05/17/42/12f6dbb48bf04c2d8f11902654eeebdd.png");
        mlbLogoUrlMap.put(112119006L, "http://i2.letvimg.com/lc04_iscms/201605/05/18/17/c928f2aede9a41048bb27776c0788cc1.png");
        mlbLogoUrlMap.put(112117006L, "http://i1.letvimg.com/lc05_iscms/201604/19/11/35/c9d6a127601d47019de90aa57d580f97.png");
        mlbLogoUrlMap.put(112109006L, "http://i0.letvimg.com/lc05_iscms/201604/19/09/54/710a6b0067ce4ccda0f31d8b84aebb9b.png");
        mlbLogoUrlMap.put(112124006L, "http://i3.letvimg.com/lc07_iscms/201605/05/18/19/cd83859c034244d19a93d073da43577b.png");
        mlbLogoUrlMap.put(112127006L, "http://i0.letvimg.com/lc05_iscms/201605/05/18/22/9f072fae78d249ee9ec1188033408b59.png");
        mlbLogoUrlMap.put(112129006L, "http://i0.letvimg.com/lc05_iscms/201605/05/18/24/6dab889ea77641f38da27f05e66c268b.png");
        mlbLogoUrlMap.put(112110006L, "http://i0.letvimg.com/lc06_iscms/201604/19/09/54/f7b4ee21e23f4587aaddb880701ddfba.png");
        mlbLogoUrlMap.put(112133006L, "http://i0.letvimg.com/lc04_iscms/201605/05/18/28/8770d0a4bd8447a9b696969773826b66.png");
        mlbLogoUrlMap.put(112132006L, "http://i1.letvimg.com/lc06_iscms/201605/05/18/33/26e1cec3e2c64833a6b3b4ddbd432665.png");
        mlbLogoUrlMap.put(112131006L, "http://i1.letvimg.com/lc07_iscms/201604/11/11/43/ffe44f06c6094af297ff662ec48453f6.png");
        mlbLogoUrlMap.put(112106006L, "http://i3.letvimg.com/lc02_iscms/201604/06/18/04/e966375fa6ce466d90ac31a3ac27b293.png");
        mlbLogoUrlMap.put(112105006L, "http://i3.letvimg.com/lc02_iscms/201605/06/12/01/8127f7da01b54f99bd6a5fffaa9bcc9e.png");

        top12LogoUrlMap.put(112084006L, "http://c.hiphotos.baidu.com/image/pic/item/14ce36d3d539b60018470b1ce150352ac75cb78b.jpg");
        top12LogoUrlMap.put(1645006L, "http://f.hiphotos.baidu.com/image/pic/item/2934349b033b5bb5d45e24553ed3d539b700bc8b.jpg");
        top12LogoUrlMap.put(2042006L, "http://c.hiphotos.baidu.com/image/pic/item/10dfa9ec8a136327905bc698998fa0ec09fac7e3.jpg");
        top12LogoUrlMap.put(1440006L, "http://b.hiphotos.baidu.com/image/pic/item/c8ea15ce36d3d53997b40b683287e950342ab08b.jpg");
        top12LogoUrlMap.put(880006L, "http://d.hiphotos.baidu.com/image/pic/item/a71ea8d3fd1f4134ecd401d52d1f95cad0c85ebd.jpg");
        top12LogoUrlMap.put(111389006L, "http://g.hiphotos.baidu.com/image/pic/item/4034970a304e251feb14e053af86c9177e3e53bd.jpg");
        top12LogoUrlMap.put(868006L, "http://d.hiphotos.baidu.com/image/pic/item/d50735fae6cd7b8935a23e28072442a7d8330e4a.jpg");
        top12LogoUrlMap.put(907006L, "http://e.hiphotos.baidu.com/image/pic/item/a9d3fd1f4134970a4c9014849dcad1c8a6865dbd.jpg");
        top12LogoUrlMap.put(901006L, "http://e.hiphotos.baidu.com/image/pic/item/34fae6cd7b899e51a3403ebf4aa7d933c9950d4a.jpg");
        top12LogoUrlMap.put(883006L, "http://g.hiphotos.baidu.com/image/pic/item/63d0f703918fa0ecfe7160a62e9759ee3c6ddbe3.jpg");
        top12LogoUrlMap.put(115413006L, "http://c.hiphotos.baidu.com/image/pic/item/622762d0f703918f4a4e3461593d269758eec4fa.jpg");
        top12LogoUrlMap.put(893006L, "http://f.hiphotos.baidu.com/image/pic/item/b3119313b07eca80480d3e4c992397dda1448314.jpg");



        top12TeamMap.put(112084006L,"伊朗");
        top12TeamMap.put(1645006L,"卡塔尔");
        top12TeamMap.put(2042006L,"韩国");
        top12TeamMap.put(1440006L,"中国");
        top12TeamMap.put(880006L,"乌兹别克");
        top12TeamMap.put(111389006L,"叙利亚");
        top12TeamMap.put(868006L,"澳大利亚");
        top12TeamMap.put(907006L,"伊拉克");
        top12TeamMap.put(901006L,"日本");
        top12TeamMap.put(893006L,"阿联酋");
        top12TeamMap.put(883006L,"沙特阿拉伯");
        top12TeamMap.put(115413006L,"泰国");

        //中超
        oneboxParamNameMap.put("file_name_47001","onebox2016csl");
        oneboxParamNameMap.put("competition_name_47001","中超");
        oneboxParamNameMap.put("ch_47001","onebox");
        oneboxParamNameMap.put("domain_47001","http://csl.lesports.com/");
        oneboxParamNameMap.put("fixture_47001","http://www.lesports.com/column/csl2016/csl-schedule/index.shtml");
        oneboxParamNameMap.put("score_47001","http://www.lesports.com/column/csl2016/scoredata/index.shtml");
        oneboxParamNameMap.put("scorer_47001","http://www.lesports.com/column/csl2016/shotdata/index.shtml");
        //英超
        oneboxParamNameMap.put("file_name_20001","onebox2016epl");
        oneboxParamNameMap.put("competition_name_20001","英超");
        oneboxParamNameMap.put("ch_20001","onebox_epl");
        oneboxParamNameMap.put("domain_20001","http://epl.lesports.com/");
        oneboxParamNameMap.put("fixture_20001","http://epl.lesports.com/fixtures");
        oneboxParamNameMap.put("score_20001","http://epl.lesports.com/table");
        oneboxParamNameMap.put("scorer_20001","http://epl.lesports.com/goals");
        //意甲
        oneboxParamNameMap.put("file_name_29001","onebox2016seriea");
        oneboxParamNameMap.put("competition_name_29001","意甲");
        oneboxParamNameMap.put("ch_29001","onebox_seriea");
        oneboxParamNameMap.put("domain_29001","http://seriea.lesports.com/");
        oneboxParamNameMap.put("fixture_29001","http://seriea.lesports.com/fixtures");
        oneboxParamNameMap.put("score_29001","http://seriea.lesports.com/table");
        oneboxParamNameMap.put("scorer_29001","http://seriea.lesports.com/goals");

    }

    public static final String getAladdinCode(String mid) {
        return aladdinMap.get(mid);
    }

    public static final String getWeekNick(String week) {
        return weekMap.get(week);
    }

    public static final String getShortName(Long id) {
        return shortNameMap.get(id);
    }

    public static final String getMlbTeamUrl(Long id) {
        return mlbTeamUrlMap.get(id);
    }

    public static final String getMlbLogoUrl(Long id) {
        return mlbLogoUrlMap.get(id);
    }
}
