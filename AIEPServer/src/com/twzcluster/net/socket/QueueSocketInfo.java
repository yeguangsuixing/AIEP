package com.twzcluster.net.socket;

import java.net.Socket;
import java.util.AbstractList;
import java.util.ArrayList;

import com.twzcluster.net.Message;

public class QueueSocketInfo extends SocketInfo {
	private AbstractList<Message> messageList = new ArrayList<Message>();

	public QueueSocketInfo(int serviceid, Socket socket, int receiverid) {
		super(serviceid, socket, receiverid);
	}

	public AbstractList<Message> getMessageList() {
		return messageList;
	}

	public void pushMessage(Message message) {
		this.messageList.add(message);
	}

}
