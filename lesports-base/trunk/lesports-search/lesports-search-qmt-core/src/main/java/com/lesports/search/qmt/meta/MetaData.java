package com.lesports.search.qmt.meta;

import com.lesports.id.api.IdType;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.index.config.DictEntryIndex;
import com.lesports.search.qmt.index.config.TagIndex;
import com.lesports.search.qmt.index.sbc.AlbumIndex;
import com.lesports.search.qmt.index.sbc.EpisodeIndex;
import com.lesports.search.qmt.index.sbc.NewsIndex;
import com.lesports.search.qmt.index.sbc.ProgramAlbumIndex;
import com.lesports.search.qmt.index.sbc.ResourceIndex;
import com.lesports.search.qmt.index.sbc.TopicIndex;
import com.lesports.search.qmt.index.sbc.VideoIndex;
import com.lesports.search.qmt.index.sbd.CompetitionIndex;
import com.lesports.search.qmt.index.sbd.CompetitionSeasonIndex;
import com.lesports.search.qmt.index.sbd.MatchIndex;
import com.lesports.search.qmt.index.sbd.PlayerIndex;
import com.lesports.search.qmt.index.sbd.TeamIndex;
import com.lesports.search.qmt.index.transcode.LiveToVideoTaskIndex;
import com.lesports.search.qmt.index.transcode.VideoTranscodeTaskIndex;

public class MetaData {

	public static final String QMT_CONFIG = "qmt_config";

	public static final String QMT_SBC = "qmt_sbc";

	public static final String QMT_SBD = "qmt_sbd";

	public static final String QMT_TRANSCODE = "qmt_transcode";
	
	// config
	public static final String DICT_ENTRY_INDEX = QMT_CONFIG+"_dict_entry";

	public static final String TAG_INDEX = QMT_CONFIG+"_tag";

	// sbc
	public static final String ALBUM_INDEX = QMT_SBC+"_album";

	public static final String PROGRAM_ALBUM_INDEX = QMT_SBC+"_program_album";

	public static final String EPISODE_INDEX = QMT_SBC+"_episode";

	public static final String NEWS_INDEX = QMT_SBC+"_news";

	public static final String RESOURCE_INDEX = QMT_SBC+"_resource";

	public static final String TOPIC_INDEX = QMT_SBC+"_topic";

	public static final String VIDEO_INDEX = QMT_SBC+"_video";

	// sbd
	public static final String COMPETITION_INDEX = QMT_SBD+"_competition";

	public static final String COMPETITION_SEASON_INDEX = QMT_SBD+"_competition_season";

	public static final String MATCH_INDEX = QMT_SBD+"_match";

	public static final String PLAYER_INDEX = QMT_SBD+"_player";

	public static final String TEAM_INDEX = QMT_SBD+"_team";

	// transcode
	public static final String TRANSCODE_VIDEO_TASK_INDEX = QMT_TRANSCODE+"_transcode_video_task";

	public static final String TRANSCODE_LIVE_TASK_INDEX = QMT_TRANSCODE+"_transcode_live_task";

	public static final String[] QMT_INDEXES = new String[] { DICT_ENTRY_INDEX, TAG_INDEX, ALBUM_INDEX,
			PROGRAM_ALBUM_INDEX, EPISODE_INDEX, NEWS_INDEX, RESOURCE_INDEX, TOPIC_INDEX, VIDEO_INDEX, COMPETITION_INDEX,
			COMPETITION_SEASON_INDEX, MATCH_INDEX, PLAYER_INDEX, TEAM_INDEX, TRANSCODE_VIDEO_TASK_INDEX,
			TRANSCODE_LIVE_TASK_INDEX };

	public enum IndexType {

		DICT_ENTRY(DICT_ENTRY_INDEX, IdType.DICT_ENTRY, DictEntryIndex.class),
		TAG(TAG_INDEX, IdType.TAG, TagIndex.class),
		
		ALBUM(ALBUM_INDEX, IdType.ALBUM, AlbumIndex.class),
		PROGRAM_ALBUM(PROGRAM_ALBUM_INDEX, IdType.PROGRAM_ALBUM, ProgramAlbumIndex.class),
		EPISODE(EPISODE_INDEX, IdType.EPISODE, EpisodeIndex.class),
		NEWS(NEWS_INDEX, IdType.NEWS, NewsIndex.class),
		RESOURCE(RESOURCE_INDEX, IdType.RESOURCE, ResourceIndex.class),
		TOPIC(TOPIC_INDEX, IdType.TOPIC, TopicIndex.class),
		VIDEO(VIDEO_INDEX, IdType.VIDEO, VideoIndex.class),

		COMPETITION(COMPETITION_INDEX, IdType.COMPETITION, CompetitionIndex.class),
		COMPETITION_SEASON(COMPETITION_SEASON_INDEX, IdType.COMPETITION_SEASON, CompetitionSeasonIndex.class),
		MATCH(MATCH_INDEX, IdType.MATCH, MatchIndex.class),
		PLAYER(PLAYER_INDEX, IdType.PLAYER, PlayerIndex.class),
		TEAM(TEAM_INDEX, IdType.TEAM, TeamIndex.class),
		
		TRANSCODE_VIDEO_TASK(TRANSCODE_VIDEO_TASK_INDEX, IdType.TRANSCODE_VIDEO_TASK,
				VideoTranscodeTaskIndex.class), 
		TRANSCODE_LIVE_TASK(TRANSCODE_LIVE_TASK_INDEX, IdType.TRANSCODE_LIVE_TASK,
						LiveToVideoTaskIndex.class);

		public final String index;

		public final String type;

		private IdType idType;
		
		private Class<? extends Indexable> indexClass;

		private IndexType(String index,IdType idType,Class<? extends Indexable> indexClass) {
			this.index = index;
			this.type = idType.name();
			this.idType = idType;
			this.indexClass=indexClass;
		}

		public String getIndex() {
			return index;
		}

		public IdType getIdType() {
			return idType;
		}
		
		public Class<? extends Indexable> getIndexClass() {
			return indexClass;
		}

		public static IndexType valueOf(IdType idType) {
			switch (idType) {
			// config
			case DICT_ENTRY:
				return DICT_ENTRY;
			case TAG:
				return TAG;
			// sbc
			case ALBUM:
				return ALBUM;
			case PROGRAM_ALBUM:
				return PROGRAM_ALBUM;
			case EPISODE:
				return EPISODE;
			case NEWS:
				return NEWS;
			case RESOURCE:
				return RESOURCE;
			case TOPIC:
				return TOPIC;
			case VIDEO:
				return VIDEO;
			// sbd
			case COMPETITION:
				return COMPETITION;
			case COMPETITION_SEASON:
				return COMPETITION_SEASON;
			case MATCH:
				return MATCH;
			case PLAYER:
				return PLAYER;
			case TEAM:
				return TEAM;
			case TRANSCODE_VIDEO_TASK:
				return TRANSCODE_VIDEO_TASK;
			case TRANSCODE_LIVE_TASK:
				return TRANSCODE_LIVE_TASK;
			default:
				return null;
			}
		}
	}
}
