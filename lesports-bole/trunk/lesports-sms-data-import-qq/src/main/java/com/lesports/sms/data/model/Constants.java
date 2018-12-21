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

    public static Integer j = 220;
    public static Integer MAX_TRY_COUNT_MESSAGE = 2;


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
        //【赛程列表】
        //NBAschedules.add(102045002L, "479---JsonPathQQ---http://matchweb.sports.qq.com/kbs/list?from=NBA_PC&columnId=100000&startTime=2017-01-01&endTime=2017-04-14");
        schedules.add(102045002L, "479---JsonPathQQ---http://matchweb.sports.qq.com/kbs/list?from=NBA_PC&columnId=100000&startTime=2017-02-01&endTime=2017-04-14");
        //获取CBA第一轮的赛程  schedules.add(102059002L, "479---JsonPathQQ_1---http://111.161.111.20/match/list?columnId=100008&date=2016-11-06&flag=1&os=android&osvid=22&appvid=4.4.2&appvcode=442&network=wifi&store=180&width=1080&height=1920&deviceId=869322020575823&guid=06C4568A00E081E142ED036B565BE58B&omgid=951f04154fcbc545fe1a304f37cf9ffead1a0010211707&omgbizid=e48ddb3a666a2a4c8868965a1f737aaf657f0120211707");
        schedules.add(102059002L, "479---JsonPathQQ_1---http://111.161.111.20/match/list?columnId=100008&os=android&osvid=22&appvid=4.4.2&appvcode=442&network=wifi&store=180&width=1080&height=1920&deviceId=869322020575823&guid=06C4568A00E081E142ED036B565BE58B&omgid=951f04154fcbc545fe1a304f37cf9ffead1a0010211707&omgbizid=e48ddb3a666a2a4c8868965a1f737aaf657f0120211707");
        //【赛季teamSeason列表】
        //骑士
        teamSeasonStats.add(102059002L, "479----JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:3704,4840,4884,4391,3835,4898,3523,3934,3601,4935,5370,3598,1962937445,3752,3750&from=sportsdatabase");
        //猛龙
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4152,4614,4729,4886,4632,4911,5014,1962937454,5186,5589,1962937422,1962937542,5331,5566,5055&from=sportsdatabase");
        // 凯尔特人
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4750,4942,5317,5068,4245,3982,5559,5164,4644,1962937413,5023,3944,5579,5328,1962937432&from=sportsdatabase");
        //黄蜂
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4487,4890,5008,4296,5155,5550,3928,4333,4479,4288,5018,5622,5105,5605,5651&from=sportsdatabase");
        // 尼克斯
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5544,3706,4484,4387,4615,5142,4287,5083,5580,4966,1962937619,3844,5611,1962937596,1962937600&from=sportsdatabase");
        //老鹰
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5187,4175,5102,3754,3818,4141,5214,5194,1962937715,3831,1962937423,5077,1962937425,5218,5354,4306&from=sportsdatabase");
        // 公牛
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4912,3708,4477,4631,4149,4905,5204,5322,5562,5481,5565,1962937424,5162,1962937464,5572&from=sportsdatabase");
        // 雄鹿
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5185,5293,5190,5249,1962937442,5020,4722,5095,3333,4388,5060,5560,1962937420,4160,5073&from=sportsdatabase");
        //  步行者
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4624,4290,5553,4725,3966,5412,5393,3832,4304,4932,4293,4748,5595,1962937451,5577&from=sportsdatabase");
        //奇才
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:3983,4716,5406,5009,4894,5557,4648,5066,5160,4298,5025,1962937668,1962937670,3954,1962937667&from=sportsdatabase");
        //活塞
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4901,5159,4895,5015,4800,4922,5150,5549,3845,4906,5581,5608,5195,1962937418,1962937446&from=sportsdatabase");
        //魔术
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5054,4486,5321,5295,4888,4897,4247,4471,5398,4646,5555,5372,5411,1962937440,5240&from=sportsdatabase");
        //热火
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4764,5376,4507,5270,4621,5585,5554,5010,4314,4633,4731,4883,5146,3765,3707&from=sportsdatabase");
        // 篮网
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5535,4913,4754,4472,5344,5563,5079,1962937455,3653,4135,4795,5152,1962937633,1962937419,5349,5569,4759&from=sportsdatabase");
        //76ren
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5253,5726,5319,5323,4617,5545,5652,5294,5106,5578,1962937457,4473,5157,1962937408&from=sportsdatabase");
        //勇士
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4244,4892,4612,5069,3826,3821,3745,5237,3721,1962937450,5573,4480,5385,3847,1962937434&from=sportsdatabase");
        //马刺
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4896,4130,3513,4660,4651,3527,5620,3956,3380,5256,5341,4926,5475,1962937436,1962937501&from=sportsdatabase");
        //快船
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4497,3930,4561,3407,5395,5016,5401,3931,4478,4719,3959,3253,3995,1962937438,1962937433&from=sportsdatabase");
        //火箭
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4563,3860,4483,4469,5336,4647,5558,5391,4285,5574,5410,5329,1962937694,4525,1962937452,4902&from=sportsdatabase");
        // 雷霆
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4390,5196,5163,5153,1962937414,5350,4899,5366,4527,5225,5202,4915,3962,3715,5340,5552&from=sportsdatabase");
        // 爵士
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5197,4724,5334,4916,3520,5551,5493,5316,3724,4488,4718,5209,5217,1962937441,4893&from=sportsdatabase");
        //灰熊
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4325,5262,5583,3842,4246,3248,3531,5220,5567,5282,1962937547,1962937415,4920,1962937431,4634,4286&from=sportsdatabase");
        //国王
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4720,4136,4623,4682,3643,4626,4305,4485,5158,4371,5548,4628,4154,1962937435,1962937426,1962937430&from=sportsdatabase");
        //掘金
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4468,5556,4301,3837,5352,4904,1962937421,5074,5327,5330,1962937428,4489,4686,1962937416,3404,5346&from=sportsdatabase");
        //开拓者
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5012,5405,5021,5201,5192,4717,4728,5017,4723,5320,5335,1962937449,5584,1962937487,5064&from=sportsdatabase");
        //醍醐
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5007,5486,5193,5024,4937,5388,1962937411,4622,4638,4499,4482,4771,5576,1962937444,4564,4710,5199,4757&from=sportsdatabase");
        //湖人
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5357,1962937412,3824,3971,5318,4794,4294,5606,5543,5383,5011,3989,5630,3339,1962937462&from=sportsdatabase");
        // 森林狼
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5292,5324,5546,5191,4610,1962937410,5165,5509,4726,5564,4475,5326,4613,5434,4493&from=sportsdatabase");
        // 太阳
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:4749,5547,5396,4889,4300,5156,3512,5413,1962937417,3731,1962937409,1962937439,5770,5057&from=sportsdatabase");
        //小牛
        teamSeasonStats.add(102059002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=8&dimId=5&params=t1:5013,4694,5245,3929,1962937618,5356,5561,3927,5616,4203,3822,1962937617,3252,1962937616,1962937448,5071,5212&from=sportsdatabase");


        //【比分直播文件列表】
        //lives.add(102045002L, "479---JsonPathQQ---http://sportswebapi.qq.com/kbs/matchStat?from=nba_database&selectParams=teamRank,periodGoals,playerStats,nbaPlayerMatchTotal,maxPlayers&mid=100000:1469471");//NBA
        lives.add(102045002L, "479---JsonPathQQ---http://sportswebapi.qq.com/kbs/matchStat?from=nba_database&selectParams=teamRank,periodGoals,playerStats,nbaPlayerMatchTotal,maxPlayers&mid=?");
        lives.add(102059002L, "479---JsonPathQQ_1---http://111.161.111.20/match/statDetail?mid=?&os=android&osvid=22&appvid=4.3.1&appvcode=431&network=wifi&store=180&width=1080&height=1920&deviceId=869322020575823&guid=06C4568A00E081E142ED036B565BE58B&omgid=951f04154fcbc545fe1a304f37cf9ffead1a0010211707&omgbizid=e48ddb3a666a2a4c8868965a1f737aaf657f0120211707");


        //【PLAYER-STANDING-2016球员榜单接入列表】
        playerStandings.add(102045002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=10&dimId=53,54,55,56,57,58&from=sportsdatabase&limit=1000&params=t2:2016|t3:1");//NBA
        playerStandings.add(102045002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=10&dimId=53,54,55,56,57,58&from=sportsdatabase&limit=20&params=t2:2016|t3:1");//NBA


        //【TEAM-STANDING-2016球队榜单接入列表】
        teamStandings.add(102045002L, "479---JsonPathQQ---http://matchweb.sports.qq.com/rank/team?competitionId=100000&from=NBA_PC");//NBA

        //【Team-season-stat】
        // teamStats.add(102045002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=12&dimId=15&params=t1:18&from=sportsdatabase");//NBA
        teamStats.add(102045002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=12&dimId=15&params=t1:?&from=sportsdatabase");//NBA

        //【player-season-stat】
        // playerStats.add(102045002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=10&dimId=9&params=t2:2016|t3:1|t1:3704&from=sportsdatabase");//NBA
        playerStats.add(102045002L, "479---JsonPathQQ---http://ziliaoku.sports.qq.com/cube/index?cubeId=10&dimId=9&params=t2:2016|t3:1|t1:?&from=sportsdatabase");//NBA
    }
}
