package com.lesports.msg.service;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/23
 */
public interface NewsService {

    boolean deleteNewsApiCache(long id);

    public boolean indexNews(long id);

    boolean addNewsToCibnRecommendTv(long id);
}
