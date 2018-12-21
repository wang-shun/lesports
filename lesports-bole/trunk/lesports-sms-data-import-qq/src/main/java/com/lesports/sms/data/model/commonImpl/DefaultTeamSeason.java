package com.lesports.sms.data.model.commonImpl;

import com.google.common.collect.Lists;
import com.lesports.sms.api.common.Gender;
import com.lesports.sms.data.model.DefaultModel;
import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;
import com.lesports.sms.data.util.formatter.*;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/11/3.
 */
public class DefaultTeamSeason extends DefaultModel {
    @JsonPathQQ(value = "$.data")
    private List<TeamModel> teamModelList;

    public static class TeamModel {
        @JsonPathQQ(value = "$.playerBaseInfo[0].teamId")
        private String partnerId;
        private String name;
        private String nickName;
        private String coachId;
        private String coachName;
        @JsonPathQQ(value = "$.playerBaseInfo.*")
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
            private boolean isCorePlayer;
            private boolean isCaptain;
            private Gender gender;
            @JsonPathQQ(value = "$.playerId")
            private String partnerId;
            @JsonPathQQ(value = "$.enName")
            private String playerEnName;
            @JsonPathQQ(value = "$.cnName")
            private String playerName;
            @JsonPathQQ(value = "$.jerseyNum")
            private String shirtNum;
            private Long position;
            private Integer weight;
            private Integer height;
            @JsonPathQQ(formatter = BirthDateFormatter.class, value = "$.birthDate")
            private String birth;
            private Long counntry;
            private String heavyFoot;
            private String school;
            private Integer experience;
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
