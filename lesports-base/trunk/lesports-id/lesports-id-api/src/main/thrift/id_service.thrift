/**************************************
 * id_service.thrift
 * Lesports Id Service
 *************************************/
namespace java com.lesports.id.api


/**
 * Lesports User Service Definition.
 * @author ellios
 */
enum IdType{
    //字典
    DICT_ENTRY = 0,
    //赛事
    COMPETITION = 1,
    //赛季
    COMPETITION_SEASON = 2,
    //比赛
    MATCH = 3,
    //体育自制节目专辑
    ALBUM = 4,
    //节目
    EPISODE = 5,
    //队伍
    TEAM = 6,
    //队员
    PLAYER = 7,
    //标签
    TAG = 8,
    //比赛实况
    MATCH_ACTION = 9,
    //参赛者赛季统计
    COMPETITOR_SEASON_STAT = 10,
    //推荐新闻
    RECOMMEND_NEWS = 11,
    //榜单
    TOP_LIST = 12,
    //队伍赛季
    TEAM_SEASON = 14,
    //活动--unused
    ACTIVITY = 16,
    //数据导入
    DATA_IMPORT_CONFIG = 17,
    //菜单
    MENU = 18,
    //新闻
    NEWS = 19,
    //新闻图片
    NEWS_IMAGE=20,
    //升级
    UPGRADE=21,
    //用户报名
    USER_ENTRY=22,
	//评论
	COMMENT=23,
	//伯乐系统比赛
	BOLE_MATCH=24,
	//伯乐系统赛程
	BOLE_COMPETITION=25,
	//用户等级
	LEVEL=26,
	//用户特权
	PRIVILEGE=27,
	//伯乐系统赛季
	BOLE_COMPETITION_SEASON=28,
	//行为计算策略
	STRATEGY=29,
	//公告
	NOTICE=30,
	//问卷调查中问题--unused
	QUESTION = 31,
	//图文直播id
    TEXT_LIVE=32,
    //图文直播消息
    LIVE_MESSAGE=33,
    //图文直播图片
    TEXT_LIVE_IMAGE=34,
    //投票
    VOTE=35,
    //用户行为记录
    ACTION_LOG=36,
    //搜索推荐
    SUGGEST=37,
    //投票选项
    VOTE_OPTION=38,
    //tv桌面
    TV_DESKTOP=39,
	//菜单选项
	MENU_ITEM=40,
	//伯乐系统对阵双方
	BOLE_COMPETITOR=41,
	//伯乐系统直播
	BOLE_LIVE=42,
	//tv推荐内容
	RECOMMEND_TV_NEWS=43,
	//伯乐系统新闻
    BOLE_NEWS=44,
    //sms后台菜单
    SMS_MENU=45,
    //奖牌
    MEDAL=46,
    //记录
    RECORD=47,
    //轮播图
    CAROUSEL=48,
    //推荐节目
    RECOMMEND_EPISODE=49,
    //奖牌榜
    MEDAL_LIST = 50,
    //球员生涯统计
    PLAYER_CAREER_STAT = 51,
    //用户系统业务端
    BUSINESS=101,
    //用户系统产品线
    PRODUCT=102,
    //用户系统行为
	ACTION=103,
	//会员订单
    MEMBER_ORDER = 104,

    //超过1000的ID类型不能用来生成,只能用来判断id的类型
    //媒资专辑ID,
    MMS_ALBUM = 1001,
    //媒资视频ID
    MMS_VIDEO = 1002,
    //乐视直播
    LETV_LIVE = 1003,
    //图文直播
    TLIVE = 1004,
    //用户订阅
    USER_SUBSCRIBE = 1005,

}


service TIdService{

    /**
     * 通过id类型枚举生成id
     */
    i64 nextId(1:IdType type),

}
