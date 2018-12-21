package com.lesports.sms.data.monitor;

import com.lesports.sms.data.model.SodaImportFileType;
import com.lesports.utils.LeProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ruiyuansheng on 2016/3/14.
 */
public class SodaMonitor {

    private static Logger logger = LoggerFactory.getLogger(SodaMonitor.class);

    private static ExecutorService fixedThreadPool = Executors.newFixedThreadPool(LeProperties.getInt("monitor.threadCount",5));
    private WatchService ws;
    private String listenerPath;
    private SodaImportFileType sodaImportFileType;

    private static final String SODA_MATCH_FILE_PATH = LeProperties.getString("soda.match.file.path");
    private static final String SODA_MATCH__STAT_FILE_PATH = LeProperties.getString("soda.match.stat.file.path");
    private static final String SODA_MATCH_STAT_FILE_NAMES = LeProperties.getString("soda.match.stat.file.names");
    private static final String SODA_RANKING_FILE_PATH = LeProperties.getString("soda.ranking.file.path");
    private static final String SODA_PLAYER_GOAL_FILE_PATH = LeProperties.getString("soda.player.goal.file.path");
    private static final String SODA_PLAYER_ASSIST_FILE_PATH = LeProperties.getString("soda.player.assist.file.path");

    private static final String SODA_CSL_MATCH_RESULT_FILE_PATH = LeProperties.getString("soda.csl.matchresult.file.path");
    private static final String SODA_CSL_TIME_LINE_FILE_PATH = LeProperties.getString("soda.csl.timeline.file.path");

    private SodaMonitor(String path, SodaImportFileType sodaImportFileType) {
        try {
            ws = FileSystems.getDefault().newWatchService();
            this.listenerPath = path;
            this.sodaImportFileType = sodaImportFileType;
            start();
        } catch (IOException e) {
            logger.error("ResourceListener file error:{}",e);
            e.printStackTrace();
        }
    }

    private void start() {
        fixedThreadPool.execute(new SodaListner(ws,this.listenerPath,this.sodaImportFileType));
    }

    public static void addMonitor() throws IOException {
        monitor(SODA_MATCH_FILE_PATH,SodaImportFileType.SODAMATCH);
        monitor(SODA_RANKING_FILE_PATH,SodaImportFileType.SODARANKING);
        monitor(SODA_PLAYER_GOAL_FILE_PATH,SodaImportFileType.SODAGOAL);
        monitor(SODA_PLAYER_ASSIST_FILE_PATH,SodaImportFileType.SODAASSIST);

        monitor(SODA_CSL_MATCH_RESULT_FILE_PATH,SodaImportFileType.SODAMATCHRESULT);
        monitor(SODA_CSL_TIME_LINE_FILE_PATH,SodaImportFileType.SODATIMELINE);

        String[] fileNames = SODA_MATCH_STAT_FILE_NAMES.split("\\|");
        if(null != fileNames) {
            for (String fileName : fileNames) {
                String filePath = SODA_MATCH__STAT_FILE_PATH + fileName;
                monitor(filePath,SodaImportFileType.SODAMATCHSTAT);
            }
        }
    }


    private static void monitor(String path,SodaImportFileType sodaImportFileType) {
        try {
            SodaMonitor sodaMonitor = new SodaMonitor(path,sodaImportFileType);
            Path p = Paths.get(path);
            if(Files.isDirectory(p,LinkOption.NOFOLLOW_LINKS)) {
                p.register(sodaMonitor.ws, StandardWatchEventKinds.ENTRY_MODIFY,
                        StandardWatchEventKinds.ENTRY_CREATE);
            }
        } catch (IOException e) {
                logger.error("monitor file error:{}",e);
                e.printStackTrace();
            }
    }


}
