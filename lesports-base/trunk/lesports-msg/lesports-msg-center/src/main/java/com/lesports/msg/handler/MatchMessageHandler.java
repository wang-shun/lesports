package com.lesports.msg.handler;

import com.google.common.base.Function;
import com.lesports.bole.api.vo.TBMatch;
import com.lesports.id.api.IdType;
import com.lesports.msg.core.*;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.msg.service.BoleMatchService;
import com.lesports.msg.service.EpisodeService;
import com.lesports.msg.service.MatchService;
import com.lesports.msg.util.MsgProducerConstants;
import com.lesports.sms.client.*;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.Match;
import com.lesports.utils.CallerUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
@Component
public class MatchMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(MatchMessageHandler.class);

    @Resource
    private EpisodeService episodeService;

    @Resource
    private MatchService matchService;

    @Resource
    private BoleMatchService boleMatchService;

    @Override
    Logger getLogger() {
        return LOG;
    }

    @Override
    public void handle(LeMessage message) {
        long matchId = message.getEntityId();
        //Index match data
        Match tMatch = SbdsInternalApis.getMatchById(matchId);
        if (null != tMatch) {
            matchService.indexMatch(tMatch);
        }
        //Delete related episodes when match is deleted.
        if (message.getActionType() == ActionType.DELETE) {
            SopsInternalApis.deleteEpisodesWithMachId(matchId);
        }
        MessageContent content = message.getContent();
        if (null != content && content.getMessageEvent() == MessageEvent.MATCH_CREATE_EPISODE) {
            //根据match创建episode
            List<Long> episodeIds = SopsInternalApis.createEpisodes(matchId);
            LOG.info("create episodes : {} for match : {}.", episodeIds, matchId);
        } else if (null != content && content.getMessageEvent() == MessageEvent.DELETE_CACHE) {
            //delete rpc cache
            boolean re = SbdsInternalApis.deleteMatchRpcCache(matchId);
            LOG.info("delete match rpc cache for : {}, result : {}.", matchId, re);
            deleteEpisode(matchId);
        }
//        boolean boleResult = BoleInternalApis.updateBoleWithSms(matchId);
//        LOG.info("update bole match : {}, result : {}", matchId, boleResult);
//        if (boleResult) {
//            TBMatch tbMatch = BoleApis.getMatchBySmsId(matchId);
//            if (null != tbMatch) {
//                boleMatchService.indexBoleMatch(tbMatch.getId());
//            }
//        }
        deleteMatch(matchId);
        SwiftMessageApis.sendMsgAsync(MsgProducerConstants.SWIFTQ_MESSAGE_CENTER_EXCHANGE, message);
        //加载redis数据
        SbdsApis.getTDetailMatchById(matchId, CallerUtils.getDefaultCaller());
    }

    private void deleteEpisode(long matchId) {
        List<Episode> episodes = SopsInternalApis.getEpisodesByMid(matchId);
        if (CollectionUtils.isNotEmpty(episodes)) {
            for (Episode episode : episodes) {
                boolean re = SopsInternalApis.deleteEpisodeRpcCache(episode.getId());
                LOG.info("delete episode rpc cache for : {}, result : {}.", matchId, re);
                episodeService.deleteEpisodeApiCache(episode.getId());
                episodeService.deleteSmsRealtimeWebEpisodeCache(episode.getId());
            }
        }
    }

    private void deleteMatch(long matchId) {
        execute(matchId, "match", new Function<Long, Boolean>() {
            @Override
            public Boolean apply(Long aLong) {
                return matchService.deleteMatchApiCache(aLong);
            }
        });
        execute(matchId, "sis match", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable Long aLong) {
                return matchService.deleteSisMatch(aLong);
            }
        });
        execute(matchId, "sms web match", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(@Nullable Long aLong) {
                return matchService.deleteSmsRealtimeWebMatch(aLong);
            }
        });
    }
}
