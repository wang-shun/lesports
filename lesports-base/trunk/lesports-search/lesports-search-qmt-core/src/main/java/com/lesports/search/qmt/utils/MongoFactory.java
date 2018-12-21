package com.lesports.search.qmt.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.annotation.Persistent;
import org.springframework.data.convert.SimpleTypeInformationMapper;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.util.ClassUtils;

import com.lesports.converter.CompoundTagReadConverter;
import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;

/**
 * MongoClient factory
 * 
 * @author yueyue.sy
 *
 */
public class MongoFactory {

	private static final Logger LOG = LoggerFactory.getLogger(MongoFactory.class);

	private String[] replicaset;

	private String username;

	private String password;

	private MongoClient getMongoClient(String dbName) {
		List<ServerAddress> saList = new ArrayList<ServerAddress>();
		for (String addrAndPort : replicaset) {
			String[] ap = addrAndPort.split(":");
			ServerAddress sa = null;
			try {
				if (ap.length == 1) {
					sa = new ServerAddress(ap[0]);
				} else if (ap.length == 2) {
					sa = new ServerAddress(ap[0], Integer.valueOf(ap[1]));
				} else {
					continue;
				}
				saList.add(sa);
			} catch (Exception e) {
				LOG.error("Connecting mongodb failed", e);
			}
		}
		MongoClient mongoClient = null;
		if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
			List<MongoCredential> mongoCredentialList = new ArrayList<MongoCredential>();
			mongoCredentialList
					.add(MongoCredential.createScramSha1Credential(username, dbName, password.toCharArray()));
			MongoClientOptions options = MongoClientOptions.builder().readPreference(ReadPreference.nearest()).build();
			mongoClient = new MongoClient(saList, mongoCredentialList, options);
		} else {
			mongoClient = new MongoClient(saList);
		}
		return mongoClient;
	}

	public DB getMongoDatabase(String dbName) {
		return getMongoClient(dbName).getDB(dbName);
	}

	public MongoTemplate getMongoTemplate(String dbName) {
		MongoClient mongoClient = getMongoClient(dbName);
		SimpleMongoDbFactory factory = new SimpleMongoDbFactory(mongoClient, dbName);
		MongoMappingContext context = new MongoMappingContext();
		context.setInitialEntitySet(getInitialEntitySet());
		MappingMongoConverter converter = new MappingMongoConverter(new DefaultDbRefResolver(factory), context);
		converter.setCustomConversions(new CustomConversions(Arrays.asList(new CompoundTagReadConverter())));
		DefaultMongoTypeMapper tm = new DefaultMongoTypeMapper(DefaultMongoTypeMapper.DEFAULT_TYPE_KEY,
				Arrays.asList(new SimpleTypeInformationMapper()));
		converter.setTypeMapper(tm);
		converter.afterPropertiesSet();
		return new MongoTemplate(factory, converter);
	}

	protected Set<Class<?>> getInitialEntitySet() {
		String[] basePackages = new String[] { "com.lesports" };
		try {
			Set<Class<?>> initialEntitySet = new HashSet<Class<?>>();
			for (String basePackage : basePackages) {
				if (StringUtils.isNotEmpty(basePackage)) {
					ClassPathScanningCandidateComponentProvider componentProvider = new ClassPathScanningCandidateComponentProvider(
							false);
					componentProvider.addIncludeFilter(new AnnotationTypeFilter(Document.class));
					componentProvider.addIncludeFilter(new AnnotationTypeFilter(Persistent.class));

					for (BeanDefinition candidate : componentProvider.findCandidateComponents(basePackage)) {
						initialEntitySet.add(
								ClassUtils.forName(candidate.getBeanClassName(), this.getClass().getClassLoader()));
					}
				}
			}
			return initialEntitySet;
		} catch (Throwable e) {
			LOG.error("Scan mongo entities failed, base packages : []", basePackages, e);
		}
		return null;
	}

	public void setReplicaset(String[] replicaset) {
		this.replicaset = replicaset;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}