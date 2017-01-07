/**************************************
AIEPP FIR AI Head
@author tqc
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/


#ifndef _FIR_AI_H_
#define _FIR_AI_H_



#include "AIEPP-Plugin-FIR/Step.h"
#include "DataTransmit/AI.h"
#include "DataTransmit/GameTree.h"
#include "DataTransmit/StudentInfo.h"



class DLLEXPORT FIR_AI : public AI {
public:
	
	virtual Step* itsmyturn( const Step*  lastStep) = 0;
	virtual Step* itsmyturn2( const Step*  opponentStep) = 0;
	
	
	virtual void setInningInfo( Status myStatus, int limitedTime, 
		 StudentInfo* opponentInfo,  Status piecesArray[]) = 0;
	
	virtual GameTree* getLastGameTree() = 0;
};








#endif