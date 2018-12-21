package com.lesports.msg.handler;

import com.lesports.bole.api.vo.TBCompetition;
import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.BoleCompetitionService;
import com.lesports.msg.service.CompetitionService;
import com.lesports.sms.client.BoleApis;
import com.lesports.sms.client.BoleInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: ellios
 * Time: 15-11-10 : 下午2:52
 */
@Component
public class CompetitionMessageHandler extends AbstractHandler implements IHandler{
    private static final Logger LOG = LoggerFactory.getLogger(CompetitionMessageHandler.class);
    @Resource
    private BoleCompetitionService boleCompetitionService;
	@Resource
	private CompetitionService competitionService;

    @Override
    public void handle(LeMessage message) {
		if (message.getActionType() == ActionType.SYNC_TO_CLOUD_UPDATE){
			long cid = message.getEntityId();
			competitionService.changeSyncToCloudOfEpisodesWithCid(cid);
		}
//        boolean boleResult = BoleInternalApis.updateBoleWithSms(message.getEntityId());
//        LOG.info("update bole competition : {}, result : {}", message.getEntityId(), boleResult);
//        if (boleResult) {
//            TBCompetition tbCompetition = BoleApis.getCompetitionBySmsId(message.getEntityId());
//            if (null != tbCompetition) {
//                boleCompetitionService.indexBoleCompetition(tbCompetition.getId());
//            }
//        }
    }

    @Override
    Logger getLogger() {
        return LOG;
    }
}
