package com.lesports.websocket.domain;


import java.io.Serializable;

/**
 * Created by lufei1 on 2015/9/20.
 */
public class TextLiveIndexVo implements Serializable {
    private static final long serialVersionUID = 6688586345985429350L;

    private int index;

    public TextLiveIndexVo(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
