package com.lesports.sms.data.processor.olympic;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.client.SopsInternalApis;
import com.lesports.sms.data.model.olympic.Code;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.model.DataImportConfig;
import com.lesports.sms.model.DictEntry;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.lesports.sms.data.Constants.*;

/**
 * lesports-projects
 *
 * @author qiaohongxin
 * @since 16-2-18
 */
@Component
public class FileListProcessor extends AbstractProcessor implements BeanProcessor<String> {
    private static Logger LOG = LoggerFactory.getLogger(FileListProcessor.class);

    @Override
    public Boolean process(String fileType, String obj) {
        Boolean result = true;
        if (!validFile(obj)) {
            LOG.warn("will not accept this type file,{}", obj);
            return true;
        }
        try {
            String[] list = obj.split("\\_______________");
            String fileNameType = list[0];
            DataImportConfig config = SopsInternalApis.findConfigByNameAndPartnerType(obj, partner_type);
            if (config == null) {
                config = new DataImportConfig();
                config.setDeleted(false);
                config.setFileName(obj);
                config.setPartnerType(partner_type);
                config.setIsCurrentNew(true);
                config.setTryCount(0);
                long id = SopsInternalApis.saveDataImportConfig(config);
                result = id > 0;
                LOG.info("save data import config : {}, id : {}, result : {}", config, id, result);
            }
//            if (result && config != null) {
//                config.setIsCurrentNew(false);
//                SopsInternalApis.saveDataImportConfig(config);
//            }
            return result;
        } catch (Exception e) {
            LOG.warn("code is saved fail, code : {}, {}", obj, e.getMessage(), e);
        }
        return false;
    }

    private boolean validFile(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return false;
        }
        if (fileName.contains("DT_RESULT") || fileName.contains("DT_SCHEDULE") || fileName.contains("DT_MEDALLISTS_DISCIPLINE") || fileName.contains("DT_PARTIC") || fileName.contains("DT_RECORD"))
            return true;
        return false;
    }
}
