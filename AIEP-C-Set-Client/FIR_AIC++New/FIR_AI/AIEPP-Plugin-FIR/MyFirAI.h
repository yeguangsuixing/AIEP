/**************************************
My FIR AI Head
@author tqc
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/

#ifndef _MY_FIR_AI_H_
#define _MY_FIR_AI_H_

#include "FIR_AI.h"

class MyFirAI : public FIR_AI {
private:
	GameTree* root;
public:
	MyFirAI(){
		
	}
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
	Step* itsmyturn( const Step* lastStep){
		Step *step = new Step(DEFENSIVE, lastStep->getX()+1, lastStep->getY());
		return step;
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
	Step* itsmyturn2(const Step* lastStep){
		return itsmyturn(lastStep);
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
	void setInningInfo( Status myStatus, int limitedTime, 
		const StudentInfo* opponentInfo, const Status piecesArray[]){
		printf( "setInningInfo is called by the system.\n" );
	}

	
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
	 *  <p>Return your AI version.</p>
	 * @since AIEPv0.1
	 * */
	char* getVersion() { return "v0.1"; }


	/**
	 * Do u want to print the receive info? Please return false if not. 
	 * 
	 * @return <code>true</code>, if u want to print the recv info, 
	 * <code>false</code> othersize 
	 * @since AIEPv0.1beta
	 * */
	bool isPrintInfo(){ return true; }
	
	/**
	 * <p>Return the root of the game tree at the last step. </p>
	 * <p>It will be called when you run the AIEP in the "Site" mode, connecting with the Judge. 
	 * Othersize, it doesn't work if you execute the FIRLoader.</p>
	 * <p>There is only a sample below. Maybe you save the root of the game tree via class member, and
	 * return it in the method.</p>
	 * 
	 * @since AIEPv0.1
	 * */
	GameTree* getLastGameTree() {
		GameTree* root = new GameTree( "S", ">=3" );
		GameTree* child1 = new GameTree( "A", "<0" );
		GameTree* child2 = new GameTree( "B", ">0" );
		root->addChild(child1);
		root->addChild(child2);
		
		/*
		 * be careful here, 
		 * a game tree will be seen as a pruning, whose name is NULL.
		 * */
		GameTree* childc = new GameTree( NULL, "<0" );
		GameTree* childd = new GameTree( "D", ">0" );
		child1->addChild(childc);
		child1->addChild(childd);

		GameTree* childe = new GameTree( "E", "<0" );
		GameTree* childf = new GameTree( "F", ">0" );
		childc->addChild(childe);
		childc->addChild(childf);
		
		return root;
	}

};



#endif