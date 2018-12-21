package com.lesports.search.qmt.utils;

import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.service.AlbumIndexService;
import com.lesports.search.qmt.service.CompetitionIndexService;
import com.lesports.search.qmt.service.CompetitionSeasonIndexService;
import com.lesports.search.qmt.service.DictEntryIndexService;
import com.lesports.search.qmt.service.EpisodeIndexService;
import com.lesports.search.qmt.service.LiveToVideoTaskIndexService;
import com.lesports.search.qmt.service.MatchIndexService;
import com.lesports.search.qmt.service.NewsIndexService;
import com.lesports.search.qmt.service.PlayerIndexService;
import com.lesports.search.qmt.service.ProgramAlbumIndexService;
import com.lesports.search.qmt.service.ResourceIndexService;
import com.lesports.search.qmt.service.SearchCrudService;
import com.lesports.search.qmt.service.TagIndexService;
import com.lesports.search.qmt.service.TeamIndexService;
import com.lesports.search.qmt.service.TopListIndexService;
import com.lesports.search.qmt.service.TopicIndexService;
import com.lesports.search.qmt.service.VideoIndexService;
import com.lesports.search.qmt.service.VideoTranscodeTaskIndexService;

/**
 * @author sunyue7
 *
 */
@Component
public class SearchServiceFactory {

	@Resource
	private CompetitionIndexService competitionIndexService;

	@Resource
	private CompetitionSeasonIndexService competitionSeasonIndexService;

	@Resource
	private EpisodeIndexService episodeIndexService;

	@Resource
	private MatchIndexService matchIndexService;

	@Resource
	private NewsIndexService newsIndexService;

	@Resource
	private ResourceIndexService resourceIndexService;

	@Resource
	private VideoIndexService videoIndexService;

	@Resource
	private PlayerIndexService playerIndexService;

	@Resource
	private TeamIndexService teamIndexService;

	@Resource
	private TopListIndexService topListIndexService;

	@Resource
	private TagIndexService tagIndexService;

	@Resource
	private DictEntryIndexService dictEntryIndexService;

	@Resource
	private TopicIndexService topicIndexService;

	@Resource
	private AlbumIndexService albumIndexService;

	@Resource
	private ProgramAlbumIndexService programAlbumIndexService;

	@Resource
	private VideoTranscodeTaskIndexService videoTranscodeTaskIndexService;

	@Resource
	private LiveToVideoTaskIndexService liveToVideoTaskIndexService;

	private HashMap<IndexType, SearchCrudService<? extends Indexable, Long>> serviceMap;

	@PostConstruct
	protected void init() {
		serviceMap = new HashMap<IndexType, SearchCrudService<? extends Indexable, Long>>();
		serviceMap.put(IndexType.COMPETITION, competitionIndexService);
		serviceMap.put(IndexType.COMPETITION_SEASON, competitionSeasonIndexService);
		serviceMap.put(IndexType.EPISODE, episodeIndexService);
		serviceMap.put(IndexType.MATCH, matchIndexService);
		serviceMap.put(IndexType.NEWS, newsIndexService);
		serviceMap.put(IndexType.VIDEO, videoIndexService);
		serviceMap.put(IndexType.PLAYER, playerIndexService);
		serviceMap.put(IndexType.RESOURCE, resourceIndexService);
		serviceMap.put(IndexType.TEAM, teamIndexService);
		serviceMap.put(IndexType.ALBUM, albumIndexService);
		serviceMap.put(IndexType.PROGRAM_ALBUM, programAlbumIndexService);
		serviceMap.put(IndexType.TAG, tagIndexService);
		serviceMap.put(IndexType.DICT_ENTRY, dictEntryIndexService);
		serviceMap.put(IndexType.TOPIC, topicIndexService);
		serviceMap.put(IndexType.TOP_LIST, topListIndexService);
		serviceMap.put(IndexType.TRANSCODE_VIDEO_TASK, videoTranscodeTaskIndexService);
		serviceMap.put(IndexType.TRANSCODE_LIVE_TASK, liveToVideoTaskIndexService);
	}

	public SearchCrudService<? extends Indexable, Long> getService(MetaData.IndexType indexType) {
		return serviceMap.get(indexType);
	}
}
