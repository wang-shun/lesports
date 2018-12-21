package com.lesports.msg.consumer;

import com.lesports.msg.core.MessageSource;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/9
 */
public class HkMmsMessageConsumer extends MmsMessageAbstractConsumer {
    @Override
    MessageSource messageSource() {
        return MessageSource.LETV_MMS_HK;
    }
}
