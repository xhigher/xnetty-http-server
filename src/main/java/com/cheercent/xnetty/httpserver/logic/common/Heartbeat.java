package com.cheercent.xnetty.httpserver.logic.common;

import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XLogicConfig;

@XLogicConfig(name = "heartbeat")
public final class Heartbeat extends XLogic {

    @Override
    protected String prepare() {

        return null;
    }

    @Override
    protected String execute() {

        return this.successResult();
    }

}
