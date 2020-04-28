package com.cheercent.xnetty.httpserver.model.business;

import com.cheercent.xnetty.httpserver.base.XModel;


public abstract class ConfigDatabase extends XModel {

	public static final String dataSourceName = "config";
	
	@Override
	protected String getDataSourceName() {
		return dataSourceName;
	}
	
}
