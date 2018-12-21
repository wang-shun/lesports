package com.lesports.sms.data.processor.olympic;

import com.google.common.collect.Sets;
import com.lesports.bole.api.vo.TOlympicSettingDataSet;
import com.lesports.sms.client.BoleApis;
import com.lesports.sms.client.BoleInternalApis;
import com.lesports.sms.client.SbdsInternalApis;
import com.lesports.sms.data.processor.BeanProcessor;
import com.lesports.sms.data.utils.CommonUtil;
import com.lesports.sms.model.DictEntry;
import org.apache.commons.collections.CollectionUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by qiaohongxin on 2016/2/23.
 */
public class SettingProcessor extends AbstractProcessor implements BeanProcessor<Document> {
    private static Logger LOG = LoggerFactory.getLogger(SettingProcessor.class);

    public Boolean process(String codeType, Document document) {
        if (document == null) return false;
        LOG.info("setting data parser begin");
        Boolean result = false;
        try {
            String documentCode = CommonUtil.getStringValue(document.selectObject("/OdfBody/@DocumentCode"));
            List<DictEntry> parentDits = SbdsInternalApis.getDictEntriesByName("^奥运小项$");
            if (CollectionUtils.isEmpty(parentDits)) return false;
            List<DictEntry> list = SbdsInternalApis.getDictEntryByCodeAndParentId(documentCode.substring(0, 6) + "000", parentDits.get(0).getId());
            if (CollectionUtils.isEmpty(list)) return false;
            TOlympicSettingDataSet settingDataSet = new TOlympicSettingDataSet();
            settingDataSet.setId(list.get(0).getId().toString());
            settingDataSet.setMatchExtendConfig(getCodeValues(document.selectNodes("/OdfBody/Competition/ExtendedInfos/ExtendedInfo")));
            settingDataSet.setTeamExtendConfig(getCodeValues(document.selectNodes("/OdfBody/Competition/Result/Competitor/EventUnitEntry")));
            settingDataSet.setPlayerExtendConfig(getCodeValues(document.selectNodes("/OdfBody/Competition/Result/Competitor/Composition/Athlete/EventUnitEntry")));
            Set<String> competitorCondition = getCodeValues(document.selectNodes("/OdfBody/Competition/Result/Competitor/Stats/Stat"));
            Set<String> competitorSats = getCodeValues(document.selectNodes("/OdfBody/Competition/Result/Competitor/Stats/Stat/ExtendedStat"));
            if (competitorSats != null) {
                competitorSats.addAll(competitorCondition);
            }
            settingDataSet.setCompetitorStatsConditionConfig(competitorCondition);
            settingDataSet.setCompetitorStatsConfig(competitorSats);
            Set<String> playerCondition = getCodeValues(document.selectNodes("/OdfBody/Competition/Result/Competitor/Composition/Athlete/Stats/Stat"));
            Set<String> playerSats = getCodeValues(document.selectNodes("/OdfBody/Competition/Result/Competitor//Composition/Athlete/Stats/Stat/ExtendedStat"));
            if (playerSats != null) {
                playerSats.addAll(playerCondition);
            }
            settingDataSet.setPlayerStatsConditionConfig(playerCondition);
            settingDataSet.setPlayerStatsConfig(playerSats);
            Set<String> sectionCondition = getCodeValues(document.selectNodes("/OdfBody/Competition/Result/Competitor/Composition/Athlete/ExtendedResults/ExtendedResult"));
            Set<String> sectionResult = getCodeValues(document.selectNodes("/OdfBody/Competition/Result/Competitor/Composition/Athlete/ExtendedResults/ExtendedResult/Extension"));
            if (sectionResult != null) {
                sectionResult.addAll(sectionCondition);
            }
            settingDataSet.setResultConditionConfig(sectionCondition);
            settingDataSet.setResultConfig(sectionResult);
            return BoleInternalApis.saveTOlympicSettingDataSet(settingDataSet);
        } catch (Exception e) {
            return true;
        }
    }

    private Set<String> getCodeValues(List<Element> elements) {
        if (CollectionUtils.isEmpty(elements)) return null;
        Set<String> sets = Sets.newHashSet();
        for (Element currentElement : elements) {
            sets.add(CommonUtil.getStringValue(currentElement.selectObject("./@Code")));
        }
        return sets;
    }
}



