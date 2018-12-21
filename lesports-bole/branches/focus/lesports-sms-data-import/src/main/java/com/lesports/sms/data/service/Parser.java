package com.lesports.sms.data.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.HanLP;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LangString;
import com.lesports.api.common.LanguageCode;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.GroundType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.util.CommonUtil;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.MatchStats;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/4/1.
 */
public abstract class Parser {
    private static Logger logger = LoggerFactory.getLogger(Parser.class);


    public Map<String, Object> getNBAStats(org.dom4j.Element statistics, Map<String, String> paths) {
        Map<String, Object> stats = Maps.newHashMap();
        Iterator iter = paths.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            String value = CommonUtil.getStringValue(statistics.selectObject(String.valueOf(entry.getValue())));
            String valueEnd = value;
            if (key.contains("percentage")||value.contains("percentage") || value.contains("%")) valueEnd = CommonUtil.getPercentFormat(value);
            else if (value.startsWith(".")) valueEnd = "0" + value;
            else if (value.startsWith("-")) valueEnd = "0.0";
            stats.put(key, valueEnd);
        }
        return stats;
    }

    public Map<String, Object> getStats(Element statistics, Map<String, String> paths) {
        Map<String, Object> stats = Maps.newHashMap();
        Iterator iter = paths.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            String key = String.valueOf(entry.getKey());
            stats.put(key, statistics.getAttributeValue(key));
        }
        return stats;
    }


    public Long getDictIdByName(String name) {
        List<DictEntry> dictEntryList = SbdsInternalApis.getDictEntriesByName("^" + name + "$");
        if (CollectionUtils.isEmpty(dictEntryList)) return 0L;
        return dictEntryList.get(0).getId();
    }

    public List<LangString> getMultiLang(String value) {
        List<LangString> langStrings = Lists.newArrayList();
        if (null != value) {
            LangString cn = new LangString();
            cn.setLanguage(LanguageCode.ZH_CN);
            cn.setValue(value);
            langStrings.add(cn);
            LangString hk = new LangString();
            hk.setLanguage(LanguageCode.ZH_HK);
            hk.setValue(HanLP.convertToTraditionalChinese(value));
            langStrings.add(hk);
        }
        return langStrings;
    }

    public List<LangString> getMultiLang(List<LangString> old, String englishName) {
        LangString cn = new LangString();
        cn.setLanguage(LanguageCode.EN_US);
        cn.setValue(englishName);
        List<LangString> langStrings = Lists.newArrayList();
        if (CollectionUtils.isEmpty(old)) {
            langStrings.add(cn);
        }
        for (LangString currentSring : old)
            if (!currentSring.getLanguage().equals(LanguageCode.EN_US)) {
                langStrings.add(currentSring);
            }
        langStrings.add(cn);
        return langStrings;
    }

    public List<LangString> getEngMultiLang(List<LangString> old, String name, String englishName) {
        LangString en = new LangString();
        en.setLanguage(LanguageCode.EN_US);
        en.setValue(englishName);

        List<LangString> langStrings = Lists.newArrayList();
        if (CollectionUtils.isEmpty(old)) {
            langStrings = getMultiLang(name);
        } else {
            for (LangString currentSring : old) {
                if (!currentSring.getLanguage().equals(LanguageCode.EN_US)) {
                    langStrings.add(currentSring);
                }
            }
        }

        langStrings.add(en);
        return langStrings;
    }

    public List<CountryCode> getAllowCountries() {
        List<CountryCode> countryCodes = Lists.newArrayList();
        countryCodes.add(CountryCode.CN);
        countryCodes.add(CountryCode.HK);
        return countryCodes;
    }

    public List<LanguageCode> getOnlineLanguage() {
        List<LanguageCode> languageCodes = Lists.newArrayList();
        languageCodes.add(LanguageCode.ZH_CN);
        return languageCodes;
    }

    public List<MatchStats.CompetitorStat> CompetitorStatToStats(Set<Match.CompetitorStat> competitorStats, List<Match.Squad> squads) {
        if (CollectionUtils.isEmpty(competitorStats)) return null;
        List<MatchStats.CompetitorStat> lists = Lists.newArrayList();
        for (Match.CompetitorStat stat : competitorStats) {
            MatchStats.CompetitorStat currentStat = new MatchStats.CompetitorStat();
            currentStat.setCompetitorType(CompetitorType.TEAM);
            currentStat.setCompetitorId(stat.getCompetitorId());
            currentStat.setStats(stat.getStats());
            currentStat.setPlayerStats(suadsToStats(stat.getCompetitorId(), squads));
            lists.add(currentStat);

        }
        return lists;
    }

    private List<MatchStats.PlayerStat> suadsToStats(Long tid, List<Match.Squad> squads) {
        if (CollectionUtils.isEmpty(squads)) return null;
        List<MatchStats.PlayerStat> lists = Lists.newArrayList();
        for (Match.Squad squad : squads) {
            if (tid.longValue() == squad.getTid().longValue()) {
                for (Match.SimplePlayer player : squad.getPlayers()) {
                    MatchStats.PlayerStat currentStat = new MatchStats.PlayerStat();
                    currentStat.setPlayerId(player.getPid());
                    currentStat.setStats(player.getStats());
                    lists.add(currentStat);
                }
            }
        }
        return lists;
    }

    public boolean saveMatch(Match entity) {
        boolean isSuccess = false;
        int tryCount = 0;
        while (!isSuccess && tryCount++ < Constants.MAX_TRY_COUNT) {
            try {

                isSuccess = SbdsInternalApis.saveMatch(entity) > 0;
            } catch (Exception e) {
                logger.error("fail to update match. id : {}. sleep and try again.", entity.toString(), e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean saveMatchState(MatchStats entity) {
        boolean isSuccess = false;
        int tryCount = 0;
        while (!isSuccess && tryCount++ < Constants.MAX_TRY_COUNT) {
            try {
                isSuccess = SbdsInternalApis.saveMatchStats(entity) > 0;

            } catch (Exception e) {
                logger.error("fail to update match. id : {}. sleep and try again.", entity.toString(), e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return true;
    }

    public boolean sendMessage(LeMessage message) {
        int tryCount = 0;
        boolean result = false;
        while (!result && tryCount++ < Constants.MAX_TRY_COUNT) {
            try {
                result = SwiftMessageApis.sendMsgSync(message);
                logger.info("message send sucessfully,msg:{}",message.toString());
            } catch (Exception e) {
                logger.error("fail to send message", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return true;
    }
    private boolean isMatchPropertyChange(Match oldMatch, Match newMatch) {
        boolean isChange = false;
        Set<Match.Competitor> oldCompetitors = oldMatch.getCompetitors();
        Set<Match.Competitor> newCompetitors = newMatch.getCompetitors();
        MatchStatus oldMatchStatus = oldMatch.getStatus();
        MatchStatus newMatchStatus = newMatch.getStatus();
        String oldHomeScore = getMatchScore(oldCompetitors, GroundType.HOME);
        String oldAwayScore = getMatchScore(oldCompetitors, GroundType.AWAY);
        String newHomeScore = getMatchScore(newCompetitors, GroundType.HOME);
        String newAwayScore = getMatchScore(newCompetitors, GroundType.AWAY);
        if (isNewScoreBigger(oldAwayScore, newAwayScore) || isNewScoreBigger(oldHomeScore, newHomeScore) || isStatusChange(oldMatchStatus, newMatchStatus) || isCurrentMomentChange(oldMatch.getCurrentMoment(), newMatch.getCurrentMoment())) {
            isChange = true;
            logger.info("the live data is changed ");
        }
        return isChange;
    }

    private boolean isCurrentMomentChange(Match.CurrentMoment overtime, Match.CurrentMoment currenttime) {
        boolean ischange = false;
        if (currenttime == null)
            return ischange;
        if (overtime == null)
            return true;
        if (currenttime.getSection() != overtime.getSection() || currenttime.getTime() != overtime.getTime())
            ischange = true;
        return ischange;
    }

    private boolean isStatusChange(MatchStatus oldStatus, MatchStatus newStatus) {
        boolean isChange = false;
        if (oldStatus.equals(MatchStatus.MATCH_NOT_START) && newStatus.equals(MatchStatus.MATCHING)) {
            isChange = true;
        }
        if (oldStatus.equals(MatchStatus.MATCHING) && newStatus.equals(MatchStatus.MATCH_END)) {
            isChange = true;
        }
        if (oldStatus.equals(MatchStatus.MATCH_NOT_START) && newStatus.equals(MatchStatus.MATCH_END)) {
            isChange = true;
        }
        return isChange;
    }

    private boolean isNewScoreBigger(String oldScore, String newScore) {
        if (StringUtils.isEmpty(oldScore)) {
            oldScore = "0";
        }
        if (StringUtils.isEmpty(newScore)) {
            newScore = "0";
        }
        int os = Integer.parseInt(oldScore);
        int ns = Integer.parseInt(newScore);
        return ns > os;
    }

    private String getMatchScore(Set<Match.Competitor> competitors, GroundType type) {
        String score = "";
        if (CollectionUtils.isNotEmpty(competitors)) {
            for (Match.Competitor competitor : competitors) {
                if (competitor.getGround().equals(type)) {
                    score = competitor.getFinalResult();
                    break;
                }
            }
        }

        return score;
    }

}
