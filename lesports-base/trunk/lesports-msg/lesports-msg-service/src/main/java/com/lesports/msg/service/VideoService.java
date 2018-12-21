package com.lesports.msg.service;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/23
 */
public interface VideoService {
    /**
     * 清除sis web的memcached缓存
     * @param id
     * @return
     */
    boolean deleteVideoInSis(long id);
    /**
     * 删除视频接口缓存
     * @param id
     * @return
     */
    boolean deleteVideoApiCache(long id);
    /**
     * 清除mem cached中的页面内容
     * @param url 视频页面地址
     * @return
     */
    boolean deletePageInMemCached(String url);

    /**
     * 清除mem cached中的页面内容
     * @param id 视频页面地址
     * @return
     */
    boolean deletePageInMemCached(long id);

    /**
     * 清除cdn中的页面缓存
     * @param url 视频页面地址
     * @return
     */
    boolean deletePageInCdn(String url);

    /**
     * 清除cdn中的页面缓存
     * @param id 视频id
     * @return
     */
    boolean deletePageInCdn(long id);

    /**
     * 为视频创建索引数据
     * @param id
     * @return
     */
    boolean indexVideo(long id);

}
