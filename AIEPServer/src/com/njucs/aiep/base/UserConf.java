package com.njucs.aiep.base;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;


/**
 * User Configuration Class
 * 
 * @author ygsx
 * 
 * @created 2013年5月4日17:43:12
 * @modified 2013年6月3日14:11:58
 * */
public class UserConf implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3381002995533114041L;
	

	
	/**
	 * the user conf file name
	 * */
	protected static String CONF_FILE_NAME = "user.conf";

	private static UserConf userConf = null;
	
	private Map<String, Map<String, Serializable>> confMap = new TreeMap<String, Map<String, Serializable>>();
	
	
	protected UserConf(){}
	
	/**
	 * Get a user conf instance, and you don't need to care about whether the conf file  exists.
	 * You can set the conf file name by the variable {@link #CONF_FILE_NAME}
	 * @see #getInstance(String)
	 * @return  the unique user conf instance
	 * */
	public static UserConf getInstance(){
		if( userConf == null ) {
			userConf = (UserConf) Recorder.open( CONF_FILE_NAME );
			if( userConf == null ){
				userConf = new UserConf();
				userConf.save();
			}
		}
		
		return userConf;
	}
	
	/**
	 * Get a user conf instance from a given file, and you don't need to care about whether the conf file  exists.
	 * You can set the conf file name by the variable {@link #CONF_FILE_NAME}
	 * @see #getInstance(String)
	 * @return  the unique user conf instance
	 * */
	public static UserConf getInstance(String userConfFileName) {
		CONF_FILE_NAME = userConfFileName;
		return getInstance();
	}
	
	
	protected void save(){
		Recorder.save(this, CONF_FILE_NAME);
	}
	
	public  boolean set(String role, String field, Serializable object){
		if( role == null || role.length() == 0 || field == null || field.length() == 0) return false;
		Set<Entry<String, Map<String, Serializable>>> set = confMap.entrySet();
		for( Entry<String, Map<String, Serializable>> entry : set ){
			if( entry.getKey().equals(role) ){
				entry.getValue().put(field, object);save();
				return true;
			}
		}
		//add new 
		Map<String, Serializable> newMap = new TreeMap<String, Serializable>();
		newMap.put(field, object);
		confMap.put(role, newMap);
		save();
		return true;
	}
	
	public Serializable get(String role, String field){
		if( role == null || role.length() == 0 || field == null || field.length() == 0) return false;
		Set<Entry<String, Map<String, Serializable>>> set = confMap.entrySet();
		for( Entry<String, Map<String, Serializable>> entry : set ){
			if( entry.getKey().equals(role) ){
				return entry.getValue().get(field);
			}
		}
		return null;
	}
	
}


