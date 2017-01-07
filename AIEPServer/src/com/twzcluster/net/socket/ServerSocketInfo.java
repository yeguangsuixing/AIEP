package com.twzcluster.net.socket;

import java.net.ServerSocket;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.EventListener;

import com.twzcluster.net.BreakConnListener;

public class ServerSocketInfo {

	public static final int QUEUE = 0;
	public static final int LISTENER = 1;
	private int type = QUEUE;
	private EventListener eventListener, breakListener;

	private int serviceId;
	private ServerSocket serverSocket;
	private AbstractList<SocketInfo> socketInfoList = new ArrayList<SocketInfo>();

	public ServerSocketInfo(int serviceId, ServerSocket serverSocket) {
		this.serviceId = serviceId;
		this.serverSocket = serverSocket;
	}

	public ServerSocketInfo(int serviceid, ServerSocket serverSocket,
			EventListener eventListener, BreakConnListener breakListener) {
		this(serviceid, serverSocket);
		if (eventListener == null)
			return;
		this.type = LISTENER;
		this.eventListener = eventListener;
		this.setBreakListener(breakListener);
	}

	public int getServiceId() {
		return serviceId;
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}

	public AbstractList<SocketInfo> getSocketList() {
		return socketInfoList;
	}

	public int getType() {
		return type;
	}

	public EventListener getEventListener() {
		return eventListener;
	}

	/**
	 * @param breakListener
	 *            the breakListener to set
	 */
	public void setBreakListener(EventListener breakListener) {
		this.breakListener = breakListener;
	}

	/**
	 * @return the breakListener
	 */
	public EventListener getBreakListener() {
		return breakListener;
	}

}
