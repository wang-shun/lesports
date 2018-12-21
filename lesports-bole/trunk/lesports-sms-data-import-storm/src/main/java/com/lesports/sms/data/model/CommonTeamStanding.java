package com.lesports.sms.data.model;

/**
 * Created by qiaohongxin on 2016/5/12.
 */
public class CommonTeamStanding {
    private Long sportId;
    private String tourmamentId;

    public Long getSportId() {
        return sportId;
    }

    public void setSportId(Long sportId) {
        this.sportId = sportId;
    }

    public String getTourmamentId() {
        return tourmamentId;
    }

    public void setTourmamentId(String tourmamentId) {
        this.tourmamentId = tourmamentId;
    }
}
