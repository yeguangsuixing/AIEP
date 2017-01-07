package com.njucs.aiep.flow;

import java.util.Date;

import org.json.JSONObject;

import com.njucs.aiep.net.DataTransmit;
import com.njucs.aiep.net.DataTransmit.StudentInfo;

/**
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ4ÈÕ12:53:40
 * */
public class FlowRunnable<E> {

	private Flow<E> flow;

	protected FlowRunnable(){
		
	}

	@SuppressWarnings("unchecked")
	protected void start( Flow<E> flow ){
		this.flow = flow;
		if( this.flow != null ) {
			if( this.flow.getFlowTrigger() != null ){
				this.flow.getFlowTrigger().trigger( );
			} else {
				DataTransmit.getInstance().bindFlowRunnable(
						(FlowRunnable<JSONObject>) this, //TODO be careful here
						this.flow.getListenId(), this.flow.getConnId());			
			}
		}
	}
	
	public void run( Date sendTime, StudentInfo info, E data ) {
		flow.stop();
		FlowEvent<E> fe = new FlowEvent<E>( this, FlowEvent.RECEIVE, sendTime, info, data );
		flow.getFlowListener()._flow( fe );
		
	}

	protected void stop() {
		//System.out.println( "unbind the FlowRunnable" );
		if( this.flow != null ) {
			if( this.flow.getFlowTrigger() != null ){
				this.flow.getFlowTrigger().untrigger();
			} else {
				DataTransmit.getInstance().bindFlowRunnable(
						null, this.flow.getListenId(), this.flow.getConnId());			
			}
		}
	}
}
