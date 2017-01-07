package com.twzcluster.net.socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.twzcluster.net.Conn;

public class SocketInfo {
	private Conn conn;
	private ObjectOutputStream objectOutputStream;
	private ObjectInputStream objectInputStream;

	public SocketInfo(int serviceId, Socket socket, int connId) {
		conn = new Conn(serviceId, connId, socket);
		if (socket == null)
			return;
		try {
			this.objectOutputStream = new ObjectOutputStream(
					socket.getOutputStream());
			this.objectInputStream = new ObjectInputStream(
					socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ObjectOutputStream getObjectOutputStream() {
		return this.objectOutputStream;
	}

	public ObjectInputStream getObjectInputStream() {
		return this.objectInputStream;
	}

	/**
	 * @param conn
	 *            the conn to set
	 */
	public void setConn(Conn conn) {
		this.conn = conn;
	}

	/**
	 * @return the conn
	 */
	public Conn getConn() {
		return conn;
	}

}
