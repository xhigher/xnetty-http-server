package com.cheercent.xnetty.httpserver.conf;

import com.alibaba.fastjson.JSONObject;
import com.cheercent.xnetty.httpserver.base.XLogic;
import com.cheercent.xnetty.httpserver.base.XLogicConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

public abstract class ServiceConfig {

	private static Logger logger = LoggerFactory.getLogger(ServiceConfig.class);
	
	public static final Pattern ALLOW_DOMAIN_PATTERN = Pattern.compile("^http(s?):\\/\\/(([\\.\\d\\w]+\\.qiusheng\\.com)|(localhost)|(127\\.0\\.0\\.1)|(10\\.0\\.0\\.\\d+))(.*?)$");
	public static final String[] INTERNAL_IP_LIST = { "127.0.0.1" };

	private static final String PACKAGE_LOGIC = "com.cheercent.xnetty.httpserver.logic";
	
	private static final int statisticDelayTime = 10 * 60;
	private static final ScheduledExecutorService statisticExecutor = Executors.newScheduledThreadPool(1); 
	
	public enum ServiceModule {
		advert, toutiao, football, basketball,
		chatroom, wiki, news, forum, favorite, scheme, match, author, user, common
	};
	
	public static Map<String, Map<String, XLogicConfig>> LOGIC_CONFIGS = new HashMap<String, Map<String, XLogicConfig>>();
	public static Map<String, Map<String, XLogic>> LOGIC_SOURCES = new HashMap<String, Map<String, XLogic>>();
	
	public enum ActionMethod {
		GET, POST
	}

	public static XLogicConfig getLogicConfig(String module, String action, int version) {
		if(LOGIC_CONFIGS.containsKey(module)){
			return LOGIC_CONFIGS.get(module).get(getVersionAction(action, version));
		}
		return null;
	}

	public static String runLogic(String module, String action, int version, JSONObject parameters){
		try{
			if(LOGIC_SOURCES.containsKey(module)){
				XLogic logic = LOGIC_SOURCES.get(module).get(getVersionAction(action, version));
				if(logic != null){
					logic = logic.clone();
					return logic.handle(parameters);
				}
			}
		} catch(Exception e){
			logger.error("runLogic.Exception: module="+module+", action="+action+", parameters="+parameters.toString());
		}finally{
			
		}
		return XLogic.errorRequestResult();
	}
	
	public static void init() {
		
		for(ServiceModule module : ServiceModule.values()){
			initModule(module.toString());
		}
		
		statisticExecutor.scheduleWithFixedDelay(new Runnable(){
			@Override
			public void run() {
				JSONObject statisticData = new JSONObject();
				Map<String, XLogic> actionList = null;
				for(String module : LOGIC_SOURCES.keySet()) {
					actionList = LOGIC_SOURCES.get(module);
					for(String action : actionList.keySet()) {
						statisticData.put(module+"#"+action, actionList.get(action).getCloneCount());
					}
				}
				logger.info("ServiceStatisticData="+statisticData.toJSONString());
			}
		}, statisticDelayTime, statisticDelayTime, TimeUnit.SECONDS);
	}

	public static void stopStatisticExecutor(){
		statisticExecutor.shutdown();
	}

	private static String getVersionAction(String action, int version) {
		return action + "@" + version;
	}
	
	private static String getVersionAction(XLogicConfig logicConfig) {
		return logicConfig.name() + "@" + logicConfig.version();
	}
	
	public static void initModule(String module){
		File file = null;
		List<String> classFiles = null;
		Class<?> clazz = null;
		XLogicConfig logicConfig = null;
		
		Map<String, XLogicConfig> configList = new HashMap<String, XLogicConfig>();
		Map<String, XLogic> sourceList = new HashMap<String, XLogic>();
		
		String modulePackage = PACKAGE_LOGIC + "." + module;
		String modulePath = modulePackage.replace(".", "/");
		String versionAction = null;
		try{
			URL url = null;
			JarURLConnection jarConnection = null;
			JarFile jarFile = null;
			Enumeration<JarEntry> jarEntryEnumeration = null;
			String jarEntryName = null;
			String fullClazz = null;
			Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(modulePath);
			while (urls.hasMoreElements()) {
				url = urls.nextElement();
				if ("jar".equalsIgnoreCase(url.getProtocol())) {
					jarConnection = (JarURLConnection) url.openConnection();
					if (jarConnection != null) {
						jarFile = jarConnection.getJarFile();
						if (jarFile != null) {
							jarEntryEnumeration = jarFile.entries();
							while (jarEntryEnumeration.hasMoreElements()) {
								jarEntryName = jarEntryEnumeration.nextElement().getName();
								if (jarEntryName.contains(".class") && jarEntryName.replace("/",".").startsWith(modulePackage)) {
									fullClazz = jarEntryName.substring(0, jarEntryName.lastIndexOf(".")).replace("/", ".");
									clazz = Class.forName(fullClazz);
									logicConfig = clazz.getAnnotation(XLogicConfig.class);
									if(logicConfig != null){
										versionAction = getVersionAction(logicConfig);
										configList.put(versionAction, logicConfig);
										sourceList.put(versionAction, (XLogic)clazz.newInstance());
									}
								}
							}
						}
					}
				}else{
					file = new File(url.toURI());
					if (file != null) {
						classFiles = new ArrayList<String>();
						listClassFiles(file, classFiles);
						for (String clz : classFiles) {
							fullClazz = clz.replaceAll("[/\\\\]", ".");
							fullClazz = fullClazz.substring(fullClazz.indexOf(modulePackage), clz.length() - 6);
							clazz = Class.forName(fullClazz);
							logicConfig = clazz.getAnnotation(XLogicConfig.class);
							if (logicConfig != null) {
								versionAction = getVersionAction(logicConfig);
								configList.put(versionAction, logicConfig);
								sourceList.put(versionAction, (XLogic) clazz.newInstance());
							}
						}
					}
				}
			}
		}catch(Exception e){
			logger.error("initModule.Exception", e);
		}
		LOGIC_CONFIGS.put(module, configList);
		LOGIC_SOURCES.put(module, sourceList);
	}

	private static void listClassFiles(File file, List<String> classFiles){
		File tf = null;
		File[] files = file.listFiles();
		for(int i=0; i<files.length; i++){
			tf = files[i];
			if(tf.isDirectory()){
				listClassFiles(tf, classFiles);
			}else if(tf.isFile() && tf.getName().endsWith(".class")){
				classFiles.add(tf.getAbsolutePath());
			}
		}
	}

}
