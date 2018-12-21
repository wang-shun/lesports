package com.lesports.bole.service;

import com.lesports.bole.api.vo.TBNews;

/**
 * trunk.
 *
 * @author pangchuanxiao
 * @since 2015/12/9
 */
public interface BoleNewsService {

    
    /**
     * 通过id查找新闻
     * @param id
     * @return
     */
    TBNews getById(Long id);

}
