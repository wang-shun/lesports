package com.lesports.crawler.pipeline;

import com.lesports.crawler.model.FetchedRequest;
import com.lesports.crawler.repository.FetchedRepository;
import com.lesports.crawler.utils.Constants;
import com.lesports.crawler.utils.SpringUtils;
import com.lesports.utils.LeDateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Date;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/12
 */
public class FetchedPipeline implements Pipeline {
    private static final Logger LOG = LoggerFactory.getLogger(FetchedPipeline.class);

    private FetchedRepository fetchedRepository = SpringUtils.getBean(FetchedRepository.class);

    @Override
    public void process(ResultItems resultItems, Task task) {
        String url = (String) resultItems.get(Constants.KEY_URL);
        if (null != url) {
            FetchedRequest fetchedObject = new FetchedRequest();
            fetchedObject.setId(url);
            fetchedObject.setFetchTime(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
            fetchedObject.setAttachResult((Boolean) resultItems.get(Constants.KEY_ATTACH_RESULT));
            fetchedObject.setIsAttachHandlerNull((Boolean) resultItems.get(Constants.KEY_IS_HANDLER_NULL));
            fetchedObject.setIsDataNull((Boolean) resultItems.get(Constants.KEY_IS_DATA_NULL));
            fetchedObject.setStatus(FetchedRequest.Status.FETCHED);
            fetchedRepository.save(fetchedObject);
            LOG.info("fetch {} finish.", url);
        }
    }
}
