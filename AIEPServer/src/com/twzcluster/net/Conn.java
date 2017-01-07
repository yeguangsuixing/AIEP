package com.twzcluster.net;

import java.net.Socket;

public class Conn {

	private int serviceId;
	private int connId;
	private String ip, remoteIp;
	private int port, remotePort;

	public Conn(int serviceId, int connId, Socket socket) {
		this.serviceId = serviceId;
		this.connId = connId;

		if (socket == null)
			return;
		this.ip = socket.getLocalAddress().getHostAddress();
		this.port = socket.getLocalPort();
		this.remoteIp = socket.getInetAddress().getHostAddress();
		this.remotePort = socket.getPort();
	}

	public int getServiceId() {
		return serviceId;
	}

	public int getConnId() {
		return connId;
	}

	public String getIp() {
		return ip;
	}

	public String getRemoteIp() {
		return remoteIp;
	}

	public int getPort() {
		return port;
	}

	public int getRemotePort() {
		return remotePort;
	}

	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}

	public void setConnId(int connId) {
		this.connId = connId;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setRemoteIp(String remoteIp) {
		this.remoteIp = remoteIp;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setRemotePort(int remotePort) {
		this.remotePort = remotePort;
	}

}
