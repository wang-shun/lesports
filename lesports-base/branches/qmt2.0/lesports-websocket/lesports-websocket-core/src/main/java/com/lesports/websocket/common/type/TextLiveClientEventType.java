package com.lesports.websocket.common.type;

/**
 * 节目图文直播-server event
 * Created by zhangdeqiang on 2016/9/12.
 */
public enum TextLiveClientEventType {
    LIVE_ROOM_INFO("LIVE_ROOM_INFO"), //直播室信息
    TEAM_LIKE("TEAM_LIKE"), //主队点赞
    ANCHOR_UPDOWN("ANCHOR_UPDOWN"), //直播员顶踩数据
    LATEST_INDEX("LATEST_INDEX"), //最新的索引
    LATEST_MESSAGE("LATEST_MESSAGE"), //最新一条消息
    LATEST_PAGE("LATEST_PAGE"), //最新一页数据
    PAGE_CONTENT("PAGE_CONTENT"), //图文直播某一页数据
    ;

    private final String value;

    TextLiveClientEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TextLiveClientEventType findByValue(String value) {
        switch (value) {
            case "LIVE_ROOM_INFO":
                return LIVE_ROOM_INFO;
            case "TEAM_LIKE":
                return TEAM_LIKE;
            case "ANCHOR_UPDOWN":
                return ANCHOR_UPDOWN;
            case "LATEST_MESSAGE":
                return LATEST_MESSAGE;
            case "LATEST_PAGE":
                return LATEST_PAGE;
            case "PAGE_CONTENT":
                return PAGE_CONTENT;
            default:
                return null;
        }
    }
}
