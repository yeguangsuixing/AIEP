package com.twzcluster.net;

import java.util.EventListener;

public interface ReceiveListener extends EventListener {
	public void receivedMessage(ReceiveEvent event);
}
