package com.lesports.sms.data.process;

import com.lesports.msg.core.LeMessage;
import com.lesports.msg.producer.SwiftMessageApis;
import com.lesports.sms.data.model.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
public abstract class BeanProcessor {
    private static Logger LOG = LoggerFactory.getLogger(BeanProcessor.class);

    public abstract Boolean process(Object object);

    public Object updateObject(List<String> configs, Object oldObject, Object newObject) {
        Class<?> matchClass = oldObject.getClass();
        for (String config : configs) {
            String propertyName = config.substring(0, 1).toUpperCase() + config.substring(1); // 将属性的首字符大写，方便构造get，set方法
            try {
                Method m = matchClass.getMethod("get" + propertyName);
                if (m.invoke(newObject) == null) m = matchClass.getMethod("is" + propertyName);
                Class propertyType = m.invoke(newObject).getClass(); // 调用getter方法获取属性值
                if (propertyType.isAssignableFrom(HashSet.class)) {
                    propertyType = java.util.Set.class;
                }
                if (propertyType.isAssignableFrom(ArrayList.class)) {
                    propertyType = List.class;
                }
                if (propertyType.isAssignableFrom(HashMap.class)) {
                    propertyType = Map.class;
                }
                Method m1 = matchClass.getMethod("set" + propertyName, propertyType);
                m1.invoke(oldObject, m.invoke(newObject));

            } catch (Exception e1) {
                LOG.warn("UPDATED ERROR object:{}", newObject.toString());
                continue;
            }

        }
        return oldObject;
    }

    public boolean sendMessage(LeMessage message) {
        int tryCount = 0;
        boolean result = false;
        while (!result && tryCount++ < Constants.MAX_TRY_COUNT_MESSAGE) {
            try {
                result = SwiftMessageApis.sendMsgSync(message);
                LOG.info("message send sucessfully,msg:{}", message.toString());
            } catch (Exception e) {
                LOG.error("fail to send message", e);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return true;
    }
}
