package com.njucs.aiep.plugin;

import java.io.Serializable;

import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.game.Status;


/**
 * Competition Result
 * @author ygsx
 * @created 2013Äê6ÔÂ5ÈÕ9:01:39
 * */
public class Result implements Serializable {//<E_Result extends Serializable>
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2020068809896678764L;
	
	public static enum ResultCode { 
		/** successful */
		DONE,
		/** unknown fatal error */
		ERROR,
		/** crash */
		CRASH
	};
	
	protected AIInfo offensiveAIInfo, defensiveAIInfo;
	protected Status winner, errorStatus, activeStatus;
	
	protected ResultCode resultCode;
	
	protected String realTimeResult = null;
	
	//protected E_Result specialResult;
	
	public Result(){}

	/**
	 * @return the offensiveAIInfo
	 */
	public AIInfo getOffensiveAIInfo() {
		return offensiveAIInfo;
	}

	/**
	 * @return the defensiveAIInfo
	 */
	public AIInfo getDefensiveAIInfo() {
		return defensiveAIInfo;
	}

	/**
	 * @return the winner
	 */
	public Status getWinner() {
		return winner;
	}
	/**
	 * @return the resultCode
	 */
	public ResultCode getResultCode() {
		return resultCode;
	}

	/**
	 * @param offensiveAIInfo the offensiveAIInfo to set
	 */
	public void setOffensiveAIInfo(AIInfo offensiveAIInfo) {
		this.offensiveAIInfo = offensiveAIInfo;
	}

	/**
	 * @param defensiveAIInfo the defensiveAIInfo to set
	 */
	public void setDefensiveAIInfo(AIInfo defensiveAIInfo) {
		this.defensiveAIInfo = defensiveAIInfo;
	}

	/**
	 * @param winner the winner to set
	 */
	public void setWinner(Status winner) {
		this.winner = winner;
	}

	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(ResultCode resultCode) {
		this.resultCode = resultCode;
	}
/*
	/**
	 * @return the specialResult
	 * /
	public E_Result getSpecialResult() {
		return specialResult;
	}

	/**
	 * @param specialResult the specialResult to set
	 * /
	public void setSpecialResult(E_Result specialResult) {
		this.specialResult = specialResult;
	}
//*/

	/**
	 * @return the realTimeResult
	 */
	public String getRealTimeResult() {
		return realTimeResult;
	}

	/**
	 * @param realTimeResult the realTimeResult to set
	 */
	public void setRealTimeResult(String realTimeResult) {
		this.realTimeResult = realTimeResult;
	}

	/**
	 * @return the errorStatus
	 */
	public Status getErrorStatus() {
		return errorStatus;
	}

	/**
	 * @param errorStatus the errorStatus to set
	 */
	public void setErrorStatus(Status errorStatus) {
		this.errorStatus = errorStatus;
	}

	/**
	 * @return the activeStatus
	 */
	public Status getActiveStatus() {
		return activeStatus;
	}

	/**
	 * @param activeStatus the activeStatus to set
	 */
	public void setActiveStatus(Status activeStatus) {
		this.activeStatus = activeStatus;
	}

}






