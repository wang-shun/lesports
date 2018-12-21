package com.lesports.crawler.repository;

import com.lesports.crawler.model.SuspendRequest;
import com.lesports.repository.LeCrudRepository;

import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
public interface SuspendRepository extends LeCrudRepository<SuspendRequest, String> {
    List<SuspendRequest> getSuspendRequestsFrom(String startTime);
}
