//package com.lesports.sms.data.processor.sportrard;
//
//import com.google.common.collect.Lists;
//import com.lesports.sms.api.common.CompetitorType;
//import com.lesports.sms.client.SbdsInternalApis;
//import com.lesports.sms.data.model.sportrard.SportrardConstants;
//import com.lesports.sms.data.model.sportrard.SportrardTeamStanding;
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
//public class TeamStandingParser extends AbstractProcessor implements BeanProcessor<SportrardTeamStanding> {
//
//    private static Logger logger = LoggerFactory.getLogger(TeamStandingParser.class);
//
//    public Boolean process(String fileType, SportrardTeamStanding standing) {
//        CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(SportrardConstants.nameMap.get(standing.getTourmamentId()));
//        if (null == competitionSeason) {
//            logger.warn("can not find relative event,uniqueTournamentId:{},", standing.getTourmamentId());
//            return false;
//        }
//        List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^积分榜$");
//        if (CollectionUtils.isEmpty(dictEntries)) {
//            logger.warn("can not find relative dict,name:{},", "积分榜");
//            return false;
//        }
//        //获取对应的榜单处理器对象
//        Processor currentProcessor = ProcessorFactory.getProcessByGameFtype(469, standing.getSportId());
//        Long groupId = currentProcessor.getGroupData(0L, null);
//        //获取榜单对象
//        TopList topList = null;
//        List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(competitionSeason.getId(), dictEntries.get(0).getId(), groupId);
//        if (CollectionUtils.isEmpty(topLists)) {
//            topList = topLists.get(0);
//        } else {
//            topList = new TopList();
//            topList.setCid(SportrardConstants.nameMap.get(standing.getTourmamentId()));
//            topList.setCsid(competitionSeason.getId());
//            topList.setLatest(true);
//            topList.setType(dictEntries.get(0).getId());
//            topList.setGroup(groupId);
//        }
//        topList.setItems(getTopListItems(currentProcessor, standing.getRows()));
//        return true;
//    }
//
//    private List<TopList.TopListItem> getTopListItems(Processor currentProcessor, List<SportrardTeamStanding.LeagueTableRow> rows) {
//        if (CollectionUtils.isEmpty(rows)) return null;
//        List<TopList.TopListItem> topListItems = Lists.newArrayList();
//        for (SportrardTeamStanding.LeagueTableRow currentRow : rows) {
//            Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(currentRow.getTeamId(), 469);
//            if (team == null) {
//                logger.warn("cannot find team,teamPartnerId:{}", currentRow.getTeamId());
//                continue;
//            }
//            TopList.TopListItem topListItem = new TopList.TopListItem();
//            topListItem.setCompetitorId(team.getId());
//            topListItem.setCompetitorType(CompetitorType.TEAM);
//            topListItem.setRank(currentRow.getRank());
//            topListItem.setShowOrder(currentRow.getRank());
//            topListItem.setStats(currentProcessor.getTeamStandingStats(currentRow.getColumns()));
//            topListItems.add(topListItem);
//        }
//        return topListItems;
//    }
//
//}
//
//
//
