package com.twzcluster.net;

public class BreakEvent {

	private Conn conn;
	private String breakMessage;

	protected BreakEvent(Conn conn, String breakMessage) {
		this.setConn(conn);
		this.breakMessage = breakMessage;
	}

	public String getBreakMessage() {
		return breakMessage;
	}

	protected void setBreakMessage(String breakMessage) {
		this.breakMessage = breakMessage;
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
