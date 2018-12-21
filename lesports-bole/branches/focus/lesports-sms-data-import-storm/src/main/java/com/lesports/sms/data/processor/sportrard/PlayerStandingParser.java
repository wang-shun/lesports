//package com.lesports.sms.data.processor.sportrard;
//
//import com.google.common.collect.Lists;
//import com.lesports.sms.api.common.CompetitorType;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.model.sportrard.SportrardPlayerStanding;
//import com.lesports.sms.data.processor.AbstractProcessor;
//import com.lesports.sms.data.processor.BeanProcessor;
//import com.lesports.sms.data.processor.Processor;
//import com.lesports.sms.data.processor.ProcessorFactory;
//import com.lesports.sms.model.*;
//import org.apache.commons.collections.CollectionUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//
///**
// * Created by qiaohongxin
// */
//
//public class PlayerStandingParser extends AbstractProcessor implements BeanProcessor<SportrardPlayerStanding> {
//
//    private static Logger logger = LoggerFactory.getLogger(PlayerStandingParser.class);
////fileType积分榜，或者是助攻榜
//    public Boolean process(String fileType, SportrardPlayerStanding standing) {
//        CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(SportrardConstants.nameMap.get(standing.getTourmamentId()));
//        if (null == competitionSeason) {
//            logger.warn("can not find relative event,uniqueTournamentId:{},", standing.getTourmamentId());
//            return false;
//        }
//        List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^"+fileType);
//        if (CollectionUtils.isEmpty(dictEntries)) {
//            logger.warn("can not find relative dict,name:{},", fileType);
//            return false;
//        }
//        //获取对应的榜单处理器对象
//        Processor currentProcessor = ProcessorFactory.getProcessByGameFtype(469,standing.getSportId());
//        TopList topList = null;
//        List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndType(competitionSeason.getId(), dictEntries.get(0).getId());
//        if (CollectionUtils.isNotEmpty(topLists)) {
//            topList = topLists.get(0);
//        } else {
//            topList = new TopList();
//            topList.setAllowCountries(getAllowCountries());
//            topList.setCid(SportrardConstants.nameMap.get(standing.getTourmamentId()));
//            topList.setCsid(competitionSeason.getId());
//            topList.setLatest(true);
//            topList.setType(dictEntries.get(0).getId());
//        }
//        topList.setItems(getTopListItems(fileType, currentProcessor, standing.getTeamDatas()));
//        return true;
//    }
//
//    private List<TopList.TopListItem> getTopListItems(String type, Processor currentProcessor, List<SportrardPlayerStanding.TeamPlayerStanding> rows) {
//        if (CollectionUtils.isEmpty(rows)) return null;
//        List<TopList.TopListItem> topListItems = Lists.newArrayList();
//        for (SportrardPlayerStanding.TeamPlayerStanding currentRow : rows) {
//            Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(currentRow.getTeamId(), 469);
//            if (team == null) {
//                logger.warn("cannot find team,teamPartnerId:{}", currentRow.getTeamId());
//                continue;
//            }
//            for (SportrardPlayerStanding.PlayerData playerData : currentRow.getPlayerDatas()) {
//                Player player1 = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(playerData.getPlayerId(), 469);
//                if (null == player1) {
//                    logger.warn("cannot find player,playerPartnerId:{}", playerData.getPlayerId());
//                    continue;
//                }
//                TopList.TopListItem topListItem = new TopList.TopListItem();
//                topListItem.setTeamId(team.getId());
//                topListItem.setCompetitorId(player1.getId());
//                topListItem.setCompetitorType(CompetitorType.PLAYER);
//                Integer rank=currentProcessor.getRankOrderData(type,playerData.getStatsEntryData());
//                topListItem.setRank(rank);
//                topListItem.setShowOrder(rank);
//                topListItem.setStats(currentProcessor.getPlayerStandingStats(type,playerData.getStatsEntryData()));
//                topListItems.add(topListItem);
//            }
//        }
//        return topListItems;
//    }
//
//}
//
//
//
