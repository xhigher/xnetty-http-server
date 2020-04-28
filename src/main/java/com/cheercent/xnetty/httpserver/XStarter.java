package com.cheercent.xnetty.httpserver;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cheercent.xnetty.httpserver.base.XServer;


/*
 * @copyright (c) xhigher 2015 
 * @author xhigher    2015-3-26 
 */
public class XStarter {
	
	private static Logger logger = LoggerFactory.getLogger(XStarter.class);
	
	private static String configFile = "/application.properties";

	public static void main(String[] args) {
		try{
			Properties properties = new Properties();
			InputStream is = Object.class.getResourceAsStream(configFile);
			properties.load(is);
			if (is != null) {
				is.close();
			}
			
			final XServer server = new XServer(properties);
			
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run() {
					server.stop();
				}
			});
			
			server.start();
			
		}catch(Exception e){
			logger.error("XStarter.Exception:", e);
		}
	}
}
