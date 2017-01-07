/**************************************
AIEPP FIR Client Head
@author tqc
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/

#ifndef _FIR_RECEIVE_EVENT_LISTENER_H_
#define _FIR_RECEIVE_EVENT_LISTENER_H_

#include "DataTransmit/DataTransmit.h"
#include "DataTransmit/AILoader.h"
#include "DataTransmit/ReceiveEventListener.h"

#include "json/json.h"
#include "AIEPP-Plugin-FIR/AIEPP_FIR.h"
#include "AIEPP-Plugin-FIR/FIR_AI.h"



class DLLEXPORT FIRReceiveEventListener : public ReceiveEventListener {
private:
	Json::Reader read;
	Status status;
	Status opponentStatus;
	StudentInfo *studentInfo, *opponentInfo;
	int limitedTime;
	FIR_AI* firAI;
	bool isPrintErrorInfo;
	Status piecesArray[AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION];
	int heuristic;
	AILoader<FIR_AI>* aiLoader;
private:
	void handleInfo(Json::Value value);
	void handleRunning(Json::Value value);
	void hanleGameOver(Json::Value value);
public:
	FIRReceiveEventListener(AILoader<FIR_AI>* aiLoader, FIR_AI* firAI, StudentInfo *studentInfo){
		this->aiLoader = aiLoader;
		this->firAI = firAI;
		this->studentInfo = studentInfo;
		this->isPrintErrorInfo = false;
		this->opponentInfo = NULL;
		//this->piecesArray = NULL;
	}
	~FIRReceiveEventListener(){ clear(); }
	void receive(ReceiveEvent* e);
	void clear();
};











#endif