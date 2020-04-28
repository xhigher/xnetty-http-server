package com.cheercent.xnetty.httpserver.logic.common;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XLogicConfig;
import com.cheercent.xnetty.httpserver.base.XRedis;
import com.cheercent.xnetty.httpserver.base.XRedis.RedisKey;
import com.cheercent.xnetty.httpserver.conf.DataKey;
import com.cheercent.xnetty.httpserver.conf.ErrorCode;
import com.cheercent.xnetty.httpserver.conf.RedisConfig;
import com.cheercent.xnetty.httpserver.conf.ServiceConfig.ActionMethod;
import com.cheercent.xnetty.httpserver.conf.UserConfig;
import com.cheercent.xnetty.httpserver.model.business.UserInfoModel;

@XLogicConfig(name = "login", method = ActionMethod.POST, requiredParameters = {
	DataKey.USERNAME,
	DataKey.PASSWORD
})
public class Login extends XLogic {


    private String username;
    private String password;


    @Override
    protected boolean requireSession() {
        return false;
    }

    @Override
    protected String prepare() {
    	xUsername = this.getString(DataKey.USERNAME);
        if (!UserConfig.checkPhoneNo(xUsername)) {
            return errorParameterResult("USERNAME_ERROR");
        }
        password = this.getString(DataKey.PASSWORD);
        if (password.length() != 32) {
            return errorParameterResult("PASSWORD_ERROR");
        }
        return null;
    }

    @Override
    protected String execute() {
        int passwordTrys = 0;
        RedisKey passwordTrysKey = RedisConfig.PASSWORD_COUNT.build().append(xUsername);
        String redisData = XRedis.get(passwordTrysKey);
        if (redisData != null) {
            passwordTrys = Integer.parseInt(redisData);
            if (passwordTrys > UserConfig.DAILY_PASSWORD_TRYS_MAX) {
                return errorResult("PASSWORD_LIMIT");
            }
        }

        JSONObject userInfo = null;
        RedisKey redisKey = RedisConfig.USERID.build().append(xUsername);
        xUserid = XRedis.get(redisKey);
        if (xUserid != null) {
            redisData = XRedis.get(RedisConfig.USER_INFO.build().append(xUserid));
            if (redisData != null) {
                userInfo = JSONObject.parseObject(redisData);
            }
        }
        if (userInfo == null) {
        	UserInfoModel userModel = new UserInfoModel();
        	userInfo = userModel.getInfoByUsername(xUsername);
            if (userInfo == null) {
                return errorInternalResult();
            }
            if (userInfo.isEmpty()) {
                return errorResult(ErrorCode.ACCOUNT_NULL, "ACCOUNT_NULL");
            }
            if (xUserid == null) {
                xUserid = userInfo.getString(DataKey.USERID);
                XRedis.set(RedisConfig.USERID.build().append(xUsername), xUserid);
            }
            XRedis.set(RedisConfig.USER_INFO.build().append(xUserid), userInfo.toJSONString());
        }

        if (userInfo.getIntValue(DataKey.STATUS) == UserConfig.UserStatus.blocked) {
            return this.errorResult(ErrorCode.ACCOUNT_BLOCKED, "ACCOUNT_BLOCKED");
        }

        if (!UserConfig.checkPassword(userInfo, password)) {
        	passwordTrys ++;
            XRedis.set(passwordTrysKey, String.valueOf(passwordTrys));
            return this.errorResult(ErrorCode.PASSWORD_ERROR, "PASSWORD_ERROR");
        }
        passwordTrys = 0;
        XRedis.set(passwordTrysKey, String.valueOf(passwordTrys));

        JSONObject sessionInfo = new JSONObject();
        sessionInfo.put(DataKey.USERNAME, xUsername);
        sessionInfo.put(DataKey.USERID, xUserid);
        sessionInfo.put(DataKey.PEERID, xPeerid);
        sessionInfo.put(DataKey.REGTIME, userInfo.getString(DataKey.REGTIME));

        XRedis.set(RedisConfig.USERID.build().append(xUsername), xUserid);

        redisKey = RedisConfig.SESSIONID.build().append(xSessionType).append(xUsername);
        String oldSessionid = XRedis.get(redisKey);
        if (oldSessionid != null) {
            XRedis.del(RedisConfig.SESSION_INFO.build().append(oldSessionid));
        }

        xSessionid = UserConfig.createSessionid();
        XRedis.set(redisKey, xSessionid);
        XRedis.set(RedisConfig.SESSION_INFO.build().append(xSessionid), sessionInfo.toJSONString());

        JSONObject resultJSON = new JSONObject();
        resultJSON.put(DataKey.SESSIONID, xSessionid);
        resultJSON.put(DataKey.USERNAME, xUsername);

        return this.successResult(resultJSON);
    }
}
