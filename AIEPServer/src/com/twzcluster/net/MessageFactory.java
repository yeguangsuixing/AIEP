package com.twzcluster.net;

public class MessageFactory {

	private static int ID = 0;

	public static Message createMessage() {
		Message msg = new Message();
		msg.setId(++ID);
		return msg;
	}

	public static Message createMessage(int type) {
		Message msg = new Message();
		msg.setId(++ID);
		msg.setType(type);
		return msg;
	}

	public static Message createMessage(Class<?> c) {
		try {
			Message msg = (Message) c.newInstance();
			msg.setId(++ID);
			return msg;
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}

}
