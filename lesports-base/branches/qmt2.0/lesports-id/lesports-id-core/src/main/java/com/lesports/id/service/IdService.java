package com.lesports.id.service;

import com.lesports.id.api.IdType;

/**
 * User: ellios
 * Time: 15-6-1 : 下午9:19
 */
public interface IdService {

    public long nextId(IdType type);
}
