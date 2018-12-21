package com.lesports.sms.data.service.soda.top12;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.PlayerType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.Parser;
import com.lesports.sms.data.util.SodaContant;
import com.lesports.sms.model.*;
import com.lesports.sms.model.Team.*;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.time.FastDateFormat;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhonglin on 2016/8/2.
 */
@Service("top12TeamParser")
public class Top12TeamParser extends Parser implements IThirdDataParser {

    private static Logger logger = LoggerFactory.getLogger(Top12TeamParser.class);
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Boolean parseData(String file) {
        Boolean result = false;
        try {
            File xmlFile = new File(file);
            if (!xmlFile.exists()) {
                logger.warn("parsing the file:{} not exists", file);
                return result;
            }

            SAXReader reader = new SAXReader();
            Document document = reader.read(xmlFile);
            Element rootElmement = document.getRootElement();

            // 球队的基本信息
            Element teamElement = rootElmement.element("Team");
            // 世界排名
            Element ranksElement = teamElement.element("Fifarank");
            if(ranksElement == null){
                ranksElement = teamElement.element("Leaguerank");
            }
            // 荣誉
            Element honoursElement = teamElement.element("Honours");
            // 赛季统计
            Element statisticsElement = teamElement.element("Statistics");

            String teamId = teamElement.attributeValue("id");
            String teamName = teamElement.attributeValue("name");
            String teamEngName = teamElement.attributeValue("nameEng");


            List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^足球$");
            if (CollectionUtils.isEmpty(dictEntryList)) {
                logger.warn("can not find gameFType,name:足球");
                return result;
            }
            Long gameFType = dictEntryList.get(0).getId();

            Team team = SbdsInternalApis.getTeamBySodaId(teamId);
            if (null == team) {
                logger.warn("can not find relative team,sodaid:{},name:{}", teamId, teamName);
                return result;
            }

            //球队的排名和荣誉
            Iterator<Element> honourIterator = honoursElement.elementIterator("Honour");
            Map<String, String> honourMap = new HashMap<String, String>();
            while (honourIterator.hasNext()) {
                Element honourElement = honourIterator.next();
                String compName = honourElement.attributeValue("compName");
                String season = honourElement.attributeValue("season");
                if (honourMap.get(compName) != null) {
                    honourMap.put(compName, honourMap.get(compName) + "，" + season.replace("/","-"));
                } else {
                    honourMap.put(compName, season.replace("/","-"));
                }
            }
            List<String> honourList = Lists.newArrayList();
            if (MapUtils.isNotEmpty(honourMap)) {
                for (String key : honourMap.keySet()) {
                    String honour = key + "冠军： " + honourMap.get(key);
                    honourList.add(honour);
                }
            }
            team.setHonors(honourList);

            Iterator<Element> rankIterator = ranksElement.elementIterator("Rank");
            List<Rank> ranks = Lists.newArrayList();
            while (rankIterator.hasNext()) {
                Element rankElement = rankIterator.next();
                Team.Rank rank = new Rank();
                rank.setTime(rankElement.attributeValue("year"));
                rank.setRank(Integer.parseInt(rankElement.attributeValue("rank")));
                ranks.add(rank);
            }
            team.setRanks(ranks);

            //更新赛季数据统计
            Iterator<Element> statIterator = statisticsElement.elementIterator("Stat");
            while (statIterator.hasNext()) {
                Element statElement = statIterator.next();
                Map<String, Object> stats = com.google.common.collect.Maps.newHashMap();

                String compId = statElement.attributeValue("compId");
                String compName = statElement.attributeValue("compName");
                String season = statElement.attributeValue("season");

                if (SodaContant.cidMap.get(compId) == null) {
                    continue;
                }
                Competition competition = SbdsInternalApis.getCompetitionById(SodaContant.cidMap.get(compId));
                if (competition == null) {
                    logger.warn("can not find competition compId:{} , compName:{} ", compId, compName);
                    continue;
                }


                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                internalQuery.addCriteria(InternalCriteria.where("cid").is(competition.getId()));
                internalQuery.addCriteria(InternalCriteria.where("season").regex(season));
                List<CompetitionSeason> competitionSeasons = SbdsInternalApis.getCompetitionSeasonsByQuery(internalQuery);

                CompetitionSeason competitionSeason = null;
                if (CollectionUtils.isEmpty(competitionSeasons)) {
                    continue;
                } else {
                    competitionSeason = competitionSeasons.get(0);
                    logger.info("compName: {}, season: {}, old csid: {} ", compName, season, competitionSeason.getId());
                }


                CompetitorSeasonStat competitorSeasonStat = SbdsInternalApis.getCompetitorSeasonStatByCsidAndCompetitor(competitionSeason.getId(), team.getId(), CompetitorType.TEAM);
                if (competitorSeasonStat == null) {
                    competitorSeasonStat = new CompetitorSeasonStat();
                    competitorSeasonStat.setAllowCountries(getAllowCountries());
                }

                competitorSeasonStat.setCompetitorId(team.getId());
                competitorSeasonStat.setCid(competition.getId());
                competitorSeasonStat.setCsid(competitionSeason.getId());

                String homeAway = statElement.attributeValue("homeAway");
                stats.put("homeAway", homeAway);

                String total = statElement.attributeValue("matchs");
                stats.put("total", total);
                String win = statElement.attributeValue("win");
                stats.put("win", win);
                String draw = statElement.attributeValue("draw");
                stats.put("draw", draw);
                String lose = statElement.attributeValue("lose");
                stats.put("lose", lose);
                String goal = statElement.attributeValue("goal");
                stats.put("goal", goal);
                String concede = statElement.attributeValue("concede");
                stats.put("concede", concede);
                String goaldiffer = statElement.attributeValue("GD");
                stats.put("goaldiffer", goaldiffer);
                String avgGoal = statElement.attributeValue("avgGoal");
                stats.put("avgGoal", avgGoal);
                String avgConcede = statElement.attributeValue("avgConc");
                stats.put("avgConcede", avgConcede);
                String point = statElement.attributeValue("score");
                stats.put("point", point);

                competitorSeasonStat.setStats(stats);
                competitorSeasonStat.setType(CompetitorType.TEAM);

                //更新赛季统计信息
                SbdsInternalApis.saveCompetitorSeasonStat(competitorSeasonStat);
                SbdsInternalApis.saveTeam(team);
            }

            result = true;

        } catch (Exception e) {
            logger.error("insert into team  error: ", e);
        }
        return result;


    }
}