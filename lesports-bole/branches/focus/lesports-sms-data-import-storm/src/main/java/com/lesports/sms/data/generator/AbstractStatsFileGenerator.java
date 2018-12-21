package com.lesports.sms.data.generator;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.job.DataImportJob;
import com.lesports.sms.model.DataImportConfig;
import com.lesports.sms.model.DictEntry;
import com.lesports.utils.LeProperties;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public abstract class AbstractStatsFileGenerator implements FileGenerator {
    private static Logger LOG = LoggerFactory.getLogger(AbstractStatsFileGenerator.class);
    public String LocalFtpHost = LeProperties.getString("olympics.ftp.host", "");
    public int LocalFtpPort = LeProperties.getInt("olympics.ftp.port", 60000);
    public String LocalFtpUserName = LeProperties.getString("olympics.ftp.userName", "");
    public String LocalFtpPassword = LeProperties.getString("olympics.ftp.password", "");
    public String statsHttpUrl = LeProperties.getString("stats.http.url", "");
    public String ftpRelativePath = "/ODF/";
    public Integer maxTryCount = 1;

    @Override
    public List<String> getFileUrl() {
        List<String> relativePath = getFileName();
        if (CollectionUtils.isEmpty(relativePath)) {
            return Collections.emptyList();
        }
        List<String> fileUrls = Lists.newArrayListWithCapacity(relativePath.size());
//        for (String relativeFileUrl : relativePath) {
//            fileUrls.add(relativeFileUrl);
//        }
//        for (String relativeFileUrl : relativePath) {
//            String fileUrl = "ftp://" + LocalFtpUserName + ":" + LocalFtpPassword + "@" + LocalFtpHost + ":" + LocalFtpPort + ftpRelativePath + relativeFileUrl;
//            fileUrls.add(fileUrl);
//        }
        for (String relativeFileUrl : relativePath) {
            String fileUrl = statsHttpUrl + relativeFileUrl;
            fileUrls.add(fileUrl);
        }
        return fileUrls;
    }


    public abstract List<String> getFileName();

    public List<String> getAllFiles(String fileType) {
        List<String> files = Lists.newArrayList();

        List<DataImportConfig> fileConfigs = SopsInternalApis.findExecConfigsByNameAndpartnerType(fileType, Constants.partner_type);
        if (CollectionUtils.isEmpty(fileConfigs)){
            return files;
        }
        LOG.info("Load data import configs for : {}, data : {}", fileType, JSONObject.toJSONString(fileConfigs));

        for (DataImportConfig config : fileConfigs) {

            files.add(config.getFileName());
            config.setTryCount(config.getTryCount() + 1);
            SopsInternalApis.saveDataImportConfig(config);
        }
        Collections.sort(files, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        return files;
    }
}
