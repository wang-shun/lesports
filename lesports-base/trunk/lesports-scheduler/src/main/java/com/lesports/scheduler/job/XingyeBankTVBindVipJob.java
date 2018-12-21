package com.lesports.scheduler.job;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import com.lesports.utils.LeProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lesports.scheduler.job.support.AbstractJob;
import com.lesports.scheduler.utils.Md5Encrypt;
import com.lesports.user.api.client.ActivityApiClient;
import com.lesports.user.model.TActivityTVInfo;

/**
 * Created by wangjiahui on 2016/11/25.
 */
@Component("xingyeBankTVBindVipJob")
public class XingyeBankTVBindVipJob extends AbstractJob {

    private static final Logger log = LoggerFactory.getLogger(XingyeBankTVBindVipJob.class);
    private final static String TV_VIP_SIGN_KEY = "ubas34778038s7jg47a5dgu898i24gs";
    private static final String deviceBindUrl = LeProperties.getString("device.bind.url.v2", "http://yuanxian.letv.com/letv/deviceBind.ldo?");

    @Scheduled(cron = "0 0/2 * * * *")
    public void scheduleBindVipToMac() {
        log.info("XingyeBankTVBindVipJob [scheduleBindVipToMac] [start]");
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            List<TActivityTVInfo> macs = ActivityApiClient.findBindFailedMac();
            if (CollectionUtils.isNotEmpty(macs)) {
                for (TActivityTVInfo tinfo : macs) {
                    HashMap<String, String> multiValueMap = new HashMap();
                    multiValueMap.put("deviceType", "1");
                    multiValueMap.put("mac", tinfo.getMac());
                    multiValueMap.put("deviceDetailType", "-1");
                    multiValueMap.put("businessId", "10008");
                    HashMap resouceListMap = Maps.newHashMap();
                    resouceListMap.put("vipType", 3);
                    resouceListMap.put("resourceType", 1);
                    resouceListMap.put("resourceId", 36);
                    List<Map> list = Lists.newArrayList();
                    list.add(resouceListMap);
                    String temp = JSONArray.toJSONString(list);
                    multiValueMap.put("resourceList", temp);
                    multiValueMap.put("type", "syncByMac");
                    String code = bindVipToMac(multiValueMap);
                    if (StringUtils.isNotBlank(code)) {
                        if (StringUtils.equals("0", code)) {
                            ActivityApiClient.updateTVInfo(tinfo.getMac(), StringUtils.EMPTY, code, StringUtils.EMPTY);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("XingyeBankTVBindVipJob [scheduleBindVipToMac]", e);
        }
        stopwatch.stop();
        long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        log.info("XingyeBankTVBindVipJob [scheduleBindVipToMac] [end] elapsed:{}", elapsed);
    }

    private String bindVipToMac(Map<String, String> multiValueMap) {
        String result = StringUtils.EMPTY;
        String bindVipToMacURL = deviceBindUrl;
        try {
            String sign = generateSign(multiValueMap);
            multiValueMap.put("sign", sign);
            multiValueMap.put("resourceList", URLEncoder.encode(multiValueMap.get("resourceList"), "GBK"));
            bindVipToMacURL += getUrlParamsByMap(multiValueMap);
            CloseableHttpResponse response = null;
            CloseableHttpClient client = null;
            try {
                client = HttpClients.createDefault();
                HttpGet get = new HttpGet(bindVipToMacURL);
                response = client.execute(get);
                HttpEntity entity = response.getEntity();
                if (null != entity) {
                    log.info("[XingyeBankTVBindVipJob] [bindVipToMac HttpClient GET status]:" + response.getStatusLine());
                    String entityString = EntityUtils.toString(entity);
                    JSONObject object = JSON.parseObject(entityString);
                    String status = StringUtils.EMPTY;
                    if (object != null) {
                        Map values = (Map) object.get("values");
                        if (StringUtils.equals("0", String.valueOf(object.get("code")))) {
                            JSONArray bindTimes = (JSONArray) values.get("bindtimeList");
                            for (Object o : bindTimes) {
                                JSONObject map = (JSONObject) o;
                                log.info("[XingyeBankTVBindVipJob] [bindVipToMac success] [mac={},viptype={},bindtime={}]",
                                        values.get("mac"), map.get("vipType"), map.get("bindTime"));
                            }
                            if (CollectionUtils.isNotEmpty(bindTimes)) {
                                result = String.valueOf(object.get("code"));
                            }
                        } else {
                            result = String.valueOf(object.get("code"));
                            log.info("[XingyeBankTVBindVipJob] [bindVipToMac HttpClient GET] [code={},msg={},url={}]",
                                    String.valueOf(object.get("code")), values.get("msg"), bindVipToMacURL);
                        }
                    }
                }
            } catch (Exception e) {
                log.error("XingyeBankTVBindVipJob [bindVipToMac HttpClient GET][url={}]", bindVipToMacURL, e);
            } finally {
                if (response != null) {
                    response.close();
                }
                if (client != null) {
                    client.close();
                }
            }
        } catch (Exception e) {
            log.error("XingyeBankTVBindVipJob [bindVipToMac][url={}]", bindVipToMacURL, e);
        }
        return result;
    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    private String getUrlParamsByMap(Map<String, String> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }

    /**
     * 生成签名
     *
     * @param multimap
     * @return
     */
    private String generateSign(Map<String, String> multimap) {
        Map<String, String> data = new TreeMap<String, String>();
        data.putAll(multimap);
        data.remove("sign");
        StringBuilder buffer = new StringBuilder();
        for (String k : data.keySet()) {
            String v = data.get(k);
            if (org.apache.commons.lang.StringUtils.isNotEmpty(v)) {
                buffer.append(k + "=" + v + "&");
            }
        }
        buffer.append("key=" + TV_VIP_SIGN_KEY);
        return Md5Encrypt.md5(buffer.toString());
    }
}
