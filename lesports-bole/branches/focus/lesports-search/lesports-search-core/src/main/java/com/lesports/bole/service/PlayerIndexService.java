package com.lesports.bole.service;

import com.lesports.bole.index.PlayerIndex;
import com.lesports.bole.utils.PageResult;

/**
 * Created by yangyu on 16/8/3.
 */
public interface PlayerIndexService extends BoleSearchCrudService<PlayerIndex, Long>{
	PageResult<PlayerIndex> findByParams(long id, String name, String englishName, Long cid, Long gameFType, Integer countryCode, int page, int count);
}
