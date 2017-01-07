package com.njucs.aiep.plugin.fir.frame;

import java.io.Serializable;

import com.njucs.aiep.flow.FlowRunnable;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.game.Status;
import com.twzcluster.net.NetManager;

public class AIWrapper implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3889635764022795979L;
	
	private AIInfo aiInfo;
	private transient FIRDesk desk = null;
	private Status status;
	private int connId = NetManager.ILLEGAL_ID;
	private transient FlowRunnable<Step> flowRunnable;
	
	public AIWrapper(){}
	
	/**
	 * @param aiInfo the aiInfo to set
	 */
	public void setAiInfo(AIInfo aiInfo) {
		this.aiInfo = aiInfo;
	}
	/**
	 * @return the aiInfo
	 */
	public AIInfo getAiInfo() {
		return aiInfo;
	}

	/**
	 * @param flowRunnable the flowRunnable to set
	 */
	public void setFlowRunnable(FlowRunnable<Step> flowRunnable) {
		this.flowRunnable = flowRunnable;
	}
	/**
	 * @return the flowRunnable
	 */
	public FlowRunnable<Step> getFlowRunnable() {
		return flowRunnable;
	}
	/**
	 * @param desk the desk to set
	 */
	public void setDesk(FIRDesk desk) {
		this.desk = desk;
	}
	/**
	 * @return the desk
	 */
	public FIRDesk getDesk() {
		return desk;
	}
	/**
	 * @param connId the connId to set
	 */
	public void setConnId(int connId) {
		this.connId = connId;
	}
	/**
	 * @return the connId
	 */
	public int getConnId() {
		return connId;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	
	
}
