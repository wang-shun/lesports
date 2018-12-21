package com.lesports.websocket.common.type;

/**
 * 节目图文直播-server event
 * Created by zhangdeqiang on 2016/9/12.
 */
public enum TextLiveServerEventType {
    LIVE_ROOM_INFO("LIVE_ROOM_INFO"), //图文直播室信息
    HOME_TEAM_LIKE("HOME_TEAM_LIKE"), //主队点赞
    GUEST_TEAM_LIKE("GUEST_TEAM_LIKE"), //客队点赞
    ANCHOR_UPDOWN("ANCHOR_UPDOWN"), //解说员顶踩，0踩 1顶
    LATEST_INDEX("LATEST_INDEX"), //最新的索引
    LATEST_MESSAGE("LATEST_MESSAGE"), //最新的消息
    LATEST_PAGE("LATEST_PAGE"), //最新的页数
    PAGE_CONTENT("PAGE_CONTENT"), //根据页数获取数据
    ;

    private final String value;

    TextLiveServerEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TextLiveServerEventType findByValue(String value) {
        switch (value) {
            case "LIVE_ROOM_INFO":
                return LIVE_ROOM_INFO;
            case "HOME_TEAM_LIKE":
                return HOME_TEAM_LIKE;
            case "GUEST_TEAM_LIKE":
                return GUEST_TEAM_LIKE;
            default:
                return null;
        }
    }
}
