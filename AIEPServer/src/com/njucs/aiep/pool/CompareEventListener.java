package com.njucs.aiep.pool;

import java.util.EventListener;

/**
 * @created 2013��6��6��14:38:00
 * */
public interface CompareEventListener<E> extends EventListener {
	public void compare(CompareEvent<E> event);
}
