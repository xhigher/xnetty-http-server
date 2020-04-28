package com.cheercent.xnetty.httpserver.model.business;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ConfigItemModel extends ConfigDatabase {

	@Override
	protected String tableName() {
		return "config_item";
	}
	
	public boolean addInfo(String cfgid, String itemid, String name, String fullname){
		Map<String, Object> values = new HashMap<String, Object>();
		values.put("cfgid_itemid", cfgid+"_"+itemid);
		values.put("itemid", itemid);
		values.put("name", name);
		values.put("fullname", fullname);
		values.put("cfgid", cfgid);
		values.put("orderno", System.currentTimeMillis());
		return this.prepare().set(values).insert();
	}
	
	public JSONArray getList(){
		return this.prepare().field("cfgid,itemid,name").order("orderno", true).select();
	}
	
	public JSONArray getList(String cfgid){
		return this.prepare().field("cfgid,itemid,name,fullname").addWhere("cfgid", cfgid).order("orderno", true).select();
	}
	
	public JSONObject getInfo(String cfgid, String itemid){
		return this.prepare().field("cfgid_itemid,itemid,name,fullname,cfgid").addWhere("cfgid_itemid", cfgid+"_"+itemid).find();
	}
	
	public JSONObject getPageList(String cfgid, int pagenum, int pagesize){
		return this.prepare().addWhere("cfgid", cfgid).order("orderno", true).page(pagenum, pagesize);
	}
	
	public boolean setTop(String cfgid, String itemid){
		return this.prepare().set("orderno", System.currentTimeMillis()).addWhere("cfgid_itemid", cfgid+"_"+itemid).update();
	}
	
	
	public boolean deleteInfo(String cfgid){
		return this.prepare().addWhere("cfgid_itemid", cfgid).delete();
	}
	
	public boolean deleteInfo(String cfgid, String itemid){
		return this.prepare().addWhere("cfgid_itemid", cfgid+"_"+itemid).delete();
	}
}
