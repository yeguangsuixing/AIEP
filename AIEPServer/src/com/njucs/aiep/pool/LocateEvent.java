package com.njucs.aiep.pool;

import java.util.EventObject;

/**
 * @created 2013Äê6ÔÂ6ÈÕ14:38:00
 * */
public class LocateEvent<E> extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8540890776299551119L;
	
	private E locateElement;
	
	public LocateEvent(Object source, E locateElement) {
		super(source);
		this.locateElement = locateElement;
	}

	/**
	 * @return the locateElement
	 */
	public E getLocateElement() {
		return locateElement;
	}

	
}
