//package com.lesports.sms.data.processor.sportrard;
//
//import com.google.common.collect.Sets;
//import com.lesports.sms.api.common.CompetitorType;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.Constants;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.processor.BeanProcessor;
//import com.lesports.sms.data.processor.olympic.AbstractProcessor;
//import com.lesports.sms.data.processor.sportrard.InnerProcessor.F1Processor;
//import com.lesports.sms.data.utils.CommonUtil;
//import com.lesports.sms.model.*;
//import com.lesports.utils.LeDateUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//
///**
// * Created by qiaohongxin on 2016/2/23.
// */
//public class F1StandingProcessor extends AbstractProcessor implements BeanProcessor<Document> {
//    private static Logger LOG = LoggerFactory.getLogger(F1StandingProcessor.class);
//
//    public Boolean process(String codeType, Document document) {
//        LOG.info("live score  parser begin");
//        Boolean result = false;
//        try {
//            Element rootElement = document.getRootElement();
//            String gameF = rootElement.element("Sport").attribute("id").getValue();
//            Element category = rootElement.element("Sport").element("Category");
//            Element stage = category.element("Stage");
//            String tornamentId = category.attributeValue("id");
//            String startDate = stage.attribute("startDate").getValue();
//            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(SportrardConstants.f1NameMap.get(tornamentId));
//            if (competitionSeason == null) {
//                LOG.warn("can not find relative competionSeason,tournamentId is:{}", tornamentId);
//                return result;
//            }
//            Iterator players = stage.element("OutrightCompetitors").elementIterator("OutrightTeam");
//            Iterator teams = stage.element("OutrightTeams").elementIterator("OutrightTeam");
//
//            Long type = 0L;
//            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^车手榜$");
//            if (CollectionUtils.isNotEmpty(dictEntries)) {
//                type = dictEntries.get(0).getId();
//            }
//            TopList topList = new TopList();
//            List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndType(competitionSeason.getId(), type);
//            if (CollectionUtils.isNotEmpty(topLists)) {
//                topList = topLists.get(0);
//            } else {
//                topList.setAllowCountries(getAllowCountries());
//                topList.setOnlineLanguages(getOnlineLang());
//            }
//            topList.setDeleted(false);
//            topList.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//            topList.setCid(SportrardConstants.f1NameMap.get(tornamentId));
//            topList.setCsid(competitionSeason.getId());
//            topList.setLatest(true);
//            topList.setType(type);
//            List<TopList.TopListItem> topListItems = new ArrayList<>();
//            while (players.hasNext()) {
//                TopList.TopListItem topListItem = new TopList.TopListItem();
//
//                Element outrightTeam = (Element) players.next();
//                Element player = outrightTeam.element("Team");
//                String partnerId = player.attributeValue("id");
//                String teamPartnerId = player.attributeValue("parentTeamId");
//                Team teamBelong = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, getPartnerType());
//                if (teamBelong == null) {
//                    LOG.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
//                    continue;
//                }
//                Player player1 = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, Constants.partner_type);
//                if (null == player1) {
//                    LOG.warn("cannot find player,playerPartnerId:{}", partnerId);
//                    player1 = new Player();
//                    player1.setAllowCountries(getAllowCountries());
//                    player1.setOnlineLanguages(getOnlineLang());
//                    player1.setName(player.attributeValue("name"));
//                    player1.setMultiLangNames(getMultiLang(player.attributeValue("name")));
//                    Element translation = player.element("Translation");
//                    if (null != translation) {
//                        Iterator translationIterator = translation.elementIterator("TranslatedValue");
//                        while (translationIterator.hasNext()) {
//                            Element translatedValue = (Element) translationIterator.next();
//                            if ("zh".equals(translatedValue.attributeValue("lang")))
//                                player1.setName(translatedValue.attributeValue("value"));
//                            player1.setMultiLangNames(getMultiLang(translatedValue.attributeValue("value")));
//                        }
//                    }
//                    Element country = player.element("Country");
//                    if (null != country) {
//                        player1.setNationality(country.attributeValue("name"));
//                        player1.setMultiLangNationalities(getMultiLang(country.attributeValue("name")));
//                    }
//                    player1.setPartnerId(partnerId);
//                    player1.setPartnerType(getPartnerType());
//                    player1.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//                    player1.setDeleted(false);
//                    List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^赛车$");
//                    if (CollectionUtils.isNotEmpty(dictEntries2)) {
//                        player1.setGameFType(dictEntries2.get(0).getId());
//                    }
//                    SbdsInternalApis.savePlayer(player1);
//                    LOG.info("insert into player sucess,partnerId:" + partnerId);
//                }
//
//                topListItem.setCompetitorId(player1.getId());
//                topListItem.setTeamId(teamBelong.getId());
//                topListItem.setCompetitorType(CompetitorType.PLAYER);
//
//                Map<String, Object> f1DriverRandStatistic = new F1Processor().getCompetitorStatsMap(outrightTeam, SportrardConstants.F1StandingStatPath);
//                topListItem.setShowOrder(CommonUtil.parseInt(f1DriverRandStatistic.get("position").toString(), 0));
//                topListItem.setRank(CommonUtil.parseInt(f1DriverRandStatistic.get("position").toString(), 0));
//                topListItem.setStats(f1DriverRandStatistic);
//                topListItems.add(topListItem);
//                //result = true;
//            }
//            topList.setItems(topListItems);
//            topList.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//            SbdsInternalApis.saveTopList(topList);
//            LOG.info("insert toplist success,cId:{},csId:{},items:{}", topList.getCid(), topList.getCsid(), topList.getItems());
//
//            Long teamType = 0L;
//            List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^车队榜$");
//            if (CollectionUtils.isNotEmpty(dictEntries2)) {
//                teamType = dictEntries2.get(0).getId();
//            }
//            TopList teamTopList = new TopList();
//            List<TopList> teamTopLists = SbdsInternalApis.getTopListsByCsidAndType(competitionSeason.getId(), teamType);
//            if (CollectionUtils.isNotEmpty(teamTopLists)) {
//                teamTopList = teamTopLists.get(0);
//            } else {
//                teamTopList.setAllowCountries(getAllowCountries());
//                teamTopList.setOnlineLanguages(getOnlineLang());
//            }
//            teamTopList.setDeleted(false);
//            teamTopList.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//            teamTopList.setCid(SportrardConstants.f1NameMap.get(tornamentId));
//            teamTopList.setCsid(competitionSeason.getId());
//            teamTopList.setLatest(true);
//            teamTopList.setType(teamType);
//            List<TopList.TopListItem> teamTopListItems = new ArrayList<>();
//            while (teams.hasNext()) {
//                TopList.TopListItem topListItem = new TopList.TopListItem();
//                Element outrightTeam = (Element) teams.next();
//                Element team = outrightTeam.element("Team");
//                String teamPartnerId = team.attributeValue("id");
//
//                Team team2 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, getPartnerType());
//                if (team2 == null) {
//                    LOG.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
//                    team2 = new Team();
//                    team2.setAllowCountries(getAllowCountries());
//                    team2.setOnlineLanguages(getOnlineLang());
//                    team2.setName(team.attributeValue("name"));
//                    team2.setMultiLangNames(getMultiLang(team.attributeValue("name")));
//                    Element translation = team.element("Translation");
//                    if (null != translation) {
//                        Iterator translationIterator = translation.elementIterator("TranslatedValue");
//                        while (translationIterator.hasNext()) {
//                            Element translatedValue = (Element) translationIterator.next();
//                            if ("zh".equals(translatedValue.attributeValue("lang")))
//                                team2.setName(translatedValue.attributeValue("value"));
//                            team2.setMultiLangNames(getMultiLang(translatedValue.attributeValue("value")));
//                        }
//                    }
//                    team2.setPartnerId(teamPartnerId);
//                    team2.setPartnerType(getPartnerType());
//                    team2.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//                    team2.setDeleted(false);
//                    List<DictEntry> dictEntries4 = SbdsInternalApis.getDictEntriesByName("^赛车$");
//                    if (CollectionUtils.isNotEmpty(dictEntries4)) {
//                        team2.setGameFType(dictEntries4.get(0).getId());
//                    }
//                    SbdsInternalApis.saveTeam(team2);
//                    LOG.info("insert into team sucess,partnerId:" + teamPartnerId);
//                }
//
//                Iterator teamValues = outrightTeam.elementIterator("TeamValue");
//                Map<String, Object> f1DriverRandStatistic = new F1Processor().getCompetitorStatsMap(outrightTeam, SportrardConstants.F1StandingStatPath);
//                topListItem.setShowOrder(CommonUtil.parseInt(f1DriverRandStatistic.get("position").toString(), 0));
//                topListItem.setRank(CommonUtil.parseInt(f1DriverRandStatistic.get("position").toString(), 0));
//
//                topListItem.setStats(f1DriverRandStatistic);
//                team2 = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, getPartnerType());
//                topListItem.setCompetitorId(team2.getId());
//                topListItem.setCompetitorType(CompetitorType.TEAM);
//                teamTopListItems.add(topListItem);
//
//            }
//            teamTopList.setItems(teamTopListItems);
//            teamTopList.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
//            SbdsInternalApis.saveTopList(teamTopList);
//            LOG.info("insert toplist success,cId:{},csId:{},items:{}", teamTopList.getCid(), teamTopList.getCsid(), teamTopList.getItems());
//            result = true;
//        } catch (Exception e) {
//            LOG.error("parse outright_Motorsport.Formula1 xml error,file:", e);
//        }
//        return result;
//    }
//
//    public Integer getPartnerType() {
//        return 469;
//    }
//
//    public String getGameFTypeName() {
//        return "^赛车";
//    }
//
//}
//
//
//
