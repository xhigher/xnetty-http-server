package com.cheercent.xnetty.httpserver.conf;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XRedis;
import com.cheercent.xnetty.httpserver.base.XRedis.RedisKey;
import com.cheercent.xnetty.httpserver.conf.PublicConfig.CommonEvent;

public abstract class SearchConfig {

	private static final RedisKey channelSearchUser = RedisConfig.CHANNEL_SEARCH_USER.build();
	
	private static final String SEARCH_TYPE = "search_type";
	private static final String SEARCH_EVENT = "search_event";
	
	public enum SearchType {
		user,//nickname
	}

	public static void notifyUser(CommonEvent event, String userid) {
		if(userid != null && !userid.isEmpty()) {
			JSONObject message = new JSONObject();
			message.put(SEARCH_TYPE, SearchType.user.toString());
			message.put(SEARCH_EVENT, event.toString());
			message.put(DataKey.USERID, userid);
			XRedis.publish(channelSearchUser, message.toJSONString());
		}
	}
}
