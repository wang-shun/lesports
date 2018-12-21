package com.lesports.sms.data.service;

import com.google.common.collect.Sets;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.F1Stats;
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
 * Created by ruiyuansheng on 2015/7/13.
 */
@Service("f1StatsParser")
public class F1StatsParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(F1StatsParser.class);

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
            Element category = rootElement.element("Sport").element("Category");
            Element stage = category.element("Stage");
            String startDate = stage.attribute("startDate").getValue();
            //查找比赛类型
            String sportsType = Constants.sportsTypeMap.get(rootElement.element("Sport").attributeValue("id"));
            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + sportsType + "$");
            Long gameFType = dictEntryList.get(0).getId();
            //查找赛事Id
            Element tournament = rootElement.element("Sport").element("Category").element("Tournament");
            String gameS = tournament.attribute("uniqueTournamentId").getValue();
            String season = tournament.element("Season").attribute("start").getValue().substring(0, 4);
            String name = name = Constants.f1NameMap.get(gameS);
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


            Iterator players = stage.element("OutrightCompetitors").elementIterator("OutrightTeam");
            String stageId = stage.attributeValue("stageId");
            Match match = SbdsInternalApis.getMatchByPartnerIdAndType(stageId, Integer.parseInt(Constants.partnerSourceId));
            // 没有比赛
            if (match == null) {
                logger.warn("the match may be not exists,partnerId:{},fileName:{}", stageId, file);
                return false;
            }
            String fastestLapsTime = "";
            String totalTime = "";
            String playerScore = "0";
            String playerPostion = "0";
            Set<Match.CompetitorStat> competitorStats = Sets.newHashSet();
            while (players.hasNext()) {
                Match.CompetitorStat competitorStat = new Match.CompetitorStat();
                playerScore = "0";
                playerPostion = "0";

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
                    player1.setOnlineLanguages(getOnlineLanguage());
                    player1.setAllowCountries(getAllowCountries());
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
                    } else if ("22".equals(typeId)) {
                        fastestLapsTime = value;
                    } else if ("71".equals(typeId)) {
                        totalTime = value;
                    }
                }
                if ("0".equals(playerPostion)) {
                    continue;
                }
                competitorStat.setShowOrder(Integer.parseInt(playerPostion));
                competitorStat.setCompetitorId(player1.getId());
                competitorStat.setTeamId(teamBelong.getId());
                competitorStat.setCompetitorType(CompetitorType.PLAYER);
                F1Stats f1Stats = new F1Stats();
                f1Stats.setPoints(Integer.parseInt(playerScore));
                f1Stats.setPosition(Integer.parseInt(playerPostion));
                f1Stats.setFastLapsTime(fastestLapsTime);
                f1Stats.setTotalTime(totalTime);
                Map<String, Object> map = CommonUtil.convertBeanToMap(f1Stats);
                competitorStat.setStats(map);
                competitorStats.add(competitorStat);
            }
            match.setCompetitorStats(competitorStats);
            boolean isSuccess = false;
            int tryCount = 0;
            while (isSuccess == false && tryCount++ < Constants.MAX_TRY_COUNT) {
                try {
                    isSuccess = SbdsInternalApis.saveMatch(match) > 0;
                } catch (Exception e) {
                    logger.error("fail to update match. id : {}. sleep and try again.", match.getId(), e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
            logger.info("update match success,matchId:" + match.getId() + ",matchName:" + match.getName() + ",matchPartnerId:" + match.getPartnerId());
            result = true;
        } catch (DocumentException e) {
            logger.error("parse match" + file + " xml error", e);
        }
        return result;
    }

}
