package com.lesports.msg.model;

import java.util.Map;

/**
 * lesports-projects.
 * http://wiki.letv.cn/pages/viewpage.action?pageId=19780434
 * [
 * properties={
 * id=95168,
 * playPlatform={
 * new=,420007,420001,420003,420005,,
 * old=, 420007,420001,420003,420005,
 * },
 * cid=4,
 * sourceId=200001
 * },
 * userId=null,
 * messageActionCode=102,
 * messageModuleCode=0,
 * jobId=1418279203607,
 * redeliveryMode=NoRedelivery
 * ]
 *
 * @author: pangchuanxiao
 * @since: 2015/7/15
 */
public class LetvMMSMessage {
    private MmsMessage LetvMMSMessage;

    public MmsMessage getLetvMMSMessage() {
        return LetvMMSMessage;
    }

    public void setLetvMMSMessage(MmsMessage letvMMSMessage) {
        LetvMMSMessage = letvMMSMessage;
    }

    public static class MmsMessage {
        private Property properties;
        private long userId;
        private int messageActionCode;
        private int messageModuleCode;
        private long jobId;
        private String redeliveryMode;

        public Property getProperties() {
            return properties;
        }

        public void setProperties(Property properties) {
            this.properties = properties;
        }

        public long getUserId() {
            return userId;
        }

        public void setUserId(long userId) {
            this.userId = userId;
        }

        public MessageActionCode getMessageActionCode() {
            return MessageActionCode.valueOf(messageActionCode);
        }

        public void setMessageActionCode(int messageActionCode) {
            this.messageActionCode = messageActionCode;
        }

        public MessageModuleCode getMessageModuleCode() {
            return MessageModuleCode.valueOf(messageModuleCode);
        }

        public void setMessageModuleCode(int messageModuleCode) {
            this.messageModuleCode = messageModuleCode;
        }

        public long getJobId() {
            return jobId;
        }

        public void setJobId(long jobId) {
            this.jobId = jobId;
        }

        public String getRedeliveryMode() {
            return redeliveryMode;
        }

        public void setRedeliveryMode(String redeliveryMode) {
            this.redeliveryMode = redeliveryMode;
        }
    }


    public static class Property {
        private long id;
        private Map<String, String> playPlatform;
        private int cid;
        private String sourceId;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public Map<String, String> getPlayPlatform() {
            return playPlatform;
        }

        public void setPlayPlatform(Map<String, String> playPlatform) {
            this.playPlatform = playPlatform;
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public String getSourceId() {
            return sourceId;
        }

        public void setSourceId(String sourceId) {
            this.sourceId = sourceId;
        }
    }

    public static enum MessageModuleCode{
        MMS(0);
        private int code;

        static MessageModuleCode valueOf(int code) {
            switch (code) {
                case 0:
                    return MMS;
            }
            return null;
        }

        MessageModuleCode(int code) {
            this.code = code;
        }
    }

    public static enum MessageActionCode {
        ALBUM_ADD(101), //专辑添加
        ALBUM_UPDATE(102),//专辑修改
        ALBUM_DELETE(103), //专辑删除
        VIDEO_ADD(104), //视频添加
        VIDEO_UPDATE(105), //视频修改
        VIDEO_DELETE(106); //视频删除
        //        107   , //明星添加
//        108    ,//明星修改
//        109   , //明星删除
//        110   , //TV添加
//        111   , //TV修改
//        112   , //TV删除
//        113   , //字典添加
//        114   , //字典修改
//        115    ;//字典删除
        private int code;

        static MessageActionCode valueOf(int code) {
            switch (code) {
                case 101:
                    return ALBUM_ADD;
                case 102:
                    return ALBUM_UPDATE;
                case 103:
                    return ALBUM_DELETE;
                case 104:
                    return VIDEO_ADD;
                case 105:
                    return VIDEO_UPDATE;
                case 106:
                    return VIDEO_DELETE;
            }
            return null;
        }
        MessageActionCode(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }
    }

}
