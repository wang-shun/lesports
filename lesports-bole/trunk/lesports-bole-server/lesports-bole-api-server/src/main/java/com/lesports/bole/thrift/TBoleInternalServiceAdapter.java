package com.lesports.bole.thrift;

import com.lesports.bole.api.service.TBoleInternalService;
import com.lesports.bole.api.vo.TOlympicSettingDataSet;
import com.lesports.bole.model.BoleOlympicSettingDataSet;
import com.lesports.bole.service.*;
import com.lesports.id.api.IdType;
import com.lesports.id.client.LeIdApis;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * User: pangchuanxiao
 * Time: 15-5-17 : 下午4:13
 */
@Service("thriftBoleInternalService")
public class TBoleInternalServiceAdapter implements TBoleInternalService.Iface {

    private static final Logger LOG = LoggerFactory.getLogger(TBoleInternalServiceAdapter.class);
    @Resource
    private BoleMatchService boleMatchService;
    @Resource
    private BoleCompetitorService boleCompetitorService;
    @Resource
    private BoleCompetitionSeasonService boleCompetitionSeasonService;
    @Resource
    private BoleCompetitionService boleCompetitionService;
    @Resource
    private BoleOlympicSettingService boleOlympicSettingService;

    @Override
    public boolean updateBoleWithSms(long entityId) throws TException {
        IdType idType = LeIdApis.checkIdTye(entityId);
        if (null == idType) {
            return false;
        }
        try {
            switch (idType) {
                case MATCH:
                    return boleMatchService.saveSms(entityId);
                case COMPETITION:
                    return boleCompetitionService.saveSms(entityId);
                case COMPETITION_SEASON:
                    return boleCompetitionSeasonService.saveSms(entityId);
                case TEAM:
                case PLAYER:
                    return boleCompetitorService.saveSms(entityId);
            }
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
            return false;
        }

        return true;
    }
    @Override
    public boolean saveOlympicLiveConfig(TOlympicSettingDataSet entity) throws TException {
        try {
            BoleOlympicSettingDataSet settingDataSet=new BoleOlympicSettingDataSet();
            settingDataSet.setId(entity.getId());
            settingDataSet.setDeleted(false);
            settingDataSet.setMatchExtendConfigData(entity.getMatchExtendConfig());
            settingDataSet.setTeamExtendConfigData(entity.getTeamExtendConfig());
            settingDataSet.setCompetitorStatsConfigConditionData(entity.getCompetitorStatsConditionConfig());
            settingDataSet.setCompetitorStatsConfigData(entity.getCompetitorStatsConfig());
            settingDataSet.setPlayerExtendConfigData(entity.getPlayerExtendConfig());
            settingDataSet.setPlayerStatsConfigConditionData(entity.getPlayerStatsConditionConfig());
            settingDataSet.setPlayerStatsConfigData(entity.getPlayerStatsConditionConfig());
            settingDataSet.setTeamResultConditionData(entity.getResultConditionConfig());
            settingDataSet.setTeamResultData(entity.getResultConfig());
            return boleOlympicSettingService.save(settingDataSet);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return false;
    }
}
