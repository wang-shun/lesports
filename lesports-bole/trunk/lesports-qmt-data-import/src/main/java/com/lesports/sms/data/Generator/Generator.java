package com.lesports.sms.data.Generator;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.parser.*;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/11/29.
 */
public class Generator {
    public List<TransModel> getFileUrl(MultiValueMap<Long, String> files) {
        List<TransModel> schedules = Lists.newArrayList();
        Set<Map.Entry<Long, List<String>>> entrySet = files.entrySet();
        for (Map.Entry<Long, List<String>> entry : entrySet) {
            Long csid = (Long) entry.getKey();
            for (String valueContent : entry.getValue()) {
                String[] names = valueContent.split("\\|");
                String fileSupportor = names[0];
                String anotationType = fileSupportor.split("\\-")[1];
                if (fileSupportor.contains("469")) {
                    for (int i = 1; i < names.length; i++) {
                        FileUrlStrategy fileUrlStrategy = new SingleFileUrlStrategy(names[i], csid, PartnerType.SPORTRADAR, anotationType);
                        FTPDecoratorFileUrl ftpDecoratorFileUrl = new FTPDecoratorFileUrl(fileUrlStrategy, Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword, "/Sport/");
                        schedules.addAll(ftpDecoratorFileUrl.getFilesUrl());
                    }
                } else if (fileSupportor.contains("53")) {
                    for (int i = 1; i < names.length; i++) {
                        FileUrlStrategy fileUrlStrategy = null;
                        if (names[i].contains("^ID:$")) {
                            fileUrlStrategy = new RegexFileUrlStrategy(anotationType, PartnerType.SODA, csid, names[i]);
                        } else {
                            fileUrlStrategy = new SingleFileUrlStrategy(names[i], csid, PartnerType.SODA, anotationType);
                        }
                        FTPDecoratorFileUrl ftpDecoratorFileUrl = new FTPDecoratorFileUrl(fileUrlStrategy, Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword, "/Soda/");
                        schedules.addAll(ftpDecoratorFileUrl.getFilesUrl());
                    }
                } else if (fileSupportor.contains("499")) {
                    for (int i = 1; i < names.length; i++) {
                        FileUrlStrategy fileUrlStrategy = null;
                        if (names[i].contains("^CODE:$") || names[i].contains("^ID:$")) {
                            fileUrlStrategy = new RegexFileUrlStrategy(anotationType, PartnerType.STATS, csid, names[i]);
                        } else if (names[i].endsWith(".XML")) {
                            fileUrlStrategy = new SingleFileUrlStrategy(names[i], csid, PartnerType.STATS, anotationType);
                        }
                        HttpDecoratorFileUrl httpDecoratorFileUrl = new HttpDecoratorFileUrl(fileUrlStrategy, Constants.statsDownloadUrl);
                        schedules.addAll(httpDecoratorFileUrl.getFilesUrl());
                    }
                }
            }
        }
        return schedules;
    }
}
