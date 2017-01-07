package com.njucs.aiep.frame;

import java.io.Serializable;
import java.util.Date;

import com.njucs.aiep.plugin.Result;
import com.njucs.aiep.pool.CompareEvent;


public class Competition implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3835529782279845590L;
	
	public static enum State { RUNNING, FINISHED };
	
	private Date startTime, endTime;
	private AIInfo offensiveInfo, defensiveInfo, winner;
	//private ResultCode resultCode;
	private Result reuslt;
	private State state;
	
	private Date lastUpdate;
	
	private CompareEvent<AIInfo> compareEvent;
	
	public Competition(AIInfo offensiveInfo, AIInfo defensiveInfo){
		this.offensiveInfo = offensiveInfo;
		this.defensiveInfo = defensiveInfo;
		this.state = State.RUNNING;
		this.startTime = new Date();
		this.lastUpdate = this.startTime;
	}
	
	/**
	 * @return the startTime
	 */
	public Date getStartTime() {
		return startTime;
	}
	/**
	 * @return the endTime
	 */
	public Date getEndTime() {
		return endTime;
	}
	/**
	 * @return the offensiveInfo
	 */
	public AIInfo getOffensiveInfo() {
		return offensiveInfo;
	}
	/**
	 * @return the defensive
	 */
	public AIInfo getDefensiveInfo() {
		return defensiveInfo;
	}
	/**
	 * @return the winner
	 */
	public AIInfo getWinner() {
		return winner;
	}
	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	/**
	 * @param offensiveInfo the offensiveInfo to set
	 */
	public void setOffensiveInfo(AIInfo offensiveInfo) {
		this.offensiveInfo = offensiveInfo;
	}
	/**
	 * @param defensiveInfo the defensiveInfo to set
	 */
	public void setDefensiveInfo(AIInfo defensiveInfo) {
		this.defensiveInfo = defensiveInfo;
	}
	/**
	 * @param winner the winner to set
	 */
	public void setWinner(AIInfo winner) {
		this.winner = winner;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}
/*
	/**
	 * @param resultCode the resultCode to set
	 * /
	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * @return the resultCode
	 * /
	public ResultCode getResultCode() {
		return resultCode;
	}//*/

	/**
	 * @param compareEvent the compareEvent to set
	 */
	public void setCompareEvent(CompareEvent<AIInfo> compareEvent) {
		this.compareEvent = compareEvent;
	}

	/**
	 * @return the compareEvent
	 */
	public CompareEvent<AIInfo> getCompareEvent() {
		return compareEvent;
	}

	/**
	 * @param reuslt the reuslt to set
	 */
	public void setReuslt(Result reuslt) {
		this.reuslt = reuslt;
	}

	/**
	 * @return the reuslt
	 */
	public Result getReuslt() {
		return reuslt;
	}

	/**
	 * @param lastUpdate the lastUpdate to set
	 */
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	/**
	 * @return the lastUpdate
	 */
	public Date getLastUpdate() {
		return lastUpdate;
	}
}
