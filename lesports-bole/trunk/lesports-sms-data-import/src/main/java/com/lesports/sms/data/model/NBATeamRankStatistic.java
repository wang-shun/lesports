package com.lesports.sms.data.model;

/**
 * Created by ruiyuansheng on 2015/6/29.
 */
public class NBATeamRankStatistic {
    /**
     * 赛季胜的场次数
     */
    private Integer wins = 0;
    /**
     * 赛季输的场次数
     */
    private Integer losses = 0;
    /**
     * 场均得分数
     */
    private Double pointWinPic = 0.0;
    /**
     * 场均失分数
     */
    private Double pointLossPic = 0.0;
    /**
     * 胜分差
     */
    private Double gameBack = 0.0;
    /**
     * 胜率
     */

    private String winPic = "0.0%";
    /**
     * 分区排名
     */
    private Integer divRank = 0;
    /**
     * 联盟排民
     */
    private Integer poRank = 0;
    /**
     * 分区名称
     */
    private String division = "";
    /**
     * 联盟名称
     */
    private String conference = "";


    @Override
    public String toString() {
        return "NBATeamRankStatistic{" +
                "wins=" + wins +
                ", losses=" + losses +
                ", winPic=" + winPic +
                ", divRank=" + divRank +
                ", poRank=" + poRank +
                ", division='" + division + '\'' +
                ", conference='" + conference + '\'' +
                '}';
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLosses() {
        return losses;
    }

    public void setLosses(Integer losses) {
        this.losses = losses;
    }

    public String getWinPic() {
        return winPic;
    }

    public void setWinPic(String winPic) {
        this.winPic = winPic;
    }

    public Integer getDivRank() {
        return divRank;
    }

    public void setDivRank(Integer divRank) {
        this.divRank = divRank;
    }

    public Integer getPoRank() {
        return poRank;
    }

    public void setPoRank(Integer poRank) {
        this.poRank = poRank;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getConference() {
        return conference;
    }

    public void setConference(String conference) {
        this.conference = conference;
    }

    public Double getPointWinPic() {
        return pointWinPic;
    }

    public void setPointWinPic(Double pointWinPic) {
        this.pointWinPic = pointWinPic;
    }

    public Double getPointLossPic() {
        return pointLossPic;
    }

    public void setPointLossPic(Double pointLossPic) {
        this.pointLossPic = pointLossPic;
    }

    public Double getGameBack() {
        return gameBack;
    }

    public void setGameBack(Double gameBack) {
        this.gameBack = gameBack;
    }
}
