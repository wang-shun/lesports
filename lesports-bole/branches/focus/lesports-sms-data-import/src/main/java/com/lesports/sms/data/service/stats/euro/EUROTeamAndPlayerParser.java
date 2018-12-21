package com.lesports.sms.data.service.stats.euro;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.Gender;
import com.lesports.sms.api.common.PlayerType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.data.util.StatsConstants;
import com.lesports.sms.model.*;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * Created by zhonglin on 2016/4/21.
 */
@Service("EUROTeamAndPlayerParser")
public class EUROTeamAndPlayerParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(EUROTeamAndPlayerParser.class);

    private final Long GAME_F_TYPE = 100015000L;


    @Override
    public Boolean parseData(String file) {
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElmement = document.getRootElement();
            Element sportsRosters = rootElmement.element("sports-roster");
            String sportName = sportsRosters.element("league").attributeValue("alias");
            String season = sportsRosters.element("season").attributeValue("year");
            Competition competition = SbdsInternalApis.getCompetitionById(StatsConstants.COMPETITIONS_ID);
            if(competition == null){
                logger.debug("competion is not exits");
                return result;
            }
            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
            internalQuery.addCriteria(InternalCriteria.where("cid").is(competition.getId()));
            internalQuery.addCriteria(InternalCriteria.where("season").regex(season));
            List<CompetitionSeason> competitionSeasons = SbdsInternalApis.getCompetitionSeasonsByQuery(internalQuery);
            if (CollectionUtils.isEmpty(competitionSeasons)) {
                logger.debug(" the right season competion is not exits");
                return result;
            }
            Long csid = competitionSeasons.get(0).getId();
            // get sport-rosters
            Element nbaRosters = sportsRosters.element("ifb-soccer-roster");
            Iterator<Element> euroRosterList = nbaRosters.elementIterator("ifb-team-roster");
            while (euroRosterList.hasNext()) {
                Element euroRoster = euroRosterList.next();
                updateTeamsAndPlayers(competition.getId(), csid, euroRoster);
            }
            result = true;
        } catch (Exception e) {
            logger.error("team and player updated error: ", e);
        }
        return result;
    }

    /**
     * 更新、创建球队信息
     *
     * @param cid
     * @param csid
     * @param euroRoster
     */
    private void updateTeamsAndPlayers(Long cid, Long csid, Element euroRoster) {
        Set cidMap = new HashSet<Long>();
        cidMap.add(cid);
        String partnerId = euroRoster.element("team-info").attributeValue("global-id");
        String teamName = "";
        if(Constants.nationalityMap.get(euroRoster.element("team-info").attributeValue("display-name"))!=null){
            teamName = Constants.nationalityMap.get(euroRoster.element("team-info").attributeValue("display-name"));
        }
        InternalQuery internalQuery = new InternalQuery();
        internalQuery.addCriteria(InternalCriteria.where("name").is(teamName));
        internalQuery.addCriteria(InternalCriteria.where("game_f_type").is(GAME_F_TYPE));
        List<Team> pageTeam =  SbdsInternalApis.getTeamsByQuery(internalQuery);

        if(CollectionUtils.isNotEmpty(pageTeam)){
            for(Team team:pageTeam){
                if(!team.getName().equals(teamName)){
                    continue;
                }
                if(team.getStatsId()!=null){
                    continue;
                }
                team.setStatsId(partnerId);
                if(SbdsInternalApis.saveTeam(team)>0L) {
                    logger.info("new team is added sucessfully,id:{} ", partnerId);
                }
                else{
                    logger.warn("team added fail ,id:{}",partnerId);
                    return;
                }
            }
        }

        Team resultTeam = SbdsInternalApis.getTeamByStatsId(partnerId);
        TeamSeason teamSeason = null;
        if(resultTeam==null){
            logger.debug("");
        }
        List<TeamSeason> seasonList = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(resultTeam.getId(), csid);
        if (null != seasonList && !seasonList.isEmpty()) {

            teamSeason = seasonList.get(0);
        }
        else {
            teamSeason = new TeamSeason();
            teamSeason.setAllowCountries(getAllowCountries());
        }
        teamSeason.setTid(resultTeam.getId());
        teamSeason.setCsid(csid);


        //教练
        String coachId = euroRoster.element("manager").attributeValue("global-id");
        Player coach = SbdsInternalApis.getPlayerByStatsId(coachId);
        if (coach == null) {
            coach = getCoach(euroRoster.element("manager"));
            if (coach != null) {
                teamSeason.setCoachId(coach.getId());
                teamSeason.setCoachName(coach.getName());
            }
        }

        List<TeamSeason.TeamPlayer> playerList = new ArrayList<>();
        Iterator<Element> players = euroRoster.elementIterator("ifb-roster-player");
        if (players != null) {
            while (players.hasNext()) {
                Element player = players.next();
                TeamSeason.TeamPlayer teamPlayer = new TeamSeason.TeamPlayer();
                String shirtNum = player.element("player-number").attributeValue("number");
                Player currentPlayer = getPlayer(player);
                if (currentPlayer == null) return;
                teamPlayer.setNumber(CommonUtil.parseLong(shirtNum, 0L));
                teamPlayer.setPid(currentPlayer.getId());
                teamPlayer.setName(currentPlayer.getName());
                teamPlayer.setPosition(currentPlayer.getPosition());
                String pos = player.element("player-position").attributeValue("english");
                String positionName = "";
                if (pos.equalsIgnoreCase("Forward")) {
                    positionName = "前锋";
                }
                if (pos.equalsIgnoreCase("Midfielder")) {
                    positionName = "中场";
                }
                if (pos.equalsIgnoreCase("Defender")) {
                    positionName = "后卫";
                }
                if (pos.equalsIgnoreCase("Goalkeeper")) {
                    positionName = "守门员";
                }
                teamPlayer.setPositionName(positionName);
                teamPlayer.setPostionOrder(LeNumberUtils.toInt(player.element("player-position").attributeValue("id")));
                playerList.add(teamPlayer);
            }
        }
        teamSeason.setPlayers(playerList);
        SbdsInternalApis.saveTeamSeason(teamSeason);
        logger.info("teamSeason is updated sucessfully" + teamSeason.getTid());
    }


    /**
     * 获取球员数据（若球员不存在则先创建，在获取）
     *
     * @param playerElement
     */
    private Player getPlayer(Element playerElement) {
        String partnerId = playerElement.element("player-code").attributeValue("global-id");

        Player currentPlayer = SbdsInternalApis.getPlayerByStatsId(partnerId);
        if (null != currentPlayer) {
            return currentPlayer;
        }
        //判断是否现有球员
        if (StatsConstants.existPidMap.get(partnerId) != null) {
            currentPlayer = SbdsInternalApis.getPlayerById(LeNumberUtils.toLong(StatsConstants.existPidMap.get(partnerId)));
            if (currentPlayer != null) {
                currentPlayer.setStatsId(partnerId);
                SbdsInternalApis.savePlayer(currentPlayer);
                return currentPlayer;
            }
        }
        Player player = new Player();
        //大项 足球
        player.setGameFType(GAME_F_TYPE);
        player.setStatsId(partnerId);
        String englishName = "";
        if (StringUtils.isBlank(playerElement.element("name").attributeValue("first-name"))) {
            englishName = playerElement.element("name").attributeValue("last-name");
        } else {
            englishName = playerElement.element("name").attributeValue("first-name") + " " + playerElement.element("name").attributeValue("last-name");
        }
        if (StatsConstants.cnNameMap.get(partnerId) != null) {
            player.setName(StatsConstants.cnNameMap.get(partnerId));
        } else {
            player.setName(englishName);
        }
        player.setEnglishName(englishName);
        player.setFirstLetter(player.getEnglishName().substring(0, 1));
        player.setGender(Gender.MALE);
        player.setHeight((int) (CommonUtil.parseDouble(playerElement.element("height").attributeValue("meters"), 0d) * 100));
        player.setWeight(CommonUtil.parseInt(playerElement.element("weight").attributeValue("kg"), 0));
        player.setAllowCountries(getAllowCountries());
        player.setMultiLangNames(getMultiLang(player.getName()));
        player.setOnlineLanguages(getOnlineLanguage());
        //生日
        String month = playerElement.element("birth-date").attributeValue("month");
        if (Integer.parseInt(month) < 10) {
            month = "0" + month;
        }
        String date = playerElement.element("birth-date").attributeValue("date");
        if (Integer.parseInt(date) < 10) {
            date = "0" + date;
        }
        String year = playerElement.element("birth-date").attributeValue("year");
        String birthDate = year + month + date;
        player.setBirthdate(birthDate);

        //国家
        player.setNationality(Constants.nationalityMap.get(playerElement.element("nationality").attributeValue("name")));

        //位置
        String id = "0";
        String pos = playerElement.element("player-position").attributeValue("english");
        if (StringUtils.isNotEmpty(pos)) {
            logger.info("thecurre pos of the player id: {}, pos: {} ", partnerId, pos);

            if (pos.equalsIgnoreCase("Forward")) {
                id = Constants.positionMap.get("F");
            }
            if (pos.equalsIgnoreCase("Midfielder")) {
                id = Constants.positionMap.get("M");
            }
            if (pos.equalsIgnoreCase("Defender")) {
                id = Constants.positionMap.get("D");
            }
            if (pos.equalsIgnoreCase("Goalkeeper")) {
                id = Constants.positionMap.get("G");
            }
        }
        player.setPosition(CommonUtil.parseLong(id, 0L));
        player.setType(PlayerType.PLAYER);
        if (SbdsInternalApis.savePlayer(player) > 0L) {
            logger.info("new team is added sucessfully" + player.getPartnerId());
            return SbdsInternalApis.getPlayerByStatsId(partnerId);
        } else return null;
    }

    /**
     * 获取教练数据（若教练不存在则先创建，在获取）
     *
     * @param coachElement
     */
    private Player getCoach(Element coachElement) {
        String partnerId = coachElement.attributeValue("global-id");
        if (StringUtils.isEmpty(partnerId)) return null;
        Player currentCoach = SbdsInternalApis.getPlayerByStatsId(partnerId);
        if (null != currentCoach) {
            return currentCoach;
        }
        Player player = new Player();
        //大项 足球
        player.setGameFType(GAME_F_TYPE);
        player.setStatsId(partnerId);
        String englishName = "";
        if (StringUtils.isBlank(coachElement.attributeValue("last"))) {
            englishName = coachElement.attributeValue("last");
        } else {
            englishName = coachElement.attributeValue("first") + " " + coachElement.attributeValue("last");
        }
        player.setName(englishName);
        player.setEnglishName(englishName);
        player.setGender(Gender.MALE);
        player.setType(PlayerType.COACH);
        player.setAllowCountries(getAllowCountries());
        player.setOnlineLanguages(getOnlineLanguage());
        player.setMultiLangNames(getMultiLang(player.getName()));
        boolean result = false;
        int tryCount = 3;
        while (!result && tryCount > 0) {
            result = SbdsInternalApis.savePlayer(player) > 0L;
            tryCount--;
        }
        logger.info("new team is added sucessfully" + player.getPartnerId());
        return SbdsInternalApis.getPlayerByStatsId(partnerId);
    }
}
