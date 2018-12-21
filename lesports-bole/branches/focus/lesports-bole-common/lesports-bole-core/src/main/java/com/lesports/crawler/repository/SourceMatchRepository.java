package com.lesports.crawler.repository;

import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceMatch;
import com.lesports.repository.LeCrudRepository;

import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
public interface SourceMatchRepository extends LeCrudRepository<SourceMatch, String> {
    public SourceMatch getByStartTimeAndCompetitors(String startTime, List<String> competitors);

    public SourceMatch getBySourceAndSourceId(Source source, String sourceId);
}
