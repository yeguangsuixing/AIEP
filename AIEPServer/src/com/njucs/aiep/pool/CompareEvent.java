package com.njucs.aiep.pool;

import java.util.EventObject;

/**
 * @created 2013Äê6ÔÂ6ÈÕ14:38:00
 * */
public class CompareEvent<E> extends EventObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6347674283079035207L;
	
	private int id;
	
	private E e1, e2;
	
	public CompareEvent(Object source, int id, E e1, E e2) {
		super(source);
		this.id = id;
		this.e1 = e1;
		this.e2 = e2;
	}

	/**
	 * @return the e1
	 */
	public E getFirstElement() {
		return e1;
	}
	/**
	 * @return the e2
	 */
	public E getSecondElement() {
		return e2;
	}


	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}


}
