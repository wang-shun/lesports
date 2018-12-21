package com.lesports.crawler.pipeline;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
public interface DataAttachHandler<T> {
    public boolean handle(ResultItems resultItems, Task task, T t);
}
