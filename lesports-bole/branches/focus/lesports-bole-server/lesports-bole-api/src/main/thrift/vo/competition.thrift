/*************************
 * competition.thrift
 * Lesports Bole
 *************************************/
namespace java com.lesports.bole.api.vo
include "common.thrift"


/**
* 赛事信息
**/
struct  TBCompetition{
    //赛事id
    1: i64 id,
    //赛事名称
    2: optional string name,
    //赛事简称
    3: optional string abbreviation,
    //sms id
    4: optional i64 smsId,
    //状态
    5: optional common.TBoleStatus status;
}
