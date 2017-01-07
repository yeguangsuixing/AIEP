package com.njucs.aiep.ai.fir;

import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.plugin.fir.ai.FIR_AI;
import com.njucs.aiep.plugin.fir.frame.Step;

public class MyFirAI implements FIR_AI {//

	/**
	 * <p> The first heuristic evaluation function.  This method will be called when 
	 * the server chooses the "Heriustic1" Item.</p>
	 * <p>This is just a sample.</p>
	 * 
	 * @param opponentStep the last step taked by the opponent
	 * @return the step you will take
	 * @see #itsmyturn2(Step) 
	 * @since AIEPv0.1beta
	 * */
	public Step itsmyturn(Step opponentStep) {
		System.out.println( "\"itsmyturn\" is called by the system." );
		int x = opponentStep.getX();
		int y = opponentStep.getY();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		int a = 0;
		return new Step(null, x, y+12/a);
	}
	/**
	 * <p> The second heuristic evaluation function. This method will be called when 
	 * the Judge chooses the "Heriustic2" Item.</p>
	 * <p>This is just a sample.</p>
	 * 
	 * @param opponentStep the last step taked by the opponent
	 * @return the step you will take
	 * @see #itsmyturn(Step)	  
	 * @since AIEPv0.1beta
	 * */
	public Step itsmyturn2(Step opponentStep) {
		return itsmyturn( opponentStep );
	}

	/**
	 * Do u want to print the receive info? Please return false if not. 
	 * 
	 * @return <code>true</code>, if u want to print the recv info, 
	 * <code>false</code> othersize 
	 * @since AIEPv0.1beta
	 * */
	public boolean isPrintInfo() {
		return true;
	}


	/**
	 * Set the inning info.When the data transmitter receives an info message, it will be called!
	 * 
	 * @param myStatus my status
	 * @param limitedTime the limited time by millisecond
	 * @param opponentInfo the opponent info
	 * @param piecesArray the inning pieces array, length = 15*15 
	 * @since AIEPv0.1beta
	 * @changed AIEPv0.1
	 * */
	@Override
	public void setInningInfo(Status myStatus, int limitedTime, StudentInfo opponentInfo,
			Status[] piecesArray) {
		System.out.println( "\"setInningInfo\" is called by the system." );
	}


	/**
	 *  <p>Return your AI id.</p>
	 * @since AIEPv0.1beta
	 * */
	public String getId() {
		return "b101220000";
	}


	/**
	 *  <p>Return your AI name.</p>
	 * @since AIEPv0.1beta
	 * */
	public String getName() {
		return "AAAA";
	}


	/**
	 *  <p>Return your AI nickname.</p>
	 * @since AIEPv0.1beta
	 * */
	public String getNickname() {
		return "aaaa";
	}
	
	/**
	 *  <p>Return your AI version.</p>
	 * @since AIEPv0.1
	 * */
	public String getVersion() {
		return "v0.1.1";
	}
	
	/**
	 * <p>Return the root of the game tree at the last step. </p>
	 * <p>It will be called when you run the AIEP in the "Site" mode, connecting with the Judge. 
	 * Othersize, it doesn't work if you execute the FIRLoader.</p>
	 * <p>There is ONLY a sample below. Maybe you save the root of the game tree via class member, and
	 * return it in the method.</p>
	 * 
	 * @since AIEPv0.1
	 * */
	public GameTree getLastGameTree() {
		GameTree root = new GameTree( "S", ">=3" );
		GameTree child1 = new GameTree( "A", "<0" );
		GameTree child2 = new GameTree( "B", ">0" );
		root.addChild(child1);
		root.addChild(child2);
		
		/*
		 * be careful here, 
		 * a game tree will be seen as a pruning, whose name is null.
		 * */
		GameTree childc = new GameTree( "CC", "<0" );
		GameTree childd = new GameTree( "D", ">0" );
		child1.addChild(childc);
		child1.addChild(childd);

		GameTree childe = new GameTree( "E", "<0" );
		GameTree childf = new GameTree( "F", ">0" );
		childc.addChild(childe);
		childc.addChild(childf);

		childe = new GameTree( "EG", "<0" );
		childf = new GameTree( "FH", ">0" );
		childd.addChild(childe);
		childd.addChild(childf);
		
		return root;
	}

	

}
