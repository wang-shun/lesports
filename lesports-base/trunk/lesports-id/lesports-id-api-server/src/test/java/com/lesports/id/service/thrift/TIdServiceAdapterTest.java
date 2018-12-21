package com.lesports.id.service.thrift;

import com.google.common.collect.Lists;
import com.lesports.AbstractIntegrationTest;
import com.lesports.id.api.IdType;
import com.lesports.id.api.TIdService;
import me.ellios.hedwig.common.utils.ConcurrentHashSet;
import me.ellios.jedis.RedisClientFactory;
import me.ellios.jedis.RedisOp;
import org.apache.thrift.TException;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class TIdServiceAdapterTest extends AbstractIntegrationTest {

    public static final RedisOp REDIS_OP = RedisClientFactory.getRedisClient("rpc_id_repo");

    @Resource
    private TIdService.Iface thriftIdService;

    @Test
    public void testNextId() throws Exception {
        long id = thriftIdService.nextId(IdType.COMPETITION);
        System.out.println("=================================");
        System.out.println(id);
        System.out.println("=================================");
    }

    @Test
    public void testUpdateNewsRedis() throws Exception {
        REDIS_OP.set("LESPORTS_ID_19", "623130".getBytes());
        String s = REDIS_OP.get("LESPORTS_ID_19");
        System.out.println(s);
    }

    @Test
    public void testNextIdConcurrency() throws Exception {
        final int max = 10000;
        int concurrency = 1;
        final CountDownLatch countDownLatch = new CountDownLatch(max);
        ExecutorService executorService = Executors.newFixedThreadPool(concurrency);
        int cur = 0;
        final List<IdType> idTypes = Lists.newArrayList(IdType.CAROUSEL, IdType.PRODUCT, IdType.BUSINESS);

        while (cur++ < max) {
            final int current = cur;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
//                        long id = thriftIdService.nextId(idTypes.get(0));
                        IdType idType = idTypes.get(0);
                        long id = thriftIdService.nextId(idType);

                        System.out.println("get " + idType + "         " + id + "          " + current);
                    } catch (TException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }

                }
            });
        }

        countDownLatch.await();
        long id1 = thriftIdService.nextId(IdType.ACTION_LOG);
        long id2 = thriftIdService.nextId(IdType.PRODUCT);
        long id3 = thriftIdService.nextId(IdType.BUSINESS);

    }
}