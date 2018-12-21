package com.lesports.id.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.lesports.id.api.IdType;
import com.lesports.id.api.TIdConstants;
import com.lesports.id.repository.IdRepository;
import com.lesports.id.utils.AreaMachineContainer;
import com.lesports.utils.LeProperties;
import com.lesports.utils.math.LeNumberUtils;
import org.omg.CORBA.IDLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;


/**
 * User: ellios
 * Time: 15-6-1 : 下午9:29
 */
@Service("idService")
public class IdServiceImpl implements IdService {

    private static final Logger LOG = LoggerFactory.getLogger(IdServiceImpl.class);

    private static final Map<IdType, Integer> ID_OFFSET_MAP = Maps.newHashMap();

    private static final int ID_INC_STEP = LeProperties.getInt("id.inc.step", 10);

    private final ConcurrentMap<IdType, IDPool> idTypePoolMap = new ConcurrentHashMap();

    @Resource
    private IdRepository idRepository;

    private IDPool getIdPool(IdType type) {
        IDPool pool = idTypePoolMap.get(type);
        if (pool == null) {
            synchronized (IdServiceImpl.class) {
                pool = idTypePoolMap.get(type);
                if (pool == null) {
                    pool = new IDPool(type);
                    idTypePoolMap.put(type, pool);
                }
            }
        }
        return pool;
    }

    @Override
    public long nextId(IdType type) {
        Preconditions.checkNotNull(type);
        if (type.getValue() > 1000) {
            LOG.warn("operator type : {}, can not generate id.", type);
            return -1;
        }

        long id = getIdPool(type).getId();

//        return (id * 100 + AreaMachineContainer.getAreaCode() * 10 + AreaMachineContainer.getMachineCode()) * TIdConstants.OFFSET_BASE + type.getValue();
        return id * TIdConstants.OFFSET_BASE + type.getValue();
    }

    private int getOffsetByType(IdType type) {
        return ID_OFFSET_MAP.get(type);
    }

    private class IDPool {
        private BlockingQueue<Long> idQueue = new ArrayBlockingQueue<>(ID_INC_STEP * 2);
        private final IdType idType;
        private IdProducer idProducer;

        private IDPool(IdType idType) {
            this.idType = idType;
            this.idProducer = new IdProducer(idQueue, idType);
        }

        private void produce() {
            ThreadFactory threadFactory = new ThreadFactoryBuilder()
                    .setNameFormat("id-" + idType.name() + "-generator-%d")
                    .build();
            Thread t = threadFactory.newThread(idProducer);
            t.start();
        }

        public Long getId() {
            try {
                Long id = null;
                while (null == id) {
                    id = idQueue.poll(10, TimeUnit.MILLISECONDS);
                    if (null == id) {
                        synchronized (this) {
                            id = idQueue.poll(10, TimeUnit.MILLISECONDS);
                            if (null == id) {
                                produce();
                            }
                        }
                    }
                }

                return id;
            } catch (InterruptedException e) {
                LOG.error("{}", e.getMessage(), e);
            }
            return 0L;
        }

    }

    private class IdProducer implements Runnable {
        private BlockingQueue<Long> idQueue;
        private IdType idType = null;
        private ReentrantLock lock = new ReentrantLock();

        private IdProducer(BlockingQueue<Long> idQueue, IdType idType) {
            this.idQueue = idQueue;
            this.idType = idType;
        }

        private void produce() {
            //没有其他线程在执行生产操作
            if (!lock.tryLock()) {
                LOG.debug("skip producing id for : {} as some other thread is producing.", idType);
                return;
            }
            try {
                LOG.info("producing id for : {}", idType);
                long nextId = idRepository.nextId(idType, ID_INC_STEP);
                for (int i = 1; i <= ID_INC_STEP; i++) {
                    try {
                        idQueue.put(nextId - (ID_INC_STEP - i));
                    } catch (InterruptedException e) {
                        LOG.error("{}", e.getMessage(), e);
                    }
                }
            } catch (Exception e) {
                LOG.error("{}", e.getMessage(), e);
            } finally {
                lock.unlock();
            }
        }

        @Override
        public void run() {
            produce();
        }
    }
}
