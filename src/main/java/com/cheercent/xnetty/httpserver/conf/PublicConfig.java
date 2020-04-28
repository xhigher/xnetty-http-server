package com.cheercent.xnetty.httpserver.conf;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.util.CommonUtils;

public abstract class PublicConfig {
	
	public static final String HEADER_PEERID = "X-QS-PEERID";
	public static final String HEADER_SESSIONID = "X-QS-SESSIONID";
	public static final String HEADER_DEVICE = "X-QS-DEVICE";
	public static final String HEADER_VERSION = "X-QS-VERSION";
	
	public static final String HEADER_REAL_IP = "X-REAL-IP";
	
	public static final int CHECKCODE_LENGTH = 4;
	
	public static final int MSGCODE_LENGTH = 4;
	
	public static final int PAGESIZE_MAX = 50;
	
	public static final int DEFAULT_PAGESIZE = 20;

	public interface CommonStatus {
		int editing = 0;
		int online  = 1;
		int offline = 2;
	}

	public enum CommonEvent {
		add, 
		delete,
		update,
	}
	
	public interface YesNo {
		int NO = 0;
		int YES  = 1;
	}
	
	public enum ClientType {
		unknown(0),
		ios(1),
		android(2),
		web(3)
		;

		int platform;

		ClientType(int platform){
			this.platform = platform;
		}

		public int getPlatform() {
			return platform;
		}
	}

	public enum SessionType {
		app, web
	}

	public interface AuditStatus {
		int editing    = 0;
		int passed     = 1;
		int submitted  = 2;
		int rejected   = 3;
		int forbidden  = 4;
	}
	
	
	public static void init(Properties properties){

	}
	
	public static String createToken(){
		return CommonUtils.randomString(4, true) + Long.toString(System.currentTimeMillis(), 36) + CommonUtils.randomString(12, true);
	}
	
	public static ClientType getClientType(String peerid){
		if(peerid!=null && !peerid.isEmpty()){
			char firstChar = peerid.charAt(0);
			switch(firstChar) {
			case '1':
				return ClientType.web;
			case '2':
				return ClientType.android;
			case '3':
				return ClientType.ios;
			}
		}
		return ClientType.unknown;
	}
	
	public static String createPeerid(char clientType) {
        long ts = System.currentTimeMillis();
        long rn = (long) Math.floor(Math.random() * 9) + 1;
        long mn = ts % rn;
        return clientType + CommonUtils.randomString(3, true) + rn + Long.toString(ts, 36) + mn + CommonUtils.randomString(6, true);
    }
	
	public static boolean checkPeerid(String peerid) {
		try {
			if(peerid!=null && peerid.length() == 20){
				int rn = Integer.parseInt(peerid.substring(4, 5));
				int mn = Integer.parseInt(peerid.substring(13, 14));
				String ts36 = peerid.substring(5, 13);
				long ts = Long.valueOf(ts36, 36);
				if(ts % rn == mn) {
					return true;
				}
			}
		}catch(Exception e){	
		}
		return false;
	}
	

	public static class SegmentResult {
		public final String error;
		public final JSONObject data;
		public final JSONArray data2;

		public SegmentResult() {
			this(null, null, null);
		}

		public SegmentResult(String error) {
			this(error, null, null);
		}

		public SegmentResult(JSONObject data) {
			this(null, data, null);
		}

		public SegmentResult(JSONArray data) {
			this(null, null, data);
		}

		private SegmentResult(String error, JSONObject data, JSONArray data2) {
			this.error = error;
			this.data = data;
			this.data2 = data2;
		}
	}
}
