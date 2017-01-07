package com.njucs.aiep.pool;

import java.util.EventListener;

/**
 * @created 2013Äê6ÔÂ6ÈÕ14:38:00
 * */
public interface CompareEventListener<E> extends EventListener {
	public void compare(CompareEvent<E> event);
}
