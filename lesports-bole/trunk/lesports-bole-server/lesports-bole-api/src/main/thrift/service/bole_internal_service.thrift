/**************************************
 * bole_internal_service.thrift
 * Lesports Bole Internal Service
 *************************************/


namespace java com.lesports.bole.api.service
include "config.thrift"

service TBoleInternalService{

	/**
	* 利用sms id更新伯乐数据
	**/
	bool updateBoleWithSms(1:i64 entityId);
	 /**
             * 保存奥运配置数据
            **/
       bool saveOlympicLiveConfig(1: config.TOlympicSettingDataSet entity),

}