package com.njucs.aiep.frame;

import java.io.Serializable;



/**
 * Judge Desk
 * @author ygsx
 * @created 2013Äê6ÔÂ9ÈÕ21:07:15
 * */
public class Desk implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4757828456301343011L;

	public final static int ILLEGAL_ID = -1;
	
	private static int ID = 100;
	
	protected int deskId;
	
	protected Desk(){}
	
	public static Desk newInstance(){
		Desk desk = new Desk();
		desk.deskId = ID++;
		return desk;
	}

	/**
	 * @return the deskId
	 */
	public int getDeskId() {
		return deskId;
	}
	
	
	
}
