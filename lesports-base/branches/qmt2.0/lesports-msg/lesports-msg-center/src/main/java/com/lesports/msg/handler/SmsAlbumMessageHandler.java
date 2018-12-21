package com.lesports.msg.handler;

import com.lesports.msg.core.ActionType;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.producer.CloudSwiftMessageApis;
import com.lesports.msg.service.AlbumService;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.model.Config;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
@Component
public class SmsAlbumMessageHandler extends AbstractHandler implements IHandler {
    private static final Logger logger = LoggerFactory.getLogger(SmsAlbumMessageHandler.class);
    @Resource
    private AlbumService albumService;

    @Override
    Logger getLogger() {
        return logger;
    }

    @Override
    public void handle(LeMessage message) {
        long albumId = message.getEntityId();
        albumService.deleteAlbumInSis(albumId);
        logger.info("success to handle sms album message {}", albumId);

        Config tConfig = SopsInternalApis.getCloudSyncConfigByQuery();
        if (tConfig != null && MapUtils.isNotEmpty(tConfig.getData())) {
            Map<String, String> data = tConfig.getData();
            String albumAddSync = data.get("album_add_sync");
            if (StringUtils.isNotBlank(albumAddSync) && albumAddSync.equals("true")) {
                CloudSwiftMessageApis.sendMsgAsync(message);
            }
        }
        if (message.getActionType() == ActionType.DELETE_ALL) {
            CloudSwiftMessageApis.sendMsgAsync(message);
            logger.info("album delete all to cloud message body : {}", message);
        }
    }
}
