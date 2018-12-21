package com.lesports.sms.data.processor.olympic;

import com.google.common.collect.ImmutableMap;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.api.common.MedalType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.annotation.Tag;
import com.lesports.sms.data.model.olympic.Schedule;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
@Tag(Constants.TAG_PROCESSOR_SCHEDULE)
public class ScheduleProcessor extends AbstractProcessor implements BeanProcessor<Schedule> {
    private static Logger LOG = LoggerFactory.getLogger(ScheduleProcessor.class);
    //CANCELLED DELAYED  GETTING_READY INTERRUPTED POSTPONED    SCHEDULED_BREAK
    private Map<String, MatchStatus> stateMap = ImmutableMap.of("SCHEDULED", MatchStatus.MATCH_NOT_START, "GETTING_READY", MatchStatus.MATCH_NOT_START, "RUNNING", MatchStatus.MATCHING, "FINISHED", MatchStatus.MATCH_END);
    private Map<String, MedalType> medalTypeMap = ImmutableMap.of("1", MedalType.GOLD, "2", MedalType.BRONZE);

    @Override
    public Boolean process(String fileType, Schedule obj) {
        boolean result = false;
        try {
            //查找赛季信息Id
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.OG_CID);
            if (null == competitionSeason) {
                LOG.warn("will not handle this, can not find relative event, uniqueTournamentId : {}, name : {}, season : {}, code : {}.", Constants.OG_CID, "2016", obj.getCode());
                return false;
            }
            //获取对应的赛程信息
            Match match = null;
            InternalQuery query = new InternalQuery();
            query.addCriteria(new InternalCriteria("partner_id", "eq", obj.getCode()));
            query.addCriteria(new InternalCriteria("partner_type", "eq", Constants.partner_typeS));
            List<Match> matches = SbdsInternalApis.getMatchsByQuery(query);
            if (CollectionUtils.isNotEmpty(matches)) {
                match = matches.get(0);
            }
            //如果有锁则不更新
            if (match != null && match.getDeleted()) {
                LOG.warn("DELETED Match, the match may be deleted, code : {}", obj.getCode());
                return true;
            } else if (match != null && null != match.getLock() && match.getLock()) {
                LOG.warn("will not handle this, the match may be locked, code : {}", obj.getCode());
                return true;
            } else if (match != null && (obj.getScheduleStatus().equals("CANCELLED") || obj.getScheduleStatus().equals("UNSHEDULE"))) {
                LOG.warn("the match may be cancelled, code : {}", obj.getCode());
                return SbdsInternalApis.deleteMatch(match.getId());
            }
            DictEntry gameSType = getCommonDictWithCache(Constants.DICT_NAME_EVENT, obj.getCode().substring(0, 6) + "000");
            if (gameSType == null) {
                LOG.warn("GMAESTYPR:MISS will not handle this, can not get game second type, code : {}.", obj.getCode());
                return false;
            }
            if (null == match) {
                if (!validCode(obj)) {
                    LOG.warn("will not handle this, invalid data empty code or document code.", obj.getCode(), obj.getDocumentCode());
                    return result;
                }
                //获取match所对应的大项小项和阶段字段
                DictEntry discipline = getCommonDictWithCache(Constants.DICT_NAME_DISCIPLINE, obj.getDocumentCode().substring(0, 2) + "$");
                if (discipline == null) {
                    LOG.warn("DISCIPLINE:MISSwill not handle this, can not get game first type {},  code : {}.", obj.getDocumentCode(), obj.getCode());
                    return false;
                }
                DictEntry gameFType = getCommonDictWithCache(Constants.DICT_NAME_SPORT, discipline.getCode().substring(0, 2) + "$");
                DictEntry gamePhase = getCommonDictWithCache(Constants.DICT_NAME_PHASE, obj.getCode().substring(0, 2) + "0000" + obj.getCode().substring(6, 7) + "00");
                if (gameFType == null) {
                    LOG.warn("GAMEPHASE:MISS will not handle this, can not get game first type {}, or game phase {}, code : {}.", gameFType, gamePhase, obj.getCode());
                    return false;
                }
                match = new Match();
                match.setGameFType(gameFType.getId());
                match.setDiscipline(discipline.getId());
                match.setVs(discipline.getVs());
                match.setAllowCountries(getAllowCountries());
                match.setGameSType(gameSType.getId());
                match.setCid(Constants.OG_CID);
                match.setCsid(competitionSeason.getId());
                match.setStage(gamePhase == null ? 0L : gamePhase.getId());
                match.setMedalType(medalTypeMap.get(obj.getMedal()));
                match.setPartnerId(obj.getCode());
                match.setPartnerType(Constants.partner_typeS);
                String number = obj.getNumber() == null ? "" : obj.getNumber().toString();
                String gamePhaseName = gamePhase == null ? "" : gamePhase.getName();
                String name = competitionSeason.getName() + " " + discipline.getName() + " " + gameSType.getName() + gamePhaseName + number;
                match.setName(name);
                match.setMultiLangNames(getMultiLangwithTwoLanguage(name, obj.getEnName()));
                match.setIsOffical(false);
                match.setOnlineLanguages(getOnlineLang());
                match.setNumber(obj.getNumber());
                match.setVenue(obj.getVenueName());
                match.setMultiLangVenues(getMultiLang(obj.getVenueName()));
            }
            //更新赛程的基本信息
            match.setStartTime(obj.getStartTime());
            match.setStartDate(StringUtils.isBlankOrNull(obj.getStartTime()) ? null : obj.getStartTime().substring(0, 8));
            match.setLocalStartTime(obj.getLocalTime());
            match.setLocalStartDate(StringUtils.isBlankOrNull(obj.getLocalTime()) ? null : obj.getLocalTime().substring(0, 8));
            match.setEndTime(obj.getEndTime());
            match.setStatus(getValidStatus(match, stateMap.get(obj.getScheduleStatus())));
            LOG.info("STATUS : {},id:{}", obj.getScheduleStatus(), match.getPartnerId());
            if (match.getId() == null || match.getId() < 0 || match.getStatus().equals(MatchStatus.MATCH_NOT_START)) {//
                //更新赛程的阵容信息
                Set<Match.Competitor> competitors = new HashSet<>();
                if (CollectionUtils.isNotEmpty(obj.getCompetitorList())) {
                    for (Schedule.Competitor currentCompetitor : obj.getCompetitorList()) {
                        Match.Competitor competitor = new Match.Competitor();
                        competitor.setOrder(CommonUtil.parseInt(currentCompetitor.getOrder(), 1));
                        competitor.setCompetitorId(getCompetitorIdWithCreate(currentCompetitor.getCompetitorCode(), gameSType.getCode() + currentCompetitor.getOrganisation(), currentCompetitor.getOrganisation(), currentCompetitor.getCompetitorType()));
                        competitor.setType(currentCompetitor.getCompetitorType().equals("A") ? CompetitorType.PLAYER : CompetitorType.TEAM);
                        DictEntry country = getCommonDictWithCache(Constants.DICT_NAME_COUNTRY, currentCompetitor.getOrganisation());
                        competitor.setCompetitorCounntryId(country != null ? country.getId() : null);
                        competitors.add(competitor);
                    }
                }
                match.setCompetitors(competitors);
            }
            boolean isSuccess = false;
            int tryCount = 0;
            while (!isSuccess && tryCount++ < Constants.MAX_TRY_COUNT) {
                try {
                    isSuccess = SbdsInternalApis.saveMatch(match) > 0;
                    if (isSuccess) {
                        LOG.info("save match : {} success.", match.getPartnerId());
                        return true;
                    }
                } catch (Exception e) {
                    LOG.error("fail to update match. id : {}. sleep and try again.", match.getId(), e);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        LOG.error("{}", e1.getMessage(), e1);
                    }
                }
            }
            return false;
        } catch (Exception e) {
            LOG.warn("the current match is updated failed, code : {}", obj.getCode(), e);
            return false;
        }
    }

    private Boolean validCode(Schedule obj) {
        if (obj == null || StringUtils.isBlankOrNull(obj.getCode())) return false;
        else if (obj.getScheduleStatus().equals("SCHEDULED") && obj.getPhraseType().equals("3") || obj.getCode().equals("CTW010100") || obj.getCode().equals("CTM010100"))
            return true;
        return false;
    }


}
