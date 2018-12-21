package com.lesports.sms.data.service.stats;

import com.google.common.collect.Sets;
import com.lesports.sms.api.common.Gender;
import com.lesports.sms.api.common.PlayerType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created by qiaohongxin on 2015/9/17.
 */

@Service
public class NBATeamAndPlayerParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(NBATeamAndPlayerParser.class);

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
            Element sportsRosters = rootElmement.element("sports-rosters");
            String sportName = sportsRosters.element("league").attributeValue("alias");
            String season = sportsRosters.element("season").attributeValue("season");
            Long gameFTypeId = SbdsInternalApis.getDictEntriesByName("篮球$").get(0).getId();
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType("^" + sportName, gameFTypeId);
            if (competitions == null || competitions.isEmpty()) {
                logger.warn("can not find relative competions,name is" + sportName);
                return result;
            }
            CompetitionSeason competitionseason = SbdsInternalApis.getCompetitionSeasonByCidAndSeason(competitions.get(0).getId(), season);
            if (competitionseason == null) {
                logger.warn("can not find the right time competions,name is" + sportName + "season is" + season);
                return result;
            }
            Long csid = competitionseason.getId();
            // get sport-rosters
            Element nbaRosters = sportsRosters.element("nba-rosters");
            Iterator<Element> nbaRosterList = nbaRosters.elementIterator("nba-roster");
            while (nbaRosterList.hasNext()) {
                Element nbaRoster = nbaRosterList.next();
                updateTeamsAndPlayers(competitions.get(0).getId(), csid, gameFTypeId, nbaRoster);
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
     * @param nbaRoster
     */
    private void updateTeamsAndPlayers(Long cid, Long csid, Long gameFTyeId, Element nbaRoster) {
        Set cidMap = new HashSet<Long>();
        cidMap.add(cid);
        String partnerId = nbaRoster.element("team-code").attributeValue("global-id");
        Integer partnerType = Constants.PartnerSourceStaticId;
        Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(partnerId, partnerType);
        if (null == team) {
            team = new Team();
            team.setCids(Sets.newHashSet(cid));
            team.setPartnerType(partnerType);
            team.setPartnerId(partnerId);
            team.setName(nbaRoster.element("team-name").attributeValue("name"));
            team.setMultiLangNames(getMultiLang(nbaRoster.element("team-name").attributeValue("name")));
            team.setCity(nbaRoster.element("team-city").attributeValue("city"));
            team.setMultiLangCities(getMultiLang(nbaRoster.element("team-city").attributeValue("city")));
            team.setAllowCountries(getAllowCountries());
            team.setOnlineLanguages(getOnlineLanguage());
            team.setGameFType(gameFTyeId);//大项 篮球
        }
        team.setCurrentCid(cid);
        team.setCurrentCsid(csid);
        Long teamId = SbdsInternalApis.saveTeam(team);
        logger.info("new team is added sucessfully" + team.getPartnerId());

        TeamSeason teamSeason = null;
        List<TeamSeason> seasonList = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(teamId, csid);
        if (null != seasonList && !seasonList.isEmpty()) {
            teamSeason = seasonList.get(0);
        } else {
            teamSeason = new TeamSeason();
            teamSeason.setAllowCountries(getAllowCountries());
        }

        teamSeason.setTid(teamId);
        teamSeason.setCsid(csid);
        Long coachId = getCoachId(cid, gameFTyeId, nbaRoster.element("coach"));
        teamSeason.setCoachId(coachId);
        teamSeason.setCoachName(nbaRoster.element("coach").attributeValue("display-name"));

        List<TeamSeason.TeamPlayer> playerList = new ArrayList<>();
        Iterator<Element> players = nbaRoster.elementIterator("nba-player");
        if (players != null) {
            while (players.hasNext()) {
                Element player = players.next();
                TeamSeason.TeamPlayer teamPlayer = new TeamSeason.TeamPlayer();
                String shirtNum = player.element("player-number").attributeValue("number");
                if (shirtNum.equals("00")) shirtNum = "100";
                Long playerId = getPlayerId(cid, gameFTyeId, player);
                teamPlayer.setNumber(CommonUtil.parseLong(shirtNum, 0L));
                teamPlayer.setPid(playerId);
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
    private Long getCoachId(Long cid, Long gameFTypeId, Element playerElement) {
        String partnerId = playerElement.attributeValue("id");
        String name = playerElement.attributeValue("display-name");
        Integer partnerType = Constants.PartnerSourceStaticId;
        Player currentPlayer = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, partnerType);
        if (currentPlayer != null) {
            return currentPlayer.getId();
        }
        currentPlayer = new Player();
        //大项 篮球
        currentPlayer.setType(PlayerType.COACH);
        currentPlayer.setGameFType(gameFTypeId);
        currentPlayer.setPartnerId(partnerId);
        currentPlayer.setPartnerType(partnerType);
        currentPlayer.setCids(Sets.newHashSet(cid));
        currentPlayer.setName(name);
        currentPlayer.setMultiLangNames(getMultiLang(name));
        currentPlayer.setGender(Gender.MALE);
        currentPlayer.setAllowCountries(getAllowCountries());
        currentPlayer.setOnlineLanguages(getOnlineLanguage());
        Long id = SbdsInternalApis.savePlayer(currentPlayer);
        logger.info("new team is added sucessfully" + currentPlayer.getPartnerId());
        return id;
    }


    /**
     * 获取球员数据（若球员不存在则先创建，在获取）
     *
     * @param playerElement
     */
    private Long getPlayerId(Long cid, Long gameFTypeId, Element playerElement) {
        String partnerId = playerElement.element("player-code").attributeValue("global-id");
        String name = playerElement.element("name").attributeValue("display-name");

        Integer partnerType = Constants.PartnerSourceStaticId;
        String positionName = Constants.basketBallPositionMap.get(playerElement.element("player-position").attributeValue("abbrev"));
        DictEntry position = SbdsInternalApis.getDictEntryByNameAndParentId("^" + positionName, 100008000L);
        if (position == null) {
            logger.error("POSITION MAP miss the position:{}", positionName);
        }
        Long positionId = position == null ? 0L : position.getId();
        String school = playerElement.element("school").attributeValue("school");
        String experience = playerElement.element("experience").attributeValue("experience");
        Element draft = playerElement.element("draft-info").element("draft");
        String draftContent = getDraftontent(draft.attributeValue("year"), draft.attributeValue("round"), draft.attributeValue("pick"));
        String birthYear1 = playerElement.element("birth-date").attributeValue("year");
        String birthMonth = playerElement.element("birth-date").attributeValue("month");
        String birthDate = playerElement.element("birth-date").attributeValue("date");
        Integer height = CommonUtil.changeInchesToCm(playerElement.element("height").attributeValue("inches"));
        Integer weight = CommonUtil.changePoundToKg(playerElement.element("weight").attributeValue("pounds"));
        if (StringUtils.isNotEmpty(birthMonth) && birthMonth.length() == 1) birthMonth = "0" + birthMonth;
        if (StringUtils.isNotEmpty(birthDate) && birthDate.length() == 1) birthDate = "0" + birthDate;
        Player currentPlayer = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, partnerType);
        if (null == currentPlayer) {
            currentPlayer = new Player();
            //大项 篮球
            currentPlayer.setType(PlayerType.PLAYER);
            currentPlayer.setGameFType(gameFTypeId);
            currentPlayer.setPartnerId(partnerId);
            currentPlayer.setPartnerType(partnerType);
            currentPlayer.setCids(Sets.newHashSet(cid));
            currentPlayer.setName(name);
            currentPlayer.setMultiLangNames(getMultiLang(name));
            currentPlayer.setGender(Gender.MALE);
            currentPlayer.setAllowCountries(getAllowCountries());
            currentPlayer.setPosition(positionId);
            currentPlayer.setPositionName(positionName);
            currentPlayer.setMultiLangPositionNames(getMultiLang(positionName));
            currentPlayer.setHeight(height);
            currentPlayer.setWeight(weight);
            currentPlayer.setOnlineLanguages(getOnlineLanguage());
            currentPlayer.setSchool(school);
            currentPlayer.setBirthdate(birthYear1 + birthMonth + birthDate);
            currentPlayer.setExperience(experience);
            currentPlayer.setDraft(draftContent);
            currentPlayer.setMultiLangDraftValue(getMultiLang(draftContent));
            Long id = SbdsInternalApis.savePlayer(currentPlayer);
            currentPlayer.setId(id);
        }
        //  the code used to refresh the data of the online
//        currentPlayer.setBirthdate(birthYear1 + birthMonth + birthDate);
//        currentPlayer.setHeight(height);
//        currentPlayer.setWeight(weight);
//        currentPlayer.setDraft(draftContent);
//        currentPlayer.setMultiLangDraftValue(getMultiLang(draftContent));

        List<DictEntry> countrys = SbdsInternalApis.getDictEntriesByCode(playerElement.element("birth-country").attributeValue("abbrev"));
        if (CollectionUtils.isNotEmpty(countrys) && currentPlayer.getCountryId() == null)
            currentPlayer.setCountryId(countrys.get(0).getId());
        currentPlayer.setBirthdate(StringUtils.isEmpty(currentPlayer.getBirthdate()) ? birthYear1 + birthMonth + birthDate : currentPlayer.getBirthdate());
        currentPlayer.setPosition(currentPlayer.getPosition() == null ? positionId : currentPlayer.getPosition());
        currentPlayer.setPositionName(currentPlayer.getPositionName() == null ? positionName : currentPlayer.getPositionName());
        currentPlayer.setMultiLangPositionNames(CollectionUtils.isEmpty(currentPlayer.getMultiLangPositionNames()) ? getMultiLang(positionName) : currentPlayer.getMultiLangPositionNames());
        currentPlayer.setSchool(currentPlayer.getSchool() == null ? school : currentPlayer.getSchool());
        currentPlayer.setExperience(currentPlayer.getExperience() == null ? experience : currentPlayer.getExperience());
           currentPlayer.setDraft(currentPlayer.getDraft() == null ? draftContent : currentPlayer.getDraft());
           currentPlayer.setMultiLangDraftValue(CollectionUtils.isEmpty(currentPlayer.getMultiLangDraftValue()) ? getMultiLang(draftContent) : currentPlayer.getMultiLangDraftValue());
        SbdsInternalApis.savePlayer(currentPlayer);
        logger.info("new team is added sucessfully" + currentPlayer.getPartnerId());
        return currentPlayer.getId();
    }

    private String getDraftontent(String year, String round, String num) {
        if (round.equalsIgnoreCase("Undrafted")) return "";
        return year + "第" + round + "轮" + num + "号";
    }

}
