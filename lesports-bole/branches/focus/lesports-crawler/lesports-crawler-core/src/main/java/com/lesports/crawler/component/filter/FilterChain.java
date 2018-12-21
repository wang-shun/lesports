package com.lesports.crawler.component.filter;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/12
 */
public interface FilterChain {
    public boolean isValid(Request request, Task task);
}
