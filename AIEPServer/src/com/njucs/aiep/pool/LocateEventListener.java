package com.njucs.aiep.pool;

import java.util.EventListener;

/**
 * @created 2013��6��6��14:38:00
 * */
public interface LocateEventListener<E> extends EventListener  {
	public void locate( LocateEvent<E> locateEvent );
}
