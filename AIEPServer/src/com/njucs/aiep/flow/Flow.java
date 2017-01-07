package com.njucs.aiep.flow;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


/**
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ4ÈÕ12:53:51
 * 
 * */
public class Flow<E> {
	
	private Date deadline;
	private FlowEventListener<E> flowListener;
	private int connId, listenId;
	private FlowRunnable<E> flowRunnable;
	private Timer timer;
	
	private FlowTrigger flowTrigger;
	
	
	/**
	 * @return the flowListener
	 */
	public FlowEventListener<E> getFlowListener() {
		return flowListener;
	}

	/**
	 * @return the connId
	 */
	public int getConnId() {
		return connId;
	}

	/**
	 * @return the listenId
	 */
	public int getListenId() {
		return listenId;
	}

	public Flow(Date deadline, FlowEventListener<E> flowListener, FlowTrigger flowTrigger){
		this.deadline = deadline;
		this.flowListener = flowListener;
		this.flowTrigger = flowTrigger;
	}
	
	public Flow(Date deadline, FlowEventListener<E> flowListener, int listenId, int connId ){
		this.deadline = deadline;
		this.flowListener = flowListener;
		this.listenId = listenId;
		this.connId = connId;
		this.flowRunnable = new FlowRunnable<E>( );
	}
	
	public void run(){
		final Flow<E> flow = this;
		flowListener.init();
		timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				FlowEvent<E> fe = new FlowEvent<E>( this, FlowEvent.TIME_OUT, new Date(), null, null );
				flowListener._flow( fe );
				flow.stop();
			}
		},  this.deadline);
		
		this.flowRunnable.start(this);

	}
	
	public void stop(){
		timer.cancel();
		flowRunnable.stop();
	}
	
	
	/**
	 * @return the deadline
	 */
	public Date getDeadline() {
		return deadline;
	}


	/**
	 * @param deadline the deadline to set
	 */
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	/**
	 * @param flowTrigger the flowTrigger to set
	 */
	public void setFlowTrigger(FlowTrigger flowTrigger) {
		this.flowTrigger = flowTrigger;
	}

	/**
	 * @return the flowTrigger
	 */
	public FlowTrigger getFlowTrigger() {
		return flowTrigger;
	}
	
	public FlowRunnable<E> getFlowRunnable(){
		return this.flowRunnable;
	}

}
