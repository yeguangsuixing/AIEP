package com.njucs.aiep.plugin;

import java.util.AbstractList;

import com.njucs.aiep.frame.AIInfo;
import com.twzcluster.net.Message;

public class RankMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9198846065140680982L;
	private AbstractList<AIInfo> result;
	
	protected RankMessage(AbstractList<AIInfo> result){
		this.result = result;
		this.receiverid = Message.DEFAULT;
	}

	/**
	 * @return the result
	 */
	public AbstractList<AIInfo> getRankList() {
		return result;
	}
}