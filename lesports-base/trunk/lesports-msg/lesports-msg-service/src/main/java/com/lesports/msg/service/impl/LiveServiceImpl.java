package com.lesports.msg.service.impl;

import com.lesports.msg.cache.CdnCacheApis;
import com.lesports.msg.cache.HkCacheApis;
import com.lesports.msg.cache.SmsMemCache;
import com.lesports.msg.service.AbstractService;
import com.lesports.msg.service.LiveService;
import com.lesports.utils.Utf8KeyCreater;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/31
 */
@Service
public class LiveServiceImpl extends AbstractService implements LiveService {
    private final static MessageFormat LIVE_PAGE_URI = new MessageFormat("http://sports.letv.com/live/{0}.html");
    private static final Utf8KeyCreater<Long> LIVEROOM_CREATE = new Utf8KeyCreater<>("V2_LIVE_LIVEROOM_");
    private static final Utf8KeyCreater<Long> OLD_LIVEROOM_CREATE = new Utf8KeyCreater<>("LIVE_LIVEROOM_");

    @Resource
    private SmsMemCache smsMemCache;

    @Override
    public boolean deleteLivePage(long liveId) {
        String url = LIVE_PAGE_URI.format(new Object[]{String.valueOf(liveId)});
        return deleteNgxPageCache(liveId) && CdnCacheApis.delete(url);
    }

    @Override
    public boolean deleteLiveWebCache(long liveId) {
        return smsMemCache.delete(OLD_LIVEROOM_CREATE.textKey(liveId), liveId) &
                smsMemCache.delete(LIVEROOM_CREATE.textKey(liveId), liveId) & HkCacheApis.deleteWeb(LIVEROOM_CREATE.textKey(liveId), liveId);
    }
}
