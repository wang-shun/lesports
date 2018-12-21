package com.lesports.sms.data.generator.sportrard;

import com.google.common.collect.Lists;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.generator.AbstractStatsFileGenerator;
import com.lesports.sms.data.generator.FileGenerator;
import com.lesports.sms.data.model.sportrard.SportrardConstants;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.model.Match;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/3/23
 */
public class F1statsFileGenerator implements FileGenerator {
    @Override
    public List<String> getFileUrl() {
        List<String> result = new ArrayList<>();
        List<String> files = getFileName();
        if (CollectionUtils.isNotEmpty(files)) {
            for (String file : files) {
                String absFilePath = "ftp://" + "Sport" + ":" + "40a38cb71" + "@" +
                        "10.120.56.176" + ":" + "65000" + "/Sport/copy/outright_Motorsport/Formula1/" + file;
                result.add(absFilePath);
            }
        }
        return result;
    }

    public List<String> getFileName() {
        List<String> fileNames = Lists.newArrayList();
        InternalQuery q = new InternalQuery();
        q.addCriteria(new InternalCriteria("csid", "eq", 100694002L));
        String currentdate = CommonUtil.getDataYYMMDD(new Date());
        q.addCriteria(new InternalCriteria("startDate", "lte", currentdate));
        List<Match> matches = SbdsInternalApis.getMatchsByQuery(q);
        if (CollectionUtils.isNotEmpty(matches)) {
            for (Match currentMatch : matches) {
                String currentName = getName(currentMatch.getSubstation());
                if (currentName == null) continue;
                String stageName = getStageName(currentMatch.getStage(), currentMatch.getNumber());
                String fileName = "outright_Motorsport.Formula1.Formula12016." + currentName + "2016." + stageName + ".xml";
                fileNames.add(fileName);
            }
        }
        return fileNames;
    }

    private String getName(Long id) {
        for (Map.Entry<String, Long> entry : SportrardConstants.substationMap.entrySet()) {
            if (entry.getValue().equals(id)) {
                String[] split = entry.getKey().split("\\_");
                if (split.length > 1) return split[1];
            }
        }
        return null;
    }

    private String getStageName(Long id, Integer number) {
        for (Map.Entry<String, Long> entry : SportrardConstants.stageMap.entrySet()) {
            if (entry.getValue().equals(id)) {
                String key = entry.getKey();
                String[] split = key.split("\\s+");
                if (number == null) return split[0];
                else return split[0] + number;
            }
        }
        return "";
    }

    ;
}

