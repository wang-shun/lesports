package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2015/6/24.
 */
public class TeamRankStatistic {
    //参加的比赛场次
    private Integer matchNumber = 0;
    //球队积分
    private Integer teamScore = 0;
    //赢球场次
    private Integer winMatch = 0;
    //平局场次
    private Integer flatMatch = 0;
    //输球场次
    private Integer lossMatch = 0;
    //失球数
    private Integer fumble = 0;
    //进球数
    private Integer goal = 0;
    //净胜球
    private Integer goalDiffer = 0;

    public Integer getGoalDiffer() {
        return goalDiffer;
    }

    public void setGoalDiffer(Integer goalDiffer) {
        this.goalDiffer = goalDiffer;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    public Integer getTeamScore() {
        return teamScore;
    }

    public void setTeamScore(Integer teamScore) {
        this.teamScore = teamScore;
    }

    public Integer getWinMatch() {
        return winMatch;
    }

    public void setWinMatch(Integer winMatch) {
        this.winMatch = winMatch;
    }

    public Integer getFlatMatch() {
        return flatMatch;
    }

    public void setFlatMatch(Integer flatMatch) {
        this.flatMatch = flatMatch;
    }

    public Integer getLossMatch() {
        return lossMatch;
    }

    public void setLossMatch(Integer lossMatch) {
        this.lossMatch = lossMatch;
    }

    public Integer getFumble() {
        return fumble;
    }

    public void setFumble(Integer fumble) {
        this.fumble = fumble;
    }

    public Integer getGoal() {
        return goal;
    }

    public void setGoal(Integer goal) {
        this.goal = goal;
    }

    @Override
    public String toString() {
        return "TeamRankStatistic{" +
                "matchNumber=" + matchNumber +
                ", teamScore=" + teamScore +
                ", winMatch=" + winMatch +
                ", flatMatch=" + flatMatch +
                ", lossMatch=" + lossMatch +
                ", fumble=" + fumble +
                ", goal=" + goal +
                '}';
    }
}
