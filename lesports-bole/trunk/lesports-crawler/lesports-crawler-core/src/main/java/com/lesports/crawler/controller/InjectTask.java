package com.lesports.crawler.controller;

import com.lesports.crawler.component.priority.Priority;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/16
 */
public interface InjectTask  {
    public void inject(Request request, Task task, Priority priority);
}
