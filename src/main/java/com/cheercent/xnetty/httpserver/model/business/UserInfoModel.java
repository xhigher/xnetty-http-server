package com.cheercent.xnetty.httpserver.model.business;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.util.CommonUtils;


public class UserInfoModel extends BusinessDatabase {

	@Override
	protected String tableName() {
		return "user_info";
	}

	public JSONObject getInfoByUsername(String username){
		return this.prepare().addWhere("username",username).find();
	}
	
	public boolean existNickname(String userid, String nickname){
		return this.prepare().addWhere("nickname", nickname).addWhere("userid", userid, WhereType.NEQ).count() > 0;
	}

	public boolean existUsername(String username){
		return this.prepare().addWhere("username", username).count() > 0;
	}

	public JSONObject getInfo(String userid){
		return this.prepare().addWhere("userid",userid).find();
	}
	
	public boolean updateInfo(String userid, String nickname){
		Map<String, Object> values = new HashMap<String, Object>();
		if(nickname != null) {
			values.put("nickname", nickname);
			values.put("nickname_num", 1);
		}
		values.put("updatetime", CommonUtils.getCurrentYMDHMS());

		this.prepare();
		if(nickname != null){
			this.addWhere("nickname_num", 0);
		}
		return this.set(values).addWhere("userid", userid).update();
	}


}
