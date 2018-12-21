package com.lesports.crawler.component.priority;

import com.lesports.crawler.model.RequestMessage;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 15-11-13
 */
public class PriorityProcessor {
    public Priority process(RequestMessage requestMessage) {
        return Priority.DEFAULT;
    }
}
