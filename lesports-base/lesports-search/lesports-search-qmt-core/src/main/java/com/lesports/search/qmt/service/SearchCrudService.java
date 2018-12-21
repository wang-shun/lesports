package com.lesports.search.qmt.service;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.elasticsearch.core.query.IndexQuery;

import com.lesports.search.qmt.param.IndexMongoParam;
import com.lesports.search.qmt.param.SearchParam;
import com.lesports.search.qmt.utils.PageResult;
import com.lesports.service.LeCrudService;

/**
 * trunk.
 *
 * @author sunyue7
 * 
 */
public interface SearchCrudService<T, ID extends Serializable> extends LeCrudService<T, ID> {

	public boolean save(ID id);

	public List<IndexQuery> getBulkData(List<ID> ids);

	public Long createIndexFromMongo(String db, String table, IndexMongoParam param);

	public PageResult<T> findByParams(SearchParam param);
}
