package com.cheercent.xnetty.httpserver.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class XMongo {

	private static Logger logger = LoggerFactory.getLogger(XMongo.class);

	private static MongoClient mongoClient = null;
	
	public static void init(Properties properties) {
		if(properties.containsKey("mongodb.status") && 1==Integer.valueOf(properties.getProperty("mongodb.status").trim())) {
			Builder builder = new MongoClientOptions.Builder();
			builder.connectionsPerHost(Integer.valueOf(properties.getProperty("mongodb.connectionsPerHost").trim()));//每个地址最大请求数  
	        builder.threadsAllowedToBlockForConnectionMultiplier(Integer.valueOf(properties.getProperty("mongodb.threadsAllowedToBlockForConnectionMultiplier").trim()));
	        builder.connectTimeout(Integer.valueOf(properties.getProperty("mongodb.connectTimeout").trim()));//设置连接超时时间
	        builder.maxWaitTime(Integer.valueOf(properties.getProperty("mongodb.maxWaitTime").trim()));   //设置最大等待时间   
	        builder.socketTimeout(Integer.valueOf(properties.getProperty("mongodb.socketTimeout").trim()));//读取数据的超时时间
	        
	        List<ServerAddress> hosts = new ArrayList<ServerAddress>();
	        int serverSize = Integer.valueOf(properties.getProperty("mongodb.server.size").trim());
	        for (int i = 1; i <= serverSize; i++) {
		        hosts.add(new ServerAddress(properties.getProperty("mongodb.server" + i + ".host").trim(),
		        		Integer.valueOf(properties.getProperty("mongodb.server" + i + ".port").trim())));
	        }
		 
	        MongoCredential credential = MongoCredential.createScramSha1Credential(
	        		properties.getProperty("mongodb.username").trim(),
	        		properties.getProperty("mongodb.database").trim(), 
	        		properties.getProperty("mongodb.password").trim().toCharArray());
	        
			mongoClient = new MongoClient(hosts, credential, builder.build());
		}
	}
	
	public static MongoDatabase getDatabase(String dbName) {
		try {
			if (dbName != null && !dbName.isEmpty()) {
				return mongoClient.getDatabase(dbName);
			}
		} catch (Exception e) {
			logger.error("XMongo.getDatabase.Exception:", e);
		}
		return null;
	}

	public static void close() {
		if(mongoClient != null) {
			mongoClient.close();
		}
	}

}
