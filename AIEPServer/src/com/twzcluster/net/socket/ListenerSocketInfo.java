package com.twzcluster.net.socket;

import java.net.Socket;

import com.twzcluster.net.ReceiveListener;

public class ListenerSocketInfo extends SocketInfo {
	private ReceiveListener listener;

	public ListenerSocketInfo(int serviceid, Socket socket, int receiverid,
			ReceiveListener listener) {
		super(serviceid, socket, receiverid);
		this.listener = listener;
	}

	public ReceiveListener getListener() {
		return listener;
	}

}
