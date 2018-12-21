package com.lesports.websocket.domain.tlive;

import java.io.Serializable;

/**
 * Created by zhangdeqiang on 2016/9/18.
 */
public class TeamLikeRequest implements Serializable {
    private static final long serialVersionUID = 382439821695420653L;

    private long eid;
    private long tliveId;
    private long anchorId;
    private int act;

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

    public long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(long anchorId) {
        this.anchorId = anchorId;
    }

    public int getAct() {
        return act;
    }

    public void setAct(int act) {
        this.act = act;
    }
}
