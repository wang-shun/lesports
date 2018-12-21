package com.lesports.sms.data.job;

import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.service.soda.SodaTeamSkillParser;
import com.lesports.sms.data.util.MD5Util;
import com.lesports.sms.model.DataImportConfig;
import com.lesports.utils.LeProperties;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

/**
 * Created by zhonglin on 2016/3/8.
 */

@Service("sodaTeamSkillJob")
public class SodaTeamSkillJob {

    private static Logger logger = LoggerFactory.getLogger(SodaTeamSkillJob.class);

    private final String SODA_TEAM_SKILL_FILE_PATH = LeProperties.getString("soda.team.skill.file.path");
    private final String SODA_TEAM_SKILL_FILE_NAMES = LeProperties.getString("soda.team.skill.file.names");

    @Resource
    private SodaTeamSkillParser sodaTeamSkillParser;


    /**
     * 导入球队技术统计信息
     */
    public void importSodaTeamSkill() {
        logger.info("【import soda team skill】 start");
        String name = SODA_TEAM_SKILL_FILE_NAMES;
        logger.info("importSodaTeamSkill name: " + name);
        String[] fileNames = name.split("\\|");
        if (null != fileNames) {
            for (String fileName : fileNames) {
                String filePath = SODA_TEAM_SKILL_FILE_PATH + "t315-team-skill-" + fileName + ".xml";
                if (compareMD5(filePath)) continue;
                String md5New = MD5Util.fileMd5(new File(filePath));
                sodaTeamSkillParser.parseData(filePath);
                saveMD5(filePath, md5New);
            }
        }
        logger.info("【import soda team skill】 end");
    }

    /**
     * 比较md5值，若无记录或不同返回false
     *
     * @param filePath
     * @return
     */
    private Boolean compareMD5(String filePath) {
        File file = new File(filePath);
        String md5New = MD5Util.fileMd5(file);
        if (null == md5New) return true;
        List<DataImportConfig> dataImportConfigList = SopsInternalApis.getDataImportConfigsBybyFileNameAndpartnerType(file.getName(), Constants.SODAOartnerSource);
        if (CollectionUtils.isEmpty(dataImportConfigList)) return false;
        DataImportConfig dataImportConfig = dataImportConfigList.get(0);
        return md5New.equals(dataImportConfig.getMd5());
    }

    private void saveMD5(String filePath, String md5New) {
        File file = new File(filePath);
        List<DataImportConfig> dataImportConfigList = SopsInternalApis.getDataImportConfigsBybyFileNameAndpartnerType(file.getName(), Constants.SODAOartnerSource);

        if (CollectionUtils.isNotEmpty(dataImportConfigList)) {
            DataImportConfig dataImportConfig = dataImportConfigList.get(0);
            dataImportConfig.setMd5(md5New);
            SopsInternalApis.saveDataImportConfig(dataImportConfig);
        } else {
            DataImportConfig dataImportConfig = new DataImportConfig();
            dataImportConfig.setFileName(file.getName());
            dataImportConfig.setPartnerType(Constants.SODAOartnerSource);
            dataImportConfig.setMd5(md5New);
            SopsInternalApis.saveDataImportConfig(dataImportConfig);
        }
    }
}
