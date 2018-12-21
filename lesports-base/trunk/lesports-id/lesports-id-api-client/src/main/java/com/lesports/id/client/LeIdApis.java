package com.lesports.id.client;

import com.lesports.id.api.IdType;
import com.lesports.id.api.TIdConstants;
import com.lesports.id.api.TIdService;
import me.ellios.hedwig.rpc.client.ClientBuilder;
import me.ellios.hedwig.rpc.core.ServiceType;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * User: ellios
 * Time: 15-6-1 : 下午10:08
 */
public class LeIdApis {

    private static final Logger LOG = LoggerFactory.getLogger(LeIdApis.class);

    private static final TIdService.Iface idService = new ClientBuilder<TIdService.Iface>()
            .serviceType(ServiceType.THRIFT).serviceFace(TIdService.Iface.class).build();

    public static long nextId(IdType type) {
        try {
            return idService.nextId(type);
        } catch (TException e) {
            LOG.error(e.getMessage(), e);
        }
        return -1;
    }

    public static IdType checkIdTye(long id) {
        int offset = (int) (id % TIdConstants.OFFSET_BASE);
        return IdType.findByValue(offset);
    }

    public static long getOldId(long newId) {
        if (newId % TIdConstants.OFFSET_BASE > 20) {
            LOG.info("newId is not in valid range. try as old id");
            return newId;
        }
        return newId / TIdConstants.OFFSET_BASE;
    }

    public static long getNewId(long oldId, IdType idType) {
        if (oldId < 300000) {
            return oldId * TIdConstants.OFFSET_BASE + idType.getValue();
        }
        return oldId;
    }

    //获取和赛事id关联的主节目id
    public static long getMainEpisodeIdByMatchId(long mid){
        return (mid/1000)*1000 + IdType.EPISODE.getValue();
    }

	//通过节目id获取对应的赛程id
	public static long getMatchIdByEpisodeId(long eid){
		return (eid/1000)*1000 + IdType.MATCH.getValue();
	}

    /**
     * 判断是否老id
     *
     * @param id
     * @return
     */
    public static boolean isOldId(long id) {
        return id < 100000000;
    }

}
