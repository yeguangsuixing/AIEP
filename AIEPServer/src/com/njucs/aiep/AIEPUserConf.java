package com.njucs.aiep;

import com.njucs.aiep.AIEP.AiepRole;
import com.njucs.aiep.AIEP.Country;
import com.njucs.aiep.base.Recorder;
import com.njucs.aiep.base.UserConf;
import com.njucs.aiep.plugin.AISite.AisRole;

public class AIEPUserConf extends UserConf {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2703452129432085863L;
	
	public final static int AIJ_PORT = 8890, 
		AIA_ONLINE_PORT = 8891, 
		AIA_RELEASE_PORT = 8892,
		AIA_UPLOAD_PORT = 8893, 
		/** recv from ai judge */
		AIA_RECV_PORT = 8894,
		AIA_RC_PORT = 8823;
	public final static String DEFAULT_IP = "127.0.0.1";
	public final static int DEFAULT_UPLOAD_FILE_LIMITED = 3*1024;//3MB
	
	
	public static enum AijBasicConf { 
		AIJ_PORT,
		AIJ_AI_JAR_FILE1,
		AIJ_AI_JAR_FILE2,
		AIJ_RUN_IN_SANDBOX,
		AIJ_RESULT_SEND_IP,
		AIJ_RESULT_SEND_PORT
	};
	public static enum AisBasicConf { 
		AIJ_RUN_IN_SANDBOX,//[boolean]
		AIJ_IP, AIJ_PORT, AIA_IP, 
		AIA_ONLINE_PORT, AIA_RELEASE_PORT, AIA_RC_PORT,
		AIA_UPLOAD_PORT,//upload ai file port
		AI_FILE_NAME
	};
	public enum AiaBasicConf {
		AIA_ONLINE_PORT,//online port
		/** recv from ai judge */
		AIA_RECV_PORT,
		AIA_RELEASE_PORT, 
		AIA_UPLOAD_PORT,//upload ai file port
		AIA_RC_PORT, //RC - Remote Control
		AIA_ONLINE_OR_UPLOAD,//[boolean]true=online, false=upload
		AIA_ALLOW_RC,//[boolean]allow be controlled remotely
		AIA_RELEASE_RSL,//[boolean]release the compition result
		/**
		 * the regular exp of ai.getId
		 * */
		AIA_UPLOAD_AI_ID_REGEXP,
		
		AIA_RUN_IN_SANDBOX,//[boolean]
		/**
		 * upload file limit, by KB
		 * */
		AIA_LIMITED_AI_FILE_SIZE,
		AIA_UPLOAD_PATH
	};
	
	private static AIEPUserConf userConf = null;
	
	public static AIEPUserConf getInstance(){
		if( userConf == null ) {
			userConf = (AIEPUserConf) Recorder.open( CONF_FILE_NAME );
			if( userConf == null ){
				userConf = new AIEPUserConf();
				userConf.setRole(AiepRole.AISite);
				userConf.setCountryNo(Country.ENG);
				userConf.setCurrentPluginName(null);
				userConf.setAisRole(AisRole.CONN_AIJ);

				userConf.setInt(AijBasicConf.AIJ_PORT, AIJ_PORT);
				userConf.setBoolean(AijBasicConf.AIJ_RUN_IN_SANDBOX, false);//不运行在安全沙箱中
				userConf.setString(AijBasicConf.AIJ_RESULT_SEND_IP, DEFAULT_IP);
				userConf.setInt(AijBasicConf.AIJ_RESULT_SEND_PORT, AIA_RECV_PORT);
				
				userConf.setString(AisBasicConf.AIJ_IP, DEFAULT_IP);
				userConf.setString( AisBasicConf.AIA_IP, DEFAULT_IP );
				userConf.setInt(AisBasicConf.AIJ_PORT, AIJ_PORT);
				userConf.setInt(AisBasicConf.AIA_ONLINE_PORT, AIA_ONLINE_PORT);
				userConf.setInt(AisBasicConf.AIA_UPLOAD_PORT, AIA_UPLOAD_PORT);
				userConf.setInt(AisBasicConf.AIA_RELEASE_PORT, AIA_RELEASE_PORT);
				userConf.setBoolean(AisBasicConf.AIJ_RUN_IN_SANDBOX, false);//
				userConf.setAisRole(AisRole.CONN_AIJ);

				userConf.setInt(AiaBasicConf.AIA_ONLINE_PORT, AIA_ONLINE_PORT);
				userConf.setInt(AiaBasicConf.AIA_RECV_PORT, AIA_RECV_PORT);
				userConf.setInt(AiaBasicConf.AIA_UPLOAD_PORT, AIA_UPLOAD_PORT);
				userConf.setInt(AiaBasicConf.AIA_RELEASE_PORT, AIA_RELEASE_PORT);
				userConf.setInt(AiaBasicConf.AIA_RC_PORT, AIA_RC_PORT);
				userConf.setBoolean(AiaBasicConf.AIA_ONLINE_OR_UPLOAD, true);//online or upload
				userConf.setBoolean( AiaBasicConf.AIA_RELEASE_RSL, true );//release the compition result
				userConf.setBoolean(AiaBasicConf.AIA_RUN_IN_SANDBOX, false);//run in sandbox
				userConf.setBoolean( AiaBasicConf.AIA_ALLOW_RC, false );//forbid remotely controlling
				userConf.setInt(AiaBasicConf.AIA_LIMITED_AI_FILE_SIZE, DEFAULT_UPLOAD_FILE_LIMITED);
				
				userConf.save();
			}
		}
		
		return userConf;
	}
	
	public boolean setRole( AiepRole role ){
		return super.set( AIEP.class.getName(), AiepRole.class.getName(), role);
	}
	
	public AiepRole getRole(){
		return (AiepRole) super.get( AIEP.class.getName(), AiepRole.class.getName());
	}

	public boolean setCurrentPluginName(String pluginname) {
		String field = "CurrentPluginName";
		return super.set( AIEP.class.getName(), field, pluginname);
	}
	
	public String getCurrentPluginName(){
		String field = "CurrentPluginName";
		return (String) super.get( AIEP.class.getName(), field);
	}

	public boolean setCountryNo( Country country ){
		String field = "CountryNo";
		return super.set( AIEP.class.getName(), field, country);
	}
	
	public Country getCountryNo() {
		String field = "CountryNo";
		return (Country) super.get( AIEP.class.getName(), field);
	}
	
	
	//AIj
	public boolean setString( AijBasicConf conf, String value ){
		return super.set( AIEP.class.getName(), AijBasicConf.class.getName()+conf.toString(), value );
	}
	public boolean setInt( AijBasicConf conf, int value ){
		return super.set( AIEP.class.getName(), AijBasicConf.class.getName()+conf.toString(), String.valueOf(value) );
	}
	public boolean setBoolean( AijBasicConf conf, boolean value ){
		return super.set( AIEP.class.getName(), AijBasicConf.class.getName()+conf.toString(), String.valueOf(value) );
	}
	public String getString( AijBasicConf conf){
		return (String) super.get( AIEP.class.getName(), AijBasicConf.class.getName()+conf.toString() );
	}
	public int getInt( AijBasicConf conf ){
		return Integer.parseInt((String) super.get( AIEP.class.getName(), AijBasicConf.class.getName()+conf.toString()));
	}
	public boolean getBoolean( AijBasicConf conf ){
		return Boolean.parseBoolean((String) super.get( AIEP.class.getName(), AijBasicConf.class.getName()+conf.toString() ));
	}
	//AIS
	public boolean setString( AisBasicConf conf, String value ){
		return super.set( AIEP.class.getName(), AisBasicConf.class.getName()+conf.toString(), value );
	}
	public boolean setInt( AisBasicConf conf, int value ){
		return super.set( AIEP.class.getName(), AisBasicConf.class.getName()+conf.toString(), String.valueOf(value) );
	}
	public boolean setBoolean( AisBasicConf conf, boolean value ){
		return super.set( AIEP.class.getName(), AisBasicConf.class.getName()+conf.toString(), String.valueOf(value) );
	}
	public String getString( AisBasicConf conf){
		return (String) super.get( AIEP.class.getName(), AisBasicConf.class.getName()+conf.toString() );
	}
	public int getInt( AisBasicConf conf){
		return Integer.parseInt((String) super.get( AIEP.class.getName(), AisBasicConf.class.getName()+conf.toString()));
	}
	public boolean getBoolean( AisBasicConf conf ){
		return Boolean.parseBoolean((String) super.get( AIEP.class.getName(), AisBasicConf.class.getName()+conf.toString() ));
	}
	public boolean setAisRole( AisRole role ){
		return super.set( AIEP.class.getName(), AisRole.class.toString(), role );
	}
	public AisRole getAisRole(){
		return (AisRole) super.get( AIEP.class.getName(), AisRole.class.toString() );
	} 
	
	//AIA
	public boolean setString( AiaBasicConf conf, String value ){
		return super.set( AIEP.class.getName(), AiaBasicConf.class.getName()+conf.toString(), value );
	}
	public boolean setInt( AiaBasicConf conf, int value ){
		return super.set( AIEP.class.getName(), AiaBasicConf.class.getName()+conf.toString(), String.valueOf(value) );
	}
	public boolean setBoolean( AiaBasicConf conf, boolean value ){
		return super.set( AIEP.class.getName(), AiaBasicConf.class.getName()+conf.toString(), String.valueOf(value) );
	}
	public String getString( AiaBasicConf conf){
		return (String) super.get( AIEP.class.getName(), AiaBasicConf.class.getName()+conf.toString() );
	}
	public int getInt( AiaBasicConf conf){
		return Integer.parseInt((String) super.get( AIEP.class.getName(), AiaBasicConf.class.getName()+conf.toString()));
	}
	public boolean getBoolean( AiaBasicConf conf ){
		return Boolean.parseBoolean((String) super.get( AIEP.class.getName(), AiaBasicConf.class.getName()+conf.toString() ));
	}
}



