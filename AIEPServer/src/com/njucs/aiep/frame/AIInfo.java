package com.njucs.aiep.frame;

import java.io.Serializable;
import java.util.Date;

import com.njucs.aiep.net.DataTransmit.Lang;


/**
 * AI Info
 * 
 * @author ygsx
 * @created 2013Äê6ÔÂ4ÈÕ22:10:26
 * */
public class AIInfo implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6378917351576290326L;
	protected String id, name, nickname, version;
	protected String jarFileName, className;
	protected String srcFileName;
	protected Date fileRecvTime;
	
	public static enum AIState { OFFLINE, WATCHING, WAITING, RUNNING };
	
	protected Lang language;
	//private int connId = NetManager.ILLEGAL_ID;
	private Desk desk = null;
	private AIState aiState = AIState.OFFLINE;
	//count
	protected int totalCount = 0, winCount = 0, escapeCount = 0;
	
	public AIInfo(){}
	
	public AIInfo( String id, String name, String nickname, String version ){
		this.id = id;
		this.name = name;
		this.nickname = nickname;
		this.version = version;
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @return the className
	 */
	public String getClassName() {
		return className;
	}
	
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @return the jarFileName
	 */
	public String getJarFileName() {
		return jarFileName;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @param jarFileName the jarFileName to set
	 */
	public void setJarFileName(String jarFileName) {
		this.jarFileName = jarFileName;
	}

	/**
	 * @param srcFileName the srcFileName to set
	 */
	public void setSrcFileName(String srcFileName) {
		this.srcFileName = srcFileName;
	}

	/**
	 * @return the srcFileName
	 */
	public String getSrcFileName() {
		return srcFileName;
	}

	/**
	 * @param fileRecvTime the fileRecvTime to set
	 */
	public void setFileRecvTime(Date fileRecvTime) {
		this.fileRecvTime = fileRecvTime;
	}

	/**
	 * @return the fileRecvTime
	 */
	public Date getFileRecvTime() {
		return fileRecvTime;
	}

	
	
	/**
	 * @return the totalCount
	 */
	public int getTotalCount() {
		return totalCount;
	}
	
	public int addTotalCount() {
		return ++totalCount;
	}

	/**
	 * @return the winCount
	 */
	public int getWinCount() {
		return winCount;
	}

	public int addWinCount() {
		return ++winCount;
	}

	/**
	 * @return the escapeCount
	 */
	public int getEscapeCount() {
		return escapeCount;
	}

	/**
	 * @param totalCount the totalCount to set
	 */
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * @param winCount the winCount to set
	 */
	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

	/**
	 * @param escapeCount the escapeCount to set
	 */
	public void setEscapeCount(int escapeCount) {
		this.escapeCount = escapeCount;
	}
	
	

	/**
	 * @return the language
	 */
	public Lang getLanguage() {
		return language;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(Lang language) {
		this.language = language;
	}

	public boolean equals(Object aiInfo){
		if( aiInfo instanceof AIInfo ){
			AIInfo ai = (AIInfo)aiInfo;
			if( id ==null ){
				if( ai.id != null ){
					return false;
				}
			} else {
				if( ! id.equals(ai.id) ){
					return  false;
				}
			}
			if( version ==null ){
				if( ai.version != null ){
					return false;
				}
			} else {
				if( ! version.equals(ai.version) ){
					return  false;
				}
			}
			return true;
		} else {			
			return false;
		}
	}
	
	public static void main(String[] args){
		AIInfo a = new AIInfo("id1", null, null, "v");
		AIInfo b = new AIInfo("id1", null, null, "v");
		System.out.println( a.equals(b) );
	}

	/**
	 * @param aiState the aiState to set
	 */
	public void setAiState(AIState aiState) {
		this.aiState = aiState;
	}

	/**
	 * @return the aiState
	 */
	public AIState getAiState() {
		return aiState;
	}

/*
	/**
	 * @param connId the connId to set
	 * /
	public void setConnId(int connId) {
		this.connId = connId;
	}

	/**
	 * @return the connId
	 * /
	public int getConnId() {
		return connId;
	}
//*/
	/**
	 * @param desk the desk to set
	 */
	public void setDesk(Desk desk) {
		this.desk = desk;
	}

	/**
	 * @return the desk
	 */
	public Desk getDesk() {
		return desk;
	}

	
}
