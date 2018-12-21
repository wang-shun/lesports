package com.lesports.sms.data.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

/**
 * Created by qiaohongxin on 2015/12/30.
 */
public class HttpRestUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpRestUtil.class);
    private static RestTemplate template =new RestTemplate();
    //通过http接口获取数据
    public static String getData(String requestUrl) {
        try {
            String fileContent = template.getForObject(requestUrl, String.class);
            return fileContent;
        } catch (Exception e) {
            logger.warn("can not get the content from" + requestUrl, e);
            return null;
        }
    }

}
