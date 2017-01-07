package com.njucs.aiep.flow;

import java.util.EventListener;

/**
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ4ÈÕ12:50:52
 * */
public abstract class FlowEventListener<E> implements EventListener {
	
	private boolean _synchronized;
	
	protected synchronized void init(){
		 _synchronized = false;
	}
	
	protected synchronized void _flow( FlowEvent<E> event ) {
		//System.out.println( "call _flow" );
		if( ! _synchronized ){
			_synchronized = true;
			flow( event );
		}
	}
	public abstract void flow( FlowEvent<E> event );
}
