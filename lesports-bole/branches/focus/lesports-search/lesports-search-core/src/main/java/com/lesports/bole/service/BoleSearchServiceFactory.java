package com.lesports.bole.service;

import com.lesports.bole.index.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
@Component
public class BoleSearchServiceFactory {
    @Resource
    private BoleCompetitionService boleCompetitionService;
    @Resource
    private BoleCompetitorService boleCompetitorService;
    @Resource
    private BoleMatchService boleMatchService;
    @Resource
    private BoleNewsService boleNewsService;
    @Resource
    private EpisodeIndexService episodeIndexService;
    @Resource
    private MatchIndexService matchIndexService;
    @Resource
    private NewsIndexService newsIndexService;
    @Resource
    private VideoIndexService videoIndexService;
	@Resource
	private PlayerIndexService playerIndexService;

    public BoleSearchCrudService getService(Class clazz) {
        if (null == clazz) {
            return null;
        }
        if (EpisodeIndex.class == clazz) {
            return episodeIndexService;
        } else if (MatchIndex.class == clazz) {
            return matchIndexService;
        } else if (NewsIndex.class == clazz) {
            return newsIndexService;
        } else if (VideoIndex.class == clazz) {
            return videoIndexService;
        } else if (BoleMatchIndex.class == clazz) {
            return boleMatchService;
        } else if (BoleCompetitionIndex.class == clazz) {
            return boleCompetitionService;
        } else if (BoleCompetitorIndex.class == clazz) {
            return boleCompetitorService;
        } else if (BoleNewsIndex.class == clazz) {
            return boleNewsService;
        } else if (PlayerIndex.class == clazz) {
			return playerIndexService;
		}
        return null;
    }
}
