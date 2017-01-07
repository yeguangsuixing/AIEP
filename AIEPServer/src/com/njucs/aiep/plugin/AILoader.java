package com.njucs.aiep.plugin;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.AIEPUserConf.AisBasicConf;
import com.njucs.aiep.base.ClassOperator;
import com.njucs.aiep.net.DataTransmit.Lang;
import com.njucs.aiep.sandbox.AISandBox;


/**
 * AI Loader
 * 
 * @author ygsx
 * 
 * @created 2013Äê6ÔÂ1ÈÕ20:38:06
 * */
public abstract class AILoader<E_AI extends AI>  {
	
	public static enum AilMode { NET, LOCAL };
	
	protected E_AI loaderAI = null;
	protected AilMode ailMode = AilMode.NET;
	protected AISandBox aiSandBox = null;
	
	public abstract void setAISite( AISite<E_AI> aiSite );
	
	public abstract   void setAI(E_AI ai);
	/**
	 * Set the AI by the jar file
	 * @param aiJarFileName the AI Class Name
	 * */
	public abstract boolean setAI( String aiJarFileName ) throws Exception;
	
	public abstract E_AI getAI();
	
	public abstract Lang getAILang();
	
	public abstract boolean initialize();
	
	public abstract boolean execute();
	
	public abstract void forceExit();

	public void setAISandBox( AISandBox aiSandBox ){
		this.aiSandBox = aiSandBox;
	}
	public AISandBox getAISandBox(){
		return this.aiSandBox;
	}
	
	public void setAilMode(AilMode mode ){
		this.ailMode = mode;
	}
	public AilMode getAilMode(){
		return this.ailMode;
	}

	/**
	 * @return the hostIp
	 */
	public String getHostIp() {
		return AIEPUserConf.getInstance().getString(AisBasicConf.AIJ_IP);
	}

	/**
	 * @return the hostPort
	 */
	public int getHostPort() {
		return AIEPUserConf.getInstance().getInt(AisBasicConf.AIJ_PORT);
	}

	/**
	 * @param hostIp
	 *            the hostIp to set
	 */
	public void setHostIp(String hostIp) {
		AIEPUserConf.getInstance().setString(AisBasicConf.AIJ_IP, hostIp);
	}

	/**
	 * @param hostPort
	 *            the hostPort to set
	 */
	public void setHostPort(int hostPort) {
		AIEPUserConf.getInstance().setInt(AisBasicConf.AIJ_PORT, hostPort);
	}
	/**
	 * load a jar file, and get the AI Class name from it
	 * @param jarFileName jar file name
	 * @return the name of the first class extending class "AI"
	 * @exception Exception the given file error
	 * */
	protected static String loadAIJarFile( String jarFileName )  {
		Map<String, AbstractList<String>> childClassmap = new TreeMap<String, AbstractList<String>>();
		childClassmap.put(AI.class.getName(), new ArrayList<String>());
		try {
			ClassOperator.addURL( jarFileName, childClassmap);
		} catch (IllegalArgumentException e) {
			//e.printStackTrace(); 
			return null;
		} catch (SecurityException e) {
			//e.printStackTrace(); 
			return null;
		} catch (IllegalAccessException e) {
			//e.printStackTrace(); 
			return null;
		} catch (InvocationTargetException e) {
			//e.printStackTrace(); 
			return null;
		} catch (NoSuchMethodException e) {
			//e.printStackTrace(); 
			return null;
		} catch (IOException e) {
			//e.printStackTrace(); 
			return null;
		} catch (ClassNotFoundException e) {
			//e.printStackTrace(); 
			return null;
		}
		AbstractList<String> list = childClassmap.get(AI.class.getName());
		if( list.size() > 0 ){
			return list.get(0);
		} else {//System.out.println( "cannot find the ai class" );
			return null;
		}
	}//
	/*
	public static AIInfo genAIInfo( String aiJarFileName ){
		String aiClassName = loadAIJarFile( aiJarFileName );
		if( aiClassName == null ){
			//System.out.println( "aiClassName == null " ); 
			return null;
		}
		try {
			Class<?> c = Class.forName(aiClassName);
			Method getversionMehtod = c.getDeclaredMethod("getVersion", new Class<?>[]{} );
			if( getversionMehtod == null ) return null;
			AI ai = (AI)c.newInstance();
			AIInfo aiInfo = new AIInfo( ai.getId(), ai.getName(), ai.getNickname(), ai.getVersion() );
			aiInfo.setClassName(aiClassName);
			aiInfo.setSrcFileName(aiJarFileName);
			return aiInfo;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();  return null;
		} catch (InstantiationException e) {
			e.printStackTrace();  return null;
		} catch (IllegalAccessException e) {
			e.printStackTrace(); return null;
		} catch (SecurityException e) {
			e.printStackTrace(); return null;
		} catch (NoSuchMethodException e) {
			e.printStackTrace(); return null;
		}
	}//*/
	
}


