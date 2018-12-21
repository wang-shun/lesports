/*************************
 * config.thrift
 * Lesports Bole
 *************************************/
namespace java com.lesports.bole.api.vo

struct TOlympicConfig{
    //元素在xml中的相对路径
    1: optional string elementPath,
    //map中属性的key值
    2: optional string propertyName,
    //属性值对应的转化函数
    3: optional string formatterType,
    //注释
    4: optional string annotation,
    //右节点
    5: optional string rightElementPath,
    //运算符
    6: optional string op,

}

struct TOlympicLiveConfigSet{
    //配置id
    1: string id,
    //队伍统计配置
    3: optional list<TOlympicConfig> competitorStatsConfig,
    //个人统计配置
    4: optional list<TOlympicConfig> playerStatsConfig,
    //队伍扩展配置
    5: optional list<TOlympicConfig> teamExtendConfig,
    //个人扩展配置
    6: optional list<TOlympicConfig> playerExtendConfig,
    //比赛扩展配置
    7: optional list<TOlympicConfig> matchExtendConfig,
    8: optional list<TOlympicConfig> resultConfig,
}

struct TOlympicSettingDataSet{
    //配置id
    1: string id,
    //队伍统计配置条件属性
     2: optional set<string> competitorStatsConditionConfig,
    //队伍统计配置
    3: optional set<string> competitorStatsConfig,
     //个人统计配置条件属性
     4: optional set<string> playerStatsConditionConfig,
    //个人统计配置
    5: optional set<string> playerStatsConfig,
    //队伍扩展配置
    6: optional set<string> teamExtendConfig,
    //个人扩展配置
    7: optional set<string> playerExtendConfig,
    //比赛扩展配置
    8: optional set<string> matchExtendConfig,
     9: optional set<string> resultConditionConfig,
        //比赛成绩扩展配置
     10: optional set<string> resultConfig,
}
