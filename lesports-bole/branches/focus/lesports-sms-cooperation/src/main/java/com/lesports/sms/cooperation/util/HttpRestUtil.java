package com.lesports.sms.cooperation.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lesports.LeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by qiaohongxin on 2015/12/30.
 */
public class HttpRestUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpRestUtil.class);
    private static RestTemplate template =new RestTemplate();
    //通过http接口获取数据
    public static JSONObject getData(String requestUrl) {
        try {
            JSONObject fileContent = template.getForObject(requestUrl, JSONObject.class);
            return fileContent;
        } catch (Exception e) {
            logger.warn("can not get the content from" + requestUrl, e);
            return null;
        }
    }

    public static void main(String[] args){
//        StringBuffer paramMap = new StringBuffer();
//        paramMap.append("?").append("mmsid=").append(58961898);
//        paramMap.append("&").append("vtype=").append("13");
//        paramMap.append("&").append("platid=").append(16);
//        paramMap.append("&").append("splatid=").append(1601);
//        paramMap.append("&").append("version=").append("2.0");
//        paramMap.append("&").append("playid=").append(2);
//        paramMap.append("&").append("tss=").append("tvts");
//        paramMap.append("&").append("fromat=").append("json");
//        System.out.println("url: " + "http://i.api.letv.com/geturlv2"+paramMap.toString());
//        String result = template.getForObject("http://i.api.letv.com/geturlv2"+paramMap.toString(),String.class);
//        Map<String,Object> resultMap = JSONObject.parseObject(result,new HashMap<String,Object>().getClass());
//        List<Map<String,Object>> dataList = (List<Map<String,Object>>)resultMap.get("data");
//        Map<String,Object> dataMap = dataList.get(0);
//        System.out.println("dataMap: " + dataMap);
//        List<Map<String,Object>> infoList = (List<Map<String,Object>>)dataMap.get("infos");
//        Map<String,Object> infosMap = infoList.get(0);
//        System.out.println("infosMap: " + infosMap);
//        System.out.println("storePath: " + infosMap.get("storePath"));
//
//        try{
//            HttpUtils.downloadFile("http://g3.letv.com/api/geturl?fmt=0&b=588800&splatid=10301&platid=103&storepath="+infosMap.get("storePath")+"&mmsid=58961898", "e:\\58961898.mp4");
//        }
//        catch (Exception e){
//            e.printStackTrace();
//        }

//        System.out.println(LeIdApis.checkIdTye(1009180005));


    }

}
