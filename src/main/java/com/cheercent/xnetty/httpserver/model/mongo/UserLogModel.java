package com.cheercent.xnetty.httpserver.model.mongo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XMongoModel;
import com.mongodb.client.model.Filters;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;


public class UserLogModel extends FootballModel {

	public static final String collectionName = "analysis_distribution";

	@Override
	protected String collectionName(){
		return collectionName;
	}

	public JSONObject getInfo() {
		JSONObject info = null;
		Document myDoc = this.find(Filters.eq("aa", 1)).tryNext();
		if(myDoc != null){
			info = JSONObject.parseObject(myDoc.toJson());
		}
		return info;
	}

	public JSONObject getInfo(long matchid ) {
		JSONObject info = null;
		Document myDoc = this.find(Filters.eq("matchid", matchid)).tryNext();
		if(myDoc != null){
			info = JSONObject.parseObject(myDoc.toJson());
		}
		return info;
	}
}
