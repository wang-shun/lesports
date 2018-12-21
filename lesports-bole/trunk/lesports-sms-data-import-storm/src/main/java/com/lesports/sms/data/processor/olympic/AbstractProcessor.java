package com.lesports.sms.data.processor.olympic;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LangString;
import com.lesports.api.common.LanguageCode;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.model.GetDictCacheKey;
import com.lesports.sms.data.model.olympic.Code;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Player;
import com.lesports.sms.model.Team;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.lesports.sms.data.Constants.*;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public abstract class AbstractProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractProcessor.class);
    private LoadingCache<GetDictCacheKey, DictEntry> dictEntryLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build(new CacheLoader<GetDictCacheKey, DictEntry>() {
                @Override
                public DictEntry load(GetDictCacheKey key) throws Exception {
                    return getCommonDict(key.getPartnerType(), key.getCode());
                }
            });

    Long getParentId(String type, Code obj) {
        String code = obj.getCode();
        if (code == null) return 0L;
        DictEntry dictEntry = null;
        if (type.equals(CODE_COUNTRY)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_COUNTRY, 0);
        } else if (type.equals(CODE_SPORT)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_SPORT, 0);
        } else if (type.equals(CODE_PHASE)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_PHASE, 0);
        } else if (type.equals(CODE_DISCIPLINE)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_DISCIPLINE, 0);
        } else if (type.equals(CODE_EVENT) && !obj.getCode().endsWith("000000")) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_EVENT, 0);
        } else if (type.equals(CODE_SPORT_CODE)) {
            dictEntry = getSportCodeParent(obj);
        }
        if (dictEntry == null) return 0L;
        return dictEntry.getId();
    }

    Long getParentId(String type) {
        DictEntry dictEntry = null;
        if (type.equals(CODE_COUNTRY)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_COUNTRY, 0);
        } else if (type.equals(CODE_SPORT)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_SPORT, 0);
        } else if (type.equals(CODE_PHASE)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_PHASE, 0);
        } else if (type.equals(CODE_DISCIPLINE)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_DISCIPLINE, 0);
        } else if (type.equals(CODE_EVENT)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_EVENT, 0);
        }
        if (dictEntry == null) return 0L;
        return dictEntry.getId();
    }


    DictEntry getSportCodeParent(Code obj) {
        String type = obj.getType();
        DictEntry dictEntry = null;
        if (type.equals(CODE_EVENT_TYPE)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_EVENT_TYPE, 0);
        } else if (type.equals(CODE_EVENT_RESULT)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_EVENT_RESULT, 0);
        } else if (type.equals(CODE_PERIOD)) {
            dictEntry = SbdsInternalApis.getDictEntryByNameAndParentId(DICT_NAME_PERIOD, 0);
        }
        return dictEntry;
    }

    //获取常规的字典项（大项，阶段，国籍）,带缓存
    DictEntry getCommonDictWithCache(String parentType, String code) {
        try {
            return dictEntryLoadingCache.get(new GetDictCacheKey(code, parentType));
        } catch (Exception e) {
            LOG.warn("Fail to get dict by parent type : {}, code : {}.", parentType, code);
        }
        return null;
    }

    //获取常规的字典项（大项，阶段，国籍）
    DictEntry getCommonDict(String parentType, String code) {
        DictEntry parentDict = SbdsInternalApis.getDictEntryByNameAndParentId(parentType, 0);
        if (parentDict == null) return null;
        List<DictEntry> gameTypeDict = SbdsInternalApis.getDictEntryByCodeAndParentId(code, parentDict.getId());
        if (CollectionUtils.isEmpty(gameTypeDict)) {
            return null;
        }
        return gameTypeDict.get(0);
    }

    //获取参赛者id，运动队或者运动员
    Long getCompetitorIdWithCreate(String partnerCode, String partnerName, String organisation, String type) {
        Long id = 0L;
        if (type == null) return id;
        if (type.equalsIgnoreCase("A")) {
            Player currentPlayer = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerCode, Constants.partner_type);
            if (currentPlayer == null) {
                Player newPlayer = new Player();
                newPlayer.setPartnerId(partnerCode);
                newPlayer.setPartnerType(partner_type);
                newPlayer.setDeleted(false);
                DictEntry dictEntry = getCommonDictWithCache(DICT_NAME_COUNTRY, organisation);
                newPlayer.setCountryId(dictEntry != null ? dictEntry.getId() : 0L);
                newPlayer.setName(StringUtils.isBlankOrNull(partnerName) ? organisation : partnerName);
                newPlayer.setMultiLangNames(getMultiLang(newPlayer.getName()));
                newPlayer.setOnlineLanguages(getOnlineLang());
                newPlayer.setAllowCountries(getAllowCountries());
                return SbdsInternalApis.savePlayer(newPlayer);
            }
            id = currentPlayer.getId();
        } else if (type.equalsIgnoreCase("T")) {
            Team currentTeam = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(partnerCode, Constants.partner_type);
            if (currentTeam == null) {
                Team newTeam = new Team();
                newTeam.setPartnerId(partnerCode);
                newTeam.setPartnerType(partner_type);
                newTeam.setDeleted(false);
                DictEntry dictEntry = getCommonDictWithCache(DICT_NAME_COUNTRY, organisation);
                newTeam.setCountryId(dictEntry != null ? dictEntry.getId() : 0L);
                newTeam.setName(StringUtils.isBlankOrNull(partnerName) ? organisation : partnerName);
                newTeam.setMultiLangNames(getMultiLang(newTeam.getName()));
                newTeam.setOnlineLanguages(getOnlineLang());
                newTeam.setAllowCountries(getAllowCountries());
                return SbdsInternalApis.saveTeam(newTeam);
            }
            id = currentTeam.getId();
        }
        return id;
    }

    Long getCompetitorId(String partnerCode, String type) {
        Long id = 0L;
        if (type == null) return id;
        if (type.equalsIgnoreCase("A")) {
            Player currentPlayer = SbdsInternalApis.getPlayerByPartnerIdAndPartnerType(partnerCode, Constants.partner_type);
            if (currentPlayer != null) {
                id = currentPlayer.getId();
            }
        } else if (type.equalsIgnoreCase("T")) {
            Team currentTeam = SbdsInternalApis.getTeamByPartnerIdAndPartnerType(partnerCode, Constants.partner_type);
            if (currentTeam != null) {

                id = currentTeam.getId();
            }
        }
        return id;
    }

    //获取参赛者名称，运动队或者运动员
    String getCompetitorName(Long competitionId, String type) {
        String name = "";
        if (type == null) return name;
        if (type.equalsIgnoreCase("A")) {
            Player currentPlayer = SbdsInternalApis.getPlayerById(competitionId);
            name = currentPlayer.getName();
        } else if (type.equalsIgnoreCase("T")) {
            Team currentTeam = SbdsInternalApis.getTeamById(competitionId);
            name = currentTeam.getName();
        }
        return name;
    }

    //获取指定参赛者在指定比赛中的成绩
    String getCompetitorResult(Long matchId, Long competitionId) {
        String result = "0";
        Match currentMatch = SbdsInternalApis.getMatchById(matchId);
        if (currentMatch == null || CollectionUtils.isEmpty(currentMatch.getCompetitors())) return result;
        for (Match.Competitor competitor : currentMatch.getCompetitors()) {
            if (competitor.getCompetitorId().equals(competitionId)) {
                result = competitor.getFinalResult();
                break;
            }
        }
        return result;
    }

    protected String getEnglishName(List<LangString> lists) {
        if (CollectionUtils.isEmpty(lists)) return "";
        for (LangString currentString : lists) {
            if (currentString.getLanguage().equals(LanguageCode.EN_US)) {
                return currentString.getValue();
            }
        }
        return "";
    }

    protected List<LangString> getMultiLang(String value) {
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
            LangString en = new LangString();
            en.setLanguage(LanguageCode.EN_US);
            en.setValue(value);
            langStrings.add(en);
        }
        return langStrings;
    }

    protected List<LanguageCode> getOnlineLang() {
        List<LanguageCode> langStrings = Lists.newArrayList();
        LanguageCode cn = LanguageCode.ZH_CN;
        langStrings.add(cn);
        return langStrings;
    }

    protected List<LangString> getMultiLangwithTwoLanguage(String value, String englishValue) {
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
            LangString en = new LangString();
            en.setLanguage(LanguageCode.EN_US);
            en.setValue(englishValue);
            langStrings.add(en);
        }
        return langStrings;
    }

    protected List<CountryCode> getAllowCountries() {
        List allowCountries = Lists.newArrayList();
        allowCountries.add(CountryCode.CN);
        return allowCountries;
    }

    protected MatchStatus getValidStatus(Match currentMatch, MatchStatus newStatus) {
        if(currentMatch.getStatus()==null)currentMatch.setStatus(MatchStatus.MATCH_NOT_START);
        if (newStatus != null) {
            if (currentMatch.getStatus().equals(MatchStatus.MATCH_NOT_START)) return newStatus;
            else if (currentMatch.getStatus().equals(MatchStatus.MATCHING) && newStatus.equals(MatchStatus.MATCH_END))
                return newStatus;
        }
        return currentMatch.getStatus();
    }
}
