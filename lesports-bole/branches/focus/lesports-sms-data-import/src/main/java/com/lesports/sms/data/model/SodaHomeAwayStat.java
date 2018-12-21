package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2016/3/8.
 */
public class SodaHomeAwayStat {

    private Integer homeWin = 0;//主胜
    private Integer homeLose = 0;//主负
    private Integer homeFlat = 0;//主平
    private Integer homeGoal = 0;//主队进球
    private Integer homeconcede = 0;//主队失球
    private Integer awayWin = 0;//客胜
    private Integer awayLose = 0;//客负
    private Integer awayFlat = 0;//客平
    private Integer awayGoal = 0;//客队进球
    private Integer awayConcede = 0;//客队失球


    public Integer getHomeWin() {
        return homeWin;
    }

    public void setHomeWin(Integer homeWin) {
        this.homeWin = homeWin;
    }

    public Integer getHomeLose() {
        return homeLose;
    }

    public void setHomeLose(Integer homeLose) {
        this.homeLose = homeLose;
    }

    public Integer getHomeFlat() {
        return homeFlat;
    }

    public void setHomeFlat(Integer homeFlat) {
        this.homeFlat = homeFlat;
    }

    public Integer getHomeGoal() {
        return homeGoal;
    }

    public void setHomeGoal(Integer homeGoal) {
        this.homeGoal = homeGoal;
    }

    public Integer getHomeconcede() {
        return homeconcede;
    }

    public void setHomeconcede(Integer homeconcede) {
        this.homeconcede = homeconcede;
    }

    public Integer getAwayWin() {
        return awayWin;
    }

    public void setAwayWin(Integer awayWin) {
        this.awayWin = awayWin;
    }

    public Integer getAwayLose() {
        return awayLose;
    }

    public void setAwayLose(Integer awayLose) {
        this.awayLose = awayLose;
    }

    public Integer getAwayFlat() {
        return awayFlat;
    }

    public void setAwayFlat(Integer awayFlat) {
        this.awayFlat = awayFlat;
    }

    public Integer getAwayGoal() {
        return awayGoal;
    }

    public void setAwayGoal(Integer awayGoal) {
        this.awayGoal = awayGoal;
    }

    public Integer getAwayConcede() {
        return awayConcede;
    }

    public void setAwayConcede(Integer awayConcede) {
        this.awayConcede = awayConcede;
    }

    @Override
    public String toString() {
        return "SodaHomeAwayStat{" +
                "homeWin=" + homeWin +
                ", homeLose=" + homeLose +
                ", homeFlat=" + homeFlat +
                ", homeGoal=" + homeGoal +
                ", homeconcede=" + homeconcede +
                ", awayWin=" + awayWin +
                ", awayLose=" + awayLose +
                ", awayFlat=" + awayFlat +
                ", awayGoal=" + awayGoal +
                ", awayConcede=" + awayConcede +
                '}';
    }
}
