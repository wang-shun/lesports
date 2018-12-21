package com.lesports.sms.data.model;


import com.lesports.qmt.sbd.model.PartnerType;

/**
 * Created by qiaohongxin on 2016/11/17.
 */
public class TransModel {
    private Long csid;
    private String anotationType;
    private PartnerType partnerType;
    private String fileUrl;

    public Long getCsid() {
        return csid;
    }

    public void setCsid(Long csid) {
        this.csid = csid;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public PartnerType getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(PartnerType partnerType) {
        this.partnerType = partnerType;
    }

    public String getAnotationType() {
        return anotationType;
    }

    public void setAnotationType(String anotationType) {
        this.anotationType = anotationType;
    }
}
