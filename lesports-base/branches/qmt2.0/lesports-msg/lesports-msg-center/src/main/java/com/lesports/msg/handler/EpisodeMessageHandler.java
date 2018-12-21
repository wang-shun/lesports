package com.lesports.msg.handler;

import client.SopsApis;
import com.lesports.msg.core.Field;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageContent;
import com.lesports.msg.core.MessageEvent;
import com.lesports.msg.producer.CloudSwiftMessageApis;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.msg.service.EpisodeService;
import com.lesports.msg.service.LiveService;
import com.lesports.msg.service.MatchService;
import com.lesports.msg.util.MsgProducerConstants;
import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.api.vo.TLiveStream;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.Episode;
import com.lesports.utils.CallerUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * User: ellios
 * Time: 15-6-28 : 下午10:07
 */
@Component
public class EpisodeMessageHandler extends AbstractHandler implements IHandler {

    private static final Logger logger = LoggerFactory.getLogger(EpisodeMessageHandler.class);

    @Resource
    private EpisodeService episodeService;
    @Resource
    private MatchService matchService;
    @Resource
    private LiveService liveService;

    @Override
    public void handle(LeMessage message) {
        long episodeId = message.getEntityId();
        boolean result = episodeService.deleteEpisodeApiCache(episodeId)
                & episodeService.deleteSmsRealtimeWebEpisodeCache(episodeId);
        logger.info("delete episode cache for {}, result {}.", episodeId, result);
        MessageContent content = message.getContent();
        Episode episode = SopsInternalApis.getEpisodeById(episodeId);
        if (null != content && null != content.getMessageEvent() && content.getMessageEvent() == MessageEvent.STATUS_CHANGED
                || null != content && CollectionUtils.isNotEmpty(content.getFields()) && content.getFields().contains(Field.LIVE_STREAMS)) {
            if (null != episode) {
                if (LeNumberUtils.toLong(episode.getMid()) > 0) {
                    result = matchService.deleteMatchPage(episode.getMid());
                    logger.info("delete match page for {} as episode {} status changed, result {}.", episode.getMid(), episodeId, result);
                }

                Set<Episode.LiveStream> liveStreams = episode.getLiveStreams();
                if (CollectionUtils.isNotEmpty(liveStreams)) {
                    for (Episode.LiveStream liveStream : liveStreams) {
                        result = liveService.deleteLivePage(LeNumberUtils.toLong(liveStream.getLiveId()));
                        logger.info("delete live page for {} as episode {} status changed, result {}.", liveStream.getLiveId(), episodeId, result);
                    }
                }
            }
        }

        SwiftMessageApis.sendMsgAsync(MsgProducerConstants.SWIFTQ_MESSAGE_CENTER_EXCHANGE, message);
        //索引episode数据
        if (null != episode) {
			if (episode.getIsSyncToCloud()) {
				CloudSwiftMessageApis.sendMsgAsync(message);
				logger.info("episode to cloud message body : {}", message);
			}
			episodeService.indexEpisode(episode);
        }
        //加载redis数据
        SopsApis.getTComboEpisodeById(episodeId, CallerUtils.getDefaultCaller());
    }

    @Override
    Logger getLogger() {
        return logger;
    }
}
