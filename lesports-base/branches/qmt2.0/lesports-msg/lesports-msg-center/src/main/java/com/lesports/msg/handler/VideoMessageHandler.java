package com.lesports.msg.handler;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.lesports.api.common.CallerParam;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.service.EpisodeService;
import com.lesports.msg.service.NewsService;
import com.lesports.msg.service.VideoService;
import com.lesports.msg.util.LeExecutors;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.News;
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
public class VideoMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(VideoMessageHandler.class);

    @Resource
    private EpisodeService episodeService;

    @Resource
    private VideoService videoService;

    @Resource
    private NewsService newsService;

    @Override
    Logger getLogger() {
        return LOG;
    }

    @Override
    public void handle(final LeMessage message) {
        final long videoId = message.getEntityId();
        boolean result = true;
        if (message.getSource() == MessageSource.LETV_MMS || message.getSource() == MessageSource.LETV_MMS_HK) {
            final CallerParam callerParam = getCaller(message.getSource());
            if (message.getActionType() == ActionType.DELETE) {
                result = SopsInternalApis.deleteVideo(videoId, callerParam);
            } else{
                News news = SopsInternalApis.getNewsByVid(videoId, callerParam);
                if (null != news) {
                    result = LeExecutors.executeWithRetry(1, 1000, new Predicate<Long>() {
                        @Override
                        public boolean apply(@Nullable Long input) {
                            return SopsInternalApis.saveVideoById(videoId, callerParam);
                        }
                    }, videoId, "save video");
                }
            }
        }
        if (!result) {
            LOG.warn("fail handle video message, fail to save video id : {}", videoId);
            return;
        } else {
            //delete video cache
            result = deleteVideo(videoId) && deleteSisWebOld(videoId);
            //delete news cache
            List<News> newsList = SopsInternalApis.getNewsByVid(videoId);
            if (CollectionUtils.isNotEmpty(newsList)) {
                for (News news : newsList) {
                    if (null != news) {
                        result = deleteNews(news.getId());
                        newsService.indexNews(news.getId());
                    } else {
                        LOG.info("msm video {} has no news in db, will not delete news.", videoId);
                    }
                }
            }

            //delete episode cache
            List<Long> episodeIds = SopsInternalApis.getEpisodeIdsByVideoId(videoId);
            if (CollectionUtils.isNotEmpty(episodeIds)) {
                for (long episodeId : episodeIds) {
                    deleteEpisode(episodeId);
                }
            }
            videoService.indexVideo(videoId);
            LOG.info("success handle video message {}, result : {}", videoId, result);
        }

    }

    private boolean deleteNews(final long newsId) {
        return execute(newsId, "video related news", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Long aLong) {
                return newsService.deleteNewsApiCache(newsId);
            }
        });
    }

    private boolean deleteEpisode(final long episodeId) {
        return execute(episodeId, "video related episode", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Long aLong) {
                return episodeService.deleteEpisodeApiCache(aLong)
                        & episodeService.deleteSmsRealtimeWebEpisodeCache(episodeId);
            }
        });
    }

    private boolean deleteVideo(long videoId) {
        return execute(videoId, "video", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Long aLong) {
                return videoService.deleteVideoApiCache(aLong);
            }
        });
    }

    private boolean deleteSisWebOld(long videoId) {
        return execute(videoId, "sis web old", new Function<Long, Boolean>() {
            @Nullable
            @Override
            public Boolean apply(Long aLong) {
                return videoService.deleteVideoInSis(aLong);
            }
        });
    }
}
