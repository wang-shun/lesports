package com.lesports.websocket.domain;



import com.lesports.sms.api.common.TextLiveMessageType;
import com.lesports.sms.api.common.TextLiveType;
import com.lesports.sms.api.vo.TSimpleAnchor;
import com.lesports.sms.api.vo.TTextLiveMessage;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by lufei1 on 2015/9/20.
 */
public class TextLiveMessageVo implements Serializable {
    private static final long serialVersionUID = 6688586345985429350L;

    //
    private long id;
    //图文直播id
    private long textLiveId;
    //分节Id
    private long sectionId;
    //
    private String section;
    private String time;
    private String matchResult;
    private TextLiveMessageType type;
    //内容
    private Object content;
    private SimpleAnchor anchor;
    private String sendTime;
    private TextLiveType textLiveType;

    private Boolean deleted;

    /**
     * 直播员
     */
    public class SimpleAnchor implements Serializable {
        private static final long serialVersionUID = 6688586345985429351L;

        //直播员昵称
        private String name;
        //直播员头像
        private String logo;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }

        @Override
        public String toString() {
            return "SimpleAnchor{" +
                    "name='" + name + '\'' +
                    ", logo='" + logo + '\'' +
                    '}';
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTextLiveId() {
        return textLiveId;
    }

    public void setTextLiveId(long textLiveId) {
        this.textLiveId = textLiveId;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMatchResult() {
        return matchResult;
    }

    public void setMatchResult(String matchResult) {
        this.matchResult = matchResult;
    }


    public TextLiveMessageType getType() {
        return type;
    }

    public void setType(TextLiveMessageType type) {
        this.type = type;
    }

    public TextLiveType getTextLiveType() {
        return textLiveType;
    }

    public void setTextLiveType(TextLiveType textLiveType) {
        this.textLiveType = textLiveType;
    }

    public SimpleAnchor getAnchor() {
        return anchor;
    }

    public void setAnchor(SimpleAnchor anchor) {
        this.anchor = anchor;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }



    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public long getSectionId() {
        return sectionId;
    }

    public void setSectionId(long sectionId) {
        this.sectionId = sectionId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

}
