package com.lesports.sms.cooperation.service.olympic;

import com.lesports.LeConstants;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.Model.SearchResult;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.OlyUtil;
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
 * Created by zhonglin on 2016/7/25.
 */
@Service("baiduSearchOlyUpdate")
public class OlyBaiduSearchUpdate {

    private static final Logger LOG = LoggerFactory.getLogger(OlyBaiduSearchUpdate.class);
    private static final RestTemplate TEMPLATE = RestTemplateFactory.getTemplate();

    //每个文件最多条数
    public static final int MAX_PAGE = 50;
    //每次读取数据条数
    public static final int COUNT = 100;
    //每个文件最多条数
    public static final int MAX_COUNT = 5000;

    //定时生成更新文件xml
    public void createSearchOlyXml() {
        List<String> fileList = Lists.newArrayList();
        int curPage = 1;
        int fileSize = 1;
        String timePath = BaiduXmlUtil.getTimePath(new Date());
        String beginTime = LeDateUtils.formatYYYYMMDDHHMMSS(LeDateUtils.addHour(new Date(), -1));
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
                            if(!video.getTagIds().contains(OlyUtil.OLY_OLY_TAG_ID)){
                                continue;
                            }
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
                if (CollectionUtils.isEmpty(videos)) {
                    if(curPage!=1){
                        String time = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
                        BaiduXmlUtil.createAndUploadXmlFile(document,"searchOly-"+time,timePath ,fileSize, fileList,Constants.fileuploadflag);
                    }
                    break;
                }

                for (Video video : videos) {
                    Element videoElement = BaiduXmlUtil.createOlyVideoElement(video,Constants.OLY_BAIDU_VIDEO_CH);
                    root.addContent(videoElement);
                }

                //达到文件最大数重新开始
                if(curPage % MAX_PAGE == 0){
                    //创建文件
                    String time = LeDateUtils.formatYYYYMMDDHHMMSS(new Date());
                    BaiduXmlUtil.createAndUploadXmlFile(document,"searchOly-"+time,timePath ,fileSize, fileList,Constants.fileuploadflag);
                    //重新开始
                    root = new Element("urlset");
                    document = new Document(root);
                    fileSize ++;
                }

                //翻页
                curPage++;
            }

            //创建索引文件
            BaiduXmlUtil.createAndUploadOlyIndexXmlFile(fileList, "v.api.letv.com","searchOlyIndex",Constants.fileuploadflag);

        } catch (Exception e) {
            LOG.error("createSearchOlyXml error", e);
        }

        LOG.info("createSearchOlyXml end");
    }

//    //定时生成最热的视频
//    public void createSearchOlyXml() {
//        List<String> fileList = Lists.newArrayList();
//        int curPage = 0;
//        int fileSize = 1;
//        LOG.info("createOlyXml begin");
//        try {
//            //视频列表
//            long totalBeginTime = System.currentTimeMillis();
//
//            Element root = new Element("urlset");
//            Document document = new Document(root);
//            CallerParam caller = CallerUtils.getDefaultCaller();
//            while (true) {
////                PageParam page = PageUtils.createPageParam(curPage,COUNT);
////                GetRelatedVideosParam p = new GetRelatedVideosParam();
////                p.setRelatedId(OlyUtil.OLY_OLY_TAG_ID);
////                List<Long> ids = SopsApis.getVideoIdsByRelatedId(p, page, caller);
////                List<TVideo> videos = SopsApis.getTVideosByIds(ids,caller);
//                LOG.info("curPage: {} , cunt: {} " ,curPage,COUNT );
//                Pageable validPageable = PageUtils.createPageable(curPage, COUNT);
//                InternalQuery internalQuery = new InternalQuery();
//                internalQuery.addCriteria(InternalCriteria.where("clone").is(false));
//                internalQuery.addCriteria(InternalCriteria.where("platforms").is("MOBILE"));
//                internalQuery.addCriteria(InternalCriteria.where("tag_ids").is(OlyUtil.OLY_OLY_TAG_ID));
//                internalQuery.setPageable(validPageable);
//                internalQuery.setSort(new org.springframework.data.domain.Sort(org.springframework.data.domain.Sort.Direction.DESC, "update_at"));
//                List<Video> videos = SopsInternalApis.getVideosByQuery(internalQuery);
//
//                //翻到最后一页了
//                if (CollectionUtils.isEmpty(videos)) {
//                    if(curPage!=1){
//                        BaiduXmlUtil.createAndUploadXmlFile(document,"searchOly","" ,fileSize, fileList, Constants.fileuploadflag);
//                    }
//                    break;
//                }
//
////                for (TVideo video : videos) {
////                    Element videoElement = BaiduXmlUtil.createSearchOlyVideoElement(video);
////                    root.addContent(videoElement);
////                }
//
//                for (Video video : videos) {
//                    if(!video.getPlatforms().contains(Platform.PC))continue;
//                    Element videoElement = BaiduXmlUtil.createSearchOlyVideoElement(video);
//                    root.addContent(videoElement);
//                }
//
//                //翻页
//                curPage++;
//
//                //达到文件最大数重新开始
//                if(curPage % MAX_PAGE == 0){
//                    //创建文件
//                    BaiduXmlUtil.createAndUploadXmlFile(document,"searchOly","" ,fileSize, fileList,Constants.fileuploadflag);
//                    //重新开始
//                    root = new Element("urlset");
//                    document = new Document(root);
//                    fileSize ++;
//                }
//
//            }
//            long totalEndTime = System.currentTimeMillis();
//
//            LOG.info("hot video total time: {}",(totalEndTime -totalBeginTime));
//
//            //创建索引文件
//            BaiduXmlUtil.createAndUploadIndexXmlFile(fileList, "v.api.letv.com","searchOlyIndex",Constants.fileuploadflag);
//
//
//        } catch (Exception e) {
//            LOG.error("createSearchOlyXml error", e);
//        }
//
//        LOG.info("createSearchOlyXml end");
//    }
}
