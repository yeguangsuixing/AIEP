package com.twzcluster.net;

import java.net.ServerSocket;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Vector;

public interface NewConnListener extends EventListener {
	public void newConn(NewConnEvent event);
}

class ServerSocketDoor {
	private Vector<ReceiveListener> repository = new Vector<ReceiveListener>();
	private ServerSocket serverSocket;

	public ServerSocketDoor(ServerSocket serverSocket) {
		this.serverSocket = serverSocket;
	}

	public void addReceiveListener(ReceiveListener listener) {
		repository.add(listener);
	}

	public void removeReceiveListener(ReceiveListener listener) {
		repository.remove(listener);
	}

	public void notifyReceiveEvent(ReceiveEvent receiveEvent) {
		Enumeration<ReceiveListener> enum1 = repository.elements();
		while (enum1.hasMoreElements()) {
			ReceiveListener dl = (ReceiveListener) enum1.nextElement();
			dl.receivedMessage(receiveEvent);
		}
	}

	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}

}
