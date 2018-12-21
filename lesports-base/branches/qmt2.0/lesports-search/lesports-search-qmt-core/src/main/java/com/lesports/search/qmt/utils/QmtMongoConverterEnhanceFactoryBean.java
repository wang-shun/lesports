package com.lesports.search.qmt.utils;

import java.util.Arrays;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.convert.SimpleTypeInformationMapper;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoTypeMapper;

/**
 * Author: ellios Date: 12-12-27 Time: 下午4:37
 */
public class QmtMongoConverterEnhanceFactoryBean implements FactoryBean<MappingMongoConverter> {

	private MappingMongoConverter converter;

	public void setConverter(MappingMongoConverter converter) {
		this.converter = converter;
	}

	public MappingMongoConverter getObject() throws Exception {
		MongoTypeMapper typeMapper = new DefaultMongoTypeMapper(null, Arrays.asList(new SimpleTypeInformationMapper()));
		converter.setTypeMapper(typeMapper);
		return converter;
	}

	public Class<?> getObjectType() {
		return MappingMongoConverter.class;
	}

	public boolean isSingleton() {
		return true;
	}

}
