package com.njucs.aiep.plugin.fir.frame;

import java.io.Serializable;
import java.rmi.Remote;

public class InningInfo implements Serializable, Remote {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6238660329773867502L;
	
	
	public int offensiveHeuristic = 1, defensiveHeuristic = 1, level = 1;
	public String chessFileName;
}
