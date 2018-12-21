package com.lesports.sms.data.processor;

import com.google.common.collect.Lists;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LangString;
import com.lesports.api.common.LanguageCode;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.MatchStats;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public abstract class AbstractProcessor {

    private static Logger logger = LoggerFactory.getLogger(AbstractProcessor.class);


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
          //  hk.setValue(HanLP.convertToTraditionalChinese(value));
            langStrings.add(hk);
        }
        return langStrings;
    }

    public List<CountryCode> getAllowCountries() {
        List<CountryCode> countryCodes = Lists.newArrayList();
        countryCodes.add(CountryCode.CN);
        return countryCodes;
    }
    public  List<MatchStats.CompetitorStat> CompetitorStatToStats(Set<Match.CompetitorStat> competitorStats,List<Match.Squad> squads ){
        if(CollectionUtils.isEmpty(competitorStats)) return null;
        List<MatchStats.CompetitorStat> lists=Lists.newArrayList();
        for(Match.CompetitorStat stat:competitorStats){
            MatchStats.CompetitorStat currentStat=new MatchStats.CompetitorStat();
            currentStat.setCompetitorType(CompetitorType.TEAM);
            currentStat.setCompetitorId(stat.getCompetitorId());
            currentStat.setStats(stat.getStats());
            currentStat.setPlayerStats(suadsToStats(stat.getCompetitorId(), squads));
            lists.add(currentStat);

        }
        return lists;
    }
    private  List<MatchStats.PlayerStat> suadsToStats( Long tid,List<Match.Squad> squads){
        if(CollectionUtils.isEmpty(squads)) return null;
        List<MatchStats.PlayerStat>  lists=Lists.newArrayList();
        for(Match.Squad squad:squads){
            if(tid==squad.getTid()){
                for(Match.SimplePlayer player:squad.getPlayers()) {
                    MatchStats.PlayerStat currentStat = new MatchStats.PlayerStat();
                    currentStat.setPlayerId(player.getPid());
                    currentStat.setStats(player.getStats());
                    lists.add(currentStat);
                }
            }

        }
        return lists;
    }

    public boolean saveEntity(Object entity,Class objectClass) {
        boolean isSuccess = false;
        int tryCount = 0;
        while (!isSuccess && tryCount++ < Constants.MAX_TRY_COUNT) {
            try {
                if (entity.getClass() == Match.class) {
                    isSuccess = SbdsInternalApis.saveMatch((Match) entity) > 0;
                }
                if (entity.getClass() == MatchStats.class) {
                    isSuccess = SbdsInternalApis.saveMatchStats((MatchStats) entity) > 0;
                }
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
}
