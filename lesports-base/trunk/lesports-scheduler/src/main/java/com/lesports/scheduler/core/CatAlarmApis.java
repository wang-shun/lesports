package com.lesports.scheduler.core;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.lesports.api.common.Alarm;
import com.lesports.utils.LeProperties;
import com.lesports.utils.http.RestTemplateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * 监控报警
 * Created by denghui on 2017/2/16.
 */
public class CatAlarmApis {

    private static final Logger LOG = LoggerFactory.getLogger(CatAlarmApis.class);
    private static final RestTemplate REST_TEMPLATE = RestTemplateFactory.getTemplate();
    private static final int MAX_TRY_COUNT = 3;
    private static final String CAT_ALERT_CHANNEL = LeProperties.getString("cat.alert.channel", "mail");
    private static final String CAT_ALERT_GROUP = LeProperties.getString("cat.alert.group", "");
    private static final String CAT_ALERT_RECEIVERS = LeProperties.getString("cat.alert.receivers", "");
    private static final String CAT_ALERT_URL = LeProperties.getString("cat.alert.url", "http://10.112.36.219:2281/cat/r/alert");

    public static void errorAlert(final Alarm alarm) {
        String content = "[" + alarm.getServiceName() + "] : " + alarm.getApi()
                + "\n, 描述: " + alarm.getContent()
                + "\n------------------------------------------------------\n " + JSON.toJSONString(alarm.getExtra());
        alert("scheduler_error", alarm.getSubject(), content);
    }

    public static void exceptionAlert(final Alarm alarm, Throwable throwable) {
        String content = "[" + alarm.getServiceName() + "] : " + alarm.getApi()
                + "\n------------------------------------------------------\n " + Throwables.getStackTraceAsString(throwable);
        alert("scheduler_exception", alarm.getSubject(), content);
    }

    private static void alert(final String type, final String title, final String content) {
        int tryCount = 0;
        MultiValueMap<String, String> mvm = new LinkedMultiValueMap<>();
        mvm.add("op", "alert");
        mvm.add("channel", CAT_ALERT_CHANNEL);
        mvm.add("group", CAT_ALERT_GROUP);
        mvm.add("title", title);
        mvm.add("content", content);
        mvm.add("type", type);
        mvm.add("receivers", CAT_ALERT_RECEIVERS);
        LOG.info("begin to alert. url:{}, title:{}, receivers:{}", CAT_ALERT_URL, title, CAT_ALERT_RECEIVERS);
        REST_TEMPLATE.postForObject(CAT_ALERT_URL, mvm, Map.class);
        /*while (tryCount++ < MAX_TRY_COUNT) {
            try {
                LOG.info("begin to alert. url:{}, title:{}, receivers:{}", CAT_ALERT_URL, title, CAT_ALERT_RECEIVERS);
                REST_TEMPLATE.postForObject(CAT_ALERT_URL, mvm, Map.class);
                break;
            } catch (Exception e) {
                LOG.error("fail to alert, sleep and try again. url:{}, title:{}, receivers:{}, error:{}", CAT_ALERT_URL, title, CAT_ALERT_RECEIVERS, e.getMessage());
            }
        }*/
    }

    public static void main(String[] args) {
        Alarm alarm = new Alarm();
        alarm.setSubject("异常报警：状态扫描时报错");
        alarm.setServiceName("测试serviceName");
        alarm.setApi("SopsInternalApis.episodeStatusAlarm()");
        alarm.setContent("test content");
        //errorAlert(alarm);
        Exception exception = new RuntimeException("报了一个错");
        exceptionAlert(alarm, exception.fillInStackTrace());
    }
}
