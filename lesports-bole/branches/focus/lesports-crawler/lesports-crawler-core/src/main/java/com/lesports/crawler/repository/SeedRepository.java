package com.lesports.crawler.repository;

import com.lesports.crawler.model.SeedRequest;
import com.lesports.repository.LeCrudRepository;

import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
public interface SeedRepository extends LeCrudRepository<SeedRequest, String> {
    public List<SeedRequest> getAll();
}
