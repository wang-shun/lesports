package com.lesports.sms.data.service;

import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.F1RandStatistic;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.*;
import com.lesports.sms.service.*;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.velocity.runtime.directive.Parse;
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
 * Created by ruiyuansheng on 2015/5/29.
 */
@Service("playerAndTeamRandF1Parser")
public class PlayerAndTeamRandF1Parser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(PlayerAndTeamRandF1Parser.class);

    @Override
    public Boolean parseData(String file) {
        logger.info("player rand f1  parser begin");
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
            String gameF = rootElement.element("Sport").attribute("id").getValue();
            Element category = rootElement.element("Sport").element("Category");
            String gameS = category.attribute("id").getValue();
            Element stage = category.element("Stage");
            String startDate = stage.attribute("startDate").getValue();
            String season = startDate.substring(0, 4);
            //查找比赛类型
            String sportsType = Constants.sportsTypeMap.get(rootElement.element("Sport").attributeValue("id"));
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + sportsType + "$");
            Long gameFType = dictEntryList.get(0).getId();

            String name = Constants.f1NameMap.get(gameS);
            List<Competition> competitions = SbdsInternalApis.getCompetitionByNameAndGameFType(name, gameFType);
            if (CollectionUtils.isEmpty(competitions)) {
                logger.warn("can not find relative event,name:{}", name);
                return result;
            }
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(competitions.get(0).getId());
            if (competitionSeason == null) {
                logger.warn("can not find relative event,uniqueTournamentId:{},name:{},season:{}", gameS, name, season);
                return result;
            }

            Iterator players = stage.element("OutrightCompetitors").elementIterator("OutrightTeam");
            Iterator teams = stage.element("OutrightTeams").elementIterator("OutrightTeam");

            Long type = 0L;
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^车手榜$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                type = dictEntries.get(0).getId();
            }
            TopList topList = new TopList();
            List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndType(competitionSeason.getId(), type);
            if (CollectionUtils.isNotEmpty(topLists)) {
                topList = topLists.get(0);
            }
            topList.setDeleted(false);
            topList.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            topList.setCid(competitions.get(0).getId());
            topList.setCsid(competitionSeason.getId());
            topList.setLatest(true);
            topList.setType(type);
            List<TopList.TopListItem> topListItems = new ArrayList<>();

            String playerScore = "0";
            String playerPostion = "0";
            String carNumber = "0";
            String racesWithPoints = "0";
            String racesStarted = "0";
            String victories = "0";
            String perfectRaces = "0";
            String fastestLaps = "0";
            String podiums = "0";
            String polePositions = "0";
            while (players.hasNext()) {
                TopList.TopListItem topListItem = new TopList.TopListItem();
                playerScore = "0";
                playerPostion = "0";
                carNumber = "0";
                racesWithPoints = "0";
                racesStarted = "0";
                victories = "0";
                perfectRaces = "0";
                fastestLaps = "0";
                podiums = "0";
                polePositions = "0";

                Element outrightTeam = (Element) players.next();
                Element player = outrightTeam.element("Team");
                String partnerId = player.attributeValue("id");
                String teamPartnerId = player.attributeValue("parentTeamId");
                Team teamBelong = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, Integer.parseInt(Constants.partnerSourceId));
                if (teamBelong == null) {
                    logger.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
                    continue;
                }
                Player player1 = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, Integer.parseInt(Constants.partnerSourceId));
                if (null == player1) {
                    logger.warn("cannot find player,playerPartnerId:{}", partnerId);
                    player1 = new Player();
                    player1.setAllowCountries(getAllowCountries());
                    player1.setOnlineLanguages(getOnlineLanguage());
                    player1.setName(player.attributeValue("name"));
                    player1.setMultiLangNames(getMultiLang(player.attributeValue("name")));
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
                    Element country = player.element("Country");
                    if (null != country) {
                        player1.setNationality(country.attributeValue("name"));
                        player1.setMultiLangNationalities(getMultiLang(country.attributeValue("name")));

                    }

                    player1.setPartnerId(partnerId);
                    player1.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                    player1.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                    player1.setDeleted(false);
                    List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^赛车$");
                    if (CollectionUtils.isNotEmpty(dictEntries2)) {
                        player1.setGameFType(dictEntries2.get(0).getId());
                    }
                    SbdsInternalApis.savePlayer(player1);
                    logger.info("insert into player sucess,partnerId:" + partnerId);
                }

                Iterator teamValues = outrightTeam.elementIterator("TeamValue");

                while (teamValues.hasNext()) {
                    Element teamValue = (Element) teamValues.next();
                    String typeId = teamValue.attribute("typeId").getValue();
                    String value = teamValue.attributeValue("value");
                    if (Constants.F1Postion.equals(typeId)) {
                        playerPostion = value;
                    } else if (Constants.F1Points.equals(typeId)) {
                        playerScore = value;
                    } else if ("51".equals(typeId)) {
                        carNumber = value;
                    } else if ("55".equals(typeId)) {
                        racesWithPoints = value;
                    } else if ("54".equals(typeId)) {
                        racesStarted = value;
                    } else if ("53".equals(typeId)) {
                        victories = value;
                    } else if ("59".equals(typeId)) {
                        perfectRaces = value;
                    } else if ("58".equals(typeId)) {
                        fastestLaps = value;
                    } else if ("57".equals(typeId)) {
                        podiums = value;
                    } else if ("56".equals(typeId)) {
                        polePositions = value;
                    }
                }
                if ("0".equals(playerPostion)) {
                    continue;
                }
                topListItem.setCompetitorId(player1.getId());
                topListItem.setTeamId(teamBelong.getId());
                topListItem.setCompetitorType(CompetitorType.PLAYER);
                topListItem.setRank(Integer.parseInt(playerPostion));
                topListItem.setShowOrder(Integer.parseInt(playerPostion));
                F1RandStatistic f1DriverRandStatistic = new F1RandStatistic();
                f1DriverRandStatistic.setCarNumber(Integer.parseInt(carNumber));
                f1DriverRandStatistic.setFastestLaps(Integer.parseInt(fastestLaps));
                f1DriverRandStatistic.setPerfectRaces(Integer.parseInt(perfectRaces));
                f1DriverRandStatistic.setPodiums(Integer.parseInt(podiums));
                f1DriverRandStatistic.setPoints(Integer.parseInt(playerScore));
                f1DriverRandStatistic.setPolePositions(Integer.parseInt(polePositions));
                f1DriverRandStatistic.setRacesStarted(Integer.parseInt(racesStarted));
                f1DriverRandStatistic.setRacesWithPoints(Integer.parseInt(racesWithPoints));
                f1DriverRandStatistic.setVictories(Integer.parseInt(victories));
                f1DriverRandStatistic.setPosition(Integer.parseInt(playerPostion));
                Map<String, Object> map = CommonUtil.convertBeanToMap(f1DriverRandStatistic);
                topListItem.setStats(map);
                topListItems.add(topListItem);
                //result = true;
            }
            topList.setItems(topListItems);
            topList.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            SbdsInternalApis.saveTopList(topList);
            logger.info("insert toplist success,cId:{},csId:{},items:{}", topList.getCid(), topList.getCsid(), topList.getItems());

            Long teamType = 0L;
            List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^车队榜$");
            if (CollectionUtils.isNotEmpty(dictEntries2)) {
                teamType = dictEntries2.get(0).getId();
            }
            TopList teamTopList = new TopList();
            List<TopList> teamTopLists = SbdsInternalApis.getTopListsByCsidAndType(competitionSeason.getId(), teamType);
            if (CollectionUtils.isNotEmpty(teamTopLists)) {
                teamTopList = teamTopLists.get(0);
            }
            else{
                teamTopList.setAllowCountries(getAllowCountries());
                teamTopList.setOnlineLanguages(getOnlineLanguage());
            }
            teamTopList.setDeleted(false);
            teamTopList.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            teamTopList.setCid(competitions.get(0).getId());
            teamTopList.setCsid(competitionSeason.getId());
            teamTopList.setLatest(true);
            teamTopList.setType(teamType);
            List<TopList.TopListItem> teamTopListItems = new ArrayList<>();
            while (teams.hasNext()) {
                TopList.TopListItem topListItem = new TopList.TopListItem();
                playerScore = "0";
                playerPostion = "0";
                racesWithPoints = "0";
                racesStarted = "0";
                victories = "0";
                perfectRaces = "0";
                fastestLaps = "0";
                podiums = "0";
                polePositions = "0";

                Element outrightTeam = (Element) teams.next();
                Element team = outrightTeam.element("Team");
                String teamPartnerId = team.attributeValue("id");

                Team team2 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, Integer.parseInt(Constants.partnerSourceId));
                if (team2 == null) {
                    logger.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
                    team2 = new Team();
                    team2.setAllowCountries(getAllowCountries());
                    team2.setOnlineLanguages(getOnlineLanguage());
                    team2.setName(team.attributeValue("name"));
                    team2.setMultiLangNames(getMultiLang(team.attributeValue("name")));
                    Element translation = team.element("Translation");
                    if (null != translation) {
                        Iterator translationIterator = translation.elementIterator("TranslatedValue");
                        while (translationIterator.hasNext()) {
                            Element translatedValue = (Element) translationIterator.next();
                            if ("zh".equals(translatedValue.attributeValue("lang")))
                                team2.setName(translatedValue.attributeValue("value"));
                            team2.setMultiLangNames(getMultiLang(translatedValue.attributeValue("value")));
                        }
                    }
                    team2.setPartnerId(teamPartnerId);
                    team2.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                    team2.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                    team2.setDeleted(false);
                    List<DictEntry> dictEntries4 = SbdsInternalApis.getDictEntriesByName("^赛车$");
                    if (CollectionUtils.isNotEmpty(dictEntries4)) {
                        team2.setGameFType(dictEntries4.get(0).getId());
                    }
                    SbdsInternalApis.saveTeam(team2);
                    logger.info("insert into team sucess,partnerId:" + teamPartnerId);
                }

                Iterator teamValues = outrightTeam.elementIterator("TeamValue");

                while (teamValues.hasNext()) {
                    Element teamValue = (Element) teamValues.next();
                    String typeId = teamValue.attribute("typeId").getValue();
                    String value = teamValue.attributeValue("value");
                    if (Constants.F1Postion.equals(typeId)) {
                        playerPostion = value;
                    } else if (Constants.F1Points.equals(typeId)) {
                        playerScore = value;
                    } else if ("55".equals(typeId)) {
                        racesWithPoints = value;
                    } else if ("54".equals(typeId)) {
                        racesStarted = value;
                    } else if ("53".equals(typeId)) {
                        victories = value;
                    } else if ("59".equals(typeId)) {
                        perfectRaces = value;
                    } else if ("58".equals(typeId)) {
                        fastestLaps = value;
                    } else if ("57".equals(typeId)) {
                        podiums = value;
                    } else if ("56".equals(typeId)) {
                        polePositions = value;
                    }
                }
                topListItem.setCompetitorId(team2.getId());
                topListItem.setCompetitorType(CompetitorType.TEAM);
                topListItem.setRank(Integer.parseInt(playerPostion));
                topListItem.setShowOrder(Integer.parseInt(playerPostion));
                F1RandStatistic f1RandStatistic = new F1RandStatistic();
                f1RandStatistic.setFastestLaps(Integer.parseInt(fastestLaps));
                f1RandStatistic.setPerfectRaces(Integer.parseInt(perfectRaces));
                f1RandStatistic.setPodiums(Integer.parseInt(podiums));
                f1RandStatistic.setPoints(Integer.parseInt(playerScore));
                f1RandStatistic.setPolePositions(Integer.parseInt(polePositions));
                f1RandStatistic.setRacesStarted(Integer.parseInt(racesStarted));
                f1RandStatistic.setRacesWithPoints(Integer.parseInt(racesWithPoints));
                f1RandStatistic.setVictories(Integer.parseInt(victories));
                f1RandStatistic.setPosition(Integer.parseInt(playerPostion));
                Map<String, Object> map = CommonUtil.convertBeanToMap(f1RandStatistic);
                topListItem.setStats(map);
                teamTopListItems.add(topListItem);

            }
            teamTopList.setItems(teamTopListItems);
            teamTopList.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            SbdsInternalApis.saveTopList(teamTopList);
            logger.info("insert toplist success,cId:{},csId:{},items:{}", teamTopList.getCid(), teamTopList.getCsid(), teamTopList.getItems());
            result = true;

        } catch (DocumentException e) {
            logger.error("parse outright_Motorsport.Formula1 xml error,file:" + file, e);
        }
        return result;
    }

}
