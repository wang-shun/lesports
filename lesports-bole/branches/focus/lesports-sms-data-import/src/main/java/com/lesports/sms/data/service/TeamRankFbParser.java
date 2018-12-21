package com.lesports.sms.data.service;

import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.MatchSystem;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TeamRankStatistic;
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
 * Created by lufei1 on 2014/12/8.
 */
@Service("teamRankFbParser")
public class TeamRankFbParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(TeamRankFbParser.class);

    @Override
    public Boolean parseData(String file) {
        logger.info("team rank fb  parser begin");
        Boolean result = false;
        File xmlFile = new File(file);
        if (!xmlFile.exists()) {
            logger.warn("parsing the file:{} not exists", file);
            return result;
        }
        try {
            SAXReader reader = new SAXReader();
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
            List<DictEntry> dictEntries = SbdsInternalApis.getDictEntriesByName("^积分榜$");
            if (CollectionUtils.isNotEmpty(dictEntries)) {
                type = dictEntries.get(0).getId();
            }
            TopList topList = new TopList();
            List<TopList> topLists = new ArrayList<>();
            //添加group
            List<DictEntry> dictEntry = SbdsInternalApis.getDictEntriesByName("^分组$");
            int position = file.lastIndexOf(".xml");
            String g = file.substring(position - 1, position);
            String gname = g + "组";
            DictEntry dc = SbdsInternalApis.getDictEntryByNameAndParentId(gname, dictEntry.get(0).getId());
            if (dc != null) {
                topList.setGroup(dc.getId());
                topLists = SbdsInternalApis.getTopListsByCsidAndTypeAndGroup(competitionSeason.getId(), type, dc.getId());
            } else {
                topLists = SbdsInternalApis.getTopListsByCsidAndType(competitionSeason.getId(), type);
            }
            if (CollectionUtils.isNotEmpty(topLists)) {
                topList = topLists.get(0);
            } else {
                topList.setAllowCountries(getAllowCountries());
                topList.setOnlineLanguages(getOnlineLanguage());
            }

            topList.setDeleted(false);
            topList.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            topList.setCid(competitions.get(0).getId());
            topList.setCsid(competitionSeason.getId());
            topList.setLatest(true);
            topList.setType(type);

            List<TopList.TopListItem> topListItems = new ArrayList<>();
            Iterator leagueTableRows = tournament.element("LeagueTable").element("LeagueTableRows").elementIterator("LeagueTableRow");
            while (leagueTableRows.hasNext()) {
                String matchSum = "0";
                String teamScore = "0";
                String successMatch = "0";
                String flatMatch = "0";
                String faildMatch = "0";
                String goal = "0";
                String fumble = "0";
                String teamOrder = "0";

                Element leagueTableRow = (Element) leagueTableRows.next();
                String teamPartnerId = leagueTableRow.element("Team").attributeValue("id");
                Team team = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(teamPartnerId, Integer.parseInt(Constants.partnerSourceId));
                if (team == null) {
                    logger.warn("cannot find team,teamPartnerId:{}", teamPartnerId);
                    continue;
                }
                TopList.TopListItem topListItem = new TopList.TopListItem();
                Iterator leagueTableColumns = leagueTableRow.elementIterator("LeagueTableColumn");
                while (leagueTableColumns.hasNext()) {

                    Element leagueTableColumn = (Element) leagueTableColumns.next();
                    String attKey = leagueTableColumn.attributeValue("key");
                    if ("matchesTotal".equals(attKey)) {
                        matchSum = leagueTableColumn.attributeValue("value");
                    } else if ("pointsTotal".equals(attKey)) {
                        teamScore = leagueTableColumn.attributeValue("value");
                    } else if ("winTotal".equals(attKey)) {
                        successMatch = leagueTableColumn.attributeValue("value");
                    } else if ("drawTotal".equals(attKey)) {
                        flatMatch = leagueTableColumn.attributeValue("value");
                    } else if ("lossTotal".equals(attKey)) {
                        faildMatch = leagueTableColumn.attributeValue("value");
                    } else if ("goalsForTotal".equals(attKey)) {
                        goal = leagueTableColumn.attributeValue("value");
                    } else if ("goalsAgainstTotal".equals(attKey)) {
                        fumble = leagueTableColumn.attributeValue("value");
                    } else if ("positionTotal".equals(attKey)) {
                        teamOrder = leagueTableColumn.attributeValue("value");
                    }
                }
                topListItem.setCompetitorId(team.getId());
                topListItem.setCompetitorType(CompetitorType.TEAM);
                topListItem.setRank(Integer.parseInt(teamOrder));
                topListItem.setShowOrder(Integer.parseInt(teamOrder));

                TeamRankStatistic teamRankStatistic = new TeamRankStatistic();
                teamRankStatistic.setMatchNumber(Integer.parseInt(matchSum));
                teamRankStatistic.setTeamScore(Integer.parseInt(teamScore));
                teamRankStatistic.setWinMatch(Integer.parseInt(successMatch));
                teamRankStatistic.setLossMatch(Integer.parseInt(faildMatch));
                teamRankStatistic.setFlatMatch(Integer.parseInt(flatMatch));
                teamRankStatistic.setGoal(Integer.parseInt(goal));
                teamRankStatistic.setFumble(Integer.parseInt(fumble));
                teamRankStatistic.setGoalDiffer(teamRankStatistic.getGoal() - teamRankStatistic.getFumble());

                Map<String, Object> map = CommonUtil.convertBeanToMap(teamRankStatistic);
                topListItem.setStats(map);
                topListItems.add(topListItem);
            }


            topList.setItems(topListItems);
            topList.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            SbdsInternalApis.saveTopList(topList);
            logger.info("insert toplist success,cId:{},csId:{}", topList.getCid(), topList.getCsid());
            result = true;
        } catch (DocumentException e) {
            logger.error("parse leaguetable_Soccer xml error,file: " + file, e);
        }
        return result;
    }
}
