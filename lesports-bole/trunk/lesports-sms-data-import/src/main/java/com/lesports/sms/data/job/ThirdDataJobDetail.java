package com.lesports.sms.data.job;

import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.DataImportConfig;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Date;
import java.util.List;

public class ThirdDataJobDetail {

    private static Logger logger = LoggerFactory.getLogger(ThirdDataJobDetail.class);


    public void downloadAndParseData(FtpUtil ftpUtil, String jobName, String parseFiles,
                                     String localRootPath, String remoteRootPath, IThirdDataParser thirdDataParser) {
        try {
            //  Stopwatch stopwatch = new Stopwatch();
            // stopwatch.start();
            logger.info("{} job execute begin", jobName);
            if (!ftpUtil.loginFtp(60)) {
                logger.error("login ftp:{} fail", ftpUtil.getFtpName());
                return;
            }
            if (StringUtils.isNotBlank(parseFiles)) {
                String[] files = parseFiles.split("\\|");
                for (int i = 0; i < files.length; i++) {
                    List<String> flist = ftpUtil.getNamesByReg(files[i] + ".*", remoteRootPath);
                    for (String f : flist) {
                        Boolean downloadResult = ftpUtil.downloadFile(f, localRootPath, remoteRootPath);
                        if (!downloadResult) {
                            logger.error("thirdDataJobDetail download file:{} fail", f);
                            continue;
                        }

                        List<DataImportConfig> dataImportConfigList = SopsInternalApis.getDataImportConfigsBybyFileNameAndpartnerType(f, Integer.parseInt(Constants.partnerSourceId));

                        String md5New = MD5Util.fileMd5(new File(localRootPath + f));

                        boolean isExists = false;
                        DataImportConfig dataImportConfig = null;
                        if (CollectionUtils.isNotEmpty(dataImportConfigList)) {
                            dataImportConfig = dataImportConfigList.get(0);
                            if (StringUtils.isNotBlank(dataImportConfig.getMd5()) && dataImportConfig.getMd5().equals(md5New)) {
                                logger.info("the md5 of  file :{}  has not change ", f);
                                continue;
                            } else {
                                isExists = true;
                            }
                        }

                        if (!thirdDataParser.parseData(localRootPath + f)) {
                            logger.warn("parse file:{} fail", f);
                            continue;
                        } else {
                            if (isExists) {
                                logger.info("the md5 of  file :{}  has change ", f);
                                dataImportConfig.setMd5(md5New);
                                dataImportConfig.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                                SopsInternalApis.saveDataImportConfig(dataImportConfig);
                            } else {
                                DataImportConfig dataImportConfig2 = new DataImportConfig();
                                dataImportConfig2.setFileName(f);
                                dataImportConfig2.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                                dataImportConfig2.setMd5(md5New);
                                dataImportConfig2.setDeleted(false);
                                dataImportConfig2.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                                dataImportConfig2.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                                SopsInternalApis.saveDataImportConfig(dataImportConfig2);
                            }
                        }
                    }
                }
                //     stopwatch.stop();
                //     logger.info("{} job execute end,usedTime:{}ms", jobName, stopwatch.elapsed(TimeUnit.MILLISECONDS));
            }
        } catch (Exception e) {
            logger.error("download and parse " + jobName + " file error", e);
        } finally {
            ftpUtil.logOutFtp();
        }

    }
}
