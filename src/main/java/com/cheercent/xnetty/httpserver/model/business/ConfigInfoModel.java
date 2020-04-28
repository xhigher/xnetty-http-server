package com.cheercent.xnetty.httpserver.model.business;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ConfigInfoModel extends ConfigDatabase {

	@Override
	protected String tableName() {
		return "config_info";
	}
	
	public boolean addInfo(String cfgid, String name,String fullname){
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("cfgid", cfgid);
		values.put("name", name);
		values.put("fullname", fullname);
		values.put("upcfgid", "");
		return this.prepare().set(values).insert();
	}
	
	public boolean addInfo(String cfgid, String name,String fullname, String upcfgid){
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("cfgid", cfgid);
		values.put("name", name);
		values.put("fullname", fullname);
		values.put("upcfgid", upcfgid);
		return this.prepare().set(values).insert();
	}
	
	public boolean saveInfo(String cfgid, String name,String fullname){
		return this.prepare().set("name", name).set("fullname", fullname).addWhere("cfgid", cfgid).update();
	}
	
	public JSONArray getList(){
		return this.prepare().field("cfgid,name").select();
	}
	
	public JSONObject getInfo(String cfgid){
		return this.prepare().field("cfgid,name,fullname,upcfgid").addWhere("cfgid", cfgid).find();
	}
	
	public JSONObject getPageList(int pagenum, int pagesize){
		return this.prepare().order("cfgid", false).page(pagenum, pagesize);
	}
	
	public boolean deleteInfo(String cfgid){
		return this.prepare().addWhere("cfgid", cfgid).delete();
	}
}
