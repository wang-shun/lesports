package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2015/6/24.
 */
public class PlayerAssistsStatistic {

    private Integer assists = 0;

    private Integer caps = 0;

    public Integer getCaps() {
        return caps;
    }

    public void setCaps(Integer caps) {
        this.caps = caps;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    @Override
    public String toString() {
        return "PlayerAssistsStatistic{" +
                "assists=" + assists +
                '}';
    }
}
