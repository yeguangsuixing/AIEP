package com.twzcluster.net;

import java.util.EventObject;

public class ReceiveEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1194157364049798742L;

	private Conn conn;
	private Message message;

	public ReceiveEvent(Object source, Conn conn, Message message) {
		super(source);
		this.conn = conn;
		this.message = message;
	}

	public Message getMessage() {
		return this.message;
	}

	/**
	 * @return the conn
	 */
	public Conn getConn() {
		return conn;
	}

}