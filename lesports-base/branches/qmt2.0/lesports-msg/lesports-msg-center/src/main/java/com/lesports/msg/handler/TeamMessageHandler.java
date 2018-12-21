package com.lesports.msg.handler;

import com.lesports.bole.api.vo.TBCompetitor;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.msg.service.BoleCompetitorService;
import com.lesports.msg.service.TeamService;
import com.lesports.msg.util.MsgProducerConstants;
import com.lesports.sms.client.BoleApis;
import com.lesports.sms.client.BoleInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author pangchuanxiao
 * @since 2015/8/14
 */
@Component
public class TeamMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TeamMessageHandler.class);
    @Resource
    private TeamService teamService;
    @Resource
    private BoleCompetitorService boleCompetitorService;

    @Override
    Logger getLogger() {
        return LOG;
    }

    @Override
    public void handle(LeMessage message) {
        teamService.deleteTeamApiCache(message.getEntityId());
        SwiftMessageApis.sendMsgSync(MsgProducerConstants.SWIFTQ_MESSAGE_CENTER_EXCHANGE, message);
//        boolean boleResult = BoleInternalApis.updateBoleWithSms(message.getEntityId());
//        LOG.info("update bole team : {}, result : {}", message.getEntityId(), boleResult);
//        if (boleResult) {
//            TBCompetitor tbCompetitor = BoleApis.getCompetitorBySmsId(message.getEntityId());
//            if (null != tbCompetitor) {
//                boleCompetitorService.indexBoleCompetitor(tbCompetitor.getId());
//            }
//        }
    }
}
