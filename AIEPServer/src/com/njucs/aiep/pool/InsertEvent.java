package com.njucs.aiep.pool;

import java.util.EventObject;

/**
 * @created 2013Äê6ÔÂ6ÈÕ14:38:00
 * */
public class InsertEvent<E> extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6329991766461322840L;
	private E e;
	
	public InsertEvent(Object source, E element) {
		super(source);
		this.e = element;
	}
	
	public E getInsertElement(){
		return e;
	}

}
