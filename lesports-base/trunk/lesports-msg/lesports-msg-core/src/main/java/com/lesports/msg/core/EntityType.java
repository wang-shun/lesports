package com.lesports.msg.core;

/**
 * User: ellios
 * Time: 15-6-28 : 下午10:05
 */
public enum EntityType {

    MATCH,
    EPISODE;


    public static EntityType getEntityType(int type){
        for(EntityType entityType : EntityType.values()){
            if(entityType.ordinal() == type){
                return entityType;
            }
        }
        return null;
    }
}
