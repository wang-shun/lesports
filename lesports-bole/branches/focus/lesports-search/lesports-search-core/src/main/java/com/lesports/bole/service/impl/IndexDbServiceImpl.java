/**
 * 
 */
package com.lesports.bole.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.bole.function.SearchIndexTransformer;
import com.lesports.bole.index.EpisodeIndex;
import com.lesports.bole.index.NewsIndex;
import com.lesports.bole.index.SearchIndex;
import com.lesports.bole.service.CommonSearchService;
import com.lesports.bole.service.IndexDbService;
import com.lesports.sms.api.common.NewsType;
import com.lesports.sms.api.common.OnlineStatus;
import com.lesports.sms.api.common.Platform;
import com.lesports.sms.api.common.TvLicence;
import com.lesports.sms.api.common.LiveShowStatus;
import com.lesports.utils.LeDateUtils;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author sunyue7
 *
 */
@Service
public class IndexDbServiceImpl implements IndexDbService {

	private static final Logger LOG = LoggerFactory.getLogger(IndexDbService.class);

	@Resource
	private DB mongoDb;

	@Resource
	private CommonSearchService commonSearchService;

	private ExecutorService indexPool;

	/**
	 * 
	 */
	public IndexDbServiceImpl() {
		super();
		indexPool = new ThreadPoolExecutor(0, 3, 100L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lesports.bole.service.IndexDbService#indexFromDb(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public boolean indexFromDb(String index, String type, String table) {
		final Class clazz = SearchIndexTransformer.getClass(index, type);
		DBCollection col = mongoDb.getCollection(table);
		final DBCursor cursor = col.find();
		Runnable indexWork = new Runnable() {

			@Override
			public void run() {
				while (cursor.hasNext()) {
					ArrayList<DBObject> batch = new ArrayList<DBObject>(50);
					for (int i = 0; i < 50 && cursor.hasNext(); i++) {
						batch.add(cursor.next());
					}
					@SuppressWarnings("unchecked")
					IndexWorker worker = new IndexWorker(commonSearchService, batch, clazz);
					// TODO single thread is enough now
					// indexPool.execute(worker);
					worker.run();
				}
			}
		};
		new Thread(indexWork, "Thread-index-from-Mongodb").start();
		return true;
	}

	class IndexWorker implements Runnable {

		CommonSearchService service;

		List<DBObject> indexes;

		@SuppressWarnings("rawtypes")
		Class<SearchIndex> indexClass;

		/**
		 * @param service
		 * @param indexes
		 * @param indexClass
		 */
		@SuppressWarnings("rawtypes")
		public IndexWorker(CommonSearchService service, List<DBObject> indexes, Class<SearchIndex> indexClass) {
			super();
			this.service = service;
			this.indexes = indexes;
			this.indexClass = indexClass;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void run() {
			ArrayList<IndexQuery> datas = new ArrayList<IndexQuery>(indexes.size());
			if (indexClass.equals(NewsIndex.class)) {
				for (DBObject obj : indexes) {
					NewsIndex newsIndex = new NewsIndex();
					newsIndex.setId((Long) obj.get("_id"));
					newsIndex.setName((String) obj.get("name"));
					newsIndex.setUpdateAt((String) obj.get("update_at"));
					newsIndex.setMids((List) (obj.get("mids")));
					newsIndex.setTagIds(new HashSet((List) (obj.get("tag_ids"))));
					newsIndex.setTids(new HashSet((List) (obj.get("tids"))));
					newsIndex.setPublishAt((String) obj.get("publish_at"));
					newsIndex.setDeleted((Boolean) obj.get("deleted"));
					if (obj.get("type") != null) {
						newsIndex.setNewsType(NewsType.valueOf((String) obj.get("type")).getValue());
					}
					if (obj.get("online") != null) {
						newsIndex.setOnlineStatus(OnlineStatus.valueOf((String) obj.get("online")).getValue());
					}
					if (obj.get("allow_country") != null) {
						newsIndex.setAllowCountry(CountryCode.valueOf((String) obj.get("allow_country")).getValue());
					}
					if (obj.get("language_code") != null) {
						newsIndex.setLanguageCode(LanguageCode.valueOf((String) obj.get("language_code")).getValue());
					}
					if (obj.get("support_licences") != null) {
						List<Integer> sl = new ArrayList<Integer>();
						for (String i : (List<String>) obj.get("support_licences")) {
							sl.add(TvLicence.valueOf(i).getValue());
						}
						newsIndex.setSupportLicences(sl);
					}
					if (obj.get("platforms") != null) {
						List<Integer> sl = new ArrayList<Integer>();
						for (String i : (List<String>) obj.get("platforms")) {
							sl.add(Platform.valueOf(i).getValue());
						}
						newsIndex.setPlatforms(sl);
					}
					datas.add(new IndexQueryBuilder().withId(String.valueOf(newsIndex.getId())).withObject(newsIndex)
							.build());
					LOG.info("Index from Mongo, type = news, ID = {}", newsIndex.getId());
				}
			} else if (indexClass.equals(EpisodeIndex.class)) {
				for (DBObject obj : indexes) {
					EpisodeIndex episodeIndex = new EpisodeIndex();
					episodeIndex.setId((Long) obj.get("_id"));
					episodeIndex.setName((String) obj.get("name"));
					episodeIndex.setDeleted((Boolean) obj.get("deleted"));
					episodeIndex.setAid((Long) obj.get("aid"));
					episodeIndex.setHasLive((Boolean) obj.get("has_live"));
					episodeIndex.setOnline((Boolean) obj.get("online"));
					episodeIndex.setStartTime((String) obj.get("start_time"));
					episodeIndex.setIsLephoneChannelMatch((Boolean) obj.get("is_lephone_channel_match"));
					episodeIndex.setIsLephoneMatch((Boolean) obj.get("is_lephone_match"));
					episodeIndex.setMid((Long) obj.get("mid"));
					episodeIndex.setTagIds(new HashSet((List) (obj.get("tag_ids"))));
					episodeIndex.setCompetitorIds(new HashSet((List) (obj.get("competitor_ids"))));
					episodeIndex.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
					if (obj.get("status") != null) {
						episodeIndex.setStatus(LiveShowStatus.valueOf((String) obj.get("status")).getValue());
					}
					if (obj.get("text_live_status") != null) {
						episodeIndex.setTextLiveStatus(
								LiveShowStatus.valueOf((String) obj.get("text_live_status")).getValue());
					}
					if (obj.get("live_platforms") != null) {
						List<Integer> sl = new ArrayList<Integer>();
						for (String i : (List<String>) obj.get("live_platforms")) {
							sl.add(Platform.valueOf(i).getValue());
						}
						episodeIndex.setLivePlatforms(sl);
					}
					if (obj.get("allow_country") != null) {
						episodeIndex.setAllowCountry(CountryCode.valueOf((String) obj.get("allow_country")).getValue());
					}
					datas.add(new IndexQueryBuilder().withId(String.valueOf(episodeIndex.getId()))
							.withObject(episodeIndex).build());
					LOG.info("Index from Mongo, type = episode, ID = {}", episodeIndex.getId());
				}
			}
			service.saveBulk(datas);
		}

	}

}
