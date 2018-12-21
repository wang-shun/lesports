package com.lesports.websocket.domain.tlive;

import java.io.Serializable;

/**
 * Created by zhangdeqiang on 2016/9/18.
 */
public class PageInfoRequest implements Serializable {
    private static final long serialVersionUID = 382439821695420653L;

    private long eid; //room
    private long tliveId;
    private long section;
    private int page;

    public long getEid() {
        return eid;
    }

    public void setEid(long eid) {
        this.eid = eid;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
