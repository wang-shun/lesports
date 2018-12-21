package com.lesports.sms.data.Generator;

import com.google.common.collect.Lists;
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
public abstract class Generator {
    public abstract boolean nextProcessor();

    public List<TransModel> getFileUrl(MultiValueMap<Long, String> files) {
        List<TransModel> schedules = Lists.newArrayList();
        Set<Map.Entry<Long, List<String>>> entrySet = files.entrySet();
        for (Map.Entry<Long, List<String>> entry : entrySet) {
            Long csid = (Long) entry.getKey();
            for (String valueContent : entry.getValue()) {
                String[] contents = valueContent.split("\\---");
                String fileSupportor = contents[0];
                if (fileSupportor.contains("479")) {
                    TransModel currentFiles = new TransModel();
                    currentFiles.setCsid(csid);
                    currentFiles.setPartnerType(null);
                    currentFiles.setFileUrl(contents[2]);
                    currentFiles.setAnotationType(contents[1]);
                    schedules.add(currentFiles);
                }
            }
        }
        return schedules;
    }

}
