package jsonPathutils;

import com.lesports.sms.data.util.JsonPath.annotation.JsonPathQQ;

import java.io.Serializable;
import java.util.List;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-16
 */
public class ScheduleTest implements Serializable {
    @JsonPathQQ(value="$.data.*.*")
    private List<ScheduleModel> scheduleModels;

    public static class ScheduleModel {

      //  @XPathSportrard(value = "./Teams/Team[@type='1']/@id")
        @JsonPathQQ(value="$.leftId")

        private String homeTeamId;
        @JsonPathQQ(value="$.leftName")
        private String homeTeamName;
//        private String homeScore;
      //  @XPathSportrard(value = "./Teams/Team[@type='2']/@id")
        @JsonPathQQ(value="$.rightId")
        private String awayTeamId;
        @JsonPathQQ(value="$.rightName")
        private String awayTeamName;
//        private String awaySore;
//        private String changeToPartnerId;

//        public String getChangeToPartnerId() {
//            return changeToPartnerId;
//        }
//
//        public void setChangeToPartnerId(String changeToPartnerId) {
//            this.changeToPartnerId = changeToPartnerId;
//        }


        public String getHomeTeamId() {
            return homeTeamId;
        }

        public void setHomeTeamId(String homeTeamId) {
            this.homeTeamId = homeTeamId;
        }

//        public String getHomeScore() {
//            return homeScore;
//        }
//
//        public void setHomeScore(String homeScore) {
//            this.homeScore = homeScore;
//        }

        public String getAwayTeamId() {
            return awayTeamId;
        }

        public void setAwayTeamId(String awayTeamId) {
            this.awayTeamId = awayTeamId;
        }

        public String getAwayTeamName() {
            return awayTeamName;
        }

        public void setAwayTeamName(String awayTeamName) {
            this.awayTeamName = awayTeamName;
        }

        public String getHomeTeamName() {
            return homeTeamName;
        }

        public void setHomeTeamName(String homeTeamName) {
            this.homeTeamName = homeTeamName;
        }
    }
}
