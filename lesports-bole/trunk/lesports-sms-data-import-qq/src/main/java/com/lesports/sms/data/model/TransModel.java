package com.lesports.sms.data.model;

/**
 * Created by qiaohongxin on 2016/11/17.
 */
public class TransModel {
    //文件或者接口所属赛季Id
    private Long csid;
    //文件或者接口所有的解析器
    private String anotationType;
    private String partnerType;
    //文件或者接口的下载地址
    private String fileUrl;
    //文件或者接口的第三方id(可没有)
    private String partnerId;

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

    public String getPartnerType() {
        return partnerType;
    }

    public void setPartnerType(String partnerType) {
        this.partnerType = partnerType;
    }

    public String getAnotationType() {
        return anotationType;
    }

    public void setAnotationType(String anotationType) {
        this.anotationType = anotationType;
    }

    public String getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(String partnerId) {
        this.partnerId = partnerId;
    }
}
