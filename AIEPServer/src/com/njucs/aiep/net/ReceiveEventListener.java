package com.njucs.aiep.net;

import java.util.EventListener;

/**
 * 
 * @author ygsx
 * 
 * @time 2013��5��5��11:48:22
 * */
public interface ReceiveEventListener<E extends Object> extends EventListener {
	public void receive( ReceiveEvent<E> event );
}
