package com.lesports.id.utils;

import com.lesports.id.repository.IdRepositoryImpl;
import com.lesports.utils.LeProperties;
import com.lesports.utils.math.LeNumberUtils;
import me.ellios.jedis.RedisClientFactory;
import me.ellios.jedis.RedisOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/3/31
 */
public class AreaMachineContainer {
    private static final RedisOp REDIS_OP = RedisClientFactory.getRedisClient("rpc_id_repo");
    private static final Logger LOG = LoggerFactory.getLogger(IdRepositoryImpl.class);
    private static final Long AREA_CODE = LeProperties.getLong("lesports.id.area", 0);
    private static final String AREA_MACHINE_PATTERN = "LESPORTS_AREA_MACHINE_";
    private static final String AREA_CUR_PATTERN = "LESPORTS_AREA_CUR_";
    private static final Long MACHINE_CODE;

    private AreaMachineContainer() {
    }

    static {
        String ip = getMyIPLocal();
        String areaMachineKey = AREA_MACHINE_PATTERN + AREA_CODE;
        String machineValue = REDIS_OP.hget(areaMachineKey, ip);
        if (null == machineValue) {
            String areaCurKey = AREA_CUR_PATTERN + AREA_CODE;
            MACHINE_CODE = REDIS_OP.incr(areaCurKey) % 10;
            machineValue = String.valueOf(MACHINE_CODE);
            REDIS_OP.hset(areaMachineKey, ip, machineValue);
        } else {
            MACHINE_CODE = LeNumberUtils.toLong(machineValue);
        }
    }

    public static Long getAreaCode() {
        return AREA_CODE;
    }

    public static Long getMachineCode() {
        return MACHINE_CODE;
    }

    private static String getMyIPLocal() {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            return ia.getHostAddress();
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return "0.0.0.0";
    }
}
