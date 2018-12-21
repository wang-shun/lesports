/*************************
 * match.thrift
 * Lesports Bole
 *************************************/
namespace java com.lesports.bole.api.vo

enum TBoleStatus {
	// SMS导入
	IMPORTED = 0,
	// 由于未匹配而新建的
	CREATED = 1,
	// 确认有效的CREATED
	CONFIRMED = 2,
	// 确认无效的CREATED
	INVALID = 3,
	// 已导出到SMS
	EXPORTED = 4,
	// 已关联到SMS
	ATTACHED = 5;
}

/**
* 数据抓取来源
**/
enum TBoleSource {
	//直播吧
	ZHIBO8 = 1,
	//腾讯体育
	QQ = 2,
	//新浪体育
	SINA = 3
}

/**
* 直播类型
**/
enum TBoleLiveType {
	//
	VIDEO,
	//腾讯体育
	IMAGE_TEXT,
}