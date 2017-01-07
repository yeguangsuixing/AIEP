package com.njucs.aiep.net.file;

import java.util.EventListener;

public interface ReceiveFileEventListener extends EventListener{
	public void receiveFile( ReceiveFileEvent event );
}
