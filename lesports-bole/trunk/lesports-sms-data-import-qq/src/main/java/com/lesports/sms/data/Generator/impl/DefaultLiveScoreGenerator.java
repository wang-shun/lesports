package com.lesports.sms.data.Generator.impl;

import com.google.common.collect.Lists;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.lesports.query.InternalCriteria;
import com.lesports.query.InternalQuery;
import com.lesports.sms.api.common.MatchStatus;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.Generator.Generator;
import com.lesports.sms.data.model.Constants;
import com.lesports.sms.data.model.TransModel;
import com.lesports.sms.data.parser.FileParser;
import com.lesports.sms.model.Match;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qiaohongxin on 2016/11/17.
 */
@Service("defaultLiveScoreGenerator")
public class DefaultLiveScoreGenerator extends Generator {
    @Resource
    FileParser fileParser;
    private static Logger LOG = LoggerFactory.getLogger(DefaultLiveScoreGenerator.class);

    public boolean nextProcessor() {
        List<TransModel> schedules = getFileUrl(Constants.lives);
        for (TransModel transModel : schedules) {
            fileParser.nextProcessor("LIVE-SCORE", transModel);
        }

        return true;
    }

    @Override
    public List<TransModel> getFileUrl(MultiValueMap<Long, String> files) {
        List<TransModel> transModels = Lists.newArrayList();
        for (Map.Entry<Long, List<String>> entry : files.entrySet()) {
            Long csid = (Long) entry.getKey();
            entry.getValue();
            for (String valueContent : entry.getValue()) {
                String[] names = valueContent.split("\\---");
                String fileSupportor = names[0];
                if (fileSupportor.contains("479")) {
                    List<String> partnerCodes = getValidCodeByCsidAndPartner(csid);
                    if (CollectionUtils.isEmpty(partnerCodes)) continue;
                    for (String curentCode : partnerCodes) {
                        TransModel currentFiles = new TransModel();
                        currentFiles.setCsid(csid);
                        currentFiles.setPartnerType(null);
                        currentFiles.setFileUrl(names[2].replace("mid=?", "mid=" + curentCode));
                        currentFiles.setAnotationType(names[1]);
                        currentFiles.setPartnerId(curentCode);
                        transModels.add(currentFiles);
                    }
                }
            }
        }

        return transModels;
    }

    private List<String> getValidCodeByCsidAndPartner(Long csid) {
        String currentDate = LeDateUtils.formatYYYYMMDD(new Date());
        InternalQuery query = new InternalQuery();
        query.addCriteria(new InternalCriteria("csid", "is", csid));
        query.addCriteria(new InternalCriteria("status", "eq", MatchStatus.MATCHING));
        InternalQuery query2 = new InternalQuery();
        query2.addCriteria(new InternalCriteria("csid", "is", csid));
        query2.addCriteria(new InternalCriteria("start_date", "eq", currentDate));
        query2.addCriteria(new InternalCriteria("status", "ne", MatchStatus.MATCH_END));
        //  query.addCriteria(c1);
        List<Match> matches1 = SbdsInternalApis.getMatchsByQuery(query2);
        List<Match> matches2 = SbdsInternalApis.getMatchsByQuery(query);
        List<Match> matches = Lists.newArrayList();
        if (CollectionUtils.isEmpty(matches1) && CollectionUtils.isEmpty(matches2)) return null;
        else if (CollectionUtils.isEmpty(matches1)) matches = matches1;
        else if (CollectionUtils.isEmpty(matches2)) matches = matches1;
        else {
            matches.addAll(matches1);
            matches.addAll(matches2);
        }
        if (CollectionUtils.isEmpty(matches)) return null;
        List<String> ids = Lists.newArrayList();
        for (Match currentMatch : matches) {
            if (StringUtils.isBlankOrNull(currentMatch.getQQId())) continue;
            ids.add(currentMatch.getQQId());
        }
        return ids;
    }


}
