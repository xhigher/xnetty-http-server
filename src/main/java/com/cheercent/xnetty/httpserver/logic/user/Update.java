package com.cheercent.xnetty.httpserver.logic.user;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XLogicConfig;
import com.cheercent.xnetty.httpserver.base.XRedis;
import com.cheercent.xnetty.httpserver.base.XRedis.RedisKey;
import com.cheercent.xnetty.httpserver.conf.DataKey;
import com.cheercent.xnetty.httpserver.conf.PublicConfig.CommonEvent;
import com.cheercent.xnetty.httpserver.conf.PublicConfig.SegmentResult;
import com.cheercent.xnetty.httpserver.conf.RedisConfig;
import com.cheercent.xnetty.httpserver.conf.SearchConfig;
import com.cheercent.xnetty.httpserver.conf.ServiceConfig.ActionMethod;
import com.cheercent.xnetty.httpserver.conf.UserConfig;
import com.cheercent.xnetty.httpserver.conf.UserConfig.UserUpdateType;
import com.cheercent.xnetty.httpserver.model.business.UserInfoModel;
import com.cheercent.xnetty.httpserver.util.CommonUtils;

@XLogicConfig(name = "update", method = ActionMethod.POST, requiredParameters = {
        DataKey.TYPE,
        DataKey.DATA})
public final class Update extends XLogic {

    private UserUpdateType type = null;
    private String data = null;

    @Override
    protected String prepare() {
        type = this.getEnum(UserUpdateType.class, DataKey.TYPE);
        if (type == null) {
            return errorParameterResult("TYPE_ERROR");
        }
        data = this.getString(DataKey.DATA);
        if (data == null || data.isEmpty()) {
            return errorParameterResult("DATA_ERROR");
        }
        if (type == UserUpdateType.nickname){
            if (data.length() < 2 || data.length() > 12){
                return errorParameterResult("DATA_ERROR");
            }
        }
        return null;
    }

    @Override
    protected String execute() {
        int updatedCount = 0;
        RedisKey redisKey2 = RedisConfig.USER_UPDATED.build().append(xUserid + "_" + CommonUtils.getTodayYMD2());
        String redisData = XRedis.get(redisKey2);
        if (redisData != null) {
            updatedCount = Integer.parseInt(redisData);
        }
        // TODO Limit the frequency of nickname changes
        if (updatedCount > UserConfig.DAILY_UPDATE_COUNT) {
            return this.errorResult("UPDATE_FREQUENTLY");
        }
        SegmentResult segmentResult = UserConfig.getUserInfo(this, xUserid);
        if (segmentResult.error != null) {
            return segmentResult.error;
        }
        JSONObject userInfo = segmentResult.data;

        String nickname = null;
        String avatar = null;
        String profile = null;
        if (type == UserUpdateType.nickname) {
            if (userInfo.getIntValue("nickname_num") > 0) {
                return errorResult("NICKNAME_LIMIT");
            }

            nickname = data;
            if (nickname.equals(userInfo.getString("nickname"))) {
                return this.errorResult("NO_CHANGED");
            }
        }

        UserInfoModel userModel = new UserInfoModel();
        if (!userModel.updateInfo(xUserid, nickname)) {
            if (userModel.isErrorDuplicateEntry()){
                return this.errorResult("NICKNAME_DUPLICATE");
            }
            return this.errorResult("UPDATE_ERROR");
        }

        updatedCount++;
        XRedis.set(redisKey2, String.valueOf(updatedCount));
        XRedis.del(RedisConfig.USER_INFO.build().append(xUserid));

        SearchConfig.notifyUser(CommonEvent.update, xUserid);

        return this.successResult();
    }
}
