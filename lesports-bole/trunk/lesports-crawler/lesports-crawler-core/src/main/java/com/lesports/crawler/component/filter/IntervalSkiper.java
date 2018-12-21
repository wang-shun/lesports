package com.lesports.crawler.component.filter;

import java.util.regex.Pattern;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/24
 */
public class IntervalSkiper {
    private Pattern pattern;
    private Long interval;

    public IntervalSkiper(Pattern pattern, Long interval) {
        this.pattern = pattern;
        this.interval = interval;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }
}
