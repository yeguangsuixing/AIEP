/**************************************
My FIR AI CPP
@author 
@time
****************************************/


#include "MyFirAI.h"

Step* MyFirAI::itsmyturn( const Step* lastStep){
	printf( "\"itsmyturn\" is called by the system.\n" );
	myStep = Step(DEFENSIVE, lastStep->getX()+1, lastStep->getY());
	return &myStep;
}

Step* MyFirAI::itsmyturn2(const Step* lastStep){
	return itsmyturn(lastStep);
}

void MyFirAI::setInningInfo( Status myStatus, int limitedTime, 
	  StudentInfo* opponentInfo, Status piecesArray[]){
		printf( "\"setInningInfo\" is called by the system.\n" );
}

GameTree* MyFirAI::getLastGameTree() {
	GameTree* root = new GameTree( "S", ">=3" );
	root->removeAllChildren();
	root->setName( "S" );
	root->setValue( ">=3" );
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