package com.lesports.msg.service;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/16
 */
public interface AlbumService {
    /**
     * 清除sis web的memcached缓存
     * @param id
     * @return
     */
    boolean deleteAlbumInSis(long id);
}
