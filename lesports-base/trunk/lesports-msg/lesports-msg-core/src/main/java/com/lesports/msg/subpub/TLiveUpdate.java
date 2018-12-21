package com.lesports.msg.subpub;

/**
 * lesports-base.
 *
 * @author pangchuanxiao
 * @since 2016/9/21
 */
public class TLiveUpdate {
    private long tliveId;
    private long liveMessageId;
    private long section;

    public TLiveUpdate() {
    }

    public TLiveUpdate(long tliveId, long liveMessageId, long section) {
        this.tliveId = tliveId;
        this.liveMessageId = liveMessageId;
        this.section = section;
    }

    public long getLiveMessageId() {
        return liveMessageId;
    }

    public void setLiveMessageId(long liveMessageId) {
        this.liveMessageId = liveMessageId;
    }

    public long getTliveId() {
        return tliveId;
    }

    public void setTliveId(long tliveId) {
        this.tliveId = tliveId;
    }

    public long getSection() {
        return section;
    }

    public void setSection(long section) {
        this.section = section;
    }
}
