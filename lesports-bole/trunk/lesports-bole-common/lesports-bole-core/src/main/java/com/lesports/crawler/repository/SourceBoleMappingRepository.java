package com.lesports.crawler.repository;

import com.lesports.crawler.model.source.Source;
import com.lesports.crawler.model.source.SourceBoleMapping;
import com.lesports.crawler.model.source.SourceValueType;
import com.lesports.repository.LeCrudRepository;

/**
 * SourceBoleMapping操作接口
 * @author denghui
 *
 */
public interface SourceBoleMappingRepository extends LeCrudRepository<SourceBoleMapping, String> {

  /**
   * 根据源,源值类型,和具体的值查找Mapping
   * @param source
   * @param type
   * @param value
   * @return
   */
  SourceBoleMapping get(Source source, SourceValueType type, String value, String gameFName);

  /**
   * 根据bole id
   * @return
   */
  SourceBoleMapping get(long boleId);
}
