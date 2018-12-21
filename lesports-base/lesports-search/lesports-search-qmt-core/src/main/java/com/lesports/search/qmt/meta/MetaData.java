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
import com.lesports.search.qmt.index.sbd.TopListIndex;
import com.lesports.search.qmt.index.transcode.LiveToVideoTaskIndex;
import com.lesports.search.qmt.index.transcode.VideoTranscodeTaskIndex;

public class MetaData {

	public static final String QMT_INDEX_CONFIG = "qmt_config";

	public static final String QMT_INDEX_SBC = "qmt_sbc";

	public static final String QMT_INDEX_SBD = "qmt_sbd";

	public static final String QMT_INDEX_TRANSCODE = "qmt_transcode";

	public enum Index {

		QMT_CONFIG(QMT_INDEX_CONFIG), QMT_SBC(QMT_INDEX_SBC), QMT_SBD(QMT_INDEX_SBD), QMT_TRANSCODE(QMT_INDEX_TRANSCODE);

		private String name;

		private Index(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

	}
	
	public enum IndexType {

		DICT_ENTRY(Index.QMT_CONFIG, IdType.DICT_ENTRY, DictEntryIndex.class),
		MENU(Index.QMT_CONFIG, IdType.MENU, null),
		TAG(Index.QMT_CONFIG, IdType.TAG, TagIndex.class),
		COPYRIGHT(Index.QMT_CONFIG, IdType.COPYRIGHT, null),
		COUNTRY(Index.QMT_CONFIG, IdType.COUNTRY, null),
		
		ALBUM(Index.QMT_SBC, IdType.ALBUM, AlbumIndex.class),
		PROGRAM_ALBUM(Index.QMT_SBC, IdType.PROGRAM_ALBUM, ProgramAlbumIndex.class),
		EPISODE(Index.QMT_SBC, IdType.EPISODE, EpisodeIndex.class),
		LIVE(Index.QMT_SBC, null, null), //TODO
		NEWS(Index.QMT_SBC, IdType.NEWS, NewsIndex.class),
		PROGRAM(Index.QMT_SBC, null, null), //TODO
		RESOURCE(Index.QMT_SBC, IdType.RESOURCE, ResourceIndex.class),
		TOPIC(Index.QMT_SBC, IdType.TOPIC, TopicIndex.class),
		VIDEO(Index.QMT_SBC, IdType.VIDEO, VideoIndex.class),

		COMPETITION(Index.QMT_SBD, IdType.COMPETITION, CompetitionIndex.class),
		COMPETITION_SEASON(Index.QMT_SBD, IdType.COMPETITION_SEASON, CompetitionSeasonIndex.class),
		MATCH(Index.QMT_SBD, IdType.MATCH, MatchIndex.class),
		PLAYER(Index.QMT_SBD, IdType.PLAYER, PlayerIndex.class),
		TEAM(Index.QMT_SBD, IdType.TEAM, TeamIndex.class),
		TOP_LIST(Index.QMT_SBD, IdType.TOP_LIST, TopListIndex.class),
		
		TRANSCODE_VIDEO_TASK(Index.QMT_TRANSCODE, IdType.TRANSCODE_VIDEO_TASK,
				VideoTranscodeTaskIndex.class), 
		TRANSCODE_LIVE_TASK(Index.QMT_TRANSCODE, IdType.TRANSCODE_LIVE_TASK,
						LiveToVideoTaskIndex.class);

		private Index index;

		private IdType idType;
		
		private Class<? extends Indexable> indexClass;

		private IndexType(Index index, IdType idType,Class<? extends Indexable> indexClass) {
			this.index = index;
			this.idType = idType;
			this.indexClass=indexClass;
		}

		public Index getIndex() {
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
			case MENU:
				return MENU;
			case TAG:
				return TAG;
			case COPYRIGHT:
				return COPYRIGHT;
			case COUNTRY:
				return COUNTRY;
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
			case TOP_LIST:
				return TOP_LIST;
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
