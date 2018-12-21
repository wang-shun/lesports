package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2015/6/16.
 */
public class NBAStatistics {

    private Integer games_played = 0;   //出场次数
    private Integer games_started = 0;//首发次数
    private Integer minutes = 0;//出场时间
    private Double minutes_pergame = 0.0; //平均出场时间
    private Integer points = 0;    //总得分
    private Double points_pergame = 0.0;//平均得分
    private Integer fieldgoals_made = 0;//投篮命中次数
    private Integer fieldgoals_attempted = 0; //投篮次数
    private String fieldgoal_percentage = "0.0%";//命中率
    private Integer threepoint_made = 0; //三分球命中次数
    private Integer threepoint_attempted = 0;    //三分球投篮次数
    private String threepoint_percentage = "0.0%";   //三分球命中率
    private Integer freethrows_made = 0;  //罚球命中次数
    private Integer freethrows_attempted = 0; //罚球次数
    private String freethrow_percentage = "0.0%"; //罚球命中率
    private Integer defensive_rebounds = 0;    //后场篮板
    private Double defensive_rebounds_pergame = 0.0;//平均单场后场篮板
    private Integer offensive_rebounds = 0;  //前场篮板
    private Double offensive_rebounds_pergame = 0.0; //平均单场前场篮板
    private Double total_ebounds_pergame = 0.0;//单场比赛篮板球次数
    private Integer total_rebounds = 0;  //总篮板球次数
    private Integer assists = 0; //助攻次数
    private Double assists_pergame = 0.0;    //平均单场助攻次数
    private Integer steals = 0; //抢断次数
    private Double steals_pergame = 0.0; //平均单场抢断
    private Integer blockedshots = 0;  //封盖次数
    private Double blockedshots_pergame = 0.0;//平均单场封盖次数
    private Integer turnovers = 0;  //失误次数
    private Double turnover_pergame = 0.0;  //平均单场失误
    private Integer personalfouls = 0;  // 犯规次数
    private Double personalfouls_pergame = 0.0;  // 平均单场犯规
    private Integer disqualification = 0;    // 被罚下场次数
    private Double disqualification_pergame = 0.0; //平均被罚下场次数
    private Integer flagrantFouls = 0;//恶意犯规
    private Integer technicalFouls = 0;//技术犯规
    private Integer totalturnovers = 0;//总失误
    private Integer teamTurnovers = 0;//团队失误
    private Integer pointsOffTurnovers = 0;//利用对手失误得分
    private Integer blocksAgainst = 0;//被盖帽次数
    private Integer fastBreakPoints = 0;//快攻得分
    private Integer fullTimeoutsRemaining = 0;//剩余长暂停
    private Integer shortTimeoutsRemaining = 0;//剩余短暂停
    private Integer efficiency = 0;//效率值

    public Integer getFlagrantFouls() {
        return flagrantFouls;
    }

    public void setFlagrantFouls(Integer flagrantFouls) {
        this.flagrantFouls = flagrantFouls;
    }

    public Integer getTechnicalFouls() {
        return technicalFouls;
    }

    public void setTechnicalFouls(Integer technicalFouls) {
        this.technicalFouls = technicalFouls;
    }

    public Integer getTotalturnovers() {
        return totalturnovers;
    }

    public void setTotalturnovers(Integer totalturnovers) {
        this.totalturnovers = totalturnovers;
    }

    public Integer getTeamTurnovers() {
        return teamTurnovers;
    }

    public void setTeamTurnovers(Integer teamTurnovers) {
        this.teamTurnovers = teamTurnovers;
    }

    public Integer getPointsOffTurnovers() {
        return pointsOffTurnovers;
    }

    public void setPointsOffTurnovers(Integer pointsOffTurnovers) {
        this.pointsOffTurnovers = pointsOffTurnovers;
    }

    public Integer getBlocksAgainst() {
        return blocksAgainst;
    }

    public void setBlocksAgainst(Integer blocksAgainst) {
        this.blocksAgainst = blocksAgainst;
    }

    public Integer getFastBreakPoints() {
        return fastBreakPoints;
    }

    public void setFastBreakPoints(Integer fastBreakPoints) {
        this.fastBreakPoints = fastBreakPoints;
    }

    public Integer getFullTimeoutsRemaining() {
        return fullTimeoutsRemaining;
    }

    public void setFullTimeoutsRemaining(Integer fullTimeoutsRemaining) {
        this.fullTimeoutsRemaining = fullTimeoutsRemaining;
    }

    public Integer getShortTimeoutsRemaining() {
        return shortTimeoutsRemaining;
    }

    public void setShortTimeoutsRemaining(Integer shortTimeoutsRemaining) {
        this.shortTimeoutsRemaining = shortTimeoutsRemaining;
    }


    public Integer getGames_started() {
        return games_started;
    }

    public void setGames_started(Integer games_started) {
        this.games_started = games_started;
    }

    public Integer getMinutes() {
        return minutes;
    }

    public void setMinutes(Integer minutes) {
        this.minutes = minutes;
    }

    public Double getMinutes_pergame() {
        return minutes_pergame;
    }

    public void setMinutes_pergame(Double minutes_pergame) {
        this.minutes_pergame = minutes_pergame;
    }

    public Integer getGames_played() {
        return games_played;
    }

    public void setGames_played(Integer games_played) {
        this.games_played = games_played;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Double getPoints_pergame() {
        return points_pergame;
    }

    public void setPoints_pergame(Double points_pergame) {
        this.points_pergame = points_pergame;
    }

    public Integer getFieldgoals_made() {
        return fieldgoals_made;
    }

    public void setFieldgoals_made(Integer fieldgoals_made) {
        this.fieldgoals_made = fieldgoals_made;
    }

    public Integer getFieldgoals_attempted() {
        return fieldgoals_attempted;
    }

    public void setFieldgoals_attempted(Integer fieldgoals_attempted) {
        this.fieldgoals_attempted = fieldgoals_attempted;
    }

    public String getFieldgoal_percentage() {
        return fieldgoal_percentage;
    }

    public void setFieldgoal_percentage(String fieldgoal_percentage) {
        this.fieldgoal_percentage = fieldgoal_percentage;
    }

    public Integer getThreepoint_made() {
        return threepoint_made;
    }

    public void setThreepoint_made(Integer threepoint_made) {
        this.threepoint_made = threepoint_made;
    }

    public Integer getThreepoint_attempted() {
        return threepoint_attempted;
    }

    public void setThreepoint_attempted(Integer threepoint_attempted) {
        this.threepoint_attempted = threepoint_attempted;
    }

    public String getThreepoint_percentage() {
        return threepoint_percentage;
    }

    public void setThreepoint_percentage(String threepoint_percentage) {
        this.threepoint_percentage = threepoint_percentage;
    }

    public Integer getFreethrows_made() {
        return freethrows_made;
    }

    public void setFreethrows_made(Integer freethrows_made) {
        this.freethrows_made = freethrows_made;
    }

    public Integer getFreethrows_attempted() {
        return freethrows_attempted;
    }

    public void setFreethrows_attempted(Integer freethrows_attempted) {
        this.freethrows_attempted = freethrows_attempted;
    }

    public String getFreethrow_percentage() {
        return freethrow_percentage;
    }

    public void setFreethrow_percentage(String freethrow_percentage) {
        this.freethrow_percentage = freethrow_percentage;
    }

    public Integer getDefensive_rebounds() {
        return defensive_rebounds;
    }

    public void setDefensive_rebounds(Integer defensive_rebounds) {
        this.defensive_rebounds = defensive_rebounds;
    }

    public Double getDefensive_rebounds_pergame() {
        return defensive_rebounds_pergame;
    }

    public void setDefensive_rebounds_pergame(Double defensive_rebounds_pergame) {
        this.defensive_rebounds_pergame = defensive_rebounds_pergame;
    }

    public Integer getOffensive_rebounds() {
        return offensive_rebounds;
    }

    public void setOffensive_rebounds(Integer offensive_rebounds) {
        this.offensive_rebounds = offensive_rebounds;
    }

    public Double getOffensive_rebounds_pergame() {
        return offensive_rebounds_pergame;
    }

    public void setOffensive_rebounds_pergame(Double offensive_rebounds_pergame) {
        this.offensive_rebounds_pergame = offensive_rebounds_pergame;
    }

    public Double getTotal_ebounds_pergame() {
        return total_ebounds_pergame;
    }

    public void setTotal_ebounds_pergame(Double total_ebounds_pergame) {
        this.total_ebounds_pergame = total_ebounds_pergame;
    }

    public Integer getTotal_rebounds() {
        return total_rebounds;
    }

    public void setTotal_rebounds(Integer total_rebounds) {
        this.total_rebounds = total_rebounds;
    }

    public Integer getAssists() {
        return assists;
    }

    public void setAssists(Integer assists) {
        this.assists = assists;
    }

    public Double getAssists_pergame() {
        return assists_pergame;
    }

    public void setAssists_pergame(Double assists_pergame) {
        this.assists_pergame = assists_pergame;
    }

    public Integer getSteals() {
        return steals;
    }

    public void setSteals(Integer steals) {
        this.steals = steals;
    }

    public Double getSteals_pergame() {
        return steals_pergame;
    }

    public void setSteals_pergame(Double steals_pergame) {
        this.steals_pergame = steals_pergame;
    }

    public Integer getBlockedshots() {
        return blockedshots;
    }

    public void setBlockedshots(Integer blockedshots) {
        this.blockedshots = blockedshots;
    }

    public Double getBlockedshots_pergame() {
        return blockedshots_pergame;
    }

    public void setBlockedshots_pergame(Double blockedshots_pergame) {
        this.blockedshots_pergame = blockedshots_pergame;
    }

    public Integer getTurnovers() {
        return turnovers;
    }

    public void setTurnovers(Integer turnovers) {
        this.turnovers = turnovers;
    }

    public Double getTurnover_pergame() {
        return turnover_pergame;
    }

    public void setTurnover_pergame(Double turnover_pergame) {
        this.turnover_pergame = turnover_pergame;
    }

    public Integer getPersonalfouls() {
        return personalfouls;
    }

    public void setPersonalfouls(Integer personalfouls) {
        this.personalfouls = personalfouls;
    }

    public Double getPersonalfouls_pergame() {
        return personalfouls_pergame;
    }

    public void setPersonalfouls_pergame(Double personalfouls_pergame) {
        this.personalfouls_pergame = personalfouls_pergame;
    }

    public Integer getDisqualification() {
        return disqualification;
    }

    public void setDisqualification(Integer disqualification) {
        this.disqualification = disqualification;
    }

    public Double getDisqualification_pergame() {
        return disqualification_pergame;
    }

    public void setDisqualification_pergame(Double disqualification_pergame) {
        this.disqualification_pergame = disqualification_pergame;
    }

    public Integer getEfficiency() {
        return efficiency;
    }

    public void setEfficiency(Integer efficiency) {
        this.efficiency = efficiency;
    }

    @Override
    public String toString() {
        return "NBAStatistics{" +
                "games_played=" + games_played +
                ", games_started=" + games_started +
                ", minutes=" + minutes +
                ", minutes_pergame=" + minutes_pergame +
                ", points=" + points +
                ", points_pergame=" + points_pergame +
                ", fieldgoals_made=" + fieldgoals_made +
                ", fieldgoals_attempted=" + fieldgoals_attempted +
                ", fieldgoal_percentage=" + fieldgoal_percentage +
                ", threepoint_made=" + threepoint_made +
                ", threepoint_attempted=" + threepoint_attempted +
                ", threepoint_percentage=" + threepoint_percentage +
                ", freethrows_made=" + freethrows_made +
                ", freethrows_attempted=" + freethrows_attempted +
                ", freethrow_percentage=" + freethrow_percentage +
                ", defensive_rebounds=" + defensive_rebounds +
                ", defensive_rebounds_pergame=" + defensive_rebounds_pergame +
                ", offensive_rebounds=" + offensive_rebounds +
                ", offensive_rebounds_pergame=" + offensive_rebounds_pergame +
                ", total_ebounds_pergame=" + total_ebounds_pergame +
                ", total_rebounds=" + total_rebounds +
                ", assists=" + assists +
                ", assists_pergame=" + assists_pergame +
                ", steals=" + steals +
                ", steals_pergame=" + steals_pergame +
                ", blockedshots=" + blockedshots +
                ", blockedshots_pergame=" + blockedshots_pergame +
                ", turnovers=" + turnovers +
                ", turnover_pergame=" + turnover_pergame +
                ", personalfouls=" + personalfouls +
                ", personalfouls_pergame=" + personalfouls_pergame +
                ", disqualification=" + disqualification +
                ", disqualification_pergame=" + disqualification_pergame +
                ", flagrantFouls=" + flagrantFouls +
                ", technicalFouls=" + technicalFouls +
                ", totalturnovers=" + totalturnovers +
                ", teamTurnovers=" + teamTurnovers +
                ", pointsOffTurnovers=" + pointsOffTurnovers +
                ", blocksAgainst=" + blocksAgainst +
                ", fastBreakPoints=" + fastBreakPoints +
                ", fullTimeoutsRemaining=" + fullTimeoutsRemaining +
                ", shortTimeoutsRemaining=" + shortTimeoutsRemaining +
                ", efficiency=" + efficiency +
                '}';
    }
}
