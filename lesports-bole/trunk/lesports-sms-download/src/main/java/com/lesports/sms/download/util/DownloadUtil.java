package com.lesports.sms.download.util;

import com.alibaba.fastjson.JSONObject;
import com.lesports.sms.api.vo.TVideo;
import com.lesports.utils.MmsApis;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/8/26.
 */
public class DownloadUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadUtil.class);
    private static RestTemplate template =new RestTemplate();

    public static boolean downloadVideo(Long vid,String localPath,String localName){
        boolean downloadFlag = false;
        Map<String,Object> videoData = MmsApis.getVideoData(vid);
        if(videoData == null || videoData.get("mid")==null){
            LOG.info("get mid is null vid:{}",vid);
            return false;
        }
        long mid = Long.parseLong(videoData.get("mid").toString().replace(",",""));
        LOG.info("mid:{}",mid);
        StringBuffer paramMap = new StringBuffer();
        paramMap.append("?").append("mmsid=").append(mid);
        paramMap.append("&").append("vtype=").append("13");
        paramMap.append("&").append("platid=").append(16);
        paramMap.append("&").append("splatid=").append(1601);
        paramMap.append("&").append("version=").append("2.0");
        paramMap.append("&").append("playid=").append(2);
        paramMap.append("&").append("tss=").append("tvts");
        paramMap.append("&").append("fromat=").append("json");
        String paramStr = paramMap.toString();
        String result = template.getForObject("http://i.api.letv.com/geturlv2"+paramStr,String.class);
        try{

            Map<String,Object> resultMap = JSONObject.parseObject(result, new HashMap<String, Object>().getClass());
            if(resultMap.get("data")==null){
                LOG.info("get data is null result: {} ",result);
            }
            List<Map<String,Object>> dataList = (List<Map<String,Object>>)resultMap.get("data");
            if(CollectionUtils.isEmpty(dataList)){
                LOG.info("get dataList is null result: {} ",result);
            }
            Map<String,Object> dataMap = dataList.get(0);
            if(dataMap.get("infos")==null){
                LOG.info("get infos is null result: {} ",result);
            }
            List<Map<String,Object>> infoList = (List<Map<String,Object>>)dataMap.get("infos");
            if(CollectionUtils.isEmpty(infoList)){
                LOG.info("get infoList is null result: {} ",result);
            }
            Map<String,Object> infosMap = infoList.get(0);
            if(infosMap.get("storePath")==null){
                LOG.info("get storePath is null result: {} ",result);
            }
            LOG.info("download file:{} ",localPath+localName);
            downloadFlag = HttpUtils.downloadFile(Constants.filetoutiaovideodownloadurl + infosMap.get("storePath") + "&mmsid=" + mid, localPath + localName);

        }
        catch (Exception e){
            LOG.error("result: " +result);
            e.printStackTrace();
            return false;
        }
        return downloadFlag;
    }
}
