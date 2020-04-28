package com.cheercent.xnetty.httpserver.logic.user;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XLogicConfig;
import com.cheercent.xnetty.httpserver.conf.PublicConfig.SegmentResult;
import com.cheercent.xnetty.httpserver.conf.UserConfig;

@XLogicConfig(name = "info")
public final class Info extends XLogic {

    @Override
    protected String prepare() {
        return null;
    }

    @Override
    protected String execute() {
        SegmentResult segmentResult = UserConfig.getUserInfo(this, xUserid);
        if (segmentResult.error != null) {
            return segmentResult.error;
        }
        JSONObject userInfo = segmentResult.data;

        return this.successResult(userInfo);
    }
}
