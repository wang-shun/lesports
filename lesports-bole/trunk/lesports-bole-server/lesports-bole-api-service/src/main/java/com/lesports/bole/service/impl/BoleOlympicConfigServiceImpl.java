package com.lesports.bole.service.impl;

import com.lesports.bole.api.vo.TOlympicLiveConfigSet;
import com.lesports.bole.creater.OlympicConfigSetVoCreator;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import com.lesports.bole.repository.BoleOlympicConfigRepository;
import com.lesports.bole.service.BoleOlympicConfigService;
import com.lesports.utils.LeDateUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yangyu on 2015/7/1.
 */
@Service("olympicConfigService")
public class BoleOlympicConfigServiceImpl implements BoleOlympicConfigService {

    private static final Logger LOG = LoggerFactory.getLogger(BoleOlympicConfigServiceImpl.class);
    @Resource
    private OlympicConfigSetVoCreator configSetVoCreator;
    @Resource
    BoleOlympicConfigRepository olympicConfigRepository;

    @Override
    public boolean deleteConfig(long gameSType, String configName, String key) {
        BoleOlympicsLiveConfigSet boleOlympicLiveConfigSet = olympicConfigRepository.findOne(gameSType);
        List<BoleOlympicsLiveConfigSet.OlympicsConfig> olympicsConfigs = getConfigByName(boleOlympicLiveConfigSet, configName);
        Iterator<BoleOlympicsLiveConfigSet.OlympicsConfig> olympicsConfigIterator = olympicsConfigs.iterator();
        while (olympicsConfigIterator.hasNext()) {
            BoleOlympicsLiveConfigSet.OlympicsConfig olympicsConfig = olympicsConfigIterator.next();
            if (olympicsConfig.getPositionKey().equals(key)) {
                olympicsConfigIterator.remove();
            }
        }

        return save(boleOlympicLiveConfigSet);
    }

    @Override
    public boolean saveConfig(long gameStype, String configName, BoleOlympicsLiveConfigSet.OlympicsConfig config) {
        BoleOlympicsLiveConfigSet boleOlympicLiveConfigSet = olympicConfigRepository.findOne(gameStype);
        if (null == boleOlympicLiveConfigSet) {
            boleOlympicLiveConfigSet = new BoleOlympicsLiveConfigSet();
            boleOlympicLiveConfigSet.setId(gameStype);
            boleOlympicLiveConfigSet.setDeleted(false);
            boleOlympicLiveConfigSet.setCreateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        }
        List<BoleOlympicsLiveConfigSet.OlympicsConfig> olympicsConfigs = getConfigByName(boleOlympicLiveConfigSet, configName);
        Iterator<BoleOlympicsLiveConfigSet.OlympicsConfig> olympicsConfigIterator = olympicsConfigs.iterator();
        while (olympicsConfigIterator.hasNext()) {
            BoleOlympicsLiveConfigSet.OlympicsConfig olympicsConfig = olympicsConfigIterator.next();
            if (olympicsConfig.getPositionKey().equals(config.getPositionKey())) {
                olympicsConfigIterator.remove();
            }
        }
        olympicsConfigs.add(config);
        boleOlympicLiveConfigSet.setUpdateAt(LeDateUtils.formatYYYYMMDDHHMMSS(new Date()));
        return save(boleOlympicLiveConfigSet);
    }

    @Override
    public TOlympicLiveConfigSet getConfig(long gameSType) {
        BoleOlympicsLiveConfigSet boleOlympicsLiveConfigSet = olympicConfigRepository.findOne(gameSType);
        if (null != boleOlympicsLiveConfigSet) {
            return configSetVoCreator.createTConfigSet(boleOlympicsLiveConfigSet);
        }
        return null;
    }

    @Override
    public List<BoleOlympicsLiveConfigSet.OlympicsConfig> findConfigList(long gameSType, String configName) {
        if (StringUtils.isEmpty(configName)) {
            return Collections.emptyList();
        }
        BoleOlympicsLiveConfigSet boleOlympicLiveConfigSet = olympicConfigRepository.findOne(gameSType);

        return getConfigByName(boleOlympicLiveConfigSet, configName);
    }

    private List<BoleOlympicsLiveConfigSet.OlympicsConfig> getConfigByName(BoleOlympicsLiveConfigSet boleOlympicLiveConfigSet, String configName) {
        if (null == boleOlympicLiveConfigSet) {
            return Collections.emptyList();
        }
        if (configName.equals("match_config")) {
            return boleOlympicLiveConfigSet.getMatchExtendConfig();
        } else if (configName.equals("player_config")) {
            return boleOlympicLiveConfigSet.getPlayerExtendConfig();
        } else if (configName.equals("team_config")) {
            return boleOlympicLiveConfigSet.getTeamExtendConfig();
        } else if (configName.equals("player_stats_config")) {
            return boleOlympicLiveConfigSet.getPlayerStatsConfig();
        } else if (configName.equals("team_stats_config")) {
            return boleOlympicLiveConfigSet.getCompetitorStatsConfig();
        } else if (configName.equals("team_result_config")) {
            return boleOlympicLiveConfigSet.getTeamSectionResultConfig();
        }
        return Collections.emptyList();
    }


    @Override
    public boolean save(BoleOlympicsLiveConfigSet entity) {
        if (entity == null) return false;
        return olympicConfigRepository.save(entity);
    }

    @Override
    public BoleOlympicsLiveConfigSet findOne(Long aLong) {
        return olympicConfigRepository.findOne(aLong);
    }

    @Override
    public boolean delete(Long aLong) {
        BoleOlympicsLiveConfigSet boleOlympicsLiveConfigSet = olympicConfigRepository.findOne(aLong);
        if (null == boleOlympicsLiveConfigSet) {
            return false;
        }
        boleOlympicsLiveConfigSet.setDeleted(true);
        return olympicConfigRepository.save(boleOlympicsLiveConfigSet);
    }
}
