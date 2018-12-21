package com.lesports.sms.data.service;

import com.google.common.collect.Lists;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.PlayerRandStatistic;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;

/**
 * Created by lufei1 on 2014/12/9.
 */
@Service("playerRandFbParser")
public class PlayerRandFbParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(PlayerRandFbParser.class);

    @Override
    public Boolean parseData(String file) {
        logger.info("player rand fb  parser begin");
        Boolean result = false;

        File xmlFile = new File(file);
        if (!xmlFile.exists()) {
            logger.warn("parsing the file:{} not exists", file);
            return result;
        }
        SAXReader reader = new SAXReader();
        try {
            Document document = reader.read(xmlFile);
            Element rootElement = document.getRootElement();
            Element tournament = rootElement.element("Sport").element("Category").element("Tournament");
            String gameS = tournament.attribute("uniqueTournamentId").getValue();
            String season = tournament.element("Season").attribute("start").getValue().substring(0, 4);
            String name = Constants.nameMap.get(gameS);
            //查找比赛类型
            String sportsType = Constants.sportsTypeMap.get(rootElement.element("Sport").attributeValue("id"));
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + sportsType + "$");
            Long gameFType = dictEntryList.get(0).getId();
            //赛事查询

            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType(name, gameFType);
            if (CollectionUtils.isEmpty(competitions)) {
                logger.warn("can not find relative event,uniqueTournamentId:{},name:{}", gameS, name);
                return result;
            }
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(competitions.get(0).getId());
            if (competitionSeason == null) {
                logger.warn("can not find relative event,uniqueTournamentId:{},name:{},season:{}", gameS, name, season);
                return result;
            }
            Long type = 0L;
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^射手榜$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                type = dictEntries.get(0).getId();
            }
            TopList topList = new TopList();
            List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndType(competitionSeason.getId(), type);
            if (CollectionUtils.isNotEmpty(topLists)) {
                topList = topLists.get(0);
            } else {
                topList.setAllowCountries(getAllowCountries());
                topList.setOnlineLanguages(getOnlineLanguage());
            }
            Iterator teams = tournament.element("Teams").elementIterator("Team");

            topList.setDeleted(false);
            topList.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            topList.setCid(competitions.get(0).getId());
            topList.setCsid(competitionSeason.getId());
            topList.setLatest(true);
            topList.setType(type);
            List<TopList.TopListItem> topListItems = new ArrayList<>();
            List<String> competitorIds = Lists.newArrayList();
            while (teams.hasNext()) {
                Element team = (Element) teams.next();
                String teamId = team.attributeValue("id");
                Team team1 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamId, Integer.parseInt(Constants.partnerSourceId));
                if (team1 == null) {
                    logger.warn("cannot find team,teamPartnerId:{}", teamId);
                    continue;
                }
                Iterator players = team.element("GoalScorers").elementIterator("Player");
                String playerPartnerId = "0";
                while (players.hasNext()) {
                    String penaltyNum = "0";
                    String goalNum = "0";
                    String scoreOrder = "0";
                    String matchNumber = "0";
                    TopList.TopListItem topListItem = new TopList.TopListItem();
                    Element player = (Element) players.next();
                    playerPartnerId = player.attributeValue("playerID");
                    if (competitorIds.contains(playerPartnerId)) continue;
                    competitorIds.add(playerPartnerId);
                    Player player1 = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerPartnerId, Integer.parseInt(Constants.partnerSourceId));
                    if (null == player1) {
                        logger.warn("cannot find player,playerPartnerId:{}", playerPartnerId);
                        player1 = new Player();
                        player1.setAllowCountries(getAllowCountries());
                        player1.setOnlineLanguages(getOnlineLanguage());
                        player1.setName(player.attributeValue("playerName"));
                        player1.setMultiLangNames(getMultiLang(player.attributeValue("playerName")));
                        Element translation = player.element("Translation");
                        if (null != translation) {
                            Iterator translationIterator = translation.elementIterator("TranslatedValue");
                            while (translationIterator.hasNext()) {
                                Element translatedValue = (Element) translationIterator.next();
                                if ("zh".equals(translatedValue.attributeValue("lang")))
                                    player1.setName(translatedValue.attributeValue("value"));
                                player1.setMultiLangNames(getMultiLang(translatedValue.attributeValue("value")));
                            }
                        }

                        player1.setPartnerId(playerPartnerId);
                        player1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                        player1.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                        player1.setDeleted(false);
                        List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^足球$");
                        if (CollectionUtils.isNotEmpty(dictEntries2)) {
                            player1.setGameFType(dictEntries2.get(0).getId());
                        }
                        Long id = SbdsInternalApis.savePlayer(player1);
                        player1.setId(id);
                        logger.info("insert into player sucess,partnerId:" + playerPartnerId);
                    }
                    Iterator playerStatsEntries = player.element("PlayerStats").elementIterator("PlayerStatsEntry");
                    while (playerStatsEntries.hasNext()) {
                        Element playerStatsEntry = (Element) playerStatsEntries.next();
                        String statsId = playerStatsEntry.attributeValue("id");
                        //点球数
                        if ("36".equals(statsId)) {
                            penaltyNum = playerStatsEntry.attributeValue("value");
                        } else if ("2".equals(statsId)) {
                            goalNum = playerStatsEntry.attributeValue("value");
                        } else if ("4".equals(statsId)) {
                            scoreOrder = playerStatsEntry.attributeValue("value");
                        } else if ("40".equals(statsId)) {
                            matchNumber = playerStatsEntry.attributeValue("value");
                        }
                    }
                    if (scoreOrder.equals("0")) {
                        logger.warn("The player rand fb ranking is 0,cannot insert db.cId:{},playerId:{}", competitions.get(0).getId(), player1.getId());
                        continue;
                    }
                    if (player1.getId() != null && player1.getId() > 0) {
                        topListItem.setCompetitorId(player1.getId());
                        topListItem.setTeamId(team1.getId());
                        topListItem.setCompetitorType(CompetitorType.PLAYER);
                        topListItem.setRank(Integer.parseInt(scoreOrder));
                        topListItem.setShowOrder(Integer.parseInt(scoreOrder));
                        PlayerRandStatistic playerRandStatistic = new PlayerRandStatistic();
                        playerRandStatistic.setGoals(Integer.parseInt(goalNum));
                        playerRandStatistic.setPenaltyNumber(Integer.parseInt(penaltyNum));
                        playerRandStatistic.setCaps(Integer.parseInt(matchNumber));
                        Map<String, Object> map = CommonUtil.convertBeanToMap(playerRandStatistic);
                        topListItem.setStats(map);
                        topListItems.add(topListItem);
                    }
                }
            }
            topList.setItems(topListItems);
            topList.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            SbdsInternalApis.saveTopList(topList);

            logger.info("insert toplist success,cId:{},csId:{}", topList.getCid(), topList.getCsid());
            result = true;
        } catch (DocumentException e) {
            logger.error("parse goalscorers_Soccer xml error,file:" + file, e);
        }
        return result;
    }
}
