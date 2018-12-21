package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2015/7/6.
 */
public class F1RandStatistic {

    private Integer carNumber = 0;//车号
    private Integer racesWithPoints = 0;//获得积分的比赛场次
    private Integer racesStarted = 0;//参加的已开始的比赛场次
    private Integer victories  = 0;//胜利场次
    private Integer position  = 0;//排名
    private Integer perfectRaces  = 0;//完美比赛（获胜，排位第一，单圈最快）场次
    private Integer fastestLaps  = 0;//单圈最快次数
    private Integer podiums  = 0;//登上领奖台次数
    private Integer polePositions  = 0;//获得杆位次数
    private Integer points = 0;//积分

    @Override
    public String toString() {
        return "F1DriverRandStatistic{" +
                "carNumber=" + carNumber +
                ", racesWithPoints=" + racesWithPoints +
                ", racesStarted=" + racesStarted +
                ", victories=" + victories +
                ", position=" + position +
                ", perfectRaces=" + perfectRaces +
                ", fastestLaps=" + fastestLaps +
                ", podiums=" + podiums +
                ", polePositions=" + polePositions +
                ", points=" + points +
                '}';
    }

    public Integer getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(Integer carNumber) {
        this.carNumber = carNumber;
    }

    public Integer getRacesWithPoints() {
        return racesWithPoints;
    }

    public void setRacesWithPoints(Integer racesWithPoints) {
        this.racesWithPoints = racesWithPoints;
    }

    public Integer getRacesStarted() {
        return racesStarted;
    }

    public void setRacesStarted(Integer racesStarted) {
        this.racesStarted = racesStarted;
    }

    public Integer getVictories() {
        return victories;
    }

    public void setVictories(Integer victories) {
        this.victories = victories;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getPerfectRaces() {
        return perfectRaces;
    }

    public void setPerfectRaces(Integer perfectRaces) {
        this.perfectRaces = perfectRaces;
    }

    public Integer getFastestLaps() {
        return fastestLaps;
    }

    public void setFastestLaps(Integer fastestLaps) {
        this.fastestLaps = fastestLaps;
    }

    public Integer getPodiums() {
        return podiums;
    }

    public void setPodiums(Integer podiums) {
        this.podiums = podiums;
    }

    public Integer getPolePositions() {
        return polePositions;
    }

    public void setPolePositions(Integer polePositions) {
        this.polePositions = polePositions;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }
}
