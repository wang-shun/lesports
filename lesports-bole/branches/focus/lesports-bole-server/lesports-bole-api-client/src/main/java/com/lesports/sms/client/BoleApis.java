package com.lesports.sms.client;

import com.lesports.bole.api.service.TBoleApiService;
import com.lesports.bole.api.vo.*;
import me.ellios.hedwig.rpc.client.ClientBuilder;
import me.ellios.hedwig.rpc.core.ServiceType;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/15
 */
public class BoleApis {
    private static final Logger LOG = LoggerFactory.getLogger(BoleApis.class);
    private static final TBoleApiService.Iface boleApiService = new ClientBuilder<TBoleApiService.Iface>()
            .serviceType(ServiceType.THRIFT).serviceFace(TBoleApiService.Iface.class).build();

    public static TBMatch getMatchById(long id) {
        try {
            return boleApiService.getMatchById(id);
        } catch (TException e) {
            LOG.error("fail to getMatchById. id : {}", id, e);
        }
        return null;
    }

    public static TBCompetition getCompetitionById(long id) {
        try {
            return boleApiService.getCompetitionById(id);
        } catch (TException e) {
            LOG.error("fail to getCompetitionById. id : {}", id, e);
        }
        return null;
    }

    public static TBCompetitionSeason getCompetitionSeasonById(long id) {
        try {
            return boleApiService.getCompetitionSeasonById(id);
        } catch (TException e) {
            LOG.error("fail to getCompetitionSeasonById. id : {}", id, e);
        }
        return null;
    }

    public static TBCompetitor getCompetitorById(long id) {
        try {
            return boleApiService.getCompetitorById(id);
        } catch (TException e) {
            LOG.error("fail to getCompetitorById. id : {}", id, e);
        }
        return null;
    }

    public static TBMatch getMatchBySmsId(long id) {
        try {
            return boleApiService.getMatchBySmsId(id);
        } catch (TException e) {
            LOG.error("fail to getMatchBySmsId. id : {}", id, e);
        }
        return null;
    }

    public static TBCompetition getCompetitionBySmsId(long id) {
        try {
            return boleApiService.getCompetitionBySmsId(id);
        } catch (TException e) {
            LOG.error("fail to getCompetitionBySmsId. id : {}", id, e);
        }
        return null;
    }

    public static TBCompetitionSeason getCompetitionSeasonBySmsId(long id) {
        try {
            return boleApiService.getCompetitionSeasonBySmsId(id);
        } catch (TException e) {
            LOG.error("fail to getCompetitionSeasonBySmsId. id : {}", id, e);
        }
        return null;
    }

    public static TBCompetitor getCompetitorBySmsId(long id) {
        try {
            return boleApiService.getCompetitorBySmsId(id);
        } catch (TException e) {
            LOG.error("fail to getCompetitorBySmsId. id : {}", id, e);
        }
        return null;
    }

    public static List<TBLive> getLivesBySmsMatchId(long id) {
        try {
            return boleApiService.getLivesBySmsMatchId(id);
        } catch (TException e) {
            LOG.error("fail to getLivesBySmsMatchId. id : {}", id, e);
        }
        return null;
    }

    public static TBNews getNewsById(Long id) {
        try {
            return boleApiService.getNewsById(id);
        } catch (TException e) {
            LOG.error("fail to getNewsById. id : {}", id, e);
        }
        return null;
    }

    public static TOlympicLiveConfigSet getOlympicsConfigSet(Long id) {
        try {
            return boleApiService.getOlympicLiveConfig(id);
        } catch (TException e) {
            LOG.error("fail to getOlympicsConfigSet. id : {}", id, e);
        }
        return null;
    }

}
