package com.lesports.sms.data.adapter.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lesports.qmt.sbd.*;
import com.lesports.qmt.sbd.api.common.CompetitorType;
import com.lesports.qmt.sbd.api.common.GroundType;
import com.lesports.qmt.sbd.model.*;
import com.lesports.qmt.sbd.model.Competition;
import com.lesports.qmt.sbd.model.CompetitionSeason;
import com.lesports.qmt.sbd.model.Match;
import com.lesports.qmt.sbd.model.MatchReview;
import com.lesports.qmt.sbd.model.Team;
import com.lesports.sms.data.adapter.DefualtAdaptor;
import com.lesports.sms.data.model.commonImpl.DefaultMatchReviews;
import com.lesports.sms.data.process.impl.DefaultMatchReviewProcessor;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("defaultReviewAdaptor")
public class DefaultMatchReviewAdaptor extends DefualtAdaptor {
    private static Logger LOG = LoggerFactory.getLogger(DefaultMatchReviewAdaptor.class);
    @Resource
    DefaultMatchReviewProcessor matchReviewProcessor;

    public Boolean nextProcessor(PartnerType partnerType, Long csid, Object object) {
        try {
            DefaultMatchReviews.MatchReviewModel matchReviewModel = (DefaultMatchReviews.MatchReviewModel) object;
            Match currentMatch = SbdMatchInternalApis.getMatchByPartner(getPartner(matchReviewModel.getPartnerId(), partnerType));
            if (currentMatch == null) {
                LOG.warn("match :{} not exist", matchReviewModel.getPartnerId());
            //    return false;
            }
            CompetitionSeason competitionSeason = SbdCompetitionSeasonInternalApis.getCompetitionSeasonById(csid);
            if (competitionSeason == null) return false;
            Competition competition = SbdCompetitionInternalApis.getCompetitionById(competitionSeason.getCid());
            if (competition == null) return false;
            MatchReview matchReview = convertModelToMatchReview(partnerType, matchReviewModel);
            if (matchReview == null) {
                LOG.warn("adpter matchReview:{} fail", matchReviewModel.getPartnerId());
                return false;
            }
            matchReview.setId(currentMatch.getId());
            matchReview.setName(currentMatch.getName());
            matchReview.setMultiLangNames(currentMatch.getMultiLangNames());
            matchReview.setAllowCountries(competition.getAllowCountries());
            return matchReviewProcessor.process(matchReview);
        } catch (
                Exception e
                )

        {
            LOG.error("adaptor playerStat fail", e);
        }

        return false;
    }

    private MatchReview convertModelToMatchReview(PartnerType partnerType, DefaultMatchReviews.MatchReviewModel matchReviewModel) {
        MatchReview matchReview = new MatchReview();
        List<Long> confrontationsMatchId = convertModelToLongList(partnerType, matchReviewModel.getConfrontationsList());
        if (CollectionUtils.isNotEmpty(confrontationsMatchId)) matchReview.setConfrontations(confrontationsMatchId);
        Set<MatchReview.MatchInfo> sets = Sets.newHashSet();
        MatchReview.MatchInfo homeMatchInfo = convertModelToMatchInfo(partnerType, matchReviewModel.getHomeTeamId(), GroundType.HOME, matchReviewModel.getHomeCompetitorBeforeMatchInfoList(), matchReviewModel.getHomeCompetitorAfterMatchInfoList());
        MatchReview.MatchInfo awayMatchInfo = convertModelToMatchInfo(partnerType, matchReviewModel.getAwayTeamId(), GroundType.AWAY, matchReviewModel.getAwayCompetitorBeforeMatchInfoList(), matchReviewModel.getAwayCompetitorAfterMatchInfoList());
        if (homeMatchInfo != null) sets.add(homeMatchInfo);
        if (awayMatchInfo != null) sets.add(awayMatchInfo);
        matchReview.setMatchInfos(sets);
        return matchReview;
    }

    private MatchReview.MatchInfo convertModelToMatchInfo(PartnerType partnerType, String partnerId, GroundType groundType, List<DefaultMatchReviews.MatchReviewModel.MatchInfoModel> beforeModels, List<DefaultMatchReviews.MatchReviewModel.MatchInfoModel> AfterModels) {
        Team currentTeam = SbdTeamInternalApis.getTeamByPartner(getPartner(partnerId, partnerType));
        MatchReview.MatchInfo matchInfo = new MatchReview.MatchInfo();
        if (currentTeam == null) return null;
        matchInfo.setCompetitorId(currentTeam.getId());
        matchInfo.setGround(groundType);
        matchInfo.setType(CompetitorType.TEAM);
        matchInfo.setNearMatches(convertModelToLongList(partnerType, beforeModels));
        matchInfo.setAfterMatches(convertModelToLongList(partnerType, AfterModels));
        return matchInfo;
    }


    private List<Long> convertModelToLongList(PartnerType partnerType, List<DefaultMatchReviews.MatchReviewModel.MatchInfoModel> matchInfoModels) {
        List<Long> matchIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(matchInfoModels)) {
            for (DefaultMatchReviews.MatchReviewModel.MatchInfoModel infoModel : matchInfoModels) {
                if (infoModel.getMatchPartnerId() == null) continue;
                Match match = SbdMatchInternalApis.getMatchByPartner(getPartner(infoModel.getMatchPartnerId(), partnerType));
                if (match == null) continue;
                matchIds.add(match.getId());
            }
        }
        return matchIds;
    }

}