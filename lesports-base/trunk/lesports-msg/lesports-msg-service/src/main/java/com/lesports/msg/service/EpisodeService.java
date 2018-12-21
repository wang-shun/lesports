package com.lesports.msg.service;

import com.lesports.sms.api.vo.TComboEpisode;
import com.lesports.sms.model.Episode;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
public interface EpisodeService {

    public boolean deleteEpisodeApiCache(long id);

    public boolean indexEpisode(Episode episode);

    /**
     * 清除sms realtime中的episode缓存
     * @param id
     * @return
     */
    boolean deleteSmsRealtimeWebEpisodeCache(long id);
}
