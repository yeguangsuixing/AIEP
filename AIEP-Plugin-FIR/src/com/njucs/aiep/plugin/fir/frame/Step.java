package com.njucs.aiep.plugin.fir.frame;

import java.io.Serializable;

import com.njucs.aiep.game.Status;


/**
 * 
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ5ÈÕ23:09:48
 * */
public class Step implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8836586951658153100L;
	private int x, y;
	private Status status = Status.EMPTY;
	private int usedTime = 0;
	
	public Step(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	public Step(Status status, int x, int y){
		this(x, y);
		this.status = status;
	}
	
	public Step(Status status, int x, int y, int usedTime){
		this( status, x, y );
		this.usedTime = usedTime;
	}
	
	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}
	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}
	/**
	 * @param x the x to set
	 */
	protected void setX(int x) {
		this.x = x;
	}
	/**
	 * @param y the y to set
	 */
	protected void setY(int y) {
		this.y = y;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @param usedTime the usedTime to set
	 */
	public void setUsedTime(int usedTime) {
		this.usedTime = usedTime;
	}

	/**
	 * @return the usedTime
	 */
	public int getUsedTime() {
		return usedTime;
	}
	
	
	
	
}
