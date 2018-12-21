package com.lesports.bole.thrift;

import com.lesports.bole.api.service.TBoleApiService;
import com.lesports.bole.api.vo.*;
import com.lesports.bole.model.BoleOlympicSettingDataSet;
import com.lesports.bole.service.*;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * User: pangchuanxiao
 * Time: 15-5-17 : 下午4:13
 */
@Service("thriftBoleApiService")
public class TBoleApiServiceAdapter implements TBoleApiService.Iface {

    private static final Logger LOG = LoggerFactory.getLogger(TBoleApiServiceAdapter.class);
    @Resource
    private BoleMatchService boleMatchService;
    @Resource
    private BoleCompetitorService boleCompetitorService;
    @Resource
    private BoleCompetitionSeasonService boleCompetitionSeasonService;
    @Resource
    private BoleCompetitionService boleCompetitionService;
    @Resource
    private BoleNewsService boleNewsService;
    @Resource
    private BoleOlympicConfigService boleOlympicConfigService;
    @Resource
    private BoleOlympicSettingService boleOlympicSettingService;

    @Override
    public TBCompetition getCompetitionById(long entityId) throws TException {
        try {
            return boleCompetitionService.getTBCompetitionById(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TBCompetitionSeason getCompetitionSeasonById(long entityId) throws TException {
        try {
            return boleCompetitionSeasonService.getTBCompetitionSeasonById(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TBCompetitor getCompetitorById(long entityId) throws TException {
        try {
            return boleCompetitorService.getTBCompetitorById(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TBMatch getMatchById(long entityId) throws TException {
        try {
            return boleMatchService.getTBMatchById(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TBCompetition getCompetitionBySmsId(long entityId) throws TException {
        try {
            return boleCompetitionService.getTBCompetitionBySmsId(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TBCompetitionSeason getCompetitionSeasonBySmsId(long entityId) throws TException {
        try {
            return boleCompetitionSeasonService.getTBCompetitionSeasonBySmsId(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TBCompetitor getCompetitorBySmsId(long entityId) throws TException {
        try {
            return boleCompetitorService.getTBCompetitorBySmsId(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TBMatch getMatchBySmsId(long entityId) throws TException {
        try {
            return boleMatchService.getTBMatchBySmsId(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<TBLive> getLivesBySmsMatchId(long entityId) throws TException {
        try {
            return boleMatchService.getTLivesBySmsMatchId(entityId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TBNews getNewsById(long id) throws TException {
        try {
            return boleNewsService.getById(id);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public TOlympicLiveConfigSet getOlympicLiveConfig(long gameSTypeId) throws TException {
        try {
            return boleOlympicConfigService.getConfig(gameSTypeId);
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return null;
    }

}
