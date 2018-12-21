package com.lesports.sms.data.model;

import java.util.List;

/**
 * Created by ruiyuansheng on 2015/6/11.
 */
public class FbStatistics {

    //控球率
    private Integer ballPossession = 0;
    //球门范围内射门
    private Integer shotsOnGoal = 0;
    //球门范围外射门
    private Integer shotsOffGoal = 0;
    //任意球
    private Integer freeKicks = 0;
    //角球
    private Integer cornerKicks = 0;
    //越位
    private Integer offsides = 0;
    //掷界外球
    private Integer throwIns = 0;
    //守门员扑救
    private Integer goalkeeperSaves = 0;
    //球门球
    private Integer goalKicks = 0;
    //犯规
    private Integer fouls = 0;

    private Integer yellow = 0;

    private Integer red = 0;

   // private List<Card> cards;

    //private List<Substitution> substitutions;

//    public static class Substitution{
//
//        private String time;
//
//        private Long playerOutId;
//
//        private Long playerInId;
//
//        private Long tid;
//
//        @Override
//        public String toString() {
//            return "Substitution{" +
//                    "time='" + time + '\'' +
//                    ", playerOutId=" + playerOutId +
//                    ", playerInId=" + playerInId +
//                    ", tid=" + tid +
//                    '}';
//        }
//
//        public String getTime() {
//            return time;
//        }
//
//        public void setTime(String time) {
//            this.time = time;
//        }
//
//        public Long getPlayerOutId() {
//            return playerOutId;
//        }
//
//        public void setPlayerOutId(Long playerOutId) {
//            this.playerOutId = playerOutId;
//        }
//
//        public Long getPlayerInId() {
//            return playerInId;
//        }
//
//        public void setPlayerInId(Long playerInId) {
//            this.playerInId = playerInId;
//        }
//
//        public Long getTid() {
//            return tid;
//        }
//
//        public void setTid(Long tid) {
//            this.tid = tid;
//        }
//    }

//    public static class Card
//    {
//        private String time;
//
//        private Long tid;
//
//        private Long pid;
//
//        private String type;
//
//        public String getTime() {
//            return time;
//        }
//
//        public void setTime(String time) {
//            this.time = time;
//        }
//
//        public Long getTid() {
//            return tid;
//        }
//
//        public void setTid(Long tid) {
//            this.tid = tid;
//        }
//
//        public Long getPid() {
//            return pid;
//        }
//
//        public void setPid(Long pid) {
//            this.pid = pid;
//        }
//
//        public String getType() {
//            return type;
//        }
//
//        public void setType(String type) {
//            this.type = type;
//        }
//
//        @Override
//        public String toString() {
//            return "Card{" +
//                    "time='" + time + '\'' +
//                    ", tid=" + tid +
//                    ", pid=" + pid +
//                    ", type='" + type + '\'' +
//                    '}';
//        }
//    }


    @Override
    public String toString() {
        return "FbStatistics{" +
                "ballPossession=" + ballPossession +
                ", shotsOnGoal=" + shotsOnGoal +
                ", shotsOffGoal=" + shotsOffGoal +
                ", freeKicks=" + freeKicks +
                ", cornerKicks=" + cornerKicks +
                ", offsides=" + offsides +
                ", throwIns=" + throwIns +
                ", goalkeeperSaves=" + goalkeeperSaves +
                ", goalKicks=" + goalKicks +
                ", fouls=" + fouls +
                ", yellow=" + yellow +
                ", red=" + red +
                '}';
    }

    public Integer getBallPossession() {
        return ballPossession;
    }

    public void setBallPossession(Integer ballPossession) {
        this.ballPossession = ballPossession;
    }

    public Integer getShotsOnGoal() {
        return shotsOnGoal;
    }

    public void setShotsOnGoal(Integer shotsOnGoal) {
        this.shotsOnGoal = shotsOnGoal;
    }

    public Integer getShotsOffGoal() {
        return shotsOffGoal;
    }

    public void setShotsOffGoal(Integer shotsOffGoal) {
        this.shotsOffGoal = shotsOffGoal;
    }

    public Integer getFreeKicks() {
        return freeKicks;
    }

    public void setFreeKicks(Integer freeKicks) {
        this.freeKicks = freeKicks;
    }

    public Integer getCornerKicks() {
        return cornerKicks;
    }

    public void setCornerKicks(Integer cornerKicks) {
        this.cornerKicks = cornerKicks;
    }

    public Integer getOffsides() {
        return offsides;
    }

    public void setOffsides(Integer offsides) {
        this.offsides = offsides;
    }

    public Integer getThrowIns() {
        return throwIns;
    }

    public void setThrowIns(Integer throwIns) {
        this.throwIns = throwIns;
    }

    public Integer getGoalkeeperSaves() {
        return goalkeeperSaves;
    }

    public void setGoalkeeperSaves(Integer goalkeeperSaves) {
        this.goalkeeperSaves = goalkeeperSaves;
    }

    public Integer getGoalKicks() {
        return goalKicks;
    }

    public void setGoalKicks(Integer goalKicks) {
        this.goalKicks = goalKicks;
    }

    public Integer getFouls() {
        return fouls;
    }

    public void setFouls(Integer fouls) {
        this.fouls = fouls;
    }


    public Integer getYellow() {
        return yellow;
    }

    public void setYellow(Integer yellow) {
        this.yellow = yellow;
    }

    public Integer getRed() {
        return red;
    }

    public void setRed(Integer red) {
        this.red = red;
    }
}
