package com.lesports.sms.data.job;

import com.lesports.storm.job.QuartzJob;

import java.io.Serializable;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/20
 */
public interface DataImportJob extends QuartzJob, Serializable {
    public String fileGenerator();

    public String xmlTemplate();

    public String processor();
}
