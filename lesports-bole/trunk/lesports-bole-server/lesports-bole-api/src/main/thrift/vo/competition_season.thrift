/*************************
 * competition_season.thrift
 * Lesports Bole
 *************************************/
namespace java com.lesports.bole.api.vo


/**
* 赛季信息
**/
struct  TBCompetitionSeason{
    //赛季id
    1: i64 id,
    //赛事名称
    2: optional string name,
    //赛事id
    3: optional i64 competitionId,
     //sms id
    4: optional i64 smsId,
}
