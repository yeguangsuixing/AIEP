package com.njucs.aiep.net.file;

import java.util.EventObject;

import com.njucs.aiep.frame.AIInfo;

public class ReceiveFileEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4801088976656560011L;
	
	private AIInfo aiInfo;
	
	public ReceiveFileEvent(Object source, AIInfo aiInfo) {
		super(source);
		this.aiInfo = aiInfo;
	}

	public AIInfo getAIInfo(){
		return this.aiInfo;
	}
}
