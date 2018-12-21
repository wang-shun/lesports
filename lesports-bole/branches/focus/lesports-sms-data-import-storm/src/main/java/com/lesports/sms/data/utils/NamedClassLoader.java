package com.lesports.sms.data.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/21
 */
public class NamedClassLoader {
    private static final Logger LOG = LoggerFactory.getLogger(NamedClassLoader.class);
    private static NamedClassLoader classLoader;

    private NamedClassLoader() {

    }

    public static NamedClassLoader newInstance() {
        synchronized (NamedClassLoader.class) {
            if (null == classLoader) {
                classLoader = new NamedClassLoader();
            }
        }
        return classLoader;
    }

    public <T> T getBean(String name) {
        Class clazz = getClazz(name);
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException e) {
            LOG.error("{}", e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        throw new RuntimeException("can not init class " + clazz);
    }

    public Class getClazz(String name) {
        Class clazz = null;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        if (null == clazz) {
            throw new RuntimeException("no bean for name " + name);
        }
        return clazz;
    }

}
