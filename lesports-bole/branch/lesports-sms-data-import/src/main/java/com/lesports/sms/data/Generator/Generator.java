package com.lesports.sms.data.Generator;

import com.google.common.collect.Lists;
import com.lesports.qmt.sbd.model.PartnerType;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;
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
            entry.getValue();
            for (String valueContent : entry.getValue()) {
                String[] names = valueContent.split("\\|");
                String fileSupportor = names[0];
                if (fileSupportor.contains("469")) {
                    String anotationType = fileSupportor.split("\\-")[1];
                    for (int i = 1; i < names.length; i++) {
                        if (names[i].endsWith(".xml")) {
                            String fileUrl = "ftp://" + Constants.srUserName + ":" + Constants.srPassword + "@" + Constants.srHost + ":" + Constants.srPort + "/Sport/" + names[i];
                            TransModel currentFiles = new TransModel();
                            currentFiles.setCsid(csid);
                            currentFiles.setPartnerType(PartnerType.SPORTRADAR);
                            currentFiles.setFileUrl(fileUrl);
                            currentFiles.setAnotationType(anotationType);
                            schedules.add(currentFiles);
                        } else if (names[i].endsWith("/")) {
                            List<String> urls = FileUtil.getAllFilesUrlFromDir(Constants.srHost, Constants.srPort, Constants.srUserName, Constants.srPassword, "/Sport" + names[i]);
                            schedules.addAll(PackFileUrl(csid, PartnerType.SPORTRADAR, anotationType, urls));
                        } else continue;
                    }
                } else if (fileSupportor.contains("53")) {
                    String anotationType = fileSupportor.split("\\-")[1];
                    for (int i = 1; i < names.length; i++) {
                        if (names[i].endsWith(".xml") && names[i].contains("^ID$")) {
                            List<String> fileUrls = getValidUrls(PartnerType.SODA, Constants.statsDownloadUrl, names[i], "^ID");
                            schedules.addAll(PackFileUrl(csid, PartnerType.SODA, anotationType, fileUrls));
                        }
                        if (names[i].endsWith(".xml")) {
                            String fileUrl = "ftp://" + Constants.LocalFtpUserName + ":" + Constants.LocalFtpPassword + "@" + Constants.LocalFtpHost + ":" + Constants.LocalFtpPort + "/soda/" + names[i];
                            TransModel currentFiles = new TransModel();
                            currentFiles.setCsid(csid);
                            currentFiles.setPartnerType(PartnerType.SODA);
                            currentFiles.setFileUrl(fileUrl);
                            currentFiles.setAnotationType(anotationType);
                            schedules.add(currentFiles);
                        } else if (names[i].endsWith("/")) {
                            List<String> urls = FileUtil.getAllFilesUrlFromDir(Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword, "/soda/" + names[i]);
                            schedules.addAll(PackFileUrl(csid, PartnerType.SODA, anotationType, urls));
                        } else continue;
                    }
                } else if (fileSupportor.contains("499")) {
                    String anotationType = fileSupportor.split("\\-")[1];
                    for (int i = 1; i < names.length; i++) {
                        if (names[i].endsWith(".XML") && names[i].contains("^CODE$")) {
                            List<String> fileUrls = getValidUrls(PartnerType.STATS, Constants.statsDownloadUrl, names[i], "^CODE$");
                            schedules.addAll(PackFileUrl(csid, PartnerType.STATS, anotationType, fileUrls));
                        } else if (names[i].endsWith(".XML")) {
                            String fileUrl = Constants.statsDownloadUrl + names[i];
                            TransModel currentFiles = new TransModel();
                            currentFiles.setCsid(csid);
                            currentFiles.setPartnerType(PartnerType.STATS);
                            currentFiles.setFileUrl(fileUrl);
                            currentFiles.setAnotationType(anotationType);
                            schedules.add(currentFiles);
                        }
                    }
                }
            }
        }
        return schedules;
    }

    private List<TransModel> PackFileUrl(Long csid, PartnerType type, String annotationType, List<String> urls) {
        List<TransModel> transModels = Lists.newArrayList();
        if (CollectionUtils.isEmpty(urls)) return transModels;
        for (String url : urls) {
            TransModel transModel = new TransModel();
            transModel.setAnotationType(annotationType);
            transModel.setFileUrl(url);
            transModel.setPartnerType(type);
            transModel.setCsid(csid);
            transModels.add(transModel);
        }
        return transModels;
    }

    private List<String> getValidUrls(PartnerType partnerType, String downloadDir, String fileRegex, String regexPatten) {
        List<String> finalUrls = Lists.newArrayList();
        List<String> validCodes = Lists.newArrayList();
        if (regexPatten.equals("^CODE$")) {
            validCodes = null;
        } else if (regexPatten.equals("^ID$")) {
            validCodes = null;
        }
        if (CollectionUtils.isNotEmpty(validCodes)) {
            for (String code : validCodes) {
                String validFileName = fileRegex.replace(regexPatten, code);
                finalUrls.add(downloadDir + validFileName);
            }
        }
        return finalUrls;
    }
}
