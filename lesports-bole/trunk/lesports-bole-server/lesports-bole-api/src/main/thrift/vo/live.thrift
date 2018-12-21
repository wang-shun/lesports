/*************************
 * competition.thrift
 * Lesports Bole
 *************************************/
namespace java com.lesports.bole.api.vo
include "common.thrift"

/**
* 赛事信息
**/
struct  TBLive{
    //id
    1: i64 id,
    //页面跳转方
    2: optional string site,
    //直播类型
    3: optional common.TBoleLiveType type,
    //数据抓取来源
    4: optional common.TBoleSource source,
    //跳转地址
    5: optional string url;
    //伯乐赛程id
    6: optional i64 matchId;
}
