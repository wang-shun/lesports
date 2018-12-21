package com.lesports.sms.data.parser;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.sms.data.model.TransModel;

import java.util.List;

/**
 * Created by qiaohongxin on 2017/2/27.
 */
public class SingleFileUrlStrategy implements FileUrlStrategy {
    private String fileDir;
    private Long csid;
    private PartnerType partnerType;
    private String annotationType;

    public SingleFileUrlStrategy(String fileDir, Long csid, PartnerType partnerType, String annotationType) {
        this.fileDir = fileDir;
        this.csid = csid;
        this.partnerType = partnerType;
        this.annotationType = annotationType;
    }

    public List<TransModel> getFilesUrl() {
        List<TransModel> lists = Lists.newArrayList();
        TransModel currentFiles = new TransModel();
        currentFiles.setCsid(this.csid);
        currentFiles.setPartnerType(this.partnerType);
        currentFiles.setFileUrl(this.fileDir);
        currentFiles.setAnotationType(this.annotationType);
        lists.add(currentFiles);
        return lists;
    }


}
