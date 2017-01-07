/**************************************
Student Info cpp
@author tqc
@time 2013Äê5ÔÂ9ÈÕ17:51:25
****************************************/

#ifndef _STUDENT_INFO_H_
#define _STUDENT_INFO_H_

#include "std.h"
#include <iostream>


class DLL_EXPORT StudentInfo {
private:
	const static int MAX_LEN = 20;
	private:
		char id[MAX_LEN];
		char name[MAX_LEN];
		char nickname[MAX_LEN];
		int connectionId;
	private:
		StudentInfo(){connectionId=0;/*do nothing*/};
	public:
		StudentInfo(char* id, char* name, char* nickname){
			this->setId(id);
			this->setName(name);
			this->setNickname(nickname);
			this->connectionId = 0;
		};
		~StudentInfo(){/*do nothing*/};
		char* getId()  { return this->id; };
		char* getName()  { return this->name; };
		char* getNickname()  { return this->nickname; };
		int getConnetionId(){ return this->connectionId; };
		void setId(char* id){ strncpy_s(this->id, id, MAX_LEN); };
		void setName(char* name){strncpy_s(this->name, name,MAX_LEN);};
		void setNickname(char* nickname){strncpy_s(this->nickname,nickname,MAX_LEN);};
	protected:
		friend class DataTransmit;//for accessing the function 'setConnectionId'
		void setConnectionId( int connId ){ this->connectionId = connId; };
	};




#endif