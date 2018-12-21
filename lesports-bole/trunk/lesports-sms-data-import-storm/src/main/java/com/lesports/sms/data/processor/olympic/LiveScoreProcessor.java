package com.lesports.sms.data.processor.olympic;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.bole.api.vo.TOlympicConfig;
import com.lesports.bole.api.vo.TOlympicLiveConfigSet;
import com.lesports.sms.api.common.CoachType;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.BoleApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.data.utils.dataFormatterUtils.FormatterFactory;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.MatchStats;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class LiveScoreProcessor extends AbstractProcessor implements BeanProcessor<Document> {
    private static Logger LOG = LoggerFactory.getLogger(LiveScoreProcessor.class);
    //init match status information8
    private Map<String, MatchStatus> stateMap = ImmutableMap.of("UNOFFICIAL", MatchStatus.MATCH_END, "OFFICIAL", MatchStatus.MATCH_END, "START_LIST", MatchStatus.MATCH_NOT_START, "LIVE", MatchStatus.MATCHING);

    public Boolean process(String codeType, Document document) {
        Boolean result = false;
        String documentCode = CommonUtil.getStringValue(document.selectObject(Constants.PATH_MATCH_CODE));
        String resultStatus = CommonUtil.getStringValue(document.selectObject(Constants.PATH_MATCH_STATUS));
        LOG.info("live score  parser begin, document code : {}, result status : {}", documentCode, resultStatus);
        try {
            //get the match from db
            Match match = SbdsInternalApis.getMatchByPartnerIdAndType(documentCode, Constants.partner_type);
            if (null == match) {
                LOG.warn("live match is not exist,do not need update,documentCode:{}", documentCode);
                return result;
            }
            if (match.getIsOffical() != null && match.getIsOffical()) {
                LOG.warn("live match is official,do not need update,documentCode:{}", documentCode);
                return true;
            }
            if (resultStatus != null && resultStatus.equalsIgnoreCase("official")) {
                match.setIsOffical(true);
            }

            //更新match的实时扩展信息
            TOlympicLiveConfigSet matchConfigs = BoleApis.getOlympicsConfigSet(match.getGameSType());
            if (matchConfigs != null && CollectionUtils.isNotEmpty(matchConfigs.getMatchExtendConfig()))
                match.setExtendInfos(getExtendInfos(match.getExtendInfos(), document.getRootElement(), matchConfigs.getMatchExtendConfig()));
            //更新Match.competions 信息
            List<Element> results = document.selectNodes("//Result");
            boolean isSuccess = false;
            int tryCount = 0;
            if (CollectionUtils.isEmpty(results)) return false;
            match.setStatus(getValidStatus(match, stateMap.get(resultStatus)));
            updateDetailMatch(match, results, matchConfigs, documentCode.substring(0, 2));
            while (!isSuccess && tryCount++ < Constants.MAX_TRY_COUNT) {
                try {
                    isSuccess = SbdsInternalApis.saveMatch(match) > 0;
                    LOG.info("save match {} result {}", match.getId(), isSuccess);
                } catch (Exception e) {
                    LOG.error("fail to update match. id : {}. sleep and try again.", match.getId(), e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

                MatchStats matchStats = getMatchStats(match.getId(), matchConfigs);
                if (matchStats != null) {
                    updateMatchStats(matchStats, results, matchConfigs);
                    boolean statsIsSuccess = false;
                    while (!statsIsSuccess && tryCount++ < Constants.MAX_TRY_COUNT) {
                        try {
                            statsIsSuccess = SbdsInternalApis.saveMatchStats(matchStats) > 0;
                        } catch (Exception e) {
                            LOG.error("fail to update match stats. id : {}. sleep and try again.", match.getId(), e);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                    LOG.info("update match success, matchId : {}, matchName : {} and matchPartnerId : {} and matchStatus : {}.", match.getId(), match.getName(), match.getStatus());
                }
            }
            return true;
        } catch (Exception e) {
            LOG.error("parse the match live score : {} error", documentCode, e);
            return result;
        }
    }

    /**
     * 更新MatchState信息
     */
    private void updateMatchStats(MatchStats matchStats, List<Element> results, TOlympicLiveConfigSet matchConfigs) {
        List<MatchStats.CompetitorStat> currentCompetitorStats = new ArrayList<>();
        for (Element resultElement : results) {
            String competitorCode = CommonUtil.getStringValue(resultElement.selectObject(Constants.PATH_COMPETITOR_CODE));
            String competitorOrganisation = CommonUtil.getStringValue(resultElement.selectObject(Constants.PATH_COMPETITOR_COUNTRY));
            String type = CommonUtil.getStringValue(resultElement.selectObject(Constants.PATH_COMPETITOR_Type));
            String order = CommonUtil.getStringValue(resultElement.selectObject(Constants.PATH_COMPETITOR_ORDER));
            Long competitorId = getCompetitorIdWithCreate(competitorCode, competitorCode + competitorOrganisation, competitorOrganisation, type);
            if (competitorId == 0L) continue;
            MatchStats.CompetitorStat competitorStat = new MatchStats.CompetitorStat();
            competitorStat.setTeamId(competitorId);
            competitorStat.setCompetitorId(competitorId);
            competitorStat.setCompetitorType(type.equals("A") ? CompetitorType.PLAYER : CompetitorType.TEAM);
            competitorStat.setShowOrder(CommonUtil.getIntegerValue(order));
            //add 技术统计
            if (CollectionUtils.isNotEmpty(matchConfigs.getCompetitorStatsConfig())) {
                competitorStat.setStats(getExtendInfos(null, resultElement, matchConfigs.getCompetitorStatsConfig()));
            }
            List<Element> athletes = resultElement.selectNodes(Constants.PATH_ATHLETE);
            if (CollectionUtils.isNotEmpty(athletes) && CollectionUtils.isNotEmpty(matchConfigs.getPlayerStatsConfig())) {
                List<MatchStats.PlayerStat> playerStats = new ArrayList<>();
                for (Element athlete : athletes) {
                    MatchStats.PlayerStat playerStat = new MatchStats.PlayerStat();
                    Long athleteId = getCompetitorIdWithCreate(CommonUtil.getStringValue(athlete.selectObject("./@Code")), CommonUtil.getStringValue(athlete.selectObject("./Description/@GivenName")), CommonUtil.getStringValue(athlete.selectObject("./Description/@Organisation")), "A");
                    playerStat.setPlayerId(athleteId);
                    playerStat.setStats(getExtendInfos(null, athlete, matchConfigs.getPlayerStatsConfig()));
                    playerStats.add(playerStat);
                }
                competitorStat.setPlayerStats(playerStats);
            }
            currentCompetitorStats.add(competitorStat);
        }
        matchStats.setCompetitorStats(currentCompetitorStats);
    }

    /**
     * 更新Match包含阵容和比分，和参赛者基础信息
     */
    private void updateDetailMatch(Match currentMatch, List<Element> results, TOlympicLiveConfigSet matchConfigs, String gameFType) {
        Set<Match.Competitor> currentCompetitors = Sets.newHashSet();
        List<Match.Squad> squads = new ArrayList<>();
        for (Element resultElement : results) {
            String competitorCode = CommonUtil.getStringValue(resultElement.selectObject(Constants.PATH_COMPETITOR_CODE));
            String type = CommonUtil.getStringValue(resultElement.selectObject(Constants.PATH_COMPETITOR_Type));
            Integer order = CommonUtil.getIntegerValue(resultElement.selectObject(Constants.PATH_COMPETITOR_ORDER));
            String name = CommonUtil.getStringValue(resultElement.selectObject(Constants.PATH_COMPETITOR_Type));
            String competitorOrganisation = type.equals("A") ? CommonUtil.getStringValue(resultElement.selectObject(Constants.PATH_PLAYER_COUNTRY)) : competitorCode.substring(6, 9);
            Long competitorId = getCompetitorIdWithCreate(competitorCode, name, competitorOrganisation, type);
            if (competitorId == 0) continue;
            Object currentResult = CommonUtil.getStringValue(resultElement.selectObject("./@Result"));
            Match.Competitor oldCompetitor = getMatchCompetitor(currentMatch.getCompetitors(), competitorId);
            Match.Competitor competitor = oldCompetitor == null ? new Match.Competitor() : oldCompetitor;
            competitor.setCompetitorId(competitorId);
            competitor.setOrder(order);
            if (!StringUtils.isBlankOrNull(type)) {
                competitor.setType(type.equals("A") ? CompetitorType.PLAYER : CompetitorType.TEAM);
            }
            DictEntry country = getCommonDictWithCache(Constants.DICT_NAME_COUNTRY, competitorOrganisation);
            competitor.setCompetitorCounntryId(country != null ? country.getId() : competitor.getCompetitorCounntryId());
            if (matchConfigs != null && CollectionUtils.isNotEmpty(matchConfigs.getTeamExtendConfig())) {
                competitor.setExtendInfos(getExtendInfos(competitor.getExtendInfos(), resultElement, matchConfigs.getTeamExtendConfig()));
            }   //update competitor  basic  extendInfo
            competitor.setFinalResult(currentResult == null ? competitor.getFinalResult() : currentResult.toString());
            //update Competitor finalResult
            if (CollectionUtils.isNotEmpty(getHomeAwaySectionResults(resultElement, competitorCode, gameFType))) {
                competitor.setSectionResults(getHomeAwaySectionResults(resultElement, competitorCode, gameFType));
            }//update section Result;
            currentCompetitors.add(competitor);//update Competitor sectionResult
            LOG.info("update competitor information successfully competitorId : {}", competitor.getCompetitorId());
            Match.Squad squad = getSquadByCompetitorId(currentMatch.getSquads(), competitorId) == null ? new Match.Squad() : getSquadByCompetitorId(currentMatch.getSquads(), competitorId);
            squad.setTid(competitorId);
            List<Element> coaches = resultElement.selectNodes(Constants.PATH_COACH);
            if (CollectionUtils.isNotEmpty(coaches)) {
                squad.setCoaches(getSquadCoaches(coaches, matchConfigs));
                LOG.info("update competitor squad-caoches successfully competitorId : {}", competitor.getCompetitorId());
            }
            List<Element> athletes = resultElement.selectNodes(Constants.PATH_ATHLETE);
            if (CollectionUtils.isNotEmpty(athletes)) {
                squad.setPlayers(getSquadPlayers(athletes, matchConfigs));
                LOG.info("update competitor squad-simplePlayers successfully competitorId : {}", competitor.getCompetitorId());
            }
            squads.add(squad);//get squad of the current Competitor
        }
        currentMatch.setCompetitors(currentCompetitors);
        currentMatch.setSquads(squads);
        LOG.info("the matchId : {} team and player squard and statics information are saved ", currentMatch.toString());
    }

    private Match.Competitor getMatchCompetitor(Set<Match.Competitor> competitors, Long id) {
        if (CollectionUtils.isEmpty(competitors)) return null;
        for (Match.Competitor competitor : competitors) {
            if (competitor.getCompetitorId().equals(id)) return competitor;
        }
        return null;
    }

    private Match.Squad getSquadByCompetitorId(List<Match.Squad> squads, Long id) {
        if (CollectionUtils.isEmpty(squads)) return null;
        for (Match.Squad squad : squads) {
            if (squad.getTid().equals(id)) return squad;
        }
        return null;
    }

    private List<Match.SectionResult> getHomeAwaySectionResults(Element document, String competitorCode, String gameType) {
        String homeCode = CommonUtil.getStringValue(document.selectObject(Constants.PATH_PERIODS_HOME_CODE));
        String awayCode = CommonUtil.getStringValue(document.selectObject(Constants.PATH_PERIODS_AWAY_CODE));
        List<Element> periods = document.selectNodes(Constants.PATH_PERIODS);
        if (homeCode == null || awayCode == null || CollectionUtils.isEmpty(periods)) return null;
        List<Match.SectionResult> sectionResults = new ArrayList<>();
        if (homeCode.equalsIgnoreCase(competitorCode)) {
            for (Element currentPeriod : periods) {
                DictEntry code = getCommonDictWithCache(Constants.DICT_NAME_PERIOD, CommonUtil.getStringValue(currentPeriod.selectObject(Constants.PATH_PERIOD_CODE)));
                if (code == null) {
                    LOG.warn("section code : {} is not exist", CommonUtil.getStringValue(currentPeriod.selectObject(Constants.PATH_PERIOD_CODE)));
                    continue;
                }
                Match.SectionResult currentPeriodResult = new Match.SectionResult();
                currentPeriodResult.setSection(code != null ? code.getId() : 0L);
                currentPeriodResult.setResult(CommonUtil.getStringValue(currentPeriod.selectObject(Constants.PATH_PERIOD_HOMESCORE)));
                if (currentPeriodResult.getSection() == null && currentPeriodResult.getResult() == null) continue;
                sectionResults.add(currentPeriodResult);
                LOG.info("the curent competitor Id ,is{}.and sectionResult ,is{}", competitorCode, sectionResults.toString());

            }
        } else {
            for (Element currentPeriod : periods) {
                DictEntry code = getCommonDictWithCache(Constants.DICT_NAME_PERIOD, CommonUtil.getStringValue(currentPeriod.selectObject(Constants.PATH_PERIOD_CODE)));
                if (code == null) {
                    LOG.warn("section code : {} is not exist", CommonUtil.getStringValue(currentPeriod.selectObject(Constants.PATH_PERIOD_CODE)));
                    continue;
                }
                Match.SectionResult currentPeriodResult = new Match.SectionResult();
                currentPeriodResult.setSection(code != null ? code.getId() : null);
                currentPeriodResult.setResult(CommonUtil.getStringValue(currentPeriod.selectObject(Constants.PATH_PERIOD_AWAYSCORE)));
                if (currentPeriodResult.getSection() == null && currentPeriodResult.getResult() == null) continue;
                sectionResults.add(currentPeriodResult);
            }
        }
        return sectionResults;
    }


    /**
     * 获取CompetitorState对象，依据MatchID和动态配置对象
     */
    private MatchStats getMatchStats(Long matchId, TOlympicLiveConfigSet configSet) {
        MatchStats currentState = SbdsInternalApis.getMatchStatsById(matchId);
        if (currentState != null) return currentState;
        if (configSet == null) return null;
        if (CollectionUtils.isEmpty(configSet.getPlayerStatsConfig()) && CollectionUtils.isEmpty(configSet.getCompetitorStatsConfig()))
            return null;
        currentState = new MatchStats();
        currentState.setId(matchId);
        return currentState;
    }

    /**
     * 获取当前比赛的实时运动员阵容
     *
     * @param caoches
     * @param matchConfigs
     * @return
     */
    private List<Match.SimpleCoach> getSquadCoaches(List<Element> caoches, TOlympicLiveConfigSet matchConfigs) {

        List<Match.SimpleCoach> caoches1 = new ArrayList<>();
        for (Element coach : caoches) {
            Match.SimpleCoach simpleCoach = new Match.SimpleCoach();
            Long athleteId = getCompetitorIdWithCreate(CommonUtil.getStringValue(coach.selectObject("./@Code")), CommonUtil.getStringValue(coach.selectObject("./Description/@GivenName")), CommonUtil.getStringValue(coach.selectObject("./Description/@Organisation")), "A");
            CoachType type = CommonUtil.getStringValue(coach.selectObject("./@Function")).equals("COACH") ? CoachType.MAIN : CoachType.ASSIST;
            simpleCoach.setType(type);
            simpleCoach.setCoachId(athleteId);
            caoches1.add(simpleCoach);
        }
        return caoches1;
    }

    /**
     * 获取当前比赛的实时运动员阵容
     *
     * @param athletes
     * @param matchConfigs
     * @return
     */
    private List<Match.SimplePlayer> getSquadPlayers(List<Element> athletes, TOlympicLiveConfigSet matchConfigs) {

        List<Match.SimplePlayer> players = new ArrayList<>();
        for (Element athlete : athletes) {
            Match.SimplePlayer player = new Match.SimplePlayer();
            Long athleteId = getCompetitorIdWithCreate(CommonUtil.getStringValue(athlete.selectObject("./@Code")), CommonUtil.getStringValue(athlete.selectObject("./Description/@GivenName")), CommonUtil.getStringValue(athlete.selectObject("./Description/@Organisation")), "A");
            player.setPid(athleteId);
            Integer number=athlete.selectObject("./@Bib")==null?CommonUtil.getIntegerValue(athlete.selectObject("./@Order")):CommonUtil.getIntegerValue(athlete.selectObject("./@Bib"));
            player.setNumber(number);
            if (matchConfigs != null) {
                if (CollectionUtils.isNotEmpty(matchConfigs.getPlayerExtendConfig())) {
                    player.setExtendInfos(getExtendInfos(null, athlete, matchConfigs.getPlayerExtendConfig()));
                }
                if (CollectionUtils.isNotEmpty(matchConfigs.getResultConfig())) {
                    player.setResultExtendInfos(getExtendInfos(null, athlete, matchConfigs.getResultConfig()));
                }
            }
            players.add(player);
        }
        return players;
    }

    /**
     * 获取所欲动态配置信息所对应的Map对象
     */
    private Map getExtendInfos(Map oldMap, Element document, List<TOlympicConfig> list) {
        if (CollectionUtils.isEmpty(list)) return null;
        Map<String, Object> extendInfos = oldMap == null ? Maps.newConcurrentMap() : oldMap;
        for (TOlympicConfig config : list) {
            if (config.getElementPath() == null) continue;
            if (!StringUtils.isBlankOrNull(config.getOp())) {
                String value = getElemmentValue(document, config.getElementPath(), config.getRightElementPath(), config.getOp());
                extendInfos.put(config.getPropertyName(), value);
            } else {
                String key = config.getPropertyName();
                Object value = CommonUtil.getStringValue(document.selectObject(config.getElementPath()));
                if (StringUtils.isBlankOrNull(config.getFormatterType())) extendInfos.put(key, value);
                else {
                    if (config.getFormatterType().equals("ListFormatter")) {
                        value = getListValues(document, config.getElementPath());
                        if (null == extendInfos || null == key || null == value) {
                            extendInfos.put(key, value);
                        }
                        extendInfos.put(key, value);
                    } else {
                        Function<String, ?> currentValueFunction = FormatterFactory.getFunction(config.getFormatterType());
                        if (config.getFormatterType() != null && currentValueFunction != null) {
                            extendInfos.put(key, currentValueFunction.apply(value.toString()));
                        }
                    }
                }
            }
        }
        return extendInfos;
    }

    private String getElemmentValue(Element document, String elementPath, String rightElementPath, String op) {
        String opLeft = CommonUtil.getStringValue(document.selectObject(elementPath));
        String opRight = CommonUtil.getStringValue(document.selectObject(rightElementPath));
        if (elementPath == null || rightElementPath == null || StringUtils.isBlankOrNull(opLeft) || StringUtils.isBlankOrNull(opRight))
            return "0";
        Integer opLeftInt = CommonUtil.parseInt(opLeft, 0);
        Integer opRightInt = CommonUtil.parseInt(opRight, 0);
        Integer result = 0;
        if (op.equals("+")) {
            result = opLeftInt + opRightInt;
            return result.toString();
        } else if (op.equals("-")) {
            result = opLeftInt - opRightInt;
            return result.toString();
        } else if (op.equals("*")) {
            result = opRightInt * opLeftInt;
            return result.toString();
        } else if (op.equals("//")) {
            if (opRightInt != 0) {
                result = opLeftInt / opRightInt;
            }
            return result.toString();
        } else {
            return opLeft + op + opRight;

        }
    }

    private List getListValues(Element config, String path) {

        List values = Lists.newArrayList();
        if (!path.startsWith(".") && path.startsWith("/")) path = "/" + path;
        String path1 = path.replace("and @Pos='?'", "").replace("/@Value", "");
        List<Element> elements = config.selectNodes(path1);
        if (CollectionUtils.isEmpty(elements)) return values;
        for (int i = 1; i <= elements.size(); i++) {
            String currentPath = path.replace("?", i + "");
            String value = "0";
            value = CommonUtil.getStringValue(config.selectObject(currentPath));
            if (value != null) {
                values.add(value);
            }
        }
        return values;
    }


}



