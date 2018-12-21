package com.lesports.msg.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.api.common.LanguageCode;
import com.lesports.msg.Constants;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageContent;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.ScopeType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * User: qiaohongxin
 * Time: 16-09-29 : 下午2:52
 */
@Component
public class ScopeTopListHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(ScopeTopListHandler.class);

    @Override
    public void handle(LeMessage message) {
        MessageContent messageContent = message.getContent();

        Long cid = Long.valueOf(messageContent.getFromMsgBody("cid"));
        Long csid = Long.valueOf(messageContent.getFromMsgBody("csid"));
        Long scopeId = Long.valueOf(messageContent.getFromMsgBody("scope"));
        String competitorType1 = messageContent.getFromMsgBody("competitorType");
        CompetitorType competitorType = competitorType1.equalsIgnoreCase("PLAYER") ? CompetitorType.PLAYER : CompetitorType.TEAM;
        ScopeType type = getScopeType(messageContent.getFromMsgBody("scopeType"));
        List<CompetitorSeasonStat> stats = getCompetitorSeasonStatByScope(csid, scopeId, type);
        Iterator iter = Constants.avgRankingStats.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            Long typeId = Long.valueOf(entry.getValue().toString());
            TopList topList = null;
            InternalQuery query = new InternalQuery();
            query.addCriteria(new InternalCriteria("deleted", "eq", false));
            query.addCriteria(new InternalCriteria("csid", "eq", csid));
            query.addCriteria(new InternalCriteria("type", "eq", typeId));
            query.addCriteria(new InternalCriteria("scope", "eq", scopeId));
            query.addCriteria(new InternalCriteria("competitor_type", "eq", competitorType));
            List<TopList> topLists = SbdsInternalApis.getTopListsByQuery(query);
            if (CollectionUtils.isEmpty(topLists)) {
                topList = new TopList();
                topList.setCsid(csid);
                topList.setCid(cid);
                topList.setAllowCountries(SbdsInternalApis.getCompetitionById(cid).getAllowCountries());
                topList.setType(typeId);
                topList.setScope(scopeId);
                topList.setScopType(type);
                topList.setCompetitorType(competitorType);
                topList.setOnlineLanguages(Lists.newArrayList(LanguageCode.ZH_CN));
            } else {
                topList = topLists.get(0);
                topList.setCid(cid);
            }
            sortByKey(key, stats);
            Object max = stats.get(0).getAvgStats().get(key);
            Object avg = getAvg(key, stats);
            if (key.contains("percentage")) avg = getPercentFormatFromDigital(avg);
            int count = 0;
            List<TopList.TopListItem> items = Lists.newArrayList();
            for (CompetitorSeasonStat currentStat : stats) {
                count++;
                TopList.TopListItem currentItem = new TopList.TopListItem();
                currentItem.setRank(count);
                currentItem.setShowOrder(count);
                currentItem.setCompetitorId(currentStat.getCompetitorId());
                currentItem.setCompetitorType(currentStat.getType());
                Map statMap = Maps.newHashMap();
                statMap.put(key, currentStat.getAvgStats().get(key));
                statMap.put("avg_value", avg);
                statMap.put("max_value", max);
                currentItem.setStats(statMap);
                items.add(currentItem);
            }
            topList.setItems(items);
            long id = SbdsInternalApis.saveTopList(topList);
            LOG.info("Save top list : {}, id : {}.", JSONObject.toJSONString(topList), id);
        }
    }

    private void sortByKey(final String key, List<CompetitorSeasonStat> stats) {
        Collections.sort(stats, new Comparator<CompetitorSeasonStat>() {
            @Override
            public int compare(CompetitorSeasonStat o1, CompetitorSeasonStat o2) {
                Double value1 = StringToDouble(o1.getAvgStats().get(key));
                Double value2 = StringToDouble(o2.getAvgStats().get(key));
                return value2.compareTo(value1);
            }
        });
    }

    private Object getAvg(String key, List<CompetitorSeasonStat> stats) {
        double sum = 0.0;
        int count1 = 0;
        for (CompetitorSeasonStat currentStat : stats) {
            if (currentStat == null) continue;
            count1++;
            double currentValue = StringToDouble(currentStat.getAvgStats().get(key));
            sum = sum + currentValue;
        }
        DecimalFormat df = new DecimalFormat("######0.00");
        return df.format(sum / count1);
    }

    private Double StringToDouble(Object value) {
        try {
            String value1 = value.toString();
            if (!StringUtils.isEmpty(value1) && value1.startsWith(".")) value1 = "0" + value1;
            else if (!StringUtils.isEmpty(value1) && value1.contains("%")) value1 = value1.replace("%", "");
            Double valueNew = Double.valueOf(value1);
            return valueNew;
        } catch (Exception e) {
            return new Double(0.0);
        }
    }

    private Object getPercentFormatFromDigital(Object d) {
        try {
            Double digital = Double.valueOf(d.toString()) / 100;
            NumberFormat nf = NumberFormat.getPercentInstance();
            nf.setMaximumIntegerDigits(3);//小数点前保留几位
            nf.setMinimumFractionDigits(1);// 小数点后保留几位
            String str = nf.format(digital);
            return str;
        } catch (Exception e) {
            return "0.0%";
        }
    }

    private ScopeType getScopeType(String type) {
        if (StringUtils.isEmpty(type)) return null;
        else if (type.equalsIgnoreCase(ScopeType.CONFERENCE.toString())) return ScopeType.CONFERENCE;
        else if (type.equalsIgnoreCase(ScopeType.DIVISION.toString())) return ScopeType.DIVISION;
        else if (type.equalsIgnoreCase(ScopeType.GROUP.toString())) return ScopeType.GROUP;
        else if (type.equalsIgnoreCase(ScopeType.TEAM.toString())) return ScopeType.TEAM;
        return null;
    }

    private List<CompetitorSeasonStat> getCompetitorSeasonStatByScope(Long csid, Long scopeId, ScopeType type) {
        List<CompetitorSeasonStat> stats = Lists.newArrayList();
        if (type.equals(ScopeType.CONFERENCE)) {
            InternalQuery query = new InternalQuery();
            query.addCriteria(new InternalCriteria("csid", "eq", csid));
            List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByQuery(query);
            if (CollectionUtils.isEmpty(teamSeasons)) return null;
            for (TeamSeason teamSeason : teamSeasons) {
                CompetitorSeasonStat stat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(csid, teamSeason.getTid(), CompetitorType.TEAM);
                if (stat != null && stat.getAvgStats() != null) stats.add(stat);
            }
        } else if (type.equals(ScopeType.TEAM)) {
            TeamSeason currentTeam = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(scopeId, csid).get(0);
            for (TeamSeason.TeamPlayer player : currentTeam.getPlayers()) {
                CompetitorSeasonStat stat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(csid, player.getPid(), CompetitorType.PLAYER);
                if (stat != null && stat.getAvgStats() != null) stats.add(stat);
            }
        }
        return stats;
    }

    @Override
    Logger getLogger() {
        return LOG;
    }
}
