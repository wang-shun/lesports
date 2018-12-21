package com.lesports.sms.data.parser;

import com.google.common.collect.Lists;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Created by qiaohongxin on 2017/2/27.
 */
public class FTPDecoratorFileUrl extends DecoratorFileUrlStrategy {
    private String ftpName;
    private int ftpPort;
    private String userName;
    private String password;
    private String dir;

    public FTPDecoratorFileUrl(FileUrlStrategy fileUrlStrategy, String ftpName, int ftpPort, String userName, String password, String dir) {
        super(fileUrlStrategy);
        this.ftpPort = ftpPort;
        this.userName = userName;
        this.password = password;
        this.dir = dir;
        this.ftpName = ftpName;
    }

    public List<TransModel> getFilesUrl() {
        List<TransModel> transModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(this.getFileUrlStrategy().getFilesUrl())) return transModels;
        for (TransModel transModel : this.getFileUrlStrategy().getFilesUrl()) {
            String ftpUrl = "ftp://" + this.userName + ":" + this.password + "@" + this.ftpName + ":" + this.ftpPort + this.dir;
            if (transModel.getFileUrl().endsWith("/")) {
                List<String> files = FileUtil.getAllFilesUrlFromDir(this.ftpName, this.ftpPort, this.userName, this.password, dir + transModel.getFileUrl());
                for (String file : files) {
                    transModel.setFileUrl(ftpUrl + file);
                    transModels.add(transModel);
                }
            }
            transModel.setFileUrl(ftpUrl + transModel.getFileUrl());
            transModels.add(transModel);
        }
        return transModels;
    }
}
