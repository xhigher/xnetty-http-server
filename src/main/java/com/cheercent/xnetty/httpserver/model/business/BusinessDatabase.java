package com.cheercent.xnetty.httpserver.model.business;

import com.cheercent.xnetty.httpserver.base.XModel;


public abstract class BusinessDatabase extends XModel {

	public static final String dataSourceName = "business";
	
	@Override
	protected String getDataSourceName() {
		return dataSourceName;
	}
	
}
