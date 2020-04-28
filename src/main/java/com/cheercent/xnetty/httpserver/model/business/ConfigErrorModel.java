package com.cheercent.xnetty.httpserver.model.business;

import com.alibaba.fastjson.JSONArray;

public class ConfigErrorModel extends ConfigDatabase {

	@Override
	protected String tableName() {
		return "config_error";
	}

	public JSONArray getList(String project){
		return this.prepare().addWhere("project", project).select();
	}
}
