package com.lesports.crawler.scheduler;

import com.lesports.crawler.utils.Constants;

/**
 * 抽象定时任务
 * 
 * @author denghui
 *
 */
public abstract class SchedulerTask implements Runnable {
    protected Integer frequency = Constants.SCHEDULER_DELAY;

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(Integer frequency) {
        this.frequency = frequency;
    }
}
