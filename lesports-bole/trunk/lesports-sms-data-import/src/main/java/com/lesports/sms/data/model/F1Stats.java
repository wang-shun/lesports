package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2015/7/13.
 */
public class F1Stats {
    private Integer position = 0;//排名
    private Integer points = 0;//积分
    private String fastLapsTime="";//单圈最快时间
    private String totalTime="";//总时间

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getFastLapsTime() {
        return fastLapsTime;
    }

    public void setFastLapsTime(String fastLapsTime) {
        this.fastLapsTime = fastLapsTime;
    }
}
