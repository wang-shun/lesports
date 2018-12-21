package com.lesports.crawler.scheduler;

import com.lesports.crawler.MsgQueue;
import com.lesports.crawler.model.RequestMessage;
import com.lesports.crawler.model.SuspendRequest;
import com.lesports.crawler.repository.SuspendRepository;
import com.lesports.crawler.utils.Constants;
import com.lesports.crawler.utils.SpringUtils;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/24
 */
public class SuspendRequestSchedulerTask extends SchedulerTask {
    private static final Logger LOG = LoggerFactory.getLogger(SuspendRequestSchedulerTask.class);
    private SuspendRepository suspendRepository = SpringUtils.getBean(SuspendRepository.class);
    private MsgQueue msgQueue = SpringUtils.getBean(MsgQueue.class);

    @Override
    public void run() {
        DateTime dateTime = new DateTime();
        dateTime.plusSeconds(-1 * Constants.SCHEDULER_INIT_DELAY);
        List<SuspendRequest> suspendRequestList =
                suspendRepository.getSuspendRequestsFrom(LeDateUtils.formatYYYYMMDDHHMMSS(dateTime.toDate()));
        if (CollectionUtils.isNotEmpty(suspendRequestList)) {
            return;
        }
        for (SuspendRequest suspendRequest : suspendRequestList) {
            RequestMessage requestMessage = new RequestMessage();
            requestMessage.setUrl(suspendRequest.getId());
            msgQueue.push(requestMessage);
            LOG.info("restart suspend request : {}", requestMessage.getUrl());
        }
    }
}
