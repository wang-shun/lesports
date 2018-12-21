package com.lesports.sms.data.service.stats.euro;

import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TeamRankStatistic;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.data.util.StatsConstants;
import com.lesports.sms.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.lesports.sms.api.common.CompetitorType;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/4/21.
 */
@Service("EUROTeamStandingsParser")
public class EUROTeamStandingsParser extends Parser implements IThirdDataParser {
    private static Logger logger = LoggerFactory.getLogger(EUROTeamStandingsParser.class);


    @Override
    public Boolean parseData(String file) {
        logger.info("team standing file  parser begin");
        Boolean result = false;
        File xmlFile = new File(file);
        if (!xmlFile.exists()) {
            logger.warn("parsing the file:{} not exists", file);
            return result;
        }
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Long stage = 0L;
            Element rootElement = document.getRootElement();
            Element euroStandings = rootElement.element("sports-standings").element("soccer-ifb-standings").element("game-type");
            //get tournament name and season
            String name = rootElement.element("sports-standings").element("league").attributeValue("alias");
            String season = rootElement.element("sports-standings").element("season").attributeValue("year");
            // get mathseason information
            Competition competition = SbdsInternalApis.getCompetitionById(StatsConstants.COMPETITIONS_ID);
            if(competition == null){
                logger.warn("can not find relative event,name:{}", name);
                return result;
            }
            InternalQuery internalQuery = new InternalQuery();
            internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
            internalQuery.addCriteria(InternalCriteria.where("cid").is(competition.getId()));
            internalQuery.addCriteria(InternalCriteria.where("season").regex(season));
            List<CompetitionSeason> competitionSeasons = SbdsInternalApis.getCompetitionSeasonsByQuery(internalQuery);
            if (CollectionUtils.isEmpty(competitionSeasons)) {
                logger.warn("can not find relative event,name:{},season:{}", name, season);
                return result;
            }
            Iterator<Element> euroConferencesStandings = euroStandings.elementIterator("ifb-division-standings");
            int order = 1;
            while (euroConferencesStandings.hasNext()) {
                Element euroConferencesStanding = euroConferencesStandings.next();
                //判定topList is exits
                List<DictEntry> dictEntry = SbdsInternalApis.getDictEntriesByName("分组");
                String group = Constants.euroGroupMap.get(euroConferencesStanding.element("title").attributeValue("id"));
                DictEntry dc = SbdsInternalApis.getDictEntryByNameAndParentId(group, dictEntry.get(0).getId());
                long groupId = 0;
                if (dc != null) {
                    groupId = dc.getId();
                }
                Long type = 0L;
                List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^积分榜$");
                if (CollectionUtils.isNotEmpty(dictEntries)) {
                    type = dictEntries.get(0).getId();
                }

                logger.info("group: {} ", group);


                //get group ranking data
                Iterator<Element> euroGroupStandings = euroConferencesStanding.elementIterator("ifb-team-standings");
                List<TopList.TopListItem> groupTopListItems = new ArrayList<TopList.TopListItem>();
                //get topList object
                TopList groupTopList = getTopList(competition.getId(), competitionSeasons.get(0).getId(), type, 0L, groupId,order);
                order ++ ;
                while (euroGroupStandings.hasNext()) {
                    Element euroGroupStanding = euroGroupStandings.next();
                    TopList.TopListItem currentItem = getTopListItem(euroGroupStanding);
                    if (currentItem == null) continue;
                    groupTopListItems.add(currentItem);
                }
                groupTopList.setItems(groupTopListItems);
                SbdsInternalApis.saveTopList(groupTopList);
                logger.info("division Ranking is created sucessfully");
            }
        } catch (Exception e) {
            logger.error("parser the teamranking file fail", e);
        }
        return result;
    }

    /**
     * 添加球队的技术排行榜
     *
     * @param teamStanding
     */
    private TopList.TopListItem getTopListItem(Element teamStanding) {
        String teamPartnerId = teamStanding.element("team-info").attributeValue("global-id");
        Team team = SbdsInternalApis.getTeamByStatsId(teamPartnerId);
        if (team == null) {
            logger.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
            return null;
        }
        String matchSum = "0";
        String teamScore = "0";
        String successMatch = "0";
        String flatMatch = "0";
        String faildMatch = "0";
        String goal = "0";
        String fumble = "0";
        String teamOrder = "0";

        if (teamStanding.element("games-played").attributeValue("games") != null) {
            matchSum = teamStanding.element("games-played").attributeValue("games");
        }
        if (teamStanding.element("points").attributeValue("points") != null) {
            teamScore = teamStanding.element("points").attributeValue("points");
        }
        if (teamStanding.element("wins").attributeValue("number") != null) {
            successMatch = teamStanding.element("wins").attributeValue("number");
        }
        if (teamStanding.element("ties").attributeValue("number") != null) {
            flatMatch = teamStanding.element("ties").attributeValue("number");
        }
        if (teamStanding.element("losses").attributeValue("number") != null) {
            faildMatch = teamStanding.element("losses").attributeValue("number");
        }
        if (teamStanding.element("goals-for").attributeValue("goals") != null) {
            goal = teamStanding.element("goals-for").attributeValue("goals");
        }
        if (teamStanding.element("goals-against").attributeValue("goals") != null) {
            fumble = teamStanding.element("goals-against").attributeValue("goals");
        }
        if (teamStanding.element("place").attributeValue("place") != null) {
            teamOrder = teamStanding.element("place").attributeValue("place");
        }

        TeamRankStatistic teamRankStatistic = new TeamRankStatistic();
        teamRankStatistic.setMatchNumber(CommonUtil.parseInt(matchSum, 0));
        teamRankStatistic.setTeamScore(CommonUtil.parseInt(teamScore, 0));
        teamRankStatistic.setWinMatch(CommonUtil.parseInt(successMatch, 0));
        teamRankStatistic.setLossMatch(CommonUtil.parseInt(faildMatch, 0));
        teamRankStatistic.setFlatMatch(CommonUtil.parseInt(flatMatch, 0));
        teamRankStatistic.setGoal(CommonUtil.parseInt(goal, 0));
        teamRankStatistic.setFumble(CommonUtil.parseInt(fumble, 0));

        TopList.TopListItem topListItem = new TopList.TopListItem();
        Map<String, Object> map = CommonUtil.convertBeanToMap(teamRankStatistic);
        topListItem.setStats(map);
        topListItem.setCompetitorId(team.getId());
        topListItem.setCompetitorType(CompetitorType.TEAM);
        topListItem.setRank(CommonUtil.parseInt(teamOrder, 0));
        topListItem.setShowOrder(CommonUtil.parseInt(teamOrder, 0));
        topListItem.setStats(map);
        logger.info("get the topList sucessfully,teamId:{}", team.getId());
        return topListItem;

    }

    /**
     * 依据指定条件获取榜单对象
     *
     * @param cid
     * @param csid
     * @param type
     * @param stage
     * @return
     */
    private TopList getTopList(Long cid, Long csid, Long type, Long stage, Long groupId,int order) {
        TopList topList = null;
        List<TopList> topLists = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(csid, type, groupId);
        if (CollectionUtils.isNotEmpty(topLists)) {
            topList = topLists.get(0);
            return topList;
        }
        topList = new TopList();
        topList.setDeleted(false);
        topList.setCid(cid);
        topList.setCsid(csid);
        topList.setLatest(true);
        topList.setType(type);
        topList.setStage(stage);
        topList.setGroup(groupId);
        topList.setAllowCountries(getAllowCountries());
        topList.setOnlineLanguages(getOnlineLanguage());
        topList.setOrder(order);
        return topList;
    }
}
