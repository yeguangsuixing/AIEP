package com.njucs.aiep.plugin.fir.frame;

import com.njucs.aiep.frame.Desk;

public class FIRDesk extends Desk {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5447506473745812697L;

	public final static int ILLEGAL_ID = -1;
	private static int ID = 100;
	
	private AIWrapper wrapper1, wrapper2;
	
	private Inning inning;
	//private Timer timer = new Timer();
	
	//do not new an instance by this constructor
	public FIRDesk(){}
	

	public static FIRDesk newInstance(){
		FIRDesk desk = new FIRDesk();
		desk.deskId = ID++;
		//desk.inning = new Inning(null, null, null);
		return desk;
	}

	public void setDeskId( int deskId ){
		this.deskId = deskId;
	}
	
	/**
	 * @param wrapper1 the wrapper1 to set
	 */
	public void setWrapper1(AIWrapper wrapper1) {
		this.wrapper1 = wrapper1;
	}


	/**
	 * @return the wrapper1
	 */
	public AIWrapper getWrapper1() {
		return wrapper1;
	}


	/**
	 * @param wrapper2 the wrapper2 to set
	 */
	public void setWrapper2(AIWrapper wrapper2) {
		this.wrapper2 = wrapper2;
	}


	/**
	 * @return the wrapper2
	 */
	public AIWrapper getWrapper2() {
		return wrapper2;
	}



	/**
	 * @param inning the inning to set
	 */
	public void setInning(Inning inning) {
		this.inning = inning;
	}


	/**
	 * @return the inning
	 */
	public Inning getInning() {
		return inning;
	}


}
