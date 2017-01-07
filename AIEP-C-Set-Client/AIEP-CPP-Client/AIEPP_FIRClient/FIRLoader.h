/**************************************
AIEPP FIR Loader Head
@author tqc
@time 2013��5��9��20:51:37
****************************************/
#ifndef _AIEPP_FIR_LOADER_H_
#define _AIEPP_FIR_LOADER_H_

#include "../DataTransmit/DataTransmit.h"
#include "../DataTransmit/AILoader.h"

#include "AIEPP_FIR.h"
#include "FIR_AI.h"
#include "FIRReceiveEventListener.h"
//#define _DEBUG

#ifdef _DEBUG
#pragma comment(lib, "../Debug/DataTransmit.lib")
#else
#pragma comment(lib, "../Release/DataTransmit.lib")
#endif


class DLLEXPORT FIRLoader: public AILoader<FIR_AI> {

private:
	StudentInfo* studentInfo;
	StudentInfo* offensiveInfo;
	int limitedTime;
	Status status;
	
	int connId;


	FIRReceiveEventListener* recvListener;
public:
	FIRLoader(FIR_AI* firAI);
	~FIRLoader();

	bool execute();
	void forceExit();
};

#endif//_AIEPP_FIR_LOADER_H_