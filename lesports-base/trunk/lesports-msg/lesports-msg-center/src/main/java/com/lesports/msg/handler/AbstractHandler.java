package com.lesports.msg.handler;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.lesports.api.common.CallerParam;
import com.lesports.api.common.CountryCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.api.common.LanguageCode;
import com.lesports.msg.core.MessageSource;
import org.apache.thrift.TBase;
import org.slf4j.Logger;


/**
 * lesports-projects.
 *
 * @author: pangchuanxiao
 * @since: 2015/7/10
 */
public abstract class AbstractHandler {

    boolean execute(long matchId, String name, Function<Long, Boolean> function) {
        try {
            boolean result = function.apply(matchId);
            if (!result) {
                getLogger().error("fail to delete {} cache for {}.", name, matchId);
            }
            return result;
        } catch (Exception e) {
            getLogger().error("fail to delete {} cache for {} error : {}", name, matchId, e.getMessage(), e);
        }
        return false;
    }

    boolean execute(TBase entity, String name, Function<TBase, Boolean> function) {
        try {
            boolean result = function.apply(entity);
            if (!result) {
                getLogger().error("fail to {} for {}.", name, JSONObject.toJSONString(entity));
            }
            return result;
        } catch (Exception e) {
            getLogger().error("fail to {} for {} error : {}", name, JSONObject.toJSONString(entity), e.getMessage(), e);
        }
        return false;
    }

    abstract Logger getLogger();

    protected CallerParam getCaller(MessageSource messageSource) {
        if (messageSource == MessageSource.LETV_MMS) {
            CallerParam caller = new CallerParam();
            caller.setCallerId(1001L); // not used callerId
            caller.setCountry(CountryCode.CN);
            caller.setLanguage(LanguageCode.ZH_CN);
            return caller;
        } else if (messageSource == MessageSource.LETV_MMS_HK) {
            CallerParam caller = new CallerParam();
            caller.setCallerId(1001L); // not used callerId
            caller.setCountry(CountryCode.HK);
            caller.setLanguage(LanguageCode.ZH_HK);
            return caller;
        }
        throw new RuntimeException("fail to get country code by " + messageSource);
    }
}
