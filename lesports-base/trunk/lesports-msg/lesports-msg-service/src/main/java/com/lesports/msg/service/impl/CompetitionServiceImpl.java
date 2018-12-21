package com.lesports.msg.service.impl;

import com.lesports.msg.service.CompetitionService;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * User: ellios
 * Time: 15-11-10 : 下午2:55
 */
@Service("competitionService")
public class CompetitionServiceImpl implements CompetitionService {

    private static final Logger LOG = LoggerFactory.getLogger(CompetitionServiceImpl.class);

    @Override
    public boolean changeSyncToCloudOfEpisodesWithCid(long cid) {
        boolean result = SopsInternalApis.changeSyncToCloudOfEpisodesWithCid(cid);
        if(!result){
            LOG.info("fail to changeSyncToCloudOfEpisodesWithCid. cid : {}", cid);
        }else{
            LOG.info("success to changeSyncToCloudOfEpisodesWithCid. cid : {}, ", cid);
        }
        return result;
    }
}
