package com.lesports.sms.data.processor.olympic;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.CompetitorType;
import com.lesports.sms.api.common.Gender;
import com.lesports.sms.api.common.MedalType;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.model.olympic.MedalDetail;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.model.CompetitionSeason;
import com.lesports.sms.model.DictEntry;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.Medal;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * lesports-projects
 *
 * @author qiaohongxin
 * @since 16-3-16
 */
@Component
public class MedalDetailProcessor extends AbstractProcessor implements BeanProcessor<MedalDetail> {
    private static Logger LOG = LoggerFactory.getLogger(MedalDetailProcessor.class);
    private Map<String, MedalType> medalTypeMap = ImmutableMap.of("ME_GOLD", MedalType.GOLD, "ME_SILVER", MedalType.SILVER, "ME_BRONZE", MedalType.BRONZE);
    private Map<String, Gender> genderMap = ImmutableMap.of("M", Gender.MALE, "W", Gender.FEMALE, "X", Gender.FEMALE);

    @Override
    public Boolean process(String fileType, MedalDetail obj) {
        boolean result = true;
        if (obj == null || obj.getDisciplineCode() == null || obj.getGenderCode() == null || obj.getEventCode() == null || obj.getMedalList() == null || obj.getMedalList().isEmpty())
            return false;
        try {
            CompetitionSeason competitionSeason = SbdsInternalApis.getLatestCompetitionSeasonByCid(Constants.OG_CID);
            if (null == competitionSeason) {
                LOG.warn("can not find relative event, uniqueTournamentId:{},name:{}, season : {}", Constants.OG_CID, "2016");
                return false;
            }
            DictEntry discipline = getCommonDictWithCache(Constants.DICT_NAME_DISCIPLINE, obj.getDisciplineCode().substring(0, 2) + "$");
            DictEntry gameSType = getCommonDictWithCache(Constants.DICT_NAME_EVENT, obj.getDisciplineCode().substring(0, 2) + obj.getGenderCode() + obj.getEventCode() + "000");
            if (gameSType == null) {
                LOG.warn("Medal Parser : gameSType get fail : {}", obj.getDisciplineCode());
                return true;
            }
            List<Long> oldIds = SbdsInternalApis.getMedalIdsByGameStype(gameSType.getId());
            List<Long> newIds = Lists.newArrayList();
            for (MedalDetail.EventMedal medal : obj.getMedalList()) {
                try {
                    //获取奖牌的比赛
                    MedalType medalType = medalTypeMap.get(medal.getMedalCode());
                    Long matchId = SbdsInternalApis.getMatchIdByGameStypeAndMedalType(gameSType.getId(), medalType);
                    if (matchId <= 0) {
                        matchId = SbdsInternalApis.getMatchIdByGameStypeAndMedalType(gameSType.getId(), MedalType.GOLD);
                    }
                    Long competitorId = getCompetitorId(medal.getCompetitorCode(), medal.getCompetitorType());
                    if(matchId<=0){
                        LOG.error(" MEDAL match is not exist,match id : {}", matchId, competitorId);
                    }
                    if (matchId <= 0 || competitorId <= 0) {
                        LOG.warn("medal information is not valid, match id : {}, competitor id : {}.", matchId, competitorId);
                        continue;
                    }
                    Medal medalDetail = SbdsInternalApis.getMedalByGameStypeAndCompetitorId(gameSType.getId(), competitorId);
                    if (medalDetail == null) {
                        medalDetail = new Medal();
                        medalDetail.setCid(Constants.OG_CID);
                        medalDetail.setCsid(competitionSeason.getId());
                        medalDetail.setGameFType(discipline.getParentId());
                        medalDetail.setDisciplineId(discipline.getId());
                        medalDetail.setGameSType(gameSType.getId());
                        medalDetail.setDeleted(false);
                    }
                    Match currentMatch = SbdsInternalApis.getMatchById(matchId);
                    medalDetail.setCompetitorType(medal.getCompetitorType().equals("A") ? CompetitorType.PLAYER : CompetitorType.TEAM);
                    medalDetail.setCompetitorId(competitorId);
                    medalDetail.setCompetitorName(getCompetitorName(competitorId, medal.getCompetitorType()));
                    medalDetail.setResult(getCompetitorResult(matchId, competitorId));
                    DictEntry country = getCommonDictWithCache(Constants.DICT_NAME_COUNTRY, medal.getCountry());
                    medalDetail.setOrganisationId(country == null ? 0L : country.getId());
                    medalDetail.setOrganisationName(country == null ? "" : country.getName());
                    medalDetail.setGender(genderMap.get(obj.getGenderCode()));
                    medalDetail.setDeleted(false);
                    medalDetail.setMedalMatchId(matchId);
                    medalDetail.setMedalAhieveTime(currentMatch.getEndTime() == null ? currentMatch.getStartTime() : currentMatch.getEndTime());
                    medalDetail.setMedalType(medalTypeMap.get(medal.getMedalCode()));
                    Long id = SbdsInternalApis.saveMedal(medalDetail);
                    if (id > 0) {
                        LOG.warn("medal is saved  gameSTypeId : {},medalType:{}",gameSType.getId(), medal.getMedalCode());
                        newIds.add(id);
                        result = result & true;
                    }
                } catch (Exception e) {
                    LOG.warn("medal is saved fail and medal type : {}", medal.getMedalCode(), e);
                    continue;
                }
            }
            if (result && CollectionUtils.isNotEmpty(oldIds)) {
                for (Long oldId : oldIds) {
                    if (newIds.contains(oldId)) continue;
                    else {
                        SbdsInternalApis.deleteMedal(oldId);
                        LOG.info("old Medal is delete,id:{}", oldIds);
                    }
                }
            }
            LOG.warn("medal is saved true and medal type : ");
            return result;
        } catch (Exception e) {
            LOG.warn("code is saved fail,code:{}", obj.getDisciplineCode(), e);
            return false;
        }
    }
}
