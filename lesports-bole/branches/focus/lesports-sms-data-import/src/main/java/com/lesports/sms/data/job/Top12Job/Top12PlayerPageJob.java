package com.lesports.sms.data.job.Top12Job;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.lesports.sms.data.job.ThirdDataJob;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.top12.PlayerPageContentParser;
import com.lesports.sms.data.util.FtpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service("playerPageJob")
public class Top12PlayerPageJob extends ThirdDataJob {
    @Resource
    private PlayerPageContentParser playerPageContentParser;
    private static Logger LOG = LoggerFactory.getLogger(Top12PlayerPageJob.class);

    public void run() {
//        List<String> files = getFileswithCache("playerStatis");
//        if (CollectionUtils.isEmpty(files)) return;

        List<String> files = getFilesByType("");
        FtpUtil ftpUtil = new FtpUtil(Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword);
        for (String filename : files) {
            super.downloadAndParseData(ftpUtil, "playerPageJob", filename, Constants.SODA_PLAYER_PATH, "//soda//412//player//", playerPageContentParser);
        }
    }

    private LoadingCache<String, List<String>> dictEntryLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(50)
            .expireAfterWrite(1, TimeUnit.DAYS)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String key) throws Exception {
                    return getFilesByType(key);
                }
            });

    private List<String> getFileswithCache(String fileType) {
        try {
            List<String> list =dictEntryLoadingCache.get(fileType);
            if (CollectionUtils.isEmpty(list)) {
                return getFilesByType("playerStatis");
            }
            return list;
        } catch (Exception e) {
            LOG.warn("Fail to get dict by parent type : {}, code : {}.", fileType);
        }
        return null;
    }

    private List<String> getFilesByType(String parentType) {
        List<String> files = Lists.newArrayList();
        FtpUtil ftpUtil = new FtpUtil(Constants.LocalFtpHost, Constants.LocalFtpPort, Constants.LocalFtpUserName, Constants.LocalFtpPassword);
        try {
            if (!ftpUtil.loginFtp(60)) {
                return files;
            }
            String[] list = ftpUtil.getPlayerFiles("//soda//412//player//");
            if (list != null && list.length > 0) {
                for (int i = 0; i < list.length; i++) {
                    files.add(list[i]);
                }
            }
        } catch (Exception e) {
            LOG.error("get files error", e);
        } finally {
            ftpUtil.logOutFtp();
        }
        return files;
    }

}
