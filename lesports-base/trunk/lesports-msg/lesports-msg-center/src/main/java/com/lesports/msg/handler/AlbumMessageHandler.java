package com.lesports.msg.handler;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.core.MessageSource;
import com.lesports.msg.service.AlbumService;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
@Component
public class AlbumMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger logger = LoggerFactory.getLogger(AlbumMessageHandler.class);
    @Resource
    private AlbumService albumService;

    @Override
    Logger getLogger() {
        return logger;
    }

    @Override
    public void handle(LeMessage message) {
        //媒资过来的消息重新保存专辑
        long mzAlbumId = message.getEntityId();
        long albumId = SopsInternalApis.getAlbumIdByMmsAlbumId(mzAlbumId);
        if (albumId > 0) {
            boolean result = SopsInternalApis.saveAlbumById(albumId, getCaller(message.getSource()));
            if (!result) {
                logger.error("fail to handle mms album message {}", mzAlbumId);
                return;
            } else {
                albumService.deleteAlbumInSis(albumId);
                logger.info("success to handle mms album message {}", mzAlbumId);
            }
        } else {
            logger.info("mms album {} not exists in db, will not handle this.", mzAlbumId);
        }
//        SwiftMessageApis.sendMsgSync(MsgProducerConstants.SWIFTQ_MESSAGE_CENTER_EXCHANGE, message);
    }
}
