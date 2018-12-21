package com.lesports.sms.download.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lesports.utils.MmsApis;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
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
//        List<Long> list = Lists.newArrayList();
//        list.add(26374340L);
//        list.add(26365235L);
//        list.add(26336018L);
//        list.add(26356097L);
//        list.add(26351468L);
//        list.add(26342437L);
//        list.add(26345356L);
//        list.add(26344594L);
//        list.add(26345382L);
//        list.add(26353590L);
//        list.add(26365229L);
//        list.add(26375244L);
//        for(Long vid:list){
//            Map<String,Object> videoData = MmsApis.getVideoData(vid);
//            if(videoData == null || videoData.get("mid")==null){
//                System.out.println("get mid is null vid: "+ vid);
//                continue;
//            }
//            long mid = Long.parseLong(videoData.get("mid").toString().replace(",",""));
//            StringBuffer paramMap = new StringBuffer();
//            paramMap.append("?").append("mmsid=").append(mid);
//            paramMap.append("&").append("vtype=").append("13");
//            paramMap.append("&").append("platid=").append(16);
//            paramMap.append("&").append("splatid=").append(1601);
//            paramMap.append("&").append("version=").append("2.0");
//            paramMap.append("&").append("playid=").append(2);
//            paramMap.append("&").append("tss=").append("tvts");
//            paramMap.append("&").append("fromat=").append("json");
//            String result = template.getForObject("http://i.api.letv.com/geturlv2"+paramMap.toString(),String.class);
//            Map<String,Object> resultMap = JSONObject.parseObject(result,new HashMap<String,Object>().getClass());
//            List<Map<String,Object>> dataList = (List<Map<String,Object>>)resultMap.get("data");
//            Map<String,Object> dataMap = dataList.get(0);
//            List<Map<String,Object>> infoList = (List<Map<String,Object>>)dataMap.get("infos");
//            Map<String,Object> infosMap = infoList.get(0);
//            try{
//                HttpUtils.downloadFile("http://g3.letv.com/api/geturl?fmt=0&b=588800&splatid=10301&platid=103&storepath="+infosMap.get("storePath")+"&mmsid=58961898", "e:\\top12\\"+vid+".mp4");
//            }
//            catch (Exception e){
//                e.printStackTrace();
//            }
//        }

        try{
            File file = new File("E:\\toutiao\\csl\\csl.xml");
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(file);
            Element rootElement = document.getRootElement();

            List<Element> items = rootElement.getChildren("item");
            for(Element item:items){
                System.out.println(item.getChildText("id"));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }


    }

}
