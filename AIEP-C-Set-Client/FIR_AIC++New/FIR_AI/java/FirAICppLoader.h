


#ifndef _FIR_AI_CPP_
#define _FIR_AI_CPP_

#include "../MyFirAI.h"
#include "../DataTransmit/std.h"

#ifdef __cplusplus
extern "C" {
#endif
	
	DLL_EXPORT Step* itsmyturn( const Step* lastStep);
	DLL_EXPORT Step* itsmyturn2( const Step* lastStep);
	
	DLL_EXPORT void setInningInfo( Status myStatus, int limitedTime, 
		 StudentInfo* opponentInfo, Status piecesArray[]);

	DLL_EXPORT char* getId();
	DLL_EXPORT char* getName();
	DLL_EXPORT char* getNickname();
	DLL_EXPORT bool isPrintInfo();
	DLL_EXPORT char* getVersion();

	DLL_EXPORT GameTree* getLastGameTree();

#ifdef __cplusplus
}
#endif
#endif//_FIR_AI_CPP_
