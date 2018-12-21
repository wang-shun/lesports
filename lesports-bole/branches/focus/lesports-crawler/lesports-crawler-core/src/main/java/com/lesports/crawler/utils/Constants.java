package com.lesports.crawler.utils;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/20
 */
public abstract class Constants {
    public static final String KEY_ATTACH_RESULT = "attach_result";
    public static final String KEY_IS_DATA_NULL = "is_data_null";
    public static final String KEY_IS_HANDLER_NULL = "is_handler_null";
    public static final String KEY_URL = "url";
    public static final String KEY_DATA = "data";
    public static final int SCHEDULER_INIT_DELAY = 0 * 60;//TIME UNIT SECOND
    public static final int SCHEDULER_DELAY = 30 * 60;//TIME UNIT SECOND
    public static final int CONCURRENT_THREAD_PER_HOST = 5;//每台机器上worker并发数
    public static final int SUSPEND_TASK_RETRY_COUNT = 3;//每台机器上worker并发数
    public static final int SEED_LOAD_DELAY = 1800; // SECONDS
}
