package com.lesports.bole.creater;

import com.lesports.bole.api.vo.TOlympicConfig;
import com.lesports.bole.api.vo.TOlympicLiveConfigSet;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ellios
 * Time: 15-6-14 : 下午6:18
 */
@Component("olympicConfigSetVoCreator")
public class OlympicConfigSetVoCreator {

    private static final Logger LOG = LoggerFactory.getLogger(OlympicConfigSetVoCreator.class);

    public TOlympicLiveConfigSet createTConfigSet(BoleOlympicsLiveConfigSet olympicConfigSet) {
        if (olympicConfigSet == null) {
            return null;
        }
        TOlympicLiveConfigSet tConfig = new TOlympicLiveConfigSet();
        fillTConfigWithConfig(tConfig, olympicConfigSet);
        return tConfig;
    }


    private void fillTConfigWithConfig(TOlympicLiveConfigSet tConfigSet, BoleOlympicsLiveConfigSet configSet) {
        tConfigSet.setId(String.valueOf(configSet.getId()));
        tConfigSet.setCompetitorStatsConfig(getTConfigList(configSet.getCompetitorStatsConfig()));
        tConfigSet.setMatchExtendConfig(getTConfigList(configSet.getMatchExtendConfig()));
        tConfigSet.setTeamExtendConfig(getTConfigList(configSet.getTeamExtendConfig()));
        tConfigSet.setPlayerExtendConfig(getTConfigList(configSet.getPlayerExtendConfig()));
        tConfigSet.setPlayerStatsConfig(getTConfigList(configSet.getPlayerStatsConfig()));
        tConfigSet.setResultConfig(getTConfigList(configSet.getTeamSectionResultConfig()));
    }

    private List<TOlympicConfig> getTConfigList(List<BoleOlympicsLiveConfigSet.OlympicsConfig> configs) {
        List<TOlympicConfig> lists = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(configs)) {
            for (BoleOlympicsLiveConfigSet.OlympicsConfig config : configs) {
                TOlympicConfig tOlympicConfig = new TOlympicConfig();
                tOlympicConfig.setElementPath(config.getElementPath());
                tOlympicConfig.setAnnotation(config.getAttributeReName());
                tOlympicConfig.setPropertyName(config.getPositionKey());
                tOlympicConfig.setFormatterType(config.getFormatterType());
                tOlympicConfig.setRightElementPath(config.getRightElementPath());
                tOlympicConfig.setOp(config.getOp());
                lists.add(tOlympicConfig);
            }
        }
        return lists;
    }


}
