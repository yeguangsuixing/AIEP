package com.twzcluster.net;

import java.util.EventObject;

public class NewConnEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1882927580681563414L;

	private Conn conn;

	protected NewConnEvent(Object source, Conn conn) {
		super(source);
		this.setConn(conn);
	}

	/**
	 * @param conn
	 *            the conn to set
	 */
	protected void setConn(Conn conn) {
		this.conn = conn;
	}

	/**
	 * @return the conn
	 */
	public Conn getConn() {
		return conn;
	}

}