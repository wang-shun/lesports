package com.lesports.msg.service.impl;

import com.lesports.msg.cache.HkCacheApis;
import com.lesports.msg.cache.SisWebMemCache;
import com.lesports.msg.service.AlbumService;
import com.lesports.utils.Utf8KeyCreater;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/16
 */
@Service
public class AlbumServiceImpl implements AlbumService {
    private static final Utf8KeyCreater<Long> VRS_ALBUM_CREATE = new Utf8KeyCreater<>("V2_VRS_ALBUM_");
    private static final Utf8KeyCreater<Long> OLD_VRS_ALBUM_CREATE = new Utf8KeyCreater<>("VRS_ALBUM_");
    @Resource
    private SisWebMemCache sisWebMemCache;

    @Override
    public boolean deleteAlbumInSis(long id) {
        return sisWebMemCache.delete(OLD_VRS_ALBUM_CREATE.textKey(id), id) & sisWebMemCache.delete(VRS_ALBUM_CREATE.textKey(id), id);
    }
}
