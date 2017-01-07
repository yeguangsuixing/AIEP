/**************************************
My FIR AI Head
@author 
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/

#ifndef _MY_FIR_AI_H_
#define _MY_FIR_AI_H_

#include "AIEPP-Plugin-FIR/FIR_AI.h"

class MyFirAI : public FIR_AI {
private:

	Step myStep;
public:
	MyFirAI(){ }
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
	Step* itsmyturn( const Step* lastStep);
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
	Step* itsmyturn2(const Step* lastStep);
	
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
	void setInningInfo( Status myStatus, int limitedTime, 
		 StudentInfo* opponentInfo, Status piecesArray[]);

	
	/**
	 *  <p>Return your AI id.</p>
	 * @since AIEPv0.1beta
	 * */
	char* getId() { return "b101220001"; }
	
	/**
	 *  <p>Return your AI name.</p>
	 * @since AIEPv0.1beta
	 * */
	char* getName(){ return "BBB"; }
	
	/**
	 *  <p>Return your AI nickname.</p>
	 * @since AIEPv0.1beta
	 * */
	char* getNickname(){ return "bbb"; }

	/**
	 * Do u want to print the receive info? Please return false if not. 
	 * 
	 * @return <code>true</code>, if u want to print the recv info, 
	 * <code>false</code> othersize 
	 * @since AIEPv0.1beta
	 * */
	bool isPrintInfo(){ return true; }
	
	
	/**
	 *  <p>Return your AI version.</p>
	 * @since AIEPv0.1
	 * */
	char* getVersion() { return "v0.1"; }


	/**
	 * <p>Return the root of the game tree at the last step. </p>
	 * <p>It will be called when you run the AIEP in the "Site" mode, connecting with the Judge. 
	 * Othersize, it doesn't work if you execute the FIRLoader.</p>
	 * <p>There is ONLY a sample below. Maybe you save the root of the game tree via class member, and
	 * return it in the method.</p>
	 * 
	 * @since AIEPv0.1
	 * */
	GameTree* getLastGameTree();

};



#endif