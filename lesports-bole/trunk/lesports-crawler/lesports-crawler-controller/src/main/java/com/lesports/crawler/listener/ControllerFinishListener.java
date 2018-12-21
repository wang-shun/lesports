package com.lesports.crawler.listener;

import com.lesports.crawler.model.FetchedRequest;
import com.lesports.crawler.repository.FetchedRepository;
import com.lesports.crawler.utils.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.SpiderListener;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/12/1
 */
public class ControllerFinishListener implements SpiderListener {
    private static final Logger LOG = LoggerFactory.getLogger(ControllerFinishListener.class);

    private FetchedRepository fetchedRepository = SpringUtils.getBean(FetchedRepository.class);

    @Override
    public void onSuccess(Request request) {
        if (null == request) {
            return;
        }
        FetchedRequest fetchedRequest = new FetchedRequest();
        fetchedRequest.setStatus(FetchedRequest.Status.WAITING);
        fetchedRequest.setId(request.getUrl());
        fetchedRepository.save(fetchedRequest);
    }

    @Override
    public void onError(Request request) {
        if (null == request) {
            return;
        }
        LOG.error("fail to control request : {}.", request.getUrl());
    }
}
