package com.njucs.aiep.pool;

import java.io.Serializable;
import java.util.AbstractList;

public interface ISynchronizedPool<E extends Serializable> extends Serializable {

	public abstract void initialize();

	public abstract void clear();

	/**
	 * create a new info list by the sorted ai info list and return
	 * */
	public abstract AbstractList<E> getSorted();

	public abstract InsertEventListener<E> getInsertListener();

	public abstract CompareOverListener<E> getCompareOverListener();

	public abstract void setCompareEventListener(
			CompareEventListener<E> compareListener);

	public abstract void setLocateEventListener(
			LocateEventListener<E> locateEventListener);

}