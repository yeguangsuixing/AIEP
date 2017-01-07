package com.njucs.aiep.plugin;

import com.twzcluster.net.Message;


public class ResultMessage extends Message {//<E_Result extends Serializable>
	/**
	 * 
	 */
	private static final long serialVersionUID = 8897963591684742117L;
	protected Result result;
	
	public static enum ResultType { REALTIME, FINAL };
	
	protected ResultType resultType = ResultType.FINAL;
	
	public ResultMessage(Result result, ResultType resultType){
		this.result = result;
		this.resultType = resultType;
	}

	/**
	 * @return the result
	 */
	public Result getResult() {
		return result;
	}

	/**
	 * @return the resultType
	 */
	public ResultType getResultType() {
		return resultType;
	}
	
	
}
