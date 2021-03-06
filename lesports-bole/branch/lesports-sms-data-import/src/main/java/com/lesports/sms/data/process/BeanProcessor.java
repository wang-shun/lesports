package com.lesports.sms.data.process;


import com.lesports.qmt.sbd.model.Partner;
import com.lesports.qmt.sbd.model.PartnerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/5/11.
 */
public abstract class BeanProcessor {
    private static Logger LOG = LoggerFactory.getLogger(BeanProcessor.class);

    public abstract Boolean process(Object object);

    public Partner getPartner(String partnerId, PartnerType type) {
        Partner partner = new Partner();
        partner.setId(partnerId);
        partner.setType(type);
        return partner;
    }

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
                Method m1 = matchClass.getMethod("set" + propertyName, propertyType);
                m1.invoke(oldObject, m.invoke(newObject));

            } catch (Exception e1) {
                LOG.warn("UPDATED ERROR object:{}", newObject.toString());
                continue;
            }

        }
        return oldObject;
    }
}
