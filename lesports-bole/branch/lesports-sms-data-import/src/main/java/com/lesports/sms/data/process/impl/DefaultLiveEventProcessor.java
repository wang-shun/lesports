package com.lesports.sms.data.process.impl;

import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.qmt.sbd.SbdMatchInternalApis;
import com.lesports.qmt.sbd.model.CompetitorSeasonStat;
import com.lesports.qmt.sbd.model.MatchAction;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.client.TextLiveInternalApis;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.TextLive;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("liveEventProcessor")
public class DefaultLiveEventProcessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultLiveEventProcessor.class);

    public Boolean process(Object object) {
        List<MatchAction> matchActionList = (List<MatchAction>) object;
        try {
            if (CollectionUtils.isEmpty(matchActionList)) {
                LOG.warn("Match Action, List is empty");
            }
            //begin jugde whether the mid has pbp,if not conver the action ,or add the actions
            Episode currentEpisode = SopsInternalApis.getEpisodesByMidAndCountryAndLanguage(matchActionList.get(0).getMid(), CountryCode.CN, LanguageCode.ZH_CN);
            if (null == currentEpisode) {
                LOG.warn("the match:{} is not have the right eposide", matchActionList.get(0).getMid());
                return false;
            }
            InternalQuery query = new InternalQuery();
            query.addCriteria(new InternalCriteria("eid", "eq", currentEpisode.getId()));
            query.addCriteria(new InternalCriteria("type", "eq", "PLAY_BY_PLAY"));
            query.addCriteria(new InternalCriteria("deleted", "eq", false));
            List<TextLive> lives = TextLiveInternalApis.getTextLivesByQuery(query);
            // th condition that thecurrent match not have PBPText,so delete all the old actions and recreat news
            if (CollectionUtils.isEmpty(lives)) {
                List<MatchAction> matchActions = SbdMatchInternalApis.getMatchActionsByMid(matchActionList.get(0).getMid());
                if (CollectionUtils.isNotEmpty(matchActions)) {
                    Iterator iterator1 = matchActions.iterator();
                    while (iterator1.hasNext()) {
                        MatchAction matchAction = (MatchAction) iterator1.next();
                        SbdMatchInternalApis.deleteMatchAction(matchAction.getId());
                    }
                }
                LOG.warn("old match action are all deleted,mid:{}", matchActionList.get(0).getId());
            }
            //create new actions
            Iterator iterator1 = matchActionList.iterator();
            while (iterator1.hasNext()) {
                MatchAction newMatchAction = (MatchAction) iterator1.next();
                SbdMatchInternalApis.saveMatchAction(newMatchAction);
            }
            LOG.warn("match action are all updated,mid:{}", matchActionList.get(0).getId());
            return true;
        } catch (Exception e) {
            LOG.error("MATCH ACTION exception", e);
            return false;
        }
    }
}





