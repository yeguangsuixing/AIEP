/**************************************
Receive Event class
@author tqc
@time 2013Äê5ÔÂ8ÈÕ21:03:25
****************************************/

#ifndef _RECEIVE_EVENT_H_
#define _RECEIVE_EVENT_H_

#include "std.h"
#include "StudentInfo.h"
using namespace std;



class DLL_EXPORT ReceiveEvent {
public:
	static enum RecvEventType { COMMAND, DATA };
	static enum RecvCmdType { RESTART };
private:
	StudentInfo* studentInfo;
	char* sendTime;
	char* data;
	RecvEventType eventType;
	RecvCmdType cmdType;
public:
	ReceiveEvent( StudentInfo* studentInfo,char* sendTime, char* data ){
		this->studentInfo = studentInfo;
		this->sendTime = sendTime;
		this->data = data;
		this->eventType = RecvEventType::DATA;
	};
	ReceiveEvent( StudentInfo* studentInfo,char* sendTime, RecvCmdType cmdType ){
		this->studentInfo = studentInfo;
		this->sendTime = sendTime;
		this->cmdType = cmdType;
		this->eventType = RecvEventType::COMMAND;
	}
	~ReceiveEvent(){/*do nothing*/};


	StudentInfo* getStudentInfo(){
		return this->studentInfo;
	}

	char* getSendTime(){
		return this->sendTime;
	}

	char*  getData(){
		return this->data;
	}
	
	/**
	 * @return the type
	 */
	RecvEventType getEventType() {
		return eventType;
	}
	/**
	 * @return the cmdType
	 */
	RecvCmdType getCmdType() {
		return cmdType;
	}
};



#endif //_RECEIVE_EVENT_H_