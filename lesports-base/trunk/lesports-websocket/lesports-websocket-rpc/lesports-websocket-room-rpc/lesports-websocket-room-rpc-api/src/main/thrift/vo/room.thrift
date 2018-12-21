/*************************
 * room.thrift
 * Lesports Room
 *************************************/
namespace java com.lesports.room.api.vo

//直播间基本信息
struct TRoom{
 //直播间id
 1: string id,
 //直播间名称
 2: string name,
 //在线人数
 3: optional i32 online,
 //最新进入直播间用户
 4: optional list<TUser> recent,
}

struct TUser {
 //用户id
 1: string id,
 //用户名称
 2: string name,
 //用户头像
 3: string image,
}