package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2015/6/24.
 */
public class PlayerRandStatistic {

    private Integer goals = 0;//进球
    private Integer penaltyNumber = 0;//点球
    private Integer caps = 0;//场次
    private Integer own = 0;//乌龙球

    public Integer getOwn() {
        return own;
    }

    public void setOwn(Integer own) {
        this.own = own;
    }

    public Integer getCaps() {
        return caps;
    }

    public void setCaps(Integer caps) {
        this.caps = caps;
    }

    public Integer getGoals() {
        return goals;
    }

    public void setGoals(Integer goals) {
        this.goals = goals;
    }



    public Integer getPenaltyNumber() {
        return penaltyNumber;
    }

    public void setPenaltyNumber(Integer penaltyNumber) {
        this.penaltyNumber = penaltyNumber;
    }

    @Override
    public String toString() {
        return "PlayerRandStatistic{" +
                "goals=" + goals +
                ", penaltyNumber=" + penaltyNumber +
                "caps=" + caps +
                ", own=" + own +
                '}';
    }
}
