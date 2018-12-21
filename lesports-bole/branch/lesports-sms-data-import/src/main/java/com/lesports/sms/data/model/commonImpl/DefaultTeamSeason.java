package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.api.common.Gender;
import com.lesports.sms.data.util.formatter.*;
import com.lesports.sms.data.util.formatter.stats.DraftFormatter;
import com.lesports.utils.xml.annotation.XPathSoda;
import com.lesports.utils.xml.annotation.XPathSportrard;
import com.lesports.utils.xml.annotation.XPathStats;
import com.lesports.utils.xml.annotation.XPathStats2;

import java.util.List;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
public class DefaultTeamSeason extends DefaultModel {
    @XPathStats(value = "/sports-statistics/sports-rosters/bk-rosters/bk-roster")
    @XPathSoda(value = "/SoccerFeed/Team")
    private List<TeamModel> teamModelList;

    public static class TeamModel {
        @XPathStats(value = "./team-code/@global-id")
        @XPathSoda(value = "./@id")
        private String partnerId;
        @XPathStats(value = "./team-name/@name")
        @XPathSoda(value = "./@name")
        private String name;
        @XPathStats(value = "./team-name/@alias")
        @XPathSoda(value = "./@name")
        private String nickName;
        @XPathStats(value = "./coach/@id")
        @XPathSoda(value = "./Coach/@id")
        private String coachId;
        @XPathStats(value = "./coach/@display-name")
        @XPathSoda(value = "./Coach")
        private String coachName;
        @XPathStats(value = "./nba-player")
        @XPathSoda(value = "./Squads/player")
        private List<PlayerModel> playerModels;

        public String getPartnerId() {
            return partnerId;
        }

        public void setPartnerId(String partnerId) {
            this.partnerId = partnerId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }

        public String getCoachName() {
            return coachName;
        }

        public void setCoachName(String coachName) {
            this.coachName = coachName;
        }

        public String getCoachId() {
            return coachId;
        }

        public void setCoachId(String coachId) {
            this.coachId = coachId;
        }

        public List<PlayerModel> getPlayerModels() {
            return playerModels;
        }

        public void setPlayerModels(List<PlayerModel> playerModels) {
            this.playerModels = playerModels;
        }

        public static class PlayerModel {
            @XPathSoda(formatter = BooleanFormatter.class, value = "./@keyman")
            private boolean isCorePlayer;
            @XPathSoda(formatter = BooleanFormatter.class, value = "./@captain")
            private boolean isCaptain;
            @XPathStats(formatter = GenderFormatter.class, value = "./gender/@gender")
            private Gender gender;
            @XPathStats(value = "/player-code/@id")
            @XPathSoda(value = "./@id")
            private String partnerId;
            @XPathStats(value = "concat{./name/@first-name,./name/@lase-name}")
            @XPathSoda(value = "./@nameEng")
            private String playerEnName;
            @XPathStats(value = "concat{./name/@first-name,./name/@lase-name}")
            @XPathSoda(value = "./@name")
            private String playerName;
            @XPathStats(value = "./player-number/@number")
            @XPathSoda(value = "./@number")
            private String shirtNum;
            @XPathStats(formatter = BKPositionFormatter.class, value = "./player-position/@abbrev")
            @XPathSoda(formatter = FBPositionFormatter.class, value = "./Position/@name")
            private Long position;
            @XPathStats(formatter = PoundToKgFormatter.class, value = "./weight/@pounds")
            @XPathSoda(value = "./@weight")
            private Integer weight;
            @XPathStats(formatter = InchesToCmFormatter.class, value = "./height/@inches")
            @XPathSoda(value = "./@height")
            private Integer height;
            @XPathStats(formatter = BirthDateFormatter.class, value = "concat(./birth-date/@year,'-',./birth-date/@month,'-',./birth-date/@date)")
            @XPathSoda(formatter = BirthDateFormatter.class, value = "./@birthDate")
            private String birth;
            @XPathStats(formatter = CountryFormatter.class, value = "./birth-country/@bbrev")
            @XPathSoda(formatter = CountryFormatter.class, value = "./Country/@name")
            private Long counntry;
            @XPathSoda(value = "./ShootPart/@name")
            private String heavyFoot;
            @XPathStats(value = "./school/@school")
            private String school;
            @XPathStats(value = "./experience/@experience")
            private Integer experience;
            @XPathStats(formatter = DraftFormatter.class, value = "concat{./draft-info/draft/@year,'-',./draft-info/draft/@round,'-',./draft-into/draft/@pick}")
            private String draftContent;
            private String salary;

            public String getSalary() {
                return salary;
            }

            public void setSalary(String salary) {
                this.salary = salary;
            }

            public boolean isCaptain() {
                return isCaptain;
            }

            public void setCaptain(boolean isCaptain) {
                this.isCaptain = isCaptain;
            }

            public String getDraftContent() {
                return draftContent;
            }

            public void setDraftContent(String draftContent) {
                this.draftContent = draftContent;
            }

            public String getShirtNum() {
                return shirtNum;
            }

            public void setShirtNum(String shirtNum) {
                this.shirtNum = shirtNum;
            }

            public String getHeavyFoot() {
                return heavyFoot;
            }

            public void setHeavyFoot(String heavyFoot) {
                this.heavyFoot = heavyFoot;
            }

            public String getSchool() {
                return school;
            }

            public void setSchool(String school) {
                this.school = school;
            }

            public Integer getExperience() {
                return experience;
            }

            public void setExperience(Integer experience) {
                this.experience = experience;
            }

            public Gender getGender() {
                return gender;
            }

            public void setGender(Gender gender) {
                this.gender = gender;
            }

            public boolean isCorePlayer() {
                return isCorePlayer;
            }

            public void setCorePlayer(boolean isCorePlayer) {
                this.isCorePlayer = isCorePlayer;
            }

            public Long getCounntry() {
                return counntry;
            }

            public void setCounntry(Long counntry) {
                this.counntry = counntry;
            }

            public String getBirth() {
                return birth;
            }

            public void setBirth(String birth) {
                this.birth = birth;
            }

            public String getShirtName() {
                return shirtNum;
            }

            public void setShirtName(String shirtName) {
                this.shirtNum = shirtName;
            }

            public Long getPosition() {
                return position;
            }

            public void setPosition(Long position) {
                this.position = position;
            }

            public Integer getWeight() {
                return weight;
            }

            public void setWeight(Integer weight) {
                this.weight = weight;
            }

            public Integer getHeight() {
                return height;
            }

            public void setHeight(Integer height) {
                this.height = height;
            }

            public String getPlayerName() {
                return playerName;
            }

            public void setPlayerName(String playerName) {
                this.playerName = playerName;
            }

            public String getPlayerEnName() {
                return playerEnName;
            }

            public void setPlayerEnName(String playerEnName) {
                this.playerEnName = playerEnName;
            }

            public String getPartnerId() {
                return partnerId;
            }

            public void setPartnerId(String partnerId) {
                this.partnerId = partnerId;
            }
        }
    }

    @Override
    public List<Object> getData() {
        List<Object> res = Lists.newArrayList();
        for (TeamModel model : teamModelList) {
            res.add(model);
        }
        return res;
    }


}
