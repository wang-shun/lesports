/**************************************
 * room_service.thrift
 * Lesports Room Service
 *************************************/
include "room.thrift"

namespace java com.lesports.room.api.service

service TRoomService{

    ####################### room ################################

	/**
	*  通过id获取直播室信息
	**/
    room.TRoom getRoomInfo(1: string id)

	/**
	*  加入直播室
	**/
    room.TRoom joinRoom(1: string uid, 2: string roomId)

    /**
    *  离开直播室
    **/
    room.TRoom leaveRoom(1: string uid, 2: string roomId)
}

