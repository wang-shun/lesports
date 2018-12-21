package com.lesports.sms.data.parser;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.sms.data.model.TransModel;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by qiaohongxin on 2017/2/27.
 */
public class HttpDecoratorFileUrl extends DecoratorFileUrlStrategy  {
    private String httpUrl;

    public HttpDecoratorFileUrl(FileUrlStrategy fileUrlStrategy, String httpUrl) {
        super(fileUrlStrategy);
        this.httpUrl = httpUrl;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public List<TransModel> getFilesUrl() {
        List<TransModel> transModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(this.getFileUrlStrategy().getFilesUrl())) return transModels;
        for (TransModel transModel : this.getFileUrlStrategy().getFilesUrl()) {
            transModel.setFileUrl(httpUrl + transModel.getFileUrl());
            transModels.add(transModel);
        }
        return transModels;
    }
}
