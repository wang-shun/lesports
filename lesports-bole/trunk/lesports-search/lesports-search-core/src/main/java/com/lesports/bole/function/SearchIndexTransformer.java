package com.lesports.bole.function;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.lesports.bole.Constants;
import com.lesports.bole.api.vo.TBCompetition;
import com.lesports.bole.api.vo.TBCompetitionSeason;
import com.lesports.bole.api.vo.TBCompetitor;
import com.lesports.bole.api.vo.TBMatch;
import com.lesports.bole.index.*;
import com.lesports.sms.api.common.Platform;
import com.lesports.sms.client.BoleApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.Episode;
import com.lesports.sms.model.Match;
import com.lesports.sms.model.News;
import com.lesports.sms.model.Video;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
public class SearchIndexTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(SearchIndexTransformer.class);

    public static Class getClass(String index, String type) {
        if (Constants.Index.sms.equals(index)) {
            switch (type) {
                case Constants.Type.episodes:
                    return EpisodeIndex.class;
                case Constants.Type.matches:
                    return MatchIndex.class;
                case Constants.Type.news:
                    return NewsIndex.class;
                case Constants.Type.videos:
                    return VideoIndex.class;
				case Constants.Type.players:
					return PlayerIndex.class;
            }
        } else if (Constants.Index.bole.equals(index)) {
            switch (type) {
                case Constants.Type.boleMatches:
                    return BoleMatchIndex.class;
                case Constants.Type.boleCompetitions:
                    return BoleCompetitionIndex.class;
                case Constants.Type.boleCompetitors:
                    return BoleCompetitorIndex.class;
            }
        } else if (Constants.Index.bole_news.equals(index)) {
            switch (type) {
                case Constants.Type.imageText:
                    return BoleNewsIndex.class;
            }
        }
        return null;
    }

    public static SearchIndex getEmptyEntity(String index, String type) {
        Class clazz = getClass(index, type);
        if (null == clazz) {
            return null;
        }
        try {
            return (SearchIndex) clazz.newInstance();
        } catch (InstantiationException e) {
            LOG.error("{}", e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    /**
     * 根据索引数据类型构建索引数据
     *
     * @param index
     * @param type
     * @param id
     * @return
     */
    public static SearchIndex getEntity(final String index, String type, String id) {
        Class clazz = getClass(index, type);
        if (null == clazz) {
            return null;
        }
        long idLong = LeNumberUtils.toLong(id);
        if (EpisodeIndex.class == clazz) {
            return getEpisodeIndex(idLong);
        } else if (MatchIndex.class == clazz) {
            return getMatchIndex(idLong);
        } else if (NewsIndex.class == clazz) {
            return getNewsIndex(idLong);
        } else if (VideoIndex.class == clazz) {
            return getVideoIndex(idLong);
        } else if (BoleMatchIndex.class == clazz) {
            return getBoleMatchIndex(idLong);
        } else if (BoleCompetitionIndex.class == clazz) {
            return getBoleCompetitionIndex(idLong);
        } else if (BoleCompetitorIndex.class == clazz) {
            return getBoleCompetitorIndex(idLong);
        }
        return null;
    }

    /**
     * 构建伯乐赛事索引
     * @param idLong
     * @return
     */
    private static BoleCompetitionIndex getBoleCompetitionIndex(long idLong) {
        TBCompetition tbCompetition = BoleApis.getCompetitionById(idLong);
        if (null == tbCompetition) {
            return null;
        }
        BoleCompetitionIndex boleCompetitionIndex = new BoleCompetitionIndex();
        boleCompetitionIndex.setId(idLong);
        boleCompetitionIndex.setName(tbCompetition.getName());
        boleCompetitionIndex.setStatus(tbCompetition.getStatus().getValue());
        boleCompetitionIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        return boleCompetitionIndex;
    }

    /**
     * 构建伯乐对阵双方索引
     * @param idLong
     * @return
     */
    private static BoleCompetitorIndex getBoleCompetitorIndex(long idLong) {
        TBCompetitor tbCompetitor = BoleApis.getCompetitorById(idLong);
        if (null == tbCompetitor) {
            return null;
        }
        BoleCompetitorIndex boleCompetitorIndex = new BoleCompetitorIndex();
        boleCompetitorIndex.setId(idLong);
        boleCompetitorIndex.setName(tbCompetitor.getName());
        boleCompetitorIndex.setStatus(tbCompetitor.getStatus().getValue());
        boleCompetitorIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleCompetitorIndex.setGameName(tbCompetitor.getGameName());
        if (null != tbCompetitor.getType()) {
            boleCompetitorIndex.setType(tbCompetitor.getType().getValue());
        }
        return boleCompetitorIndex;
    }

    /**
     * 获取节目索引数据
     *
     * @param idLong
     * @return
     */
    private static EpisodeIndex getEpisodeIndex(long idLong) {
        Episode tComboEpisode = SopsInternalApis.getEpisodeById(idLong);
        if (null == tComboEpisode) {
            return null;
        }
        EpisodeIndex episodeIndex = new EpisodeIndex();
        episodeIndex.setId(tComboEpisode.getId());
        episodeIndex.setName(tComboEpisode.getName());
        episodeIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        episodeIndex.setCompetitorIds(tComboEpisode.getCompetitorIds());
        episodeIndex.setTagIds(tComboEpisode.getTagIds());
        return episodeIndex;
    }

    /**
     * 构建比赛索引数据
     *
     * @param idLong
     * @return
     */
    private static MatchIndex getMatchIndex(long idLong) {
        Match tMatch = SbdsInternalApis.getMatchById(idLong);
        if (null == tMatch) {
            return null;
        }
        MatchIndex matchIndex = new MatchIndex();
        matchIndex.setId(tMatch.getId());
        matchIndex.setName(tMatch.getName());
        matchIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        return matchIndex;
    }

    /**
     * 构建新闻索引数据
     *
     * @param idLong
     * @return
     */
    private static NewsIndex getNewsIndex(long idLong) {
        News tNews = SopsInternalApis.getNewsById(idLong);
        if (null == tNews) {
            return null;
        }
        NewsIndex newsIndex = new NewsIndex();
        newsIndex.setId(tNews.getId());
        newsIndex.setName(tNews.getName());
        newsIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        newsIndex.setMids(Lists.newArrayList(tNews.getMids()));
        newsIndex.setTagIds(tNews.getTagIds());
        if (null != tNews.getType()) {
            newsIndex.setNewsType(tNews.getType().getValue());
        }
        if (null != tNews.getOnline()) {
            newsIndex.setOnlineStatus(tNews.getOnline().getValue());
        }
        newsIndex.setPublishAt(tNews.getPublishAt());
        newsIndex.setDeleted(tNews.getDeleted());
        if (CollectionUtils.isNotEmpty(tNews.getPlatforms())) {
            newsIndex.setPlatforms(Lists.transform(Lists.newArrayList(tNews.getPlatforms()), new Function<Platform, Integer>() {
                @Nullable
                @Override
                public Integer apply(Platform input) {
                    return input.getValue();
                }
            }));
        }
        return newsIndex;
    }

    /**
     * 构建视频索引数据
     *
     * @param idLong
     * @return
     */
    private static VideoIndex getVideoIndex(long idLong) {
        Video tVideo = SopsInternalApis.getVideoById(idLong);
        if (null == tVideo) {
            return null;
        }
        VideoIndex videoIndex = new VideoIndex();
        videoIndex.setId(idLong);
        videoIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        videoIndex.setName(tVideo.getName());
        videoIndex.setType(tVideo.getType().getValue());
        return videoIndex;
    }

    /**
     * 构建伯乐比赛索引数据
     *
     * @param id
     * @return
     */
    private static BoleMatchIndex getBoleMatchIndex(long id) {
        TBMatch match = BoleApis.getMatchById(id);
        if (null == match) {
            return null;
        }
        BoleMatchIndex boleMatchIndex = new BoleMatchIndex();
        boleMatchIndex.setStartTime(match.getStartTime());
        boleMatchIndex.setCompetitionId(match.getCid());
        boleMatchIndex.setCompetitionSeasonId(match.getCsid());
        boleMatchIndex.setCompetitorIds(match.getCompetitorIds());
        boleMatchIndex.setName(match.getName());
        boleMatchIndex.setSmsId(match.getSmsId());
        boleMatchIndex.setStatus(match.getStatus().getValue());
        boleMatchIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        boleMatchIndex.setId(match.getId());
        if (match.getCid() > 0) {
            TBCompetition tbCompetition = BoleApis.getCompetitionById(match.getCid());
            if (null != tbCompetition) {
                boleMatchIndex.setCompetitionName(tbCompetition.getName());
            }
        }
        if (match.getCsid() > 0) {
            TBCompetitionSeason tbCompetitionSeason = BoleApis.getCompetitionSeasonById(match.getCsid());
            if (null != tbCompetitionSeason) {
                boleMatchIndex.setCompetitionSeasonName(tbCompetitionSeason.getName());
            }
        }
        if (CollectionUtils.isNotEmpty(match.getCompetitorIds())) {
            List<String> competitorNames = new ArrayList<>(2);
            for (Long competitorId : match.getCompetitorIds()) {
                TBCompetitor tbCompetitor = BoleApis.getCompetitorById(competitorId);
                if (null != tbCompetitor) {
                    competitorNames.add(tbCompetitor.getName());
                }
            }
            boleMatchIndex.setCompetitorNames(competitorNames);
        }
        return boleMatchIndex;
    }
}
