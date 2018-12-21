package com.lesports.sms.data.service;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.Gender;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.model.*;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by qiaohongxin on 2016/09/12.
 */
@Service("playerFbParser")
public class PlayerFbParser extends Parser {

    private static Logger logger = LoggerFactory.getLogger(PlayerFbParser.class);

    public Boolean parseData() {
        FtpUtil ftpUtil = new FtpUtil(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword);
        Boolean result = false;
        Iterator iter = Constants.seasonSquadFileMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Long cid = Long.valueOf(entry.getKey().toString());
            String[] teamFiles = String.valueOf(entry.getValue()).split("\\|");
            Competition competition = SbdsInternalApis.getCompetitionById(cid);
            if (competition == null) continue;
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(cid);
            if (competitionSeason == null) continue;
            if (teamFiles == null || teamFiles.length < 1) continue;
            for (String teamFile : teamFiles) {
                Boolean downloadResult = null;
                try {
                    if (!ftpUtil.loginFtp(60)) {
                        logger.error("login ftp:{} fail", ftpUtil.getFtpName());
                        return false;
                    }
                    downloadResult = ftpUtil.downloadFile(teamFile, Constants.sportradarRootPath, "//Sport//");
                } catch (IOException e) {
                    logger.error("player parse error" + teamFile);
                } finally {
                    ftpUtil.logOutFtp();
                }
                if (downloadResult == null || !downloadResult) {
                    logger.error("thirdDataJob download file:{} fail", teamFile);
                    continue;
                }
                logger.info("teamFile:{}, player parser begin", teamFile);
                File xmlFile = new File(Constants.sportradarRootPath + teamFile);
                SAXReader reader = new SAXReader();
                try {
                    Document document = reader.read(xmlFile);
                    Element rootElement = document.getRootElement();
                    Element team = (Element) rootElement.element("Sport").element("Teams").elementIterator("Team").next();
                    String gender = team.attributeValue("gender");
                    Element players = team.element("Players");
                    String teamName = team.attributeValue("name");
                    String teamPartnerId = team.attributeValue("id");

                    Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, Integer.parseInt(Constants.partnerSourceId));
                    if (null == team1) {
                        logger.warn("team not found,partnerId:{}", teamPartnerId);
                        continue;
                    }
                    TeamSeason teamSeason = new TeamSeason();
                    List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team1.getId(), competitionSeason.getId());
                    if (CollectionUtils.isNotEmpty(teamSeasons)) {
                        teamSeason = teamSeasons.get(0);
                    } else {
                        teamSeason.setAllowCountries(getAllowCountries());
                        teamSeason.setCsid(competitionSeason.getId());
                        teamSeason.setTid(team1.getId());
                    }
                    teamSeason.setPlayers(getCurrentPlayerList(gender, players, cid));
                    SbdsInternalApis.saveTeamSeason(teamSeason);

                } catch (DocumentException e) {
                    logger.error("parser player error,teamFile:" + teamFile, e);
                }
            }
        }
        return result;
    }

    private List<TeamSeason.TeamPlayer> getCurrentPlayerList(String gender, Element playersElement, Long cid) {
        Iterator players = playersElement.elementIterator("Player");
        List<TeamSeason.TeamPlayer> teamPlayers = Lists.newArrayList();
        while (players.hasNext()) {
            Element player = (Element) players.next();
            String playerEnName = player.attribute("playerName").getValue();
            String playerId = player.attribute("playerID").getValue();
            Player player1 = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerId, Integer.parseInt(Constants.partnerSourceId));
            String playerName = playerEnName;
            String height = "0";
            String weight = "0";
            String shirtNum = "-1";
            String position = "";
            String birth = "";
            String nationality = "";

            if (player.element("Translation") != null) {
                Iterator names = player.element("Translation").elementIterator("TranslatedValue");
                //球员名称，球员繁体名称
                while (names.hasNext()) {
                    Element name = (Element) names.next();
                    String langType = name.attributeValue("lang");
                    if ("zh".equals(langType)) {
                        playerName = name.attributeValue("value");
                    }
                }
            }
            Iterator playerInfoEntrys = player.element("PlayerInfo").elementIterator("PlayerInfoEntry");
            while (playerInfoEntrys.hasNext()) {
                Element playerInfoEntry = (Element) playerInfoEntrys.next();
                String entryName = playerInfoEntry.attributeValue("name");
                if ("Shirt number".equals(entryName)) {
                    shirtNum = playerInfoEntry.attributeValue("value");
                } else if ("Position".equals(entryName)) {
                    position = playerInfoEntry.attributeValue("value");
                } else if ("Weight".equals(entryName)) {
                    weight = playerInfoEntry.attributeValue("value");
                } else if ("Height".equals(entryName)) {
                    height = playerInfoEntry.attributeValue("value");
                } else if ("Date of birth".equals(entryName)) {
                    birth = playerInfoEntry.attributeValue("value");
                } else if ("Nationality".equals(entryName)) {
                    nationality = playerInfoEntry.attributeValue("value");
                }
            }
            Long positionId = 0L;
            String positionName = null;
            if (StringUtils.isNotEmpty(Constants.positionMap.get(position))) {
                positionId = Long.valueOf(Constants.positionMap.get(position));
                positionName = SbdsInternalApis.getDictById(positionId).getName();

            }
            if (null == player1) {
                InternalQuery q = new InternalQuery();
                q.addCriteria(new InternalCriteria("name", "is", playerName));
                q.addCriteria(new InternalCriteria("deleted", "is", false));
                List<Player> validPlayers = SbdsInternalApis.getPlayersByQuery(q);
                InternalQuery q1 = new InternalQuery();
                q1.addCriteria(new InternalCriteria("english_name", "is", playerEnName));
                q1.addCriteria(new InternalCriteria("deleted", "is", false));
                List<Player> validPlayers2 = SbdsInternalApis.getPlayersByQuery(q1);
                if (CollectionUtils.isNotEmpty(validPlayers)) {
                    player1 = validPlayers.get(0);
                    player1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                    player1.setPartnerId(playerId);
                } else if (CollectionUtils.isNotEmpty(validPlayers2)) {
                    player1 = validPlayers2.get(0);
                    player1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                    player1.setPartnerId(playerId);
                } else {
                    logger.warn("cannot find player,playerPartnerId:{}", playerId);
                    player1 = new Player();
                    player1.setAllowCountries(SbdsInternalApis.getCompetitionById(cid).getAllowCountries());
                    player1.setOnlineLanguages(getOnlineLanguage());
                    player1.setName(playerName);
                    player1.setMultiLangNames(getMultiLang(playerName));
                    player1.setHeight(Integer.parseInt(height));
                    player1.setWeight(Integer.parseInt(weight));
                    player1.setBirthdate(birth);
                    if (gender.equals("F"))
                        player1.setGender(Gender.FEMALE);//男：0，女：1
                    if (gender.equals("M"))
                        player1.setGender(Gender.MALE);//男：0，女：1
                    player1.setNationality(nationality);
                    player1.setMultiLangNationalities(getMultiLang(nationality));
                    player1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                    player1.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                    player1.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                    List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^足球$");
                    if (CollectionUtils.isNotEmpty(dictEntries)) {
                        player1.setGameFType(dictEntries.get(0).getId());
                    }
                    player1.setPartnerId(playerId);
                    player1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));//sportradar
                    player1.setDeleted(false);
                    player1.setPosition(positionId);
                    player1.setPositionName(positionName);
                }
            } else {
                player1.setPosition(positionId > 0 ? positionId : player1.getPosition());
                player1.setPositionName(positionName == null ? player1.getPositionName() : positionName);
            }
            Set<Long> cids = player1.getCids();
            cids.add(cid);
            player1.setCids(cids);
            Long id = SbdsInternalApis.savePlayer(player1);
            player1.setId(id);
            TeamSeason.TeamPlayer teamPlayer = new TeamSeason.TeamPlayer();
            teamPlayer.setPid(player1.getId());
            teamPlayer.setNumber(Long.valueOf(shirtNum));
            teamPlayers.add(teamPlayer);
            logger.info("insert player success },playerName:{},playerPartnerId:{}", playerEnName, playerId);
        }
        return teamPlayers;
    }

}
