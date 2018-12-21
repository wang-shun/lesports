package com.lesports.sms.data.service.stats;

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
import java.util.Iterator;
import java.util.List;

/**
 * Created by qiaohongxin on 2015/9/21.
 */
@Service("NBAPlayerParser")
public class NBAPlayerParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(NBAPlayerParser.class);

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
            // get sport-rosters
            Element seasonPlayers = rootElmement.element("sports-rosters");
            String sportName = seasonPlayers.element("league").attributeValue("alias");
            String season = seasonPlayers.element("season").attributeValue("season");
            Long gameFTypeId = SbdsInternalApis.getDictEntriesByName("篮球$").get(0).getId();
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType("^" + sportName, gameFTypeId);
            if (competitions == null || competitions.isEmpty()) {
                logger.warn("can not find relative competions,name is" + sportName);
                return result;
            }
            CompetitionSeason competitionseason = SbdsInternalApis.getLatestCompetitionSeasonByCid(competitions.get(0).getId());
            if (competitionseason == null) {
                logger.warn("can not find the right time competions,name is" + sportName + "season is" + season);
                return result;
            }
            Long csid = competitionseason.getId();
            Iterator<Element> roster = seasonPlayers.element("nba-rosters").elementIterator("nba-roster");
            while (roster.hasNext()) {
                Element teamRoster = roster.next();
                String teamId = teamRoster.element("team-code").attributeValue("global-id");
                String coachName = teamRoster.element("coach").attributeValue("display-name");
                String coachId = teamRoster.element("coach").attributeValue("id");
                Team playerTeam = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamId, Constants.PartnerSourceStaticId);
                if (null == playerTeam) {
                    logger.debug("team is not exist");
                    continue;
                }
                //添加教练信息
                Player coachPlayer = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(coachId, Constants.PartnerSourceStaticId);
                if (coachPlayer == null || coachPlayer.getType() == null || coachPlayer.getType() == PlayerType.PLAYER) {
                    coachPlayer = new Player();
                    coachPlayer.setPartnerId(coachId);
                    coachPlayer.setPartnerType(Constants.PartnerSourceStaticId);
                    coachPlayer.setName(coachName);
                    coachPlayer.setMultiLangNames(getMultiLang(coachName));
                    coachPlayer.setType(PlayerType.COACH);
                    SbdsInternalApis.savePlayer(coachPlayer);

                }
                Player coachPlayerNew = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(coachId, Constants.PartnerSourceStaticId);
                //更新赛季对象
                TeamSeason teamSeason = new TeamSeason();
                List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(playerTeam.getId(), csid);
                if (teamSeason != null && CollectionUtils.isNotEmpty(teamSeasons)) {
                    teamSeason = teamSeasons.get(0);
                    teamSeason.setCoachId(coachPlayerNew.getId());
                } else {
                    teamSeason.setCsid(csid);
                    teamSeason.setTid(playerTeam.getId());
                    teamSeason.setCoachId(coachPlayerNew.getId());
                }
                SbdsInternalApis.saveTeamSeason(teamSeason);
                TeamSeason teamSeasonId = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(playerTeam.getId(), csid).get(0);
                //添加球员
                Iterator<Element> players = teamRoster.elementIterator("nba-player");
                while (players.hasNext()) {
                    Element player = players.next();
                    String shirtNum = player.element("player-number").attributeValue("number");
                    Long playerId = getPlayerId(gameFTypeId, player);
                    List<TeamSeason.TeamPlayer> playerList = teamSeason.getPlayers();
                    if (isPlayerExist(playerId, playerList)) continue;
                    SbdsInternalApis.addTeamPlayer(teamSeason.getId(), playerId, CommonUtil.parseLong(shirtNum, 0L));
                }
            }
            result = true;
        } catch (Exception e) {
            logger.error("team and player info parse error: ", e);
        }
        return result;
    }

    private boolean isPlayerExist(Long playerId, List<TeamSeason.TeamPlayer> list) {
        boolean result = false;
        if (list == null || list.isEmpty()) return false;
        for (TeamSeason.TeamPlayer currentPlayer : list) {
            if (currentPlayer.getPid().longValue() != playerId.longValue()) continue;
            result = true;
        }
        return result;
    }

    private Long getPlayerId(Long gameFTypeId, Element playerElement) {

        String partnerId = playerElement.element("player-code").attributeValue("global-id");
        Player player = new Player();
        Player currentPlayer = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, Constants.PartnerSourceStaticId);
        if (null != currentPlayer) {
            return currentPlayer.getId();
        }
        //大项 篮球

        player.setGameFType(gameFTypeId);
        player.setPartnerId(partnerId);
        player.setPartnerType(Constants.PartnerSourceStaticId);
        player.setName(playerElement.element("name").attributeValue("display-name"));
        player.setMultiLangNames(getMultiLang(playerElement.element("name").attributeValue("display-name")));
        player.setGender(Gender.MALE);
        player.setEnglishName(playerElement.element("name").attributeValue("display-name"));
        player.setCity(playerElement.element("birth-city").attributeValue("city"));
        player.setMultiLangCities(getMultiLang(playerElement.element("birth-city").attributeValue("city")));
        player.setWeight(CommonUtil.parseInt(playerElement.element("weight").attributeValue("pounds"), 0));
        String birthday = playerElement.element("birth-date").attributeValue("year") + playerElement.element("birth-date").attributeValue("month") + playerElement.element("birth-date").attributeValue("date");
        player.setBirthdate(birthday);
        player.setHeight(CommonUtil.parseInt(playerElement.element("height").attributeValue("inches"), 0));
        String position = playerElement.element("player-position").attributeValue("abbrev");
        if (StringUtils.isNotEmpty(Constants.basketBallPositionMap.get(position))) {
            List<DictEntry> dictEntrys = SbdsInternalApis.getDictEntriesByName((Constants.basketBallPositionMap.get(position)));
            if (CollectionUtils.isNotEmpty(dictEntrys)) {
                player.setPosition(player.getPosition() == null ? dictEntrys.get(0).getId() : player.getPosition());
            }
        }
        player.setAllowCountries(getAllowCountries());
        player.setOnlineLanguages(getOnlineLanguage());
        SbdsInternalApis.savePlayer(player);
        logger.info("add new  player success andplayerId:{}" + partnerId);
        return SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, Constants.PartnerSourceStaticId).getId();
    }
}
