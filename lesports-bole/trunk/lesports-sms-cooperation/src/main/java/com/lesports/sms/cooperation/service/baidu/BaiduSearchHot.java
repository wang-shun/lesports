package com.lesports.sms.cooperation.service.baidu;

import com.google.common.collect.Maps;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
import com.lesports.sms.cooperation.util.Constants;
import com.lesports.sms.cooperation.util.FtpUtil;
import com.lesports.sms.cooperation.util.XmlUtil;
import com.lesports.sms.model.Video;
import com.lesports.utils.LeProperties;
import com.lesports.utils.PageUtils;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.commons.collections.MapUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhonglin on 2016/3/29.
 */
@Service("baiduSearchHot")
public class BaiduSearchHot {


    private static final Logger LOG = LoggerFactory.getLogger(BaiduSearchHot.class);

    //每个文件最多条数
    public static final int MAX_PAGE = 100;
    //每次读取数据条数
    public static final int COUNT = 50;
    //每个文件最多条数
    public static final int MAX_COUNT = 5000;
    //正式或者测试  上传标志
//    public static final boolean UPLOAD_FLAG = Constants.fileuploadflag;


    //定时生成最热的视频
    public void createSearchHotXml() {
        List<String> fileList = Lists.newArrayList();
        int curPage = 0;
        int fileSize = 1;
        String timePath = BaiduXmlUtil.getTimePath(new Date());
        LOG.info("createHotXml begin");
        try {

            //视频列表
            List<Video> videos = Lists.newArrayList();

            long totalBeginTime = System.currentTimeMillis();

//            //一次获取所有最新的视频id
//            Pageable validPageable = PageUtils.createPageable(0, 50000);
//            InternalQuery internalQuery = new InternalQuery();
//            internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
//            internalQuery.addCriteria(InternalCriteria.where("platforms").is("PC"));
//            internalQuery.setPageable(validPageable);
//            long beginTime = System.currentTimeMillis();
//            List<Long> ids = SopsInternalApis.getVideoIdsByQuery(internalQuery);
//            long endTime = System.currentTimeMillis();
//            LOG.info("hot video ids time: " + (endTime -beginTime));
//
//            //每5000条生成一个索引文件
//            for(int i =0;i<10;i++){
//                Element root = new Element("urlset");
//                Document document = new Document(root);
//                List<Long> subIds = ids.subList(i*MAX_COUNT,(i+1)*MAX_COUNT);
//                //根据id获取video
//                videos = SopsInternalApis.getVideosByIds(subIds);
//                for (Video video : videos) {
//                    Element videoElement = XmlUtil.createSearchVideoElement(video);
//                    root.addContent(videoElement);
//                }
//                XmlUtil.createAndUploadXmlFile(document,"searchHot","" ,i+1, fileList,UPLOAD_FLAG);
//            }

            Element root = new Element("urlset");
            Document document = new Document(root);

            while (true) {
                Pageable validPageable = PageUtils.createPageable(curPage, COUNT);
                InternalQuery internalQuery = new InternalQuery();
                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
                internalQuery.addCriteria(InternalCriteria.where("platforms").is("MOBILE"));
                internalQuery.setPageable(validPageable);
//                internalQuery.setSort(new Sort(Sort.Direction.DESC,"create_at"));
                long beginTime = System.currentTimeMillis();
                videos = SopsInternalApis.getVideosByQuery(internalQuery);
                long endTime = System.currentTimeMillis();
//                LOG.info("hot video ids time: {}, page: {}",(endTime -beginTime),curPage);

                //翻到最后一页了
                if (CollectionUtils.isEmpty(videos)) {
                    if(curPage!=1){
                        BaiduXmlUtil.createAndUploadXmlFile(document,"searchHot","" ,fileSize, fileList,Constants.fileuploadflag);
                    }
                    break;
                }

                for (Video video : videos) {
                    Element videoElement = BaiduXmlUtil.createVideoElement(video,Constants.SEARCH_BAIDU_VIDEO_CH);
                    root.addContent(videoElement);
                }

                //翻页
                curPage++;

                //达到文件最大数重新开始
                if(curPage>0 && curPage % MAX_PAGE == 0){
                    //创建文件
                    BaiduXmlUtil.createAndUploadXmlFile(document,"searchHot","" ,fileSize, fileList,Constants.fileuploadflag);
                    //重新开始
                    root = new Element("urlset");
                    document = new Document(root);
                    fileSize ++;
                }

                if(fileSize > 10) break;
            }
            long totalEndTime = System.currentTimeMillis();

            LOG.info("hot video total time: {}",(totalEndTime -totalBeginTime));

            //创建索引文件
            BaiduXmlUtil.createAndUploadIndexXmlFile(fileList, "v.api.le.com","searchHotIndex",Constants.fileuploadflag);


        } catch (Exception e) {
            LOG.error("createSearchHotXml error", e);
        }

        LOG.info("createSearchHotXml end");
    }

}