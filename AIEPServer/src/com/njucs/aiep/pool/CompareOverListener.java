package com.njucs.aiep.pool;

import java.util.EventListener;

/**
 * @created 2013��6��6��14:38:00
 * */
public interface CompareOverListener<E> extends EventListener {
	public void compareOver(CompareOverEvent<E> event);
}
