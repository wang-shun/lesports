/*************************
 * competitor.thrift
 * Lesports Bole
 *************************************/
namespace java com.lesports.bole.api.vo
include "common.thrift"

enum TCompetitorType{
    TEAM = 0;
    PLAYER = 1;
}

/**
* 对阵双方信息
**/
struct  TBCompetitor{
    //对阵双方id
    1: i64 id,
    //名称
    2: optional string name,
    //赛事id
    3: optional string nickname,
     //sms id
    4: optional i64 smsId,
    //对阵双方类型
    5: optional TCompetitorType type;
    //状态
    6: optional common.TBoleStatus status;
    //大项名称
    7: optional string gameName,
}
