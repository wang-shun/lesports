/*************************
 * match.thrift
 * Lesports Bole
 *************************************/
namespace java com.lesports.bole.api.vo
include "common.thrift"

/**
* 比赛信息
**/
struct  TBMatch{
    //比赛id
    1: i64 id,
    //开始时间
    2: optional string startTime,
    //赛事id
    3: optional i64 cid,
     //sms id
    4: optional i64 smsId,
    //赛季id
    5: optional i64 csid;
    //对阵双方id
    6: optional list<i64> competitorIds,
    //伯乐比赛数据的状态
    7: optional common.TBoleStatus status;
    //比赛名称
    8: optional string name;
}
