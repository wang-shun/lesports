package com.lesports.msg.handler;

import com.lesports.bole.api.vo.TBCompetitor;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.msg.service.BoleCompetitorService;
import com.lesports.msg.service.PlayerService;
import com.lesports.msg.util.MsgProducerConstants;
import com.lesports.sms.client.BoleApis;
import com.lesports.sms.client.BoleInternalApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.model.Player;
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
public class PlayerMessageHandler extends AbstractHandler implements IHandler  {
    private static final Logger LOG = LoggerFactory.getLogger(PlayerMessageHandler.class);
    @Resource
    private BoleCompetitorService boleCompetitorService;
	@Resource
	private PlayerService playerService;

    @Override
    Logger getLogger() {
        return LOG;
    }

    @Override
    public void handle(LeMessage message) {
		long playerId = message.getEntityId();
		//Index player data
		Player tplayer = SbdsInternalApis.getPlayerById(playerId);
		if (null != tplayer) {
			playerService.indexPlayer(tplayer);
		}
        SwiftMessageApis.sendMsgSync(MsgProducerConstants.SWIFTQ_MESSAGE_CENTER_EXCHANGE, message);
//        boolean boleResult = BoleInternalApis.updateBoleWithSms(message.getEntityId());
//        LOG.info("update bole player : {}, result : {}", message.getEntityId(), boleResult);
//        if (boleResult) {
//            TBCompetitor tbCompetitor = BoleApis.getCompetitorBySmsId(message.getEntityId());
//            if (null != tbCompetitor) {
//                boleCompetitorService.indexBoleCompetitor(tbCompetitor.getId());
//            }
//        }
    }
}
