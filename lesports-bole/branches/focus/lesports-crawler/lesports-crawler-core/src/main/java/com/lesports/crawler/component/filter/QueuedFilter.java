package com.lesports.crawler.component.filter;

import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

import java.util.List;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/12
 */
public class QueuedFilter implements FilterChain {
    private static final Logger LOG = LoggerFactory.getLogger(QueuedFilter.class);

    private List<FilterChain> filters = Lists.newArrayList();

    public void addFilter(FilterChain duplicateRemover) {
        filters.add(duplicateRemover);
    }

    @Override
    public boolean isValid(Request request, Task task) {
        if (CollectionUtils.isEmpty(filters)) {
            return true;
        }
        for (FilterChain remover : filters) {
            try {
                boolean res = remover.isValid(request, task);
                if (!res) {
                    LOG.info("url : {} is filtered by filter : {}", request.getUrl(), remover.getClass());
                    return false;
                }
            } catch (Exception e) {
                LOG.error("{}", e.getMessage(), e);
            }
        }
        return true;
    }

}
