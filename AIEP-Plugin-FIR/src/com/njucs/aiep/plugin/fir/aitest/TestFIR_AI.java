package com.njucs.aiep.plugin.fir.aitest;

import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.plugin.fir.ai.FIR_AI;
import com.njucs.aiep.plugin.fir.frame.Step;

public  class TestFIR_AI implements FIR_AI {//  abstract

	/**
	 * <p> The first heuristic evaluation function.  This method will be called when 
	 * the server chooses the "Heriustic1" Item.</p>
	 * <p>This is just a sample.</p>
	 * 
	 * @param opponentStep the last step taked by the opponent
	 * @return the step you will take
	 * @see #itsmyturn2(Step)
	 * */
	@Override
	public Step itsmyturn(Step opponentStep) {
		int x = opponentStep.getX();
		int y = opponentStep.getY();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}//System.out.println("here");
		return new Step( x+1, y);
		
	}
	/**
	 * <p> The second heuristic evaluation function. This method will be called when 
	 * the server chooses the "Heriustic2" Item.</p>
	 * <p>This is just a sample.</p>
	 * 
	 * @param opponentStep the last step taked by the opponent
	 * @return the step you will take
	 * @see #itsmyturn(Step)
	 * */
	@Override
	public Step itsmyturn2(Step opponentStep) {
		return itsmyturn( opponentStep );
	}

	/**
	 * Do u want to print the receive info? Please return false if not. 
	 * 
	 * @return <code>true</code>, if u want to print the recv info, 
	 * <code>false</code> othersize
	 * */
	@Override
	public boolean isPrintInfo() {
		return true;
	}


	/**
	 * Set the innings info.
	 * 
	 * @param myStatus my status
	 * @param limitedTime the limited time
	 * @param opponentInfo the opponent info
	 * @param piecesArray the innings pieces array, length = 15*15
	 * */
	@Override
	public void setInningInfo(Status myStatus, int limitedTime, StudentInfo opponentInfo,
			Status[] piecesArray) {
		//System.out.println( "setInningsInfo" );
	}

	
	@Override
	public GameTree getLastGameTree() {
		GameTree root = new GameTree( "S", ">=3" );
		GameTree child1 = new GameTree( "A", "<0" );
		GameTree child2 = new GameTree( "B", ">0" );
		root.addChild(child1);
		root.addChild(child2);

		GameTree childc = new GameTree( null, "<0" );
		GameTree childd = new GameTree( "D", ">0" );
		child1.addChild(childc);
		child1.addChild(childd);

		GameTree childe = new GameTree( "E", "<0" );
		GameTree childf = new GameTree( "F", ">0" );
		childc.addChild(childe);
		childc.addChild(childf);
		
		return root;
	}


	@Override
	public String getId() {
		return "b101220000";
	}


	@Override
	public String getName() {
		return "AAA";
	}


	@Override
	public String getNickname() {
		return "aaa";
	}
	@Override
	public String getVersion() {
		return "v0.123";
	}


}
