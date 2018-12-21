package com.lesports.crawler.repository.impl;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceNews;
import com.lesports.crawler.repository.SourceNewsRepository;
import com.lesports.mongo.repository.support.AbstractMongoRepository;
import com.lesports.utils.LeDateUtils;

/**
 * 源新闻CRUN实现
 * 
 * @author denghui
 *
 */
@Repository
public class SourceNewsRepositoryImpl extends
		AbstractMongoRepository<SourceNews, Long> implements
		SourceNewsRepository {

	@Override
	public SourceNews getBySourceAndSourceId(Source source, String sourceId) {
        Query query = new Query(Criteria.where("source").is(source));
        query.addCriteria(Criteria.where("source_id").is(sourceId));
        return findOneByQuery(query);
	}

	@Override
	protected Class<SourceNews> getEntityType() {
		return SourceNews.class;
	}

	@Override
	protected Long getId(SourceNews entity) {
		return entity.getId();
	}

    @Override
    public List<SourceNews> getInLastDay(Source source) {
        Query query = new Query(Criteria.where("source").is(source));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis() - TimeUnit.DAYS.toMillis(1));
        String datetime = LeDateUtils.formatYYYYMMDDHHMMSS(calendar.getTime());
        query.addCriteria(Criteria.where("publish_at").gte(datetime));
        return findByQuery(query);
    }

}
