package com.cheercent.xnetty.httpserver.conf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XRedis;
import com.cheercent.xnetty.httpserver.base.XRedis.RedisKey;
import com.cheercent.xnetty.httpserver.conf.PublicConfig.SegmentResult;
import com.cheercent.xnetty.httpserver.model.business.UserInfoModel;
import com.cheercent.xnetty.httpserver.util.CommonUtils;

public abstract class UserConfig {
	
	public static final int DAILY_UPDATE_COUNT = 3;
	
	public static final int DAILY_PASSWORD_TRYS_MAX = 10;
	
    public interface UserStatus {
        int activated = 1;
        int blocked = 0;
	}
    
    public enum UserUpdateType {
        nickname
	}
    
	public static boolean checkPhoneNo(String phoneno){
		Pattern pattern = Pattern.compile("^(1[0-9]{10})$");
		Matcher matcher = pattern.matcher(phoneno);
		return matcher.matches();
	}
    
	public static String createSessionid(){
		return Long.toString(System.currentTimeMillis(), 36) + CommonUtils.randomString(8, true);
	}

	public static String createPassword(String password, String regtime){
		return CommonUtils.randomString(6, true) +
				CommonUtils.md5(CommonUtils.md5(password.toLowerCase())+regtime)+
				CommonUtils.randomString(2, true);
	}

	public static boolean checkPassword(JSONObject userInfo, String password){
		return userInfo.getString(DataKey.PASSWORD).substring(6, 38)
				.equals(CommonUtils.md5(CommonUtils.md5(password.toLowerCase()) + userInfo.getString(DataKey.REGTIME)));
	}
	
	public static JSONObject getPubInfo(JSONObject userInfo){
		JSONObject pubInfo = new JSONObject();
		pubInfo.put(DataKey.USERID, userInfo.getString(DataKey.USERID));
		pubInfo.put(DataKey.NICKNAME, userInfo.getString(DataKey.NICKNAME));
		pubInfo.put(DataKey.AVATAR, userInfo.getString(DataKey.AVATAR));
		pubInfo.put(DataKey.LEVEL, userInfo.getIntValue(DataKey.LEVEL));
		return pubInfo;
	}

	public static SegmentResult getUserInfo(XLogic logic, String userid) {
		JSONObject userInfo = null;
		RedisKey redisKey = RedisConfig.USER_INFO.build().append(userid);
		String redisData = XRedis.get(redisKey);
		if(redisData == null){
			UserInfoModel userModel = new UserInfoModel();
			userInfo = userModel.getInfo(userid);
			if(userInfo == null){
				return new SegmentResult(logic.errorInternalResult());
			}
			if(userInfo.isEmpty()){
				return new SegmentResult(logic.errorResult(ErrorCode.INFO_NULL, "USER_NULL"));
			}
			XRedis.set(redisKey, userInfo.toJSONString());
		}else{
			userInfo = JSONObject.parseObject(redisData);
		}
		return new SegmentResult(userInfo);
	}

}
