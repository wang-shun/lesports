package com.lesports.sms.data.processor.olympic;

import com.alibaba.fastjson.JSONObject;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.olympic.Code;
import com.lesports.sms.data.model.olympic.CommonCode;
import com.lesports.sms.data.processor.AbstractProcessor;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.model.DictEntry;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.lesports.sms.data.Constants.CODE_DISCIPLINE;
import static com.lesports.sms.data.Constants.CODE_EVENT;
import static com.lesports.sms.data.Constants.CODE_SPORT_CODE;

/**
 * lesports-projects
 *
 * @author qiaohongxin
 * @since 16-2-18
 */
@Component
public class CommonCodeProcessor extends com.lesports.sms.data.processor.olympic.AbstractProcessor implements BeanProcessor<CommonCode> {
    private static Logger LOG = LoggerFactory.getLogger(CommonCodeProcessor.class);

    @Override
    public Boolean process(String fileType, CommonCode obj) {
        try {
            if (!valid(fileType, obj)) {
                LOG.warn("will not handle this, not valid : {}", JSONObject.toJSONString(obj));
                return false;
            }
            Long parentId = getParentId(fileType);
            if (parentId == 0L) {
                LOG.warn("will not handle this, can not find parent : {}", JSONObject.toJSONString(obj));
                return false;
            }
            DictEntry dictEntry = null;
            String validCode = getCode(fileType, obj);
            if (validCode == null) return false;
            List<DictEntry> dictEntrys = SbdsInternalApis.getDictEntryByCodeAndParentId(validCode, parentId);
            if (CollectionUtils.isEmpty(dictEntrys)) {
                dictEntry = new DictEntry();
                dictEntry.setParentId(parentId);
                dictEntry.setCode(validCode);
                dictEntry.setName(obj.getSportCode());
                dictEntry.setMultiLangNames(getMultiLang(obj.getSportCode()));
            } else {
                dictEntry = dictEntrys.get(0);
            }
            dictEntry.setMultiLangNames(getMultiLangwithTwoLanguage(obj.getCodeDEsc(), getEnglishName(dictEntry.getMultiLangNames())));
            dictEntry.setName(obj.getCodeDEsc());
            boolean result=SbdsInternalApis.saveDict(dictEntry) > 0;
            while(!result){
           result=SbdsInternalApis.saveDict(dictEntry) > 0;
            }
            return result;

        } catch (Exception e) {
            LOG.warn("code is saved fail, code : {}, {}", obj.getCode(), e.getMessage(), e);
        }
        return false;
    }

    private boolean valid(String type, CommonCode obj) {
        if (null == obj) {
            return false;
        } else if (type.equals(CODE_EVENT) && (obj.getCode().substring(2, 3).equals("0") || obj.getCode().endsWith("000000") || CommonUtil.isContainLetter(obj.getCode().substring(3, 9)))) {

            return false;
        }
        return true;
    }

    private String getCode(String type, CommonCode obj) {
        if (type.equals(CODE_DISCIPLINE)) {
            if (obj.getSportCode() != null) {
                return obj.getSportCode() + "_" + obj.getCode();
            } else {
                return null;
            }
        } else if (type.equals(CODE_SPORT_CODE)) {
            return obj.getSportCode() + "_" + obj.getCode();
        } else {
            return obj.getCode();
        }
    }
}
