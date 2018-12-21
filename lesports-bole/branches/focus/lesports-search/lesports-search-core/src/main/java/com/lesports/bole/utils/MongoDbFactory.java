package com.lesports.bole.utils;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
@Component("mongoClient")
public class MongoDbFactory implements FactoryBean<DB> {

	@Value("#{'${mongodb.replica.set}'.split(',')}")
	private String[] address;

	@Value("${mongodb.username}")
	private String username;

	@Value("${mongodb.password}")
	private String password;

	@Value("${mongodb.dbname}")
	private String dbname;

	private MongoClient mongoClient;

	@PostConstruct
	public void init() {
		List<ServerAddress> saList = new ArrayList<ServerAddress>();
		for (String addrAndPort : address) {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		List<MongoCredential> mongoCredentialList = new ArrayList<MongoCredential>();
		mongoCredentialList.add(MongoCredential.createScramSha1Credential(username, dbname, password.toCharArray()));
		MongoClientOptions options = MongoClientOptions.builder().readPreference(ReadPreference.nearest()).build();
		mongoClient = new MongoClient(saList, mongoCredentialList, options);
	}

	@Override
	public DB getObject() throws Exception {
		return mongoClient.getDB(dbname);
	}

	@Override
	public Class<?> getObjectType() {
		return DB.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
