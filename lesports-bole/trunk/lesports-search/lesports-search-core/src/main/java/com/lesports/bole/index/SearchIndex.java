package com.lesports.bole.index;

import org.springframework.data.annotation.Id;

/**
 * aa.
 *
 * @author pangchuanxiao
 * @since 2015/10/28
 */
public abstract class SearchIndex<T> implements Init {
    String updateAt;
    @Id
    T id;
    //是否删除
    private Boolean deleted;

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public T getId() {
        return id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

}
