package com.lesports.sms.data.bolt.parser;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import com.lesports.sms.data.Constants;
import com.lesports.sms.data.job.DataImportJob;
import com.lesports.sms.data.parser.FileParser;
import com.lesports.sms.data.parser.FileParserFactory;
import com.lesports.sms.data.utils.FtpUtil;
import com.lesports.utils.LeProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Date;
import java.util.List;

import static com.lesports.sms.data.Constants.*;

/**
 * lesports-bole.
 *
 * @author pangchuanxiao
 * @since 2016/4/18
 */
public class FileParserBolt extends BaseBasicBolt {
    private static final Logger LOG = LoggerFactory.getLogger(FileParserBolt.class);
    private final String LOCAL_PATH = LeProperties.getString("local.download.path", "/letv/data/olympics/");
    private final int HTTP_CONNECTION_TIME_OUT_IN_MILL_SECONDS = 10 * 1000;
    private final int HTTP_READ_TIME_OUT_IN_MILL_SECONDS = 10 * 1000;
    private final int FAIL_SLEEP_TIME_IN_MILL_SECONDS = 2 * 1000;
    private final int FTP_LOGIN_TIME_OUT_IN_SECONDS = 60;
    private final int FAIL_MAX_RETRY_COUNT = 3;
    private final FileParserFactory fileParserFactory = new FileParserFactory();

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        DataImportJob job = (DataImportJob) input.getValueByField(BOLT_FIELD_JOB);
        String fileUri = input.getStringByField(BOLT_FIELD_FILE_URI);
        String code = getCodeByName(fileUri);
        if (StringUtils.isEmpty(code)) {
            LOG.warn("will not handle this, can not get code for : {}.", fileUri);
            return;
        }
        FileParser fileParser = fileParserFactory.getFileParser(code, fileUri);
        if (null == fileParser) {
            LOG.warn("will not handle this, no file parser for : {}, {}.", code, fileUri);
            return;
        }
        FileInputStream fileInputStream = getFileInputStream(fileUri);
        if (null == fileInputStream) {
            LOG.warn("will not handle this, file not exists : {}.", fileUri);
            return;
        }
        try {
            List<Object> objects = fileParser.doExecute(job, fileInputStream, collector);
            if (CollectionUtils.isNotEmpty(objects)) {
                for (Object object : objects) {
                    collector.emit(new Values(job, code, object));
                }
            }
        } catch (Exception e) {
            LOG.error("{}", e.getMessage(), e);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(BOLT_FIELD_JOB, BOLT_FIELD_CODE, BOLT_FIELD_PARSER_RESULT));
    }


    String getCodeByName(String fileName) {
        if (fileName == null) return null;
        if (fileName.endsWith("SPORT.xml") || fileName.endsWith("SPORT.xlsx")) {
            return Constants.CODE_SPORT;
        } else if (fileName.contains("DT_CODES_ODF_SPORT_CODES.xml")) {
            return Constants.CODE_SPORT_CODE;
        } else if (fileName.contains("MEDALLISTS_DISCIPLINE")) {
            return Constants.CODE_MEDAL;
        } else if (fileName.endsWith("DISCIPLINE.xml") || fileName.endsWith("DISCIPLINE.xlsx")) {
            return Constants.CODE_DISCIPLINE;
        } else if (fileName.endsWith("EVENT.xml") || fileName.endsWith("EVENT.xlsx")) {
            return Constants.CODE_EVENT;
        } else if (fileName.endsWith("COUNTRY.xml") || fileName.endsWith("Country.xlsx")) {
            return Constants.CODE_COUNTRY;
        } else if (fileName.contains("PHASE.xml") || fileName.endsWith("PHASE.xlsx")) {
            return Constants.CODE_PHASE;
        } else if (fileName.contains("DT_PARTIC_TEAMS")) {
            return Constants.CODE_TEAM;
        } else if (fileName.contains("DT_PARTIC")) {
            return Constants.CODE_PLAYER;
        } else if (fileName.contains("DT_SCHEDULE"))
        {
            return Constants.CODE_SCHEDULE;
        } else if (fileName.contains("PLAY_BY_PLAY.xml"))

        {
            return Constants.CODE_PLAY_BY_PLAY;
        } else if (fileName.contains("RESULT") || fileName.contains("outright_Motorsport"))

        {
            return Constants.CODE_LIVE_RESULT;
        } else if (fileName.contains("RECORD"))

        {
            return Constants.CODE_RECORD;
        } else if (fileName.contains("FILE_LIST"))

        {
            return Constants.CODE_FILE_LIST;
        }else if (fileName.contains(".zip"))

        {
            return Constants.CODE_OLD_LIST;
        }
        else

        {
            return null;
        }
        //   return null;
    }

    FileInputStream getFileInputStream(String fileUri) {
        URL url = null;

        try {
            url = new URL(fileUri);
        } catch (MalformedURLException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        String schema = url.getProtocol();
        String localPath = LOCAL_PATH;
        if ("ftp".equalsIgnoreCase(schema)) {
            String host = url.getHost();
            int port = url.getPort();
            String username = getUsername(url), password = getPassword(url);
            String remotePath = getPathWithoutName(url), fileName = getFileName(url);
            downloadFileFromFtp(host, port, username, password, remotePath, fileName, localPath);
            localPath = localPath + fileName;
        } else if ("http".equalsIgnoreCase(schema)) {
            String fileName = getFileName(url), username = getUsername(url), password = getPassword(url);
            String parentDir = getparentDir(fileName);
            localPath = localPath + parentDir + fileName;
            downloadFileFromHttp(url, localPath, username, password);
        } else if ("file".equalsIgnoreCase(schema)) {
            localPath = url.getPath();
        }

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(new File(localPath));
        } catch (FileNotFoundException e) {
            LOG.error("{}", e.getMessage(), e);
        }
        return fileInputStream;
    }

    private String getparentDir(String url) {
        if (StringUtils.isBlank(url)) return "";

        if (url.contains("DT_SCHEDULE")) {
            return "/schedule/" + url.substring(4, 13) + "/";
        } else if (url.contains("DT_RESULT")) {
            return "/result/" + url.substring(4, 13) + "/";
        } else if (url.contains("DT_PARTIC_TEAMS")) {
            return "/team/" + url.substring(4, 13) + "/";
        } else if (url.contains("DT_PARTIC")) {
            return "/player/" + url.substring(4, 13) + "/";
        } else if (url.contains("DT_MEDALLIST")) {
            return "/medal/" + url.substring(4, 13) + "/";
        } else if (url.contains("DT_RECORD")) {
            return "/record/" + url.substring(4, 13) + "/";
        }
        return "";
    }

    private Boolean downloadFileFromFtp(String ftpHost, int ftpPort, String ftpUsername, String ftpPassword,
                                        String ftpPath, String fileName, String localRootPath) {
        boolean result = true;
        FtpUtil ftpUtil = new FtpUtil(ftpHost, ftpPort, ftpUsername, ftpPassword);
        try {
            if (!ftpUtil.loginFtp(FTP_LOGIN_TIME_OUT_IN_SECONDS)) {
                return false;
            }
            boolean downloadResult = ftpUtil.downloadFile(fileName, localRootPath, ftpPath);
            if (!downloadResult) {
                LOG.error("download file fail!{}", fileName);
                result = false;
            } else {
                LOG.info("download file success!{}", fileName);
            }
        } catch (IOException e) {
            LOG.error("{}", e);
            result = false;
        } finally {
            ftpUtil.logOutFtp();
        }
        return result;
    }

    public Boolean downloadFileFromHttp(URL url, String filePath, String username, String password) {
        Authenticator.setDefault(new CustomAuthenticator(username, password));
        boolean isSuccess = false;
        int tryCount = 0;
        while (isSuccess == false && tryCount++ < FAIL_MAX_RETRY_COUNT) {
            try {
                FileUtils.copyURLToFile(url, new File(filePath), HTTP_CONNECTION_TIME_OUT_IN_MILL_SECONDS, HTTP_READ_TIME_OUT_IN_MILL_SECONDS);
                isSuccess = true;
            } catch (FileNotFoundException e) {
                LOG.warn("file can not be found on this url : {}, local path : {}", url, filePath);
                return false;
            } catch (ConnectException e) {
                LOG.warn("connection to the url time out : {}", url);
                sleep(FAIL_SLEEP_TIME_IN_MILL_SECONDS);
            } catch (SocketTimeoutException e) {
                LOG.warn("read time overtime");
                sleep(FAIL_SLEEP_TIME_IN_MILL_SECONDS);
            } catch (Exception e) {
                LOG.error("{}", e.getMessage(), e);
            }
        }
        LOG.info("download file from : {}, result : {}.", url, isSuccess);
        return isSuccess;
    }

    private void sleep(int milisec) {
        try {
            Thread.sleep(milisec);
        } catch (InterruptedException e1) {
            LOG.error("{}", e1.getMessage(), e1);
        }
    }

    private String getUsername(URL url) {
        if (StringUtils.isNotEmpty(url.getUserInfo())) {
            String[] userInfo = StringUtils.split(url.getUserInfo(), ":");
            return userInfo[0];
        }
        return null;
    }

    private String getPassword(URL url) {
        if (StringUtils.isNotEmpty(url.getUserInfo())) {
            String[] userInfo = StringUtils.split(url.getUserInfo(), ":");
            return userInfo[1];
        }
        return null;
    }

    private String getFileName(URL url) {
        if (url == null || StringUtils.isBlank(url.getPath())) return null;
        if (url.getPath().contains("FILE_LIST")) {
            return StringUtils.substringAfterLast(url.getPath(), "/");
        } else {
            return StringUtils.substringAfterLast(url.getPath(), "/");
        }
    }

    private String getPathWithoutName(URL url) {
        if (StringUtils.isNotEmpty(url.getPath())) {
            return StringUtils.substringBeforeLast(url.getPath(), "/");
        }
        return null;
    }

    public static class CustomAuthenticator extends Authenticator {
        private String username;
        private String password;

        public CustomAuthenticator(String username, String password) {
            this.username = username;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password.toCharArray());
        }
    }

}
