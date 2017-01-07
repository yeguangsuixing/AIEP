package com.twzcluster.net;

import java.util.EventListener;

public interface BreakConnListener extends EventListener {
	public void breakConn(BreakEvent event);
}
