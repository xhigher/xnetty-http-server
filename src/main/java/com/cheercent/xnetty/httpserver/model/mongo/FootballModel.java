package com.cheercent.xnetty.httpserver.model.mongo;

import com.cheercent.xnetty.httpserver.base.XMongoModel;


public abstract class FootballModel extends XMongoModel {

	public static final String databaseName = "user";

	@Override
	protected String databaseName() {
		return databaseName;
	}

//	public abstract String databaseName();
//	@Override
//	protected String getDataSourceName() {
//		return dataSourceName;
//	}
	
}
