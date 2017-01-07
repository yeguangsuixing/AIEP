package com.njucs.aiep.pool;

import java.util.EventListener;

/**
 * @created 2013Äê6ÔÂ6ÈÕ14:38:00
 * */
public interface CompareOverListener<E> extends EventListener {
	public void compareOver(CompareOverEvent<E> event);
}
