package com.lesports.msg.util;

import com.google.common.base.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * lesports-projects
 *
 * @author pangchuanxiao
 * @since 15-12-25
 */
public class LeExecutors {
    private static final Logger LOG = LoggerFactory.getLogger(LeExecutors.class);

    /**
     * 重试函数
     *
     * @param retryCount    失败重试次数上限
     * @param retryInterval 失败重试间隔 单位毫秒
     * @param function      重试函数
     * @param param         函数参数
     */
    public static <T> boolean executeWithRetry(int retryCount, int retryInterval, Predicate<T> function, T param, String summary) {
        boolean result = false;
        int count = 0;
        do {
            try {
                result = function.apply(param);
            } catch (Exception e) {
                LOG.warn("{}", e.getMessage(), e);
            } finally {
                LOG.info("execute function : {}, param : {}, result : {}, retry count : {}.", summary, param, result, count);
                count++;
                if (result) {
                    break;
                } else {
                    try {
                        TimeUnit.MILLISECONDS.sleep(retryInterval);
                    } catch (InterruptedException e) {
                        LOG.warn("{}", e.getMessage(), e);
                    }
                }
            }
        } while (!result && count <= retryCount);
        return result;
    }
}
