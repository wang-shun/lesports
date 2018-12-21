//package com.lesports.sms.data.processor.sportrard;
//
//import com.google.common.base.Function;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Lists;
//import com.google.common.collect.Maps;
//import com.google.common.collect.Sets;
//import com.lesports.bole.api.vo.TOlympicConfig;
//import com.lesports.bole.api.vo.TOlympicLiveConfigSet;
//import com.lesports.sms.api.common.CompetitorType;
//import com.lesports.sms.api.common.MatchStatus;
//import com.lesports.sms.client.BoleApis;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.Constants;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.processor.BeanProcessor;
//import com.lesports.sms.data.processor.olympic.AbstractProcessor;
//import com.lesports.sms.data.utils.CommonUtil;
//import com.lesports.sms.data.utils.dataFormatterUtils.FormatterFactory;
//import com.lesports.sms.model.*;
//import com.lesports.utils.LeDateUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.lang.StringUtils;
//import org.dom4j.Document;
//import org.dom4j.DocumentException;
//import org.dom4j.Element;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import com.lesports.sms.data.processor.sportrard.InnerProcessor.F1Processor;
//
//import java.util.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
///**
// * Created by qiaohongxin on 2016/2/23.
// */
//public class F1StatsProcessor extends AbstractProcessor implements BeanProcessor<Document> {
//    private static Logger LOG = LoggerFactory.getLogger(F1StatsProcessor.class);
//
//    public Boolean process(String codeType, Document document) {
//        LOG.info("live score  parser begin");
//        Boolean result = false;
//        try {
//            Element rootElement = document.getRootElement();
//            Element category = rootElement.element("Sport").element("Category");
//            Element stage = category.element("Stage");
//            Iterator players = stage.element("OutrightCompetitors").elementIterator("OutrightTeam");
//            String stageId = stage.attributeValue("stageId");
//            Match match = SbdsInternalApis.getMatchByPartnerIdAndType(stageId, getPartnerType());
//            if (match == null) {
//                LOG.warn("the match may be not exists,partnerId:{},fileName:{}", stageId, codeType);
//                return false;
//            }
//            Set<Match.CompetitorStat> competitorStats = Sets.newHashSet();
//            while (players.hasNext()) {
//                Match.CompetitorStat competitorStat = new Match.CompetitorStat();
//                Element outrightTeam = (Element) players.next();
//                Element player = outrightTeam.element("Team");
//                String partnerId = player.attributeValue("id");
//                String teamPartnerId = player.attributeValue("parentTeamId");
//                Team teamBelong = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, getPartnerType());
//                if (teamBelong == null) {
//                    LOG.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
//                    //continue;
//                    teamBelong = new Team();
//                    teamBelong.setId(0L);
//                }
//                Player player1 = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerId, getPartnerType());
//                if (null == player1) {
//                    LOG.warn("cannot find player,playerPartnerId:{}", partnerId);
//                    player1 = new Player();
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
//                    player1.setOnlineLanguages(getOnlineLang());
//                    player1.setAllowCountries(getAllowCountries());
//                    player1.setDeleted(false);
//                    List<DictEntry> dictEntries2 = SbdsInternalApis.getDictEntriesByName("^赛车$");
//                    if (CollectionUtils.isNotEmpty(dictEntries2)) {
//                        player1.setGameFType(dictEntries2.get(0).getId());
//                    }
//                    SbdsInternalApis.savePlayer(player1);
//                    LOG.info("insert into player sucess,partnerId:" + partnerId);
//                }
//                Map<String, Object> map = new F1Processor().getCompetitorStatsMap(outrightTeam, SportrardConstants.f1StatPath);
//                if (StringUtils.isBlank(map.get("position").toString())) {
//                    competitorStat.setShowOrder(Integer.parseInt(map.get("position").toString()));
//                } else {
//                    competitorStat.setShowOrder(0);
//                }
//                competitorStat.setCompetitorId(player1.getId());
//                competitorStat.setTeamId(teamBelong.getId());
//                competitorStat.setCompetitorType(CompetitorType.PLAYER);
//
//                competitorStat.setStats(map);
//                competitorStats.add(competitorStat);
//            }
//            match.setCompetitorStats(competitorStats);
//            boolean isSuccess = false;
//            int tryCount = 0;
//            while (isSuccess == false && tryCount++ < Constants.MAX_TRY_COUNT) {
//                try {
//                    isSuccess = SbdsInternalApis.saveMatch(match) > 0;
//                } catch (Exception e) {
//                    LOG.error("fail to update match. id : {}. sleep and try again.", match.getId(), e);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e1) {
//                        e1.printStackTrace();
//                    }
//                }
//            }
//            LOG.info("update match success,matchId:" + match.getId() + ",matchName:" + match.getName() + ",matchPartnerId:" + match.getPartnerId());
//            result = true;
//        } catch (Exception e) {
//            LOG.error("parse match xml error", e);
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
