package com.cheercent.xnetty.httpserver.logic.user;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XLogicConfig;
import com.cheercent.xnetty.httpserver.conf.DataKey;
import com.cheercent.xnetty.httpserver.conf.ErrorCode;
import com.cheercent.xnetty.httpserver.conf.PublicConfig.SegmentResult;
import com.cheercent.xnetty.httpserver.conf.UserConfig;
import com.cheercent.xnetty.httpserver.conf.UserConfig.UserStatus;

@XLogicConfig(name = "pubinfo", requiredParameters = {
        DataKey.USERID
})
public final class Pubinfo extends XLogic {

    @Override
    protected boolean requireSession() {
        return false;
    }

    private String userid = null;

    @Override
    protected String prepare() {
        userid = this.getString(DataKey.USERID);
        if (userid.length() != 12) {
            return errorParameterResult("USERID_ERROR");
        }
        return null;
    }

    @Override
    protected String execute() {
        SegmentResult segmentResult = UserConfig.getUserInfo(this, userid);
        if (segmentResult.error != null) {
            return segmentResult.error;
        }
        JSONObject userInfo = segmentResult.data;

        if (UserStatus.activated != userInfo.getIntValue(DataKey.STATUS)) {
            return this.errorResult(ErrorCode.ACCOUNT_BLOCKED, "ACCOUNT_BLOCKED");
        }

        JSONObject userPubInfo = UserConfig.getPubInfo(userInfo);

        return this.successResult(userPubInfo);
    }
}
