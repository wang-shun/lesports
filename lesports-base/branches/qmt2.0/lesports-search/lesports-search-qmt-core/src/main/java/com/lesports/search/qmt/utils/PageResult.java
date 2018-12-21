package com.lesports.search.qmt.utils;

import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2016/4/13
 */

public class PageResult<T> implements Serializable {
    private List<T> rows;
    private Long total;

    public PageResult() {
    }

    public PageResult(List<T> data, long count) {
        this.rows = data;
        this.total = Long.valueOf(count);
    }


    public PageResult(Page<T> page) {
        if (null == page) {
            return;
        }
        this.rows = page.getContent();
        this.total = page.getTotalElements();
    }

    public List<T> getRows() {
        return this.rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public Long getTotal() {
        return this.total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
