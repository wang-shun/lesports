package com.lesports.sms.data.processor;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 16-2-18
 */
public interface BeanProcessor<T> {

    public Boolean process(String fileType, T obj);
}
