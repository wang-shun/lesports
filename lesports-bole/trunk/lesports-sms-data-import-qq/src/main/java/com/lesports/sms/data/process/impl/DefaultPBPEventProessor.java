package com.lesports.sms.data.process.impl;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.client.TextLiveInternalApis;
import com.lesports.sms.data.process.BeanProcessor;
import com.lesports.sms.model.TextLive;
import com.lesports.sms.model.TextLiveMessage;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import javax.jms.TextMessage;
import java.util.List;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
@Service("PBPEventProcessor")
public class DefaultPBPEventProessor extends BeanProcessor {
    private static Logger LOG = org.slf4j.LoggerFactory.getLogger(DefaultPBPEventProessor.class);

    public Boolean process(Object object) {
        List<TextLiveMessage> textLiveMessagesList = (List<TextLiveMessage>) object;
        try {
            if (CollectionUtils.isEmpty(textLiveMessagesList)) {
                LOG.warn("Match Action, List is empty");
            }
            TextLive currentTextLive = TextLiveInternalApis.getTextLiveById(textLiveMessagesList.get(0).getTextLiveId());
            String maxPartnerId = currentTextLive.getLatestPartnerId();
            for (TextLiveMessage validTextMessage : textLiveMessagesList) {

                if (TextLiveInternalApis.saveTextLiveMessage(validTextMessage) > 0) {
                    if (maxPartnerId.compareTo(validTextMessage.getPartnerId()) < 0) {
                        maxPartnerId = validTextMessage.getPartnerId();
                    }
                }
            }
            currentTextLive.setLatestPartnerId(maxPartnerId);
            if (TextLiveInternalApis.saveTextLive(currentTextLive) > 0) {
                LOG.info("text live message are all saved sucessfully,and textLive:{},updateto ,length:{}", currentTextLive.getId(), maxPartnerId);
                return true;
            }
            return false;

        } catch (Exception e) {
            LOG.error("PBPEvent  exception:{}", e);
            return false;
        }
    }
}





