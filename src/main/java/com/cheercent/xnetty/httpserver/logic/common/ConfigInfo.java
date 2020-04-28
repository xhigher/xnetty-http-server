package com.cheercent.xnetty.httpserver.logic.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XLogicConfig;
import com.cheercent.xnetty.httpserver.base.XRedis;
import com.cheercent.xnetty.httpserver.base.XRedis.RedisKey;
import com.cheercent.xnetty.httpserver.conf.DataKey;
import com.cheercent.xnetty.httpserver.conf.RedisConfig;
import com.cheercent.xnetty.httpserver.model.business.ConfigInfoModel;
import com.cheercent.xnetty.httpserver.model.business.ConfigItemModel;
import com.cheercent.xnetty.httpserver.util.CommonUtils;

@XLogicConfig(name = "config_info", requiredParameters = {DataKey.CHECKSUM})
public final class ConfigInfo extends XLogic {

    @Override
    protected boolean requireSession() {
        return false;
    }

    private String checksum;

    @Override
    protected String prepare() {
        checksum = this.getString(DataKey.CHECKSUM);
        return null;
    }

    @Override
    protected String execute() {
        String checksum2 = null;
        RedisKey redisKey1 = RedisConfig.CONFIG_INFO_CHECKSUM.build();
        RedisKey redisKey2 = RedisConfig.CONFIG_INFO.build();
        if (!checksum.isEmpty()) {
            checksum2 = XRedis.get(redisKey1);
            if (checksum2 != null && checksum2.equals(checksum)) {
                return this.successResult();
            }
        }
        JSONObject configData = null;
        String redisData = XRedis.get(redisKey2);
        if (redisData == null) {
            ConfigInfoModel model = new ConfigInfoModel();
            JSONArray baseData = model.getList();
            if (baseData == null) {
                return this.errorResult();
            }
            JSONObject tempData = null;
            configData = new JSONObject();
            for (int i = 0, n = baseData.size(); i < n; i++) {
                tempData = baseData.getJSONObject(i);
                tempData.put("items", new JSONArray());
                configData.put(tempData.getString("cfgid"), tempData);
            }

            ConfigItemModel itemModel = new ConfigItemModel();
            JSONArray itemData = itemModel.getList();
            if (itemData == null) {
                return this.errorResult();
            }
            String cfgid = null;
            for (int i = 0, n = itemData.size(); i < n; i++) {
                tempData = itemData.getJSONObject(i);
                cfgid = String.valueOf(tempData.remove("cfgid"));
                if (configData.containsKey(cfgid)) {
                    configData.getJSONObject(cfgid).getJSONArray("items").add(tempData);
                }
            }
            redisData = configData.toJSONString();
            checksum2 = CommonUtils.md5(redisData);
            XRedis.set(redisKey2, redisData);
        } else {
            configData = JSONObject.parseObject(redisData);
            checksum2 = CommonUtils.md5(redisData);
        }
        XRedis.set(redisKey1, checksum2);

        JSONObject resultData = new JSONObject();
        resultData.put(DataKey.CHECKSUM, checksum2);
        resultData.put(DataKey.DATA, configData);
        return this.successResult(resultData);
    }
}
