package com.lesports.sms.cooperation.service.baidu;

import com.lesports.LeConstants;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.Model.SearchResult;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.model.Video;
import com.lesports.utils.LeDateUtils;
import com.lesports.utils.http.RestTemplateFactory;
import jersey.repackaged.com.google.common.collect.Lists;
import org.jdom.Document;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import java.util.Date;
import java.util.List;

/**
 * Created by zhonglin on 2016/3/29.
 */
@Service("baiduSearchUpdate")
public class BaiduSearchUpdate {


    private static final Logger LOG = LoggerFactory.getLogger(BaiduSearchUpdate.class);

    //每个文件最多条数
    public static final int MAX_PAGE = 50;
    //每次读取数据条数
    public static final int COUNT = 100;
    //正式或者测试  上传标志
//    public static final boolean UPLOAD_FLAG = Constants.fileuploadflag;

    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();



    //定时生成更新文件xml
    public void createSearchUpdateXml() {
        List<String> fileList = Lists.newArrayList();
        int curPage = 1;
        int fileSize = 1;
        String timePath = BaiduXmlUtil.getTimePath(new Date());
        String beginTime = LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.addHour(new Date(),-1));
        String endTime = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
        LOG.info("createUpdateXml begin beginTime: {}",beginTime);
        try {

            //视频列表
            List<Video> videos;


            Element root = new Element("urlset");
            Document document = new Document(root);

            while (true) {
                LOG.info("videos curPage :{}", curPage);

                StringBuffer paramMap = new StringBuffer();
                paramMap.append("?").append("caller=").append(LeConstants.LESPORTS_PC_CALLER_CODE);
                paramMap.append("&").append("page=").append(curPage-1);
                paramMap.append("&").append("count=").append(COUNT);
                paramMap.append("&").append("country=").append(1);
                paramMap.append("&").append("platform=").append(1);
                paramMap.append("&").append("updateAt=").append(beginTime);
                paramMap.append("&").append("updateAt=").append(endTime);
                SearchResult result  = TEMPLATE.getForObject("http://internal.api.lesports.com/search/v1/s/sms/videos/"+paramMap.toString(),SearchResult.class);

                if(result == null){
                    LOG.info("result is null");
                }

                videos = Lists.newArrayList();
                if(result.getData() != null){
                    if (result.getData().getRows() != null ) {
                        for(SearchResult.Row row:result.getData().getRows()){
                            Video video = SopsInternalApis.getVideoById(row.getId());
                            videos.add(video);
                        }
                    }
                    else{
                        LOG.info("videos is null page:{} " + curPage);
                    }
                }
                else{
                    LOG.info("searchData is null");
                }



                //翻到最后一页了
//                if (curPage == 1) {
//                    curPage ++;
                if (CollectionUtils.isEmpty(videos)) {
                    if(curPage!=1){
                        String time = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
                        BaiduXmlUtil.createAndUploadXmlFile(document,"searchUpdate-"+time,timePath ,fileSize, fileList,Constants.fileuploadflag);
                    }
                    break;
                }

                for (Video video : videos) {
                    Element videoElement = BaiduXmlUtil.createVideoElement(video,Constants.SEARCH_BAIDU_VIDEO_CH);
                    root.addContent(videoElement);
                }

                //达到文件最大数重新开始
                if(curPage % MAX_PAGE == 0){
                    //创建文件
                    String time = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
                    BaiduXmlUtil.createAndUploadXmlFile(document,"searchUpdate-"+time,timePath ,fileSize, fileList,Constants.fileuploadflag);
                    //重新开始
                    root = new Element("urlset");
                    document = new Document(root);
                    fileSize ++;
                }

                //翻页
                curPage++;
            }

            //创建索引文件
            BaiduXmlUtil.addAndUploadIndexXmlFile(fileList, "v.api.le.com","searchUpdateIndex.xml",Constants.fileuploadflag);

            //将新建的更新文件加入到历史索引文件中
            BaiduXmlUtil.addAndUploadIndexXmlFile(fileList,"v.api.le.com","searchHistoryIndex.xml",Constants.fileuploadflag);


        } catch (Exception e) {
            LOG.error("createSearchUpdateXml error", e);
        }

        LOG.info("createSearchUpdateXml end");
    }



}