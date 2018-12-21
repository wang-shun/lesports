package com.lesports.id.repository;


import com.lesports.id.api.IdType;

/**
 * User: ellios
 * Time: 15-6-1 : 下午9:13
 */
public interface IdRepository {

    long nextId(IdType type);

    /**
     * 按一定的步长获取下一个id
     * @param type
     * @param step
     * @return
     */
    long nextId(IdType type, int step);
}
