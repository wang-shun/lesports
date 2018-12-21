package com.lesports.msg.handler;

import com.alibaba.fastjson.JSONObject;
import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;
import com.lesports.msg.core.LeMessage;
import com.lesports.msg.service.ActionService;
import com.lesports.user.api.client.UserActionApiClient;
import com.lesports.user.model.TActionLog;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.http.RestTemplateFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/23
 */
@Component
public class ActionMessageHandler extends AbstractHandler implements IHandler {
    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();
    private static final Logger LOG = LoggerFactory.getLogger(ActionMessageHandler.class);
    @Resource
    private ActionService actionService;

    @Override
    Logger getLogger() {
        return LOG;
    }

    @Override
    public void handle(LeMessage message) {
        LOG.info("{}",JSONObject.toJSONString(message));
        boolean result = false;
        String successCallback = null;
        String failureCallback = null;
        final TActionLog tActionLog = new TActionLog();
        try {
            long actionId = message.getEntityId();
            String actionCode = message.getEntityName();
            int value = message.getContent().getValue();
            long id = LeIdApis.nextId(IdType.ACTION_LOG);
            String uid = message.getContent().getUid();
            successCallback = message.getContent().getSuccessCallback();
            failureCallback = message.getContent().getFailureCallback();
            tActionLog.setActionId(actionId);
            tActionLog.setActionCode(actionCode);
            tActionLog.setId(id);
            tActionLog.setValue(value);
            tActionLog.setUid(uid);
            tActionLog.setDescription(message.getContent().getDesc());
            tActionLog.setCreateAt(message.getMessageId());
            result = UserActionApiClient.submitAction(tActionLog);

            LOG.info("submit action : {}, message id : {}, result : {}.", JSONObject.toJSONString(tActionLog), message.getMessageId(), result);
        } catch (Exception e) {
            //check and retry, most of the failure is caused by the read time out.
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e1) {
                LOG.error("{}", e.getMessage(), e1);
            }

            try {
                TActionLog remoteLog = UserActionApiClient.getActionLog(tActionLog.getId());
                if (null == remoteLog || !remoteLog.isEffect()) {
                    result = UserActionApiClient.submitAction(tActionLog);
                }
            } catch (Exception e2){
                LOG.error("{}", e.getMessage(), e2);
            }

        }

        if (!result) {
            tActionLog.setEffect(false);
            tActionLog.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            tActionLog.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));

            boolean persist = false;
            try {
                persist = UserActionApiClient.submitAction(tActionLog);
            }catch (Exception e){
                LOG.error("persist error,{}", e.getMessage(), e);
            }
            LOG.info("persist action log, message id : {}, action log id : {}, result : {}.", message.getMessageId(), tActionLog.getId(), persist);
            if (StringUtils.isNotEmpty(failureCallback)) {
                String res = TEMPLATE.getForObject(failureCallback, String.class);
                LOG.info("execute callback : {} for {}, result : {}", failureCallback, tActionLog.getId(), res);
            }
        } else {
            if (StringUtils.isNotEmpty(successCallback)) {
                String res = TEMPLATE.getForObject(successCallback, String.class);
                LOG.info("execute callback : {} for {}, result : {}", successCallback, tActionLog.getId(), res);
            }
        }
    }

}
