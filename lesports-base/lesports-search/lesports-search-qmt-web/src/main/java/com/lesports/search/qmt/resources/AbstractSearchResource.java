/**
 * 
 */
package com.lesports.search.qmt.resources;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lesports.id.api.IdType;
import com.lesports.jersey.core.LeStatus;
import com.lesports.jersey.exception.LeWebApplicationException;
import com.lesports.search.qmt.index.Indexable;
import com.lesports.search.qmt.meta.MetaData;
import com.lesports.search.qmt.meta.MetaData.IndexType;
import com.lesports.search.qmt.param.SearchParam;
import com.lesports.search.qmt.service.SearchCrudService;
import com.lesports.search.qmt.utils.PageResult;
import com.lesports.search.qmt.utils.SearchServiceFactory;

/**
 * @author sunyue7
 *
 */
public abstract class AbstractSearchResource<T extends SearchParam> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractSearchResource.class);

	@Inject
	private SearchServiceFactory searchServiceFactory;

	protected PageResult<? extends Indexable> search(T param, IndexType indexType) {
		param.validate();
		try {
			SearchCrudService<? extends Indexable, Long> service = searchServiceFactory.getService(indexType);
			if (service != null) {
				return service.findByParams(param);
			}
		} catch (Exception e) {
			LOG.error("Fail to search indexed object, type : {}", indexType != null ? indexType : "", e.getMessage(),
					e);
			throw new LeWebApplicationException("Fail to search indexed object", LeStatus.EXPECTATION_FAILED);
		}
		return null;
	}

	protected PageResult<? extends Indexable> search(T param, String type) {
		param.validate();
		IdType idType = IdType.valueOf(type);
		IndexType indexType = MetaData.IndexType.valueOf(idType);
		return search(param, indexType);
	}
}
