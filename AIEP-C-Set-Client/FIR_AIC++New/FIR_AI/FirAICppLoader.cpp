/**************************************
My FIR AI CPP for Java
@author ygsx
@time 2013Äê6ÔÂ9ÈÕ13:51:56
****************************************/


#include "java\FirAICppLoader.h"
#include "MyFirAI.h"

static MyFirAI myFirAI;

Step* itsmyturn( const Step* lastStep){
	return myFirAI.itsmyturn(lastStep);
}


Step* itsmyturn2( const Step* lastStep){
	return myFirAI.itsmyturn2(lastStep);
}

void setInningInfo( Status myStatus, int limitedTime, 
	StudentInfo* opponentInfo, Status piecesArray[]){
		myFirAI.setInningInfo(myStatus, limitedTime, opponentInfo, piecesArray);
}

char* getId(){
	return myFirAI.getId();
}

char* getName(){
	return myFirAI.getName();
}

char* getNickname(){
	return myFirAI.getNickname();
}

char* getVersion(){
	return myFirAI.getVersion();
}

bool isPrintInfo(){
	return myFirAI.isPrintInfo();
}

GameTree* getLastGameTree(){
	return myFirAI.getLastGameTree();
}