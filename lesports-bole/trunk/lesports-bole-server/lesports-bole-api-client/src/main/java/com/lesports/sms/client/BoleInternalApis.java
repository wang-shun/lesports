package com.lesports.sms.client;

import com.lesports.bole.api.service.TBoleInternalService;
import com.lesports.bole.api.vo.TOlympicSettingDataSet;
import me.ellios.hedwig.rpc.client.ClientBuilder;
import me.ellios.hedwig.rpc.core.ServiceType;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: ellios
 * Time: 15-6-3 : 下午9:32
 */
public class BoleInternalApis {

    private static final Logger LOG = LoggerFactory.getLogger(BoleInternalApis.class);

    private static final TBoleInternalService.Iface boleInternalService = new ClientBuilder<TBoleInternalService.Iface>()
            .serviceType(ServiceType.THRIFT).serviceFace(TBoleInternalService.Iface.class).build();

    public static boolean updateBoleWithSms(long id) {
        try {
            return boleInternalService.updateBoleWithSms(id);
        } catch (TException e) {
            LOG.error("fail to updateBoleWithSms. id : {}", id, e);
        }
        return false;
    }
    public static boolean saveTOlympicSettingDataSet(TOlympicSettingDataSet entity){
        try {
            return boleInternalService.saveOlympicLiveConfig(entity);
        } catch (TException e) {
            LOG.error("fail to save TOlympicSettingData. id : {}", entity.getId(), e);
        }
        return false;
    }
}
