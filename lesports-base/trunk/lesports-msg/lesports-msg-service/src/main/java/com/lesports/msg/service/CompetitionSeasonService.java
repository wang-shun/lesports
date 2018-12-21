package com.lesports.msg.service;

import com.lesports.sms.api.common.Platform;

import java.util.Set;

/**
 * User: ellios
 * Time: 15-11-10 : 下午2:54
 */
public interface CompetitionSeasonService {

    public boolean changeLivePlatformsOfEpisodesRelatedWithCSeason(long csid, Set<Platform> livePlatforms);
}
