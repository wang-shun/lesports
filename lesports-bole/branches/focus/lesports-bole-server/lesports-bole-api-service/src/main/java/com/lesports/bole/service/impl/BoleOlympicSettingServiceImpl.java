package com.lesports.bole.service.impl;

import com.google.common.collect.Lists;
import com.lesports.bole.model.BoleOlympicSettingDataSet;
import com.lesports.bole.repository.BoleOlympicSettingDataRepository;
import com.lesports.bole.service.BoleOlympicSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * Created by yangyu on 2015/7/1.
 */
@Service("olympiSettingService")
public class BoleOlympicSettingServiceImpl implements BoleOlympicSettingService {

    private static final Logger LOG = LoggerFactory.getLogger(BoleOlympicSettingServiceImpl.class);
    @Resource
    BoleOlympicSettingDataRepository olympicSettigRepository;

    @Override
    public boolean save(BoleOlympicSettingDataSet entity) {
        if (entity == null) return false;
        return olympicSettigRepository.save(entity);
    }

    @Override
    public BoleOlympicSettingDataSet findOne(String id) {
        return olympicSettigRepository.findOne(id);
    }

    @Override
    public boolean delete(String id) {
        BoleOlympicSettingDataSet entity = olympicSettigRepository.findOne(id);
        if (entity == null) return true;
        else {
            entity.setDeleted(true);
            olympicSettigRepository.save(entity);
            return true;
        }
    }

    @Override
    public List<String> findConfigList(Long id, String settingDataType) {
        BoleOlympicSettingDataSet entity = olympicSettigRepository.findOne(id.toString());
        if (null == entity) {
            return Collections.emptyList();
        }
        if (settingDataType.equals("match_config")) {
            List list = Lists.newArrayList(entity.getMatchExtendConfigData());
            list.add(list.size(), "CREATE_PATH");
            return list;
        } else if (settingDataType.equals("player_config")) {
            List list = Lists.newArrayList(entity.getPlayerExtendConfigData());
            list.add(list.size(), "CREATE_PATH");
            return list;
        } else if (settingDataType.equals("team_config")) {
            List list = Lists.newArrayList(entity.getTeamExtendConfigData());
            list.add(list.size(), "CREATE_PATH");
            return list;
        } else if (settingDataType.equals("player_stats_config1")) {
            return Lists.newArrayList(entity.getPlayerStatsConfigConditionData());
        } else if (settingDataType.equals("player_stats_config")) {
            List list = Lists.newArrayList(entity.getPlayerStatsConfigData());
            list.add(list.size(), "CREATE_PATH");
            return list;
        } else if (settingDataType.equals("team_stats_config1")) {
            return Lists.newArrayList(entity.getCompetitorStatsConfigConditionData());
        } else if (settingDataType.equals("team_stats_config")) {
            List list = Lists.newArrayList(entity.getCompetitorStatsConfigData());
            list.add(list.size(), "CREATE_PATH");
            return list;
        }
        else if (settingDataType.equals("team_result_config1")) {
            List list = Lists.newArrayList(entity.getTeamResultConditionData());
            return list;
        }
        else if (settingDataType.equals("team_result_config")) {
            List list = Lists.newArrayList(entity.getTeamResultData());
            list.add(list.size(), "CREATE_PATH");
            return list;
        }
        return Collections.emptyList();
    }
}

