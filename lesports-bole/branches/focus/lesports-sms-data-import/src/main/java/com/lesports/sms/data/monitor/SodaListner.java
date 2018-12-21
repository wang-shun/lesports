package com.lesports.sms.data.monitor;

import com.lesports.sms.data.job.SodaDataJob;
import com.lesports.sms.data.model.SodaImportFileType;
import com.lesports.sms.data.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;
import java.util.List;

/**
 * Created by ruiyuansheng on 2016/3/14.
 */
public class SodaListner implements Runnable {
    private WatchService service;
    private String rootPath;
    private SodaImportFileType sodaImportFileType;

    private static Logger logger = LoggerFactory.getLogger(SodaListner.class);

    public SodaListner(WatchService service, String rootPath, SodaImportFileType sodaImportFileType) {
        this.service = service;
        this.rootPath = rootPath;
        this.sodaImportFileType = sodaImportFileType;
    }

    public void run() {

        try {
            while(true){
                WatchKey watchKey = service.take();
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                for(WatchEvent event : watchEvents){
                    WatchEvent.Kind kind = event.kind();

                    if(kind == StandardWatchEventKinds.OVERFLOW){//事件可能lost or discarded
                        logger.info("event lost or discarded");
                        continue;
                    }
                    logger.info("[" + rootPath  + event.context() + "]文件发生了[" + kind + "]事件");
                    SodaDataJob sodaDataJob = (SodaDataJob)SpringUtil.getSpringBean("sodaDataJob");
                    if(null != sodaDataJob) {
                        parser(sodaDataJob);
                    }
                }
                watchKey.reset();
            }
            } catch (InterruptedException e) {
                logger.error("watch file  error:{}",e);
                e.printStackTrace();
            }

    }

    private void parser(SodaDataJob sodaDataJob){

        switch(sodaImportFileType){
            case SODAMATCH:
                sodaDataJob.importSodaMatch();
                return;
            case SODAGOAL:
                sodaDataJob.importSodaTopPlayerGoalParser();
                return;
            case SODARANKING:
                sodaDataJob.importSodaRanking();
                return;
            case SODAASSIST:
                sodaDataJob.importSodaAssistParser();
                return;
            case SODAMATCHSTAT:
                sodaDataJob.importSodaConfrontation();
                return;
//            case SODAMATCHRESULT:
//                sodaDataJob.importSodaMatchResult();
//                return;
//            case SODATIMELINE:
//                sodaDataJob.importSodaTimeLine();
//                return;
            default:
                return;
        }
    }


}