package com.lesports.msg.service.impl;

import com.lesports.msg.service.CompetitionSeasonService;
import com.lesports.sms.api.common.Platform;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * User: ellios
 * Time: 15-11-10 : 下午2:55
 */
@Service("competitionSeasonService")
public class CompetitonSeasonServiceImpl implements CompetitionSeasonService{

    private static final Logger LOG = LoggerFactory.getLogger(CompetitonSeasonServiceImpl.class);

    @Override
    public boolean changeLivePlatformsOfEpisodesRelatedWithCSeason(long csid, Set<Platform> livePlatforms) {
        boolean result = SopsInternalApis.updateLivePlatformsOfEpisodesRelatedWithCSeason(csid, livePlatforms);
        if(!result){
            LOG.info("fail to changeLivePlatformsOfEpisodesRelatedWithCSeason. csid : {}, livePlatforms : {}",
                    csid, livePlatforms);
        }else{
            LOG.info("success to changeLivePlatformsOfEpisodesRelatedWithCSeason. csid : {}, livePlatforms : {}",
                    csid, livePlatforms);
        }
        return result;
    }
}
