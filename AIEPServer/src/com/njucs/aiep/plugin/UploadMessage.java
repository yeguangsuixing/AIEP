package com.njucs.aiep.plugin;

import com.twzcluster.net.Message;

public class UploadMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8473839854387306049L;
	
	private boolean isSuccessful;
	
	public UploadMessage( boolean isSuccessful ){
		this.isSuccessful = isSuccessful;
	}
	
	public UploadMessage( boolean isSuccessful, String content ){
		this.isSuccessful = isSuccessful;
		this.content = content;
	}

	/**
	 * @param isSuccessful the isSuccessful to set
	 */
	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}

	/**
	 * @return the isSuccessful
	 */
	public boolean isSuccessful() {
		return isSuccessful;
	}

	
	
}
