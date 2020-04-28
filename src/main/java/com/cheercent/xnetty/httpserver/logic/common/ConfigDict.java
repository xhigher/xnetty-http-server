package com.cheercent.xnetty.httpserver.logic.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XLogicConfig;
import com.cheercent.xnetty.httpserver.base.XRedis;
import com.cheercent.xnetty.httpserver.base.XRedis.RedisKey;
import com.cheercent.xnetty.httpserver.conf.DataKey;
import com.cheercent.xnetty.httpserver.conf.RedisConfig;
import com.cheercent.xnetty.httpserver.model.business.ConfigItemModel;
import com.cheercent.xnetty.httpserver.util.CommonUtils;

@XLogicConfig(name = "config_dict", requiredParameters = {DataKey.CHECKSUM})
public class ConfigDict extends XLogic {

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
        RedisKey redisKey1 = RedisConfig.CONFIG_DICT_CHECKSUM.build();
        RedisKey redisKey2 = RedisConfig.CONFIG_DICT.build();
        if (!checksum.isEmpty()) {
            checksum2 = XRedis.get(redisKey1);
            if (checksum2 != null && checksum2.equals(checksum)) {
                return this.successResult();
            }
        }
        JSONObject configDict = null;
        String redisData = XRedis.get(redisKey2);
        if (redisData == null) {
            ConfigItemModel itemModel = new ConfigItemModel();
            JSONArray itemData = itemModel.getList();
            if (itemData == null) {
                return this.errorResult();
            }
            String cfgid = null;
            JSONObject tempData = null;
            configDict = new JSONObject();
            for (int i = 0, n = itemData.size(); i < n; i++) {
                tempData = itemData.getJSONObject(i);
                cfgid = String.valueOf(tempData.remove("cfgid"));
                if (!configDict.containsKey(cfgid)) {
                    configDict.put(cfgid, new JSONObject());
                }
                configDict.getJSONObject(cfgid).put(tempData.getString("itemid"), tempData.getString("name"));
            }
            redisData = configDict.toJSONString();
            checksum2 = CommonUtils.md5(redisData);
            XRedis.set(redisKey2, redisData);
        } else {
            configDict = JSONObject.parseObject(redisData);
            checksum2 = CommonUtils.md5(redisData);
        }
        XRedis.set(redisKey1, checksum2);

        JSONObject resultData = new JSONObject();
        resultData.put(DataKey.CHECKSUM, checksum2);
        resultData.put(DataKey.DATA, configDict);
        return this.successResult(resultData);
    }
}
