package com.lesports.bole.repository;

import com.lesports.bole.model.BoleOlympicSettingDataSet;
import com.lesports.bole.model.BoleOlympicsLiveConfigSet;
import com.lesports.mongo.repository.MongoCrudRepository;

import java.util.List;

/**
 * User: ellios
 * Time: 15-7-10 : 上午10:55
 */
public interface BoleOlympicSettingDataRepository extends MongoCrudRepository<BoleOlympicSettingDataSet, String> {
}
