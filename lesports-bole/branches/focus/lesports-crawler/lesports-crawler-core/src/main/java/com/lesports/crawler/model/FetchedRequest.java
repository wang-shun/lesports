package com.lesports.crawler.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/11/18
 */
@Document(collection = "fetched_requests")
public class FetchedRequest {
    @Id
    private String id;
    private String fetchTime;
    private Boolean attachResult;
    private Boolean isDataNull;
    private Boolean isAttachHandlerNull;
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Boolean getIsDataNull() {
        return isDataNull;
    }

    public void setIsDataNull(Boolean isDataNull) {
        this.isDataNull = isDataNull;
    }

    public Boolean getIsAttachHandlerNull() {
        return isAttachHandlerNull;
    }

    public void setIsAttachHandlerNull(Boolean isAttachHandlerNull) {
        this.isAttachHandlerNull = isAttachHandlerNull;
    }

    public Boolean getAttachResult() {
        return attachResult;
    }

    public void setAttachResult(Boolean attachResult) {
        this.attachResult = attachResult;
    }

    public String getFetchTime() {

        return fetchTime;
    }

    public void setFetchTime(String fetchTime) {
        this.fetchTime = fetchTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static enum  Status {
        WAITING, FETCHING, FETCHED,
    }
}
