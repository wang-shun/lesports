package com.lesports.crawler.listener;

import com.lesports.crawler.model.SuspendRequest;
import com.lesports.crawler.repository.SuspendRepository;
import com.lesports.crawler.utils.SpringUtils;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.math.LeNumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

import java.util.Date;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/24
 */
public class DefaultListener implements SpiderListener {
    private SuspendRepository suspendRepository = SpringUtils.getBean(SuspendRepository.class);
    private static final Logger LOG = LoggerFactory.getLogger(DefaultListener.class);

    @Override
    public void onSuccess(Request request) {
        SuspendRequest suspendRequest = suspendRepository.findOne(request.getUrl());
        if (null == suspendRequest) {
            return;
        }
        boolean result = suspendRepository.delete(request.getUrl());
        LOG.info("success process : {}, delete in suspend : {}.", request.getUrl(), result);
    }

    @Override
    public void onError(Request request) {
        SuspendRequest suspendRequest = suspendRepository.findOne(request.getUrl());
        if (null == suspendRequest) {
            suspendRequest = new SuspendRequest();
            suspendRequest.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            suspendRequest.setId(request.getUrl());
        }
        suspendRequest.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        suspendRequest.setFailureCount(LeNumberUtils.toInt(suspendRequest.getFailureCount()) + 1);
        boolean result = suspendRepository.save(suspendRequest);
        LOG.info("fail process : {}, save in suspend : {}.", request.getUrl(), result);
    }
}
