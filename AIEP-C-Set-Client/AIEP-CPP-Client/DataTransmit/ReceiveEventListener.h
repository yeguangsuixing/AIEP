/**************************************
Receive Event Listener class
@author tqc
@time 2013Äê5ÔÂ8ÈÕ21:03:56
****************************************/


#ifndef _RECEIVE_EVENT_LISTENER_H_
#define _RECEIVE_EVENT_LISTENER_H_

#include "std.h"
#include "ReceiveEvent.h"


class DLL_EXPORT ReceiveEventListener {
public:
	virtual void receive(ReceiveEvent* e) = 0;
};


#endif //_RECEIVE_EVENT_LISTENER_H_