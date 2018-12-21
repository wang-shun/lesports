package com.lesports.msg.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * lesports-projects.
 * http://wiki.letv.cn/pages/viewpage.action?pageId=25235070
 *
 * @author: pangchuanxiao
 * @since: 2015/7/15
 */
public class LiveMessage {
    private int dateType;
    private Param param;

    public int getDateType() {
        return dateType;
    }

    public void setDateType(int dataType) {
        this.dateType = dataType;
    }

    public Param getParam() {
        return param;
    }

    public void setParam(Param param) {
        this.param = param;
    }

    public static class Param {
        private int liveHallType;
        private long liveId;
        private String type;
        private String channelEName;
        private String playbillDate;
        private String belongAreas;

        public int getLiveHallType() {
            return liveHallType;
        }

        public void setLiveHallType(int liveHallType) {
            this.liveHallType = liveHallType;
        }

        public long getLiveId() {
            return liveId;
        }

        public void setLiveId(long liveId) {
            this.liveId = liveId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getChannelEName() {
            return channelEName;
        }

        public void setChannelEName(String channelEName) {
            this.channelEName = channelEName;
        }

        public String getPlaybillDate() {
            return playbillDate;
        }

        public void setPlaybillDate(String playbillDate) {
            this.playbillDate = playbillDate;
        }

        public String getBelongAreas() {
            return belongAreas;
        }

        public void setBelongAreas(String belongAreas) {
            this.belongAreas = belongAreas;
        }
    }

}
