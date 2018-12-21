package com.lesports.msg.handler;

import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.CompetitionSeasonService;
import com.lesports.sms.api.vo.TCompetitionSeason;
import com.lesports.sms.client.BoleInternalApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.CompetitionSeason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: ellios
 * Time: 15-11-10 : 下午2:52
 */
@Component
public class CompetitionSeasonHandler extends AbstractHandler implements IHandler{
    private static final Logger LOG = LoggerFactory.getLogger(CompetitionSeasonHandler.class);
    @Resource
    private CompetitionSeasonService competitionSeasonService;

    @Override
    public void handle(LeMessage message) {
        if(message.getActionType() == ActionType.LIVE_PLATFORM_UPDATE){
            long csid = message.getEntityId();
            CompetitionSeason cseason = SbdsInternalApis.getCompetitionSeasonById(csid);
            if(cseason == null){
                LOG.warn("fail to handle message : {}, cseason : {} no exists.");
            }
            competitionSeasonService.changeLivePlatformsOfEpisodesRelatedWithCSeason(csid, cseason.getLivePlatforms());
        }
//        boolean boleResult = BoleInternalApis.updateBoleWithSms(message.getEntityId());
//        LOG.info("update bole competition season : {}, result : {}", message.getEntityId(), boleResult);
    }

    @Override
    Logger getLogger() {
        return LOG;
    }
}
