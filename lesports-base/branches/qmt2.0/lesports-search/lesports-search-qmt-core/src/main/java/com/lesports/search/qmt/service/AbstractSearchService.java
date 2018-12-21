package com.lesports.search.qmt.service;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.beanutils.LeBeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.google.common.collect.Lists;
import com.lesports.qmt.model.support.QmtModel;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.param.IndexMongoParam;
import com.lesports.search.qmt.param.SearchParam;
import com.lesports.search.qmt.repository.CommonSearchRepository;
import com.lesports.search.qmt.utils.MongoFactory;
import com.lesports.search.qmt.utils.PageResult;
import com.lesports.utils.LeDateUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.QueryOperators;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
public abstract class AbstractSearchService<T extends Indexable, ID extends Serializable>
		implements SearchCrudService<T, ID> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractSearchService.class);

	@Resource
	protected CommonSearchRepository commonSearchRepository;

	@Resource
	private MongoFactory mongoFactory;

	private Class<T> indexClass;

	@SuppressWarnings("unchecked")
	@PostConstruct
	public final void init() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		indexClass = (Class<T>) params[0];
	}

	@Override
	public T findOne(ID id) {
		return doFindOne(id);
	}

	@Override
	public boolean save(ID id) {
		T entity = getEntityFromRpc(id);
		if (null == entity) {
			return false;
		}
		if (StringUtils.isEmpty(entity.getUpdateAt())) {
			entity.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
		}
		return save(entity);
	}

	@Override
	public List<IndexQuery> getBulkData(List<ID> ids) {
		if (CollectionUtils.isEmpty(ids)) {
			return null;
		}
		List<T> entities = getEntityFromRpc(ids);
		List<IndexQuery> list = Lists.newArrayList();
		for (T entity : entities) {
			if (null == entity) {
				LOG.info("getBulkData getEntityFromRpc is null.id:{}", ids);
				continue;
			}
			entity.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
			IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(entity.getId())).withObject(entity)
					.build();
			list.add(indexQuery);
		}
		return list;
	}

	@Override
	public final Long createIndexFromMongo(String db, String table, IndexMongoParam param) {
		return param.isRpc() ? createIndexRpc(db, table, param) : createIndexMongodb(db, table, param);
	}

	private Long createIndexRpc(String db, String table, IndexMongoParam param) {
		DB mongoDb = mongoFactory.getMongoDatabase(db);
		DBCollection col = mongoDb.getCollection(table);
		String[] updateAtRange = param.getUpdateAtRange();
		DBObject query = null;
		if (updateAtRange != null && updateAtRange.length > 0 && updateAtRange.length <= 2) {
			query = new BasicDBObject();
			BasicDBObject lower = null;
			BasicDBObject upper = null;
			if (StringUtils.isNotEmpty(updateAtRange[0])) {
				lower = new BasicDBObject().append("update_at",
						new BasicDBObject().append(QueryOperators.GTE, updateAtRange[0]));
			}
			if (StringUtils.isNotEmpty(updateAtRange[1])) {
				upper = new BasicDBObject().append("update_at",
						new BasicDBObject().append(QueryOperators.LTE, updateAtRange[1]));
			}
			if (lower != null && upper != null) {
				query = new BasicDBObject().append(QueryOperators.AND, new BasicDBObject[] { lower, upper });
			} else {
				query = lower != null ? lower : upper;
			}
		}

		final DBCursor cursor = query == null ? col.find() : col.find(query);
		long count = cursor.count();
		Runnable indexWork = new Runnable() {

			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				/*
				 * ThreadPoolExecutor indexPool = new ThreadPoolExecutor(0, 3,
				 * 100L, TimeUnit.MILLISECONDS, new
				 * LinkedBlockingQueue<Runnable>());
				 */
				try {
					while (cursor.hasNext()) {
						List<ID> batch = new ArrayList<ID>(param.getBatchSize());
						for (int i = 0; i < param.getBatchSize() && cursor.hasNext(); i++) {
							Object id = null;
							try {
								id = cursor.next().get("_id");
								batch.add((ID) id);
							} catch (Exception e) {
								LOG.error("Illegal ID of Mongo data, ID = {}", id);
							}
						}
						IndexWorkerRpc worker = new IndexWorkerRpc(batch);
						// TODO single thread is enough now
						// indexPool.execute(worker);
						worker.run();
					}
				} catch (Exception e) {
					LOG.error("Featch IDs from Mongo failed", e);
				} finally {
					if (cursor != null) {
						cursor.close();
					}
				}
			}
		};
		new Thread(indexWork, "Thread-index-by-RPC").start();
		return count;
	}

	private Long createIndexMongodb(String db, String table, IndexMongoParam param) {
		final MongoTemplate template = mongoFactory.getMongoTemplate(db);
		String[] updateAtRange = param.getUpdateAtRange();
		Criteria cti = null;
		if (updateAtRange != null && updateAtRange.length > 0 && updateAtRange.length <= 2) {
			Criteria lower = null;
			Criteria upper = null;
			if (StringUtils.isNotEmpty(updateAtRange[0])) {
				lower = Criteria.where("update_at").gte(updateAtRange[0]);
			}
			if (StringUtils.isNotEmpty(updateAtRange[1])) {
				upper = Criteria.where("update_at").lte(updateAtRange[1]);
			}
			if (lower != null && upper != null) {
				cti = lower.andOperator(upper);
			} else {
				cti = lower != null ? lower : upper;
			}
		}

		final Query query = cti != null ? new Query(cti) : new Query();
		final Long count = template.count(query, table);
		Runnable indexWork = new Runnable() {

			@Override
			public void run() {
				/*
				 * ThreadPoolExecutor indexPool = new ThreadPoolExecutor(0, 3,
				 * 100L, TimeUnit.MILLISECONDS, new
				 * LinkedBlockingQueue<Runnable>());
				 */
				int counter = 0;
				List<T> entities = null;
				do {
					try {
						query.skip(counter);
						counter += param.getBatchSize();
						query.limit(param.getBatchSize());
						entities = template.find(query, indexClass, table);
						IndexWorkerMongodb worker = new IndexWorkerMongodb(entities);
						// TODO single thread is enough now
						// indexPool.execute(worker);
						worker.run();
					} catch (Exception e) {
						LOG.error("Featch entities from Mongo failed", e);
					}
				} while (entities != null && !entities.isEmpty());
			}
		};
		new Thread(indexWork, "Thread-index-from-Mongodb").start();
		return count;
	}

	@Override
	public boolean delete(ID id) {
		T index = doFindOne(id);
		if (null == index) {
			return true;
		}
		index.setDeleted(true);
		return save(index);
	}

	@SuppressWarnings("unchecked")
	protected T buildIndex(Indexable index, QmtModel<ID> qmtObject) {
		LeBeanUtils.copyNotEmptyPropertiesQuietly(index, qmtObject);
		return (T) index;
	}

	protected List<T> buildIndex(List<? extends QmtModel<ID>> qmtObjects) {
		List<T> result = Lists.newArrayList();
		for (QmtModel<ID> model : qmtObjects) {
			try {
				T index = indexClass.newInstance();
				LeBeanUtils.copyNotEmptyPropertiesQuietly(index, model);
				result.add(index);
			} catch (InstantiationException e) {
				LOG.error("Create index failed, ID = {}", model.getId(), e);
			} catch (IllegalAccessException e) {
				LOG.error("Create index failed, ID = {}", model.getId(), e);
			}
		}
		return result;
	}

	@Override
	public final PageResult<T> findByParams(SearchParam param) {
		Page<T> res = commonSearchRepository.findBySearchQuery(param.createNativeSearchQueryBuilder().build(),
				indexClass);
		return new PageResult<>(res);
	}

	protected abstract T getEntityFromRpc(ID id);

	protected abstract List<T> getEntityFromRpc(List<ID> ids);

	protected abstract T doFindOne(ID id);

	class IndexWorkerRpc implements Runnable {

		List<ID> ids;

		public IndexWorkerRpc(List<ID> ids) {
			super();
			this.ids = ids;
		}

		@Override
		public void run() {
			try {
				List<T> entities = getEntityFromRpc(ids);
				if (CollectionUtils.isEmpty(entities)) {
					return;
				}
				List<IndexQuery> list = Lists.newArrayListWithCapacity(entities.size());
				for (T entity : entities) {
					IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(entity.getId()))
							.withObject(entity).build();
					list.add(indexQuery);
					LOG.debug("Entity is ready to be indexed, ID = {}", entity.getId());
				}
				if (commonSearchRepository.saveBulk(list)) {
					LOG.info("Entities are indexed from RPC, ID = {}", ids);
				} else {
					LOG.error("Entities index failed from RPC, ID = {}", ids);
				}
			} catch (Exception e) {
				LOG.error("Index from RPC error, ID = {}", ids, e);
			}
		}
	}

	class IndexWorkerMongodb implements Runnable {

		List<T> entities;

		public IndexWorkerMongodb(List<T> entities) {
			super();
			this.entities = entities;
		}

		@Override
		public void run() {
			try {
				if (CollectionUtils.isEmpty(entities)) {
					return;
				}
				List<IndexQuery> list = Lists.newArrayListWithCapacity(entities.size());
				for (T entity : entities) {
					IndexQuery indexQuery = new IndexQueryBuilder().withId(String.valueOf(entity.getId()))
							.withObject(entity).build();
					list.add(indexQuery);
					LOG.debug("Entity is ready to be indexed, ID = {}", entity.getId());
				}
				if (commonSearchRepository.saveBulk(list)) {
					LOG.info("Entities are indexed from Mongo, number = {}", entities.size());
				} else {
					LOG.error("Entities index failed from Mongo, ID = {}", entities);
				}
			} catch (Exception e) {
				LOG.error("Index from Mongo error, ID = {}", entities, e);
			}
		}
	}
}
