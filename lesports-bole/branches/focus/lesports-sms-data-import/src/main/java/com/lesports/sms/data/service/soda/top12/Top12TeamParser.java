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
            // 教练的基本信息
            Element coachElement = teamElement.element("Coach");
            // 阵容基本信息
            Element squadsElement = teamElement.element("Squads");
            // 世界排名
            Element fifarankElement = teamElement.element("Fifarank");
            // 荣誉
            Element honoursElement = teamElement.element("Honours");
            // 最近赛程
            Element fixtureElement = teamElement.element("Fixture");
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
                    honourMap.put(compName, honourMap.get(compName) + "，" + season);
                } else {
                    honourMap.put(compName, season);
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

            Iterator<Element> rankIterator = fifarankElement.elementIterator("Rank");
            List<Rank> ranks = Lists.newArrayList();
            while (rankIterator.hasNext()) {
                Element rankElement = rankIterator.next();
                Team.Rank rank = new Rank();
                rank.setTime(rankElement.attributeValue("year"));
                rank.setRank(Integer.parseInt(rankElement.attributeValue("rank")));
                ranks.add(rank);
            }
            team.setRanks(ranks);


            String coachId = coachElement.attributeValue("id");
            String coachName = coachElement.getText();


            // 教练信息
            Player coach = SbdsInternalApis.getPlayerBySodaId(coachId);
            if (null == coach) {
                coach = new Player();
                coach.setName(coachName);
                coach.setMultiLangNames(getMultiLang(coachName));
                coach.setType(PlayerType.COACH);
                coach.setSodaId(coachId);
                coach.setGameFType(gameFType);
                coach.setAllowCountries(getAllowCountries());
                coach.setOnlineLanguages(getOnlineLanguage());
            } else {
                coach.setName(coachName);
                coach.setMultiLangNames(getMultiLang(coachName));
            }
            long coach_id = SbdsInternalApis.savePlayer(coach);
            logger.info("coach_id: {} ", coach_id);

            team.setName(teamName);
            team.setMultiLangNames(getEngMultiLang(team.getMultiLangNames(), teamName, teamEngName));


            // 球员信息
            Iterator<Element> playerIterator = squadsElement.elementIterator("Player");
            List<TeamSeason.TeamPlayer> teamPlayers = Lists.newArrayList();
            Long currentCaptain = 0L;
            List<Long> coreIds = Lists.newArrayList();
            while (playerIterator.hasNext()) {
                Element playerElement = playerIterator.next();
                String playerId = playerElement.attributeValue("id");
                String playerName = playerElement.attributeValue("name");
                String playerEngName = playerElement.attributeValue("nameEng");
                String shirtNumber = playerElement.attributeValue("number");
                String positionId = playerElement.element("Position").attributeValue("id");
                String positionName = playerElement.element("Position").attributeValue("name");
                String countryName = playerElement.element("Country").attributeValue("name");
                String cityName = playerElement.element("City").attributeValue("name");
                String birthday = playerElement.attributeValue("birthDate");
                String height = playerElement.attributeValue("height");
                String weight = playerElement.attributeValue("weight");
                String captain = playerElement.attributeValue("captain");
                String keyman = playerElement.attributeValue("keyman");
                String current = playerElement.attributeValue("current");

                if("0".equals(current))continue; //不是本期球员

                Player sodaPlayer = SbdsInternalApis.getPlayerBySodaId(playerId);
                if (positionName.equals("门将")) {
                    positionName = "守门员";
                }
                DictEntry dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId("^" + positionName + "$", Constants.POSITION);
                if (null == dictEntry) {
                    dictEntry = new DictEntry();
                    dictEntry.setName(positionName);
                    dictEntry.setMultiLangNames(getMultiLang(positionName));
                    dictEntry.setParentId(Constants.POSITION);
                    SbdsInternalApis.saveDict(dictEntry);
                }

                if (null == sodaPlayer) {
                    sodaPlayer = new Player();
                    buildSodaPlayer(gameFType, playerId, playerName, playerEngName, countryName, cityName, birthday, height, weight, sodaPlayer, dictEntry);

                } else {
                    buildSodaPlayer(gameFType, playerId, playerName, playerEngName, countryName, cityName, birthday, height, weight, sodaPlayer, dictEntry);
                }

                Set<Long> cids = sodaPlayer.getCids();
                cids.add(Constants.TOP12_COMPETITION_ID);
                sodaPlayer.setCids(cids);

                SbdsInternalApis.savePlayer(sodaPlayer);
                sodaPlayer = SbdsInternalApis.getPlayerBySodaId(playerId);

                //是队长
                if ("1".equals(captain)) {
                    currentCaptain = sodaPlayer.getId();
                }

                //是核心球员
                if ("1".equals(keyman)) {
                    coreIds.add(sodaPlayer.getId());
                }

                TeamSeason.TeamPlayer teamPlayer = new TeamSeason.TeamPlayer();
                buildTeamPlayer(shirtNumber, positionId, positionName, sodaPlayer, dictEntry, teamPlayer);
                teamPlayers.add(teamPlayer);
            }


            // 赛季阵容
            List<TeamSeason> teamSeasons = SbdsInternalApis.getTeamSeasonsByTeamIdAndCSId(team.getId(), Constants.TOP12_COMPETITION_SEASON_ID);
            TeamSeason teamSeason = new TeamSeason();
            if (CollectionUtils.isNotEmpty(teamSeasons)) {
                teamSeason = teamSeasons.get(0);
                teamSeason.setCoachId(coach.getId());
                teamSeason.setCoachName(coachName);
                teamSeason.setMultiLangCoachNames(getMultiLang(coachName));
                teamSeason.setPlayers(teamPlayers);
            } else {
                teamSeason.setAllowCountries(getAllowCountries());
                teamSeason.setPlayers(teamPlayers);
                teamSeason.setCoachId(coach.getId());
                teamSeason.setTid(team.getId());
                teamSeason.setCoachName(coachName);
                teamSeason.setMultiLangCoachNames(getMultiLang(coachName));
                teamSeason.setCsid(Constants.TOP12_COMPETITION_SEASON_ID);
            }
            if(currentCaptain !=0 ){
                teamSeason.setCurrentCaptain(currentCaptain);
            }
            teamSeason.setCorePlayers(coreIds);
            logger.info("team: {} , tsid: {}, teamPlayers: {} ", team.getName(), teamSeason.getId(), teamPlayers);
            SbdsInternalApis.saveTeamSeason(teamSeason);


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

                CompetitionSeason competitionSeason = new CompetitionSeason();
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
                String point = statElement.attributeValue("point");
                stats.put("point", point);

                competitorSeasonStat.setStats(stats);
                competitorSeasonStat.setType(CompetitorType.TEAM);

                //更新赛季统计信息
                SbdsInternalApis.saveCompetitorSeasonStat(competitorSeasonStat);


                //更新球队信息
                team.setCurrentTsid(teamSeason.getId());
                team.setCurrentCid(Constants.TOP12_COMPETITION_ID);
                team.setCurrentCsid(Constants.TOP12_COMPETITION_SEASON_ID);
                SbdsInternalApis.saveTeam(team);
            }

            result = true;

        } catch (Exception e) {
            logger.error("insert into team  error: ", e);
        }
        return result;


    }


    private void buildSodaPlayer(Long gameFType, String playerId, String playerName, String playerEngName, String countryName, String cityName, String birthday, String height, String weight, Player sodaPlayer, DictEntry dictEntry) throws Exception {
        sodaPlayer.setSodaId(playerId);
        sodaPlayer.setType(PlayerType.PLAYER);
//        if(birthday.endsWith("04-16")){
//            sodaPlayer.setBirthdate(LeDateUtils.formatYYYYMMDD(LeDateUtils.parseYMDHMS(birthday+" 01:00:00")));
//        }
//        else{
//            sodaPlayer.setBirthdate(LeDateUtils.formatYYYYMMDD(LeDateUtils.parseYYYY_MM_DD(birthday)));
//        }
        DateFormat format = new SimpleDateFormat("yyyyMMdd");
        format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        if (StringUtils.isNotBlank(birthday)) {
            sodaPlayer.setBirthdate(LeDateUtils.formatYYYYMMDD(format.parse(birthday)));
        }
        sodaPlayer.setEnglishName(playerEngName);
        sodaPlayer.setHeight(LeNumberUtils.toInt(height));
        sodaPlayer.setWeight(LeNumberUtils.toInt(weight));
        sodaPlayer.setNationality(countryName);
        sodaPlayer.setMultiLangNationalities(getMultiLang(countryName));
        sodaPlayer.setCity(cityName);
        sodaPlayer.setMultiLangCities(getMultiLang(cityName));
        sodaPlayer.setPosition(dictEntry.getId());
        sodaPlayer.setPositionName(dictEntry.getName());
        sodaPlayer.setGameFType(gameFType);
        sodaPlayer.setName(playerName);
        sodaPlayer.setMultiLangNames(getEngMultiLang(sodaPlayer.getMultiLangNames(), playerName, playerEngName));
        sodaPlayer.setAllowCountries(getAllowCountries());
        sodaPlayer.setOnlineLanguages(getOnlineLanguage());
    }


    private void buildTeamPlayer(String shirtNumber, String positionId, String positionName, Player sodaPlayer, DictEntry dictEntry, TeamSeason.TeamPlayer teamPlayer) {
        teamPlayer.setPid(sodaPlayer.getId());
        teamPlayer.setName(sodaPlayer.getName());
        teamPlayer.setMultiLangNames(getEngMultiLang(sodaPlayer.getMultiLangNames(), sodaPlayer.getName(), sodaPlayer.getEnglishName()));
        teamPlayer.setNumber(LeNumberUtils.toLong(shirtNumber));
        teamPlayer.setPositionName(positionName);
        teamPlayer.setMultiLangPositionNames(getMultiLang(positionName));
        teamPlayer.setPosition(dictEntry.getId());
        teamPlayer.setPostionOrder(Integer.parseInt(positionId));
    }
}