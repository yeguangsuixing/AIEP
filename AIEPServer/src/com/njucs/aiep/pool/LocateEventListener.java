package com.njucs.aiep.pool;

import java.util.EventListener;

/**
 * @created 2013Äê6ÔÂ6ÈÕ14:38:00
 * */
public interface LocateEventListener<E> extends EventListener  {
	public void locate( LocateEvent<E> locateEvent );
}
