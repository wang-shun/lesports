package com.lesports.bole.service;

import com.lesports.service.LeCrudService;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import java.io.Serializable;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/11
 */
public interface BoleSearchCrudService<T, ID extends Serializable> extends LeCrudService<T, ID>{

    boolean save(ID id);

    List<IndexQuery> getBulkData(List<ID> ids);
}
