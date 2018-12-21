/**************************************
 * bole_internal_service.thrift
 * Lesports Bole Internal Service
 *************************************/


namespace java com.lesports.bole.api.service
include "config.thrift"
include "competition.thrift"
include "competition_season.thrift"
include "competitor.thrift"
include "match.thrift"
include "live.thrift"
include "news.thrift"

service TBoleApiService{

	/**
	* 获取赛事信息
	**/
	competition.TBCompetition getCompetitionById(1:i64 entityId);

	/**
	* 获取赛季信息
	**/
	competition_season.TBCompetitionSeason getCompetitionSeasonById(1:i64 entityId);

	/**
	* 获取对阵双方信息
	**/
	competitor.TBCompetitor getCompetitorById(1:i64 entityId);

	/**
	* 获取比赛信息
	**/
	match.TBMatch getMatchById(1:i64 entityId);

	/**
	* 获取赛事信息
	**/
	competition.TBCompetition getCompetitionBySmsId(1:i64 entityId);

	/**
	* 获取赛季信息
	**/
	competition_season.TBCompetitionSeason getCompetitionSeasonBySmsId(1:i64 entityId);

	/**
	* 获取对阵双方信息
	**/
	competitor.TBCompetitor getCompetitorBySmsId(1:i64 entityId);

	/**
	* 获取比赛信息
	**/
	match.TBMatch getMatchBySmsId(1:i64 entityId);

	/**
	* 通过sms match id获取直播跳转地址
	**/
	list<live.TBLive> getLivesBySmsMatchId(1:i64 entityId);

	/**
	* 通过id获取新闻
	**/
	news.TBNews getNewsById(1:i64 id);


    /**
     * 获取奥运配置数据
    **/
    config.TOlympicLiveConfigSet getOlympicLiveConfig(1: i64 id),


}