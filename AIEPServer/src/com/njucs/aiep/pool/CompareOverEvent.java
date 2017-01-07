package com.njucs.aiep.pool;

import java.util.EventObject;

/**
 * @created 2013Äê6ÔÂ6ÈÕ14:38:00
 * */
public class CompareOverEvent<E> extends EventObject {
	
	public static enum CompareResult { GREATER, NOT_GREATER };
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2861774082605812421L;
	
	
	private CompareEvent<E> compareEvent;
	private CompareResult compareResult;
	
	
	public CompareOverEvent(Object source, CompareEvent<E> compareEvent, CompareResult result ) {
		super(source);
		this.compareEvent = compareEvent;
		this.compareResult = result;
	}

	/**
	 * @param compareEvent the compareEvent to set
	 */
	public void setCompareEvent(CompareEvent<E> compareEvent) {
		this.compareEvent = compareEvent;
	}

	/**
	 * @return the compareEvent
	 */
	public CompareEvent<E> getCompareEvent() {
		return compareEvent;
	}

	/**
	 * @return the compareResult
	 */
	public CompareResult getCompareResult() {
		return compareResult;
	}
	
	
	
}
