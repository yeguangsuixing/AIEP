/**************************************
AIEPP FIR Loader Head
@author tqc
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/
#ifndef _AIEPP_FIR_LOADER_H_
#define _AIEPP_FIR_LOADER_H_

#include "DataTransmit/DataTransmit.h"
#include "DataTransmit/AILoader.h"

#include "AIEPP-Plugin-FIR/AIEPP_FIR.h"
#include "AIEPP-Plugin-FIR/FIR_AI.h"
#include "AIEPP-Plugin-FIR/FIRReceiveEventListener.h"

#ifdef _DEBUG
#pragma comment(lib, "lib/Debug/DataTransmit.lib")
#else
#pragma comment(lib, "lib/Release/DataTransmit.lib")
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