package com.lesports.msg.handler;

import com.google.common.base.Function;
import com.lesports.id.api.IdType;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.LeMessageBuilder;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.msg.service.EpisodeService;
import com.lesports.msg.service.LiveService;
import com.lesports.msg.util.MsgProducerConstants;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
@Component
public class LiveMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger logger = LoggerFactory.getLogger(LiveMessageHandler.class);

    @Resource
    private EpisodeService episodeService;
    @Resource
    private LiveService liveService;

    @Override
    Logger getLogger() {
        return logger;
    }

    @Override
    public void handle(LeMessage message) {
        if (message.getSource() == MessageSource.LETV_LIVE) {
            long liveId = message.getEntityId();
            long episodeId = SopsInternalApis.getEpisodeIdByLiveId(String.valueOf(liveId));
            if (episodeId > 0) {
                boolean result = SopsInternalApis.saveEpisodeByLiveId(episodeId, String.valueOf(liveId));
                if (!result) {
                    logger.error("fail to save episode by live : {} in sms", liveId);
                    return;
                }
                result = deleteEpisode(episodeId) & deleteLiveWebCache(liveId);
                if (result) {
                    logger.info("success handle live message {}", liveId);
                } else {
                    logger.error("fail to handle live message {}", liveId);
                }
                LeMessage leMessage = LeMessageBuilder.create().setSource(MessageSource.LETV_LIVE)
                        .setActionType(ActionType.UPDATE).setEntityId(episodeId).setIdType(IdType.EPISODE).build();
                SwiftMessageApis.sendMsgAsync(MsgProducerConstants.SWIFTQ_MESSAGE_CENTER_EXCHANGE, leMessage);
            }
//            SwiftMessageApis.sendMsgSync(MsgProducerConstants.SWIFTQ_MESSAGE_CENTER_EXCHANGE, message);
        }


    }

    private boolean deleteEpisode(final long episodeId) {
        return execute(episodeId, "live related episode", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Long aLong) {
                return episodeService.deleteEpisodeApiCache(aLong)
                        & episodeService.deleteSmsRealtimeWebEpisodeCache(episodeId);
            }
        });
    }


    private boolean deleteLiveWebCache(final long liveId) {
        return execute(liveId, "live web cache", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Long aLong) {
                return liveService.deleteLiveWebCache(liveId);
            }
        });
    }
}
