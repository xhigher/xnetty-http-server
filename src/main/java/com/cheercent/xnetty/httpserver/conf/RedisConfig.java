package com.cheercent.xnetty.httpserver.conf;

import com.cheercent.xnetty.httpserver.base.XRedis.RedisKeyBuilder;

public interface RedisConfig {
	
	int EXPIRE_DAY_30 = 2592000;
	int EXPIRE_DAY_7 = 604800;
	int EXPIRE_DAY_1 = 86400;
	int EXPIRE_DAY_3 = 259200;
	int EXPIRE_MIN_1 = 60;
	int EXPIRE_MIN_2 = 120;
	int EXPIRE_MIN_5 = 300;
	int EXPIRE_MIN_10 = 600;
	int EXPIRE_MIN_30 = 1800;
	int EXPIRE_HOUR_1 = 3600;
	int EXPIRE_HOUR_6 = 21800;
	int EXPIRE_SEC_10 = 10;
	int EXPIRE_SEC_20 = 20;
	int EXPIRE_SEC_30 = 30;
	
	String NODE_BUSINESS = "business";
	String NODE_CONFIG = "config";

	RedisKeyBuilder CONFIG_INFO = new RedisKeyBuilder(NODE_CONFIG, "config_info", 0);
	RedisKeyBuilder CONFIG_DICT = new RedisKeyBuilder(NODE_CONFIG, "config_dict", 0);
	RedisKeyBuilder CONFIG_INFO_CHECKSUM = new RedisKeyBuilder(NODE_CONFIG, "config_info_checksum", 0);
	RedisKeyBuilder CONFIG_DICT_CHECKSUM = new RedisKeyBuilder(NODE_CONFIG, "config_dict_checksum", 0);
	
	RedisKeyBuilder CONFIG_ERROR = new RedisKeyBuilder(NODE_CONFIG, "config_error", 0);
	RedisKeyBuilder CONFIG_ERROR_CHECKSUM = new RedisKeyBuilder(NODE_CONFIG, "config_error_checksum", 0);
	
	
	RedisKeyBuilder CHECKCODE = new RedisKeyBuilder(NODE_BUSINESS, "captcha_checkcode", EXPIRE_MIN_5);
	RedisKeyBuilder MSGCODE_TOKEN = new RedisKeyBuilder(NODE_BUSINESS, "msgcode_token", EXPIRE_MIN_10);
	RedisKeyBuilder MSGCODE = new RedisKeyBuilder(NODE_BUSINESS, "msgcode", EXPIRE_MIN_5);
	RedisKeyBuilder MSGCODE_INTERVAL = new RedisKeyBuilder(NODE_BUSINESS, "msgcode_interval", EXPIRE_MIN_1);
	RedisKeyBuilder MSGCODE_TRYS = new RedisKeyBuilder(NODE_BUSINESS, "msgcode_trys", EXPIRE_MIN_2);
	RedisKeyBuilder MSGCODE_COUNT = new RedisKeyBuilder(NODE_BUSINESS, "msgcode_count", EXPIRE_DAY_1);
	
	RedisKeyBuilder USERID = new RedisKeyBuilder(NODE_BUSINESS, "userid", EXPIRE_DAY_3);
	RedisKeyBuilder LOGIN_LOCKED = new RedisKeyBuilder(NODE_BUSINESS, "login_locked", EXPIRE_DAY_30);
	
	RedisKeyBuilder SESSIONID = new RedisKeyBuilder(NODE_BUSINESS, "sessionid", EXPIRE_DAY_30);
	RedisKeyBuilder SESSION_INFO = new RedisKeyBuilder(NODE_BUSINESS, "session_info", EXPIRE_DAY_30);
	RedisKeyBuilder USER_INFO = new RedisKeyBuilder(NODE_BUSINESS, "user_info", EXPIRE_DAY_30);
	
	RedisKeyBuilder PASSWORD_COUNT = new RedisKeyBuilder(NODE_BUSINESS, "password_count", EXPIRE_DAY_1);
	RedisKeyBuilder USER_UPDATED = new RedisKeyBuilder(NODE_BUSINESS, "user_updated", EXPIRE_DAY_1);
	
	RedisKeyBuilder SEARCH_TYPE_RESULT = new RedisKeyBuilder(NODE_BUSINESS, "search_type_result", EXPIRE_HOUR_1);

	/************************************ publish message channel ************************************/
	RedisKeyBuilder CHANNEL_SEARCH_USER = new RedisKeyBuilder(NODE_BUSINESS,  "channel_search_user", 0);
}
