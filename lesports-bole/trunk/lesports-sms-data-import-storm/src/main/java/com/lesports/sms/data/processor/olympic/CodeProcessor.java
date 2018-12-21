package com.lesports.sms.data.processor.olympic;

import com.alibaba.fastjson.JSONObject;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.model.olympic.Code;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.model.DictEntry;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.lesports.sms.data.Constants.*;

/**
 * lesports-projects
 *
 * @author qiaohongxin
 * @since 16-2-18
 */
@Component
public class CodeProcessor extends AbstractProcessor implements BeanProcessor<Code> {
    private static Logger LOG = LoggerFactory.getLogger(CodeProcessor.class);

    @Override
    public Boolean process(String fileType, Code obj) {
        if (!valid(fileType, obj)) {
            LOG.warn("will not handle this, not valid : {}", JSONObject.toJSONString(obj));
            return false;
        }
        try {
            Long parentId = getParentId(fileType, obj);
            if (parentId == 0L) {
                LOG.warn("will not handle this, can not find parent : {}", JSONObject.toJSONString(obj));
                return false;
            }
            String dictCode = getDictCode(fileType, obj);
            if (dictCode == null) return false;
            DictEntry dictEntry = null;
            List<DictEntry> dictEntrys = SbdsInternalApis.getDictEntryByCodeAndParentId(dictCode, parentId);
            if (CollectionUtils.isEmpty(dictEntrys)) {
                dictEntry = new DictEntry();
                dictEntry.setParentId(parentId);
                dictEntry.setCode(dictCode);
                dictEntry.setDeleted(false);
                dictEntry.setName(obj.getLongDescription() == null ? obj.getDescription() : obj.getLongDescription());
                dictEntry.setMultiLangNames(getMultiLang(dictEntry.getName()));
                return SbdsInternalApis.saveDict(dictEntry) > 0;
            }
            return true;
        } catch (Exception e) {
            LOG.warn("code is saved fail, code : {}, {}", obj.getCode(), e.getMessage(), e);
        }
        return false;
    }

    private boolean valid(String type, Code obj) {
        if (null == obj) {
            return false;
        }
        if (type.equals(CODE_DISCIPLINE) && null == obj.getSportCode() && obj.getSchedule().equals("Y")) {
            return false;
        } else if (type.equals(CODE_EVENT) && (obj.getCode().substring(2, 3).equals("0") || obj.getCode().endsWith("000000") || CommonUtil.isContainLetter(obj.getCode().substring(3, 9)))) {
            return false;
        } else if (type.equals(CODE_PHASE) && (obj.getCode().substring(2, 3).equals("0") || obj.getCode().substring(6, 7).equals("0") || obj.getCode().endsWith("000000") || CommonUtil.isContainLetter(obj.getCode().substring(3, 6)))) {
            return false;
        } else if (type.equals(CODE_SPORT_CODE) && !obj.getDiscipline().contains("BK")) {
            return false;
        }
        return true;
    }

    private String getDictCode(String type, Code obj) {
        if (type.equals(CODE_DISCIPLINE)) {
            if (obj.getSportCode() == null) return null;
            return obj.getSportCode() + "_" + obj.getCode().substring(0, 2);
        } else if (type.equals(CODE_PHASE)) {
            return obj.getCode().substring(0, 2) + "0000" + obj.getCode().substring(6, 7) + "00";
        } else {
            return obj.getCode();
        }
    }
}
