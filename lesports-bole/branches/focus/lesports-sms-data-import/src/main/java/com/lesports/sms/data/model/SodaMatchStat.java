package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2016/3/8.
 */
public class SodaMatchStat {


    private Integer win = 0;//胜
    private Integer lose = 0;//负
    private Integer flat = 0;//平
    private Integer goal = 0;//进球
    private Integer concede = 0;//失球


    public Integer getWin() {
        return win;
    }

    public void setWin(Integer win) {
        this.win = win;
    }

    public Integer getLose() {
        return lose;
    }

    public void setLose(Integer lose) {
        this.lose = lose;
    }

    public Integer getFlat() {
        return flat;
    }

    public void setFlat(Integer flat) {
        this.flat = flat;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    public Integer getConcede() {
        return concede;
    }

    public void setConcede(Integer concede) {
        this.concede = concede;
    }

    @Override
    public String toString() {
        return "SodaMatchStat{" +
                "win=" + win +
                ", lose=" + lose +
                ", flat=" + flat +
                ", goal=" + goal +
                ", concede=" + concede +
                '}';
    }
}
