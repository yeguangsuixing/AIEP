/**************************************
 AI Loader Head
@author tqc
@time 2013Äê6ÔÂ9ÈÕ0:38:56
****************************************/


#ifndef _AI_LOADER_H_
#define _AI_LOADER_H_

template<class E_AI>
class AILoader {

protected:
	const static int MAX_LEN = 20;
	
	const static int DEFAULT_PORT = 8890;
	const static char DEFAULT_IP[MAX_LEN];
	char hostIp[MAX_LEN];
	int hostPort;

	E_AI* loaderAI;

public:
	AILoader(){
		hostPort = 8890;
	}
	char* getHostIp(){
		return this->hostIp;
	}
	int getHostPort(){
		return this->hostPort;
	}
	void setHostIp(char* ip){
		strncpy_s( this->hostIp, ip, MAX_LEN );
	}
	void setHostPort(int port){
		this->hostPort = port;
	}
	virtual void forceExit() = 0;
	virtual bool execute() = 0;
};

#endif//_AI_LOADER_H_