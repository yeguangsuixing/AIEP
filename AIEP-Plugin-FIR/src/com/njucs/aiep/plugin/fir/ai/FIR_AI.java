package com.njucs.aiep.plugin.fir.ai;

import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.plugin.AI;
import com.njucs.aiep.plugin.fir.frame.Step;


/**
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ5ÈÕ23:59:26
 * */
public interface FIR_AI extends AI {

	public Step itsmyturn( Step opponentStep );
	public Step itsmyturn2( Step opponentStep );
	
	public void setInningInfo( Status myStatus, int limitedTime, 
			StudentInfo opponentInfo, final Status[] piecesArray);
	
	public GameTree getLastGameTree();
	
}
