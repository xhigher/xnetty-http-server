package com.cheercent.xnetty.httpserver.base;

import java.util.concurrent.atomic.LongAdder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.cheercent.xnetty.httpserver.base.XRedis.RedisKey;
import com.cheercent.xnetty.httpserver.conf.DataKey;
import com.cheercent.xnetty.httpserver.conf.ErrorCode;
import com.cheercent.xnetty.httpserver.conf.PublicConfig;
import com.cheercent.xnetty.httpserver.conf.PublicConfig.ClientType;
import com.cheercent.xnetty.httpserver.conf.PublicConfig.SessionType;
import com.cheercent.xnetty.httpserver.conf.RedisConfig;


/*
 * @copyright (c) xhigher 2015 
 * @author xhigher    2015-3-26 
 */
public abstract class XLogic implements Cloneable {

	protected static final Logger logger = LoggerFactory.getLogger(XLogic.class);
	
	public static final String RESULT_KEY_ERRCODE = "errcode";
	public static final String RESULT_KEY_ERRINFO = "errinfo";
	public static final String RESULT_KEY_DATA = "data";
	
	protected JSONObject parameters = null;
	
	protected String xPeerid = null;
	protected String xSessionid = null;
	protected String xUserid = null;
	protected String xUsername = null;

	protected String xRegtime = null;
	protected ClientType xClientType = null;
	protected SessionType xSessionType = null;
	
	private LongAdder cloneCount = new LongAdder();
	
	private XContext context = null;
	
	public String handle(JSONObject parameters){
		this.parameters = parameters;
		this.xPeerid =  this.getPeerid();
		return this.toString();
	}
	
	public boolean hasParameter(String name){
		return parameters.containsKey(name);
	}

	public String getString(String name){
		return parameters.getString(name);
	}
	
	public Integer getInteger(String name){
		return parameters.getInteger(name);
	}
	
	public Long getLong(String name){
		return parameters.getLong(name);
	}
	
	public Double getDouble(String name){
		return parameters.getDouble(name);
	}
	
	public <T extends Enum<T>> T getEnum(Class<T> enumType, String name) {
		try {
			return Enum.valueOf(enumType, parameters.getString(name));
		}catch(Exception e){}
		return null;
	}
	
	public JSONArray getJSONArray(String name){
		return parameters.getJSONArray(name);
	}
	
	public JSONObject getJSONObject(String name){
		return parameters.getJSONObject(name);
	}
	
	public String getPeerid(){
		if(xPeerid == null){
			xPeerid = this.getString(DataKey.PEERID);
			if(xPeerid!=null && !xPeerid.isEmpty()){
				xClientType = PublicConfig.getClientType(xPeerid);
				xSessionType = getSessionType(xClientType);
			}
		}
		return xPeerid;
	}

	private SessionType getSessionType(ClientType clientType){
		switch (clientType){
			case ios:
			case android:
				return SessionType.app;
			case web:
			case unknown:
			default: return SessionType.web;
		}
	}
	
	public ClientType getClientType(){
		return this.xClientType;
	}
	
	public String getClientIP(){
		return this.getString(DataKey.CLIENT_IP);
	}
	
	public String getClientVersion(){
		return this.getString(DataKey.CLIENT_VERSION);
	}
	
	public String getDeviceInfo(){
		return this.getString(DataKey.DEVICE_INFO);
	}
	
	public String outputResult(int code,String info, Object obj){
		if(context != null) {
			context.endTransaction(code == ErrorCode.OK);
		}
		if(info == null){
			info = "";
		}
		if(obj == null){
			obj = new JSONObject();
		}
		JSONObject result = new JSONObject();
		result.put(RESULT_KEY_ERRCODE, code);
		result.put(RESULT_KEY_ERRINFO, info);
		result.put(RESULT_KEY_DATA, obj);
		return JSONObject.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	public String successResult(){
		return outputResult(ErrorCode.OK, null, null);
	}
	
	public String successResult(JSONObject data){
		return outputResult(ErrorCode.OK, null, data);
	}
	
	public String successResult(JSONArray data){
		return outputResult(ErrorCode.OK, null, data);
	}
	
	public String errorResult(String info, JSONObject data){
		return outputResult(ErrorCode.NOK, info, data);
	}
	
	public String errorResult(int code, JSONObject data){
		return outputResult(code, null, data);
	}
	
	public String errorResult(int code, String info){
		return outputResult(code, info, null);
	}
	
	public String errorResult(String info){
		return outputResult(ErrorCode.NOK, info, null);
	}
	
	public String errorResult(){
		return outputResult(ErrorCode.NOK, null, null);
	}

	public String errorInternalResult(){
		return outputResult(ErrorCode.INTERNAL_ERROR, "INTERNAL_ERROR", null);
	}
	
	public static String staticOutputResult(int code,String info, Object obj){
		if(info == null){
			info = "";
		}
		if(obj == null){
			obj = new JSONObject();
		}
		JSONObject result = new JSONObject();
		result.put(RESULT_KEY_ERRCODE, code);
		result.put(RESULT_KEY_ERRINFO, info);
		result.put(RESULT_KEY_DATA, obj);
		return JSONObject.toJSONString(result, SerializerFeature.DisableCircularReferenceDetect);
	}
	
	public static String errorRequestResult(){
		return staticOutputResult(ErrorCode.REQEUST_ERROR, "REQUEST_ERROR", null);
	}
	
	public static String errorMethodResult(){
		return staticOutputResult(ErrorCode.METHOD_ERROR, "METHOD_ERROR", null);
	}
	
	public static String errorParameterResult(String info){
		return staticOutputResult(ErrorCode.PARAMETER_ERROR, info, null);
	}

	public static String errorValidationResult(){
		return staticOutputResult(ErrorCode.VALIDATION_ERROR, "VALIDATION_ERROR", null);
	}
	
	public void startTransaction(){
		if(context == null){
			context = new XContext();
		}
		context.startTransaction();
	}
	
	public XContext getContext(){
		if(context == null){
			context = new XContext();
		}
		return context;
	}
	
	public boolean submitTransaction(){
		return context.submitTransaction();
	}
	
	protected boolean requireSession(){
		return true;
	}
	
	protected boolean requireAccountBound(){
		return false;
	}
	
	protected boolean requireAuthor(){
		return false;
	}
	

	protected abstract String prepare();
	
	protected abstract String execute();
	
	protected String checkSession(){
		xSessionid = this.getString(DataKey.SESSIONID);
		if(xSessionid == null){
			return errorParameterResult("SESSIONID_NULL");
		}
		RedisKey redisKey = RedisConfig.SESSION_INFO.build().append(xSessionid);
		String redisData = XRedis.get(redisKey);
		if(redisData == null){
			return this.errorResult(ErrorCode.SESSION_INVALID, "SESSION_INVALID");
		}
		//logger.info("sessionInfo="+redisData);
		JSONObject sessionInfo = JSONObject.parseObject(redisData);
		if(!xPeerid.equals(sessionInfo.getString(DataKey.PEERID))){
			XRedis.del(redisKey);
			return this.errorResult(ErrorCode.SESSION_INVALID, "SESSION_INVALID");
		}

		xUserid = sessionInfo.getString(DataKey.USERID);
		xUsername = sessionInfo.getString(DataKey.USERNAME);
		xRegtime = sessionInfo.getString(DataKey.REGTIME);

		Long liveTime = XRedis.ttl(redisKey);
		if(liveTime == null) {
			return this.errorInternalResult();
		}
		if(liveTime > 0 && liveTime < RedisConfig.EXPIRE_DAY_1) {
			XRedis.expire(redisKey);
			XRedis.expire(RedisConfig.SESSIONID.build().append(xSessionType).append(xUsername));
		}

		return null;
	}
	
	@Override
	public String toString(){
		try{
			String prepareResult = this.prepare();
			if(prepareResult != null){
				return prepareResult;
			}

			if(this.requireSession()){
				String sessionResult = this.checkSession();
				if(sessionResult != null){
					return sessionResult;
				}
			}
			
			return this.execute();
		}catch(Exception e){
			logger.error(this.getClass().getSimpleName(), e);
			return errorInternalResult();
		}finally{

		}
	}

	public long getCloneCount() {
		return cloneCount.longValue();
	}
	
	@Override
	public XLogic clone() {
		try{
			cloneCount.increment();
			return (XLogic) super.clone();
		}catch(CloneNotSupportedException e){
		}
		return null;
	}
	
	public JSONArray getSimpleArray(JSONArray data, String field) {
		JSONArray result = null;
		if(data != null) {
			result = new JSONArray();
			for(int i=0; i<data.size(); i++) {
	        	result.add(data.getJSONObject(i).get(field));
	        }
		}
		return result;
	}
	
	public JSONObject getSimpleObject(JSONArray data, String field) {
		JSONObject result = null;
		if(data != null) {
			result = new JSONObject(true);
			JSONObject item = null;
			for(int i=0; i<data.size(); i++) {
				item = data.getJSONObject(i);
	        	result.put(item.getString(field), item);
	        }
		}
		return result;
	}

	public JSONObject getSimpleObject(JSONArray data, String field, String field2) {
		JSONObject result = null;
		if(data != null) {
			result = new JSONObject();
			JSONObject item = null;
			for(int i=0; i<data.size(); i++) {
				item = data.getJSONObject(i);
				result.put(item.getString(field), item.getString(field2));
			}
		}
		return result;
	}

}
