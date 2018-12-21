package com.lesports.sms.cooperation.service.baidu;

import com.google.common.collect.Maps;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.VideoContentType;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.cooperation.util.BaiduXmlUtil;
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
@Service("baiduMobileHistory")
public class BaiduMobileHistory {

//    private static final Logger LOG = LoggerFactory.getLogger(BaiduMobileHistory.class);
//
//    //每个文件最多条数
//    public static final int MAX_PAGE = 150;
//    //每次读取数据条数
//    public static final int COUNT = 100;
//    //正式或者测试  上传标志
//    public static final boolean UPLOAD_FLAG = false;
//
//
//    //一次性生成百度搜索的历史文件
//    public void createMobileHistoryXml() {
//        List<String> fileList = Lists.newArrayList();
//        int curPage = 1;
//        int fileSize = 1;
//
//        LOG.info("createMobileHistoryXml begin");
//        try {
//
//            //视频列表
//            List<Video> videos = Lists.newArrayList();
//
//            Element root = new Element("urlset");
//            Document document = new Document(root);
//
//            while (true) {
//                LOG.info("videos curPage :{}", curPage);
//                Pageable validPageable = PageUtils.createPageable(curPage, COUNT);
//                InternalQuery internalQuery = new InternalQuery();
//                internalQuery.addCriteria(InternalCriteria.where("deleted").is(false));
//
//                internalQuery.addCriteria(InternalCriteria.where("platforms").is("MOBILE"));
//                internalQuery.setPageable(validPageable);
//                try{
//                    videos = SopsInternalApis.getVideosByQuery(internalQuery);
//                }
//                catch (Exception ex){
//                    LOG.error("createMobileHistoryXml error", ex);
//                }
//
//
//                //翻到最后一页了
//                if (CollectionUtils.isEmpty(videos)) {
//                    if(curPage!=1){
//                        BaiduXmlUtil.createAndUploadXmlFile(document, "mobileHistory", "", fileSize, fileList, UPLOAD_FLAG);
//                    }
//                    break;
//                }
//
//                for (Video video : videos) {
//                    Element videoElement = BaiduXmlUtil.createMobileVideoElement(video);
//                    root.addContent(videoElement);
//                }
//
//
//
//                //达到文件最大数重新开始
//                if(curPage % MAX_PAGE == 0){
//                    //创建文件
//                    BaiduXmlUtil.createAndUploadXmlFile(document,"mobileHistory","" ,fileSize, fileList,UPLOAD_FLAG);
//                    //重新开始
//                    root = new Element("urlset");
//                    document = new Document(root);
//                    fileSize ++;
//                }
//
//                //翻页
//                curPage++;
//            }
//
//            //创建索引文件
//            BaiduXmlUtil.createAndUploadIndexXmlFile(fileList, "v.api.letv.com","mobileHistoryIndex",UPLOAD_FLAG);
//
//
//        } catch (Exception e) {
//            LOG.error("createMobileHistoryXml error", e);
//        }
//
//        LOG.info("createMobileHistoryXml end");
//    }

}