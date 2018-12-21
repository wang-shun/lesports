package com.lesports.sms.data.job;

import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.IThirdDataParser;
import com.lesports.sms.data.service.soda.top12.WarningMailUtil;
import com.lesports.sms.data.util.FtpUtil;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.DataImportConfig;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by lufei1 on 2014/12/12.
 */
public class ThirdDataJob {
    @Resource
    WarningMailUtil warningMailUtil;
    private static Logger logger = LoggerFactory.getLogger(ThirdDataJob.class);

    public void downloadAndParseData(FtpUtil ftpUtil, String jobName, String parseFiles,
                                     String localRootPath, String remoteRootPath, IThirdDataParser thirdDataParser) {
        try {
            // Stopwatch stopwatch = new Stopwatch();
            //  stopwatch.start();
            logger.info("{} job execute begin", jobName);
            if (!ftpUtil.loginFtp(60)) {
                logger.error("login ftp:{} fail", ftpUtil.getFtpName());
                return;
            }
            if (StringUtils.isNotBlank(parseFiles)) {
                String[] files = parseFiles.split("\\|");
                for (String file : files) {
                    try {
                        try {
                            Boolean downloadResult = ftpUtil.downloadFile(file, localRootPath, remoteRootPath);
                            if (!downloadResult) {
                                logger.error("thirdDataJob download file:{} fail", file);
                                continue;
                            }
                        } catch (IOException e) {
                            warningMailUtil.sendMai("data-import file downLoad fail", "FILE_PROVIDER:sportrard<br>" + "FILE_URL:" + remoteRootPath);
                            continue;
                        }
                        List<DataImportConfig> dataImportConfigList = SopsInternalApis.getDataImportConfigsBybyFileNameAndpartnerType(file, Integer.parseInt(Constants.partnerSourceId));

                        String md5New = MD5Util.fileMd5(new File(localRootPath + file));

                        boolean isExists = false;
                        DataImportConfig dataImportConfig = null;
                        if (CollectionUtils.isNotEmpty(dataImportConfigList)) {
                            dataImportConfig = dataImportConfigList.get(0);
                            if (StringUtils.isNotBlank(dataImportConfig.getMd5()) && dataImportConfig.getMd5().equals(md5New)) {
                                logger.info("the md5 of  file :{}  has not change ", file);
                                continue;
                            } else {
                                isExists = true;
                            }
                        }

                        if (thirdDataParser == null || !thirdDataParser.parseData(localRootPath + file)) {
                            logger.warn("the file do not need Parser or parse file:{} fail", file);
                            continue;
                        } else {
                            if (isExists) {
                                logger.info("the md5 of  file :{}  has change ", file);
                                dataImportConfig.setMd5(md5New);
                                dataImportConfig.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                                SopsInternalApis.saveDataImportConfig(dataImportConfig);
                            } else {
                                DataImportConfig dataImportConfig2 = new DataImportConfig();
                                dataImportConfig2.setFileName(file);
                                dataImportConfig2.setPartnerType(Integer.parseInt(Constants.partnerSourceId));
                                dataImportConfig2.setMd5(md5New);
                                dataImportConfig2.setDeleted(false);
                                dataImportConfig2.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                                dataImportConfig2.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
                                SopsInternalApis.saveDataImportConfig(dataImportConfig2);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("download and parse " + jobName + " file" + file + " error, {}", e.getMessage(), e);
                        continue;
                    }
                    //   stopwatch.stop();
                    //   logger.info("{} job execute end,usedTime:{}ms", jobName, stopwatch.elapsed(TimeUnit.MILLISECONDS));
                }

            }

        } catch (Exception e) {
            logger.error("download and parse " + jobName + " file error, {}", e.getMessage(), e);
        } finally {
            ftpUtil.logOutFtp();
        }

    }
}
