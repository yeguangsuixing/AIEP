package com.njucs.aiep.net;

import java.util.EventListener;

/**
 * 
 * @author ygsx
 * 
 * @time 2013��5��5��11:37:37
 * */
public interface NewConnListener extends EventListener {
	public void newConn( NewConnEvent e );
}
