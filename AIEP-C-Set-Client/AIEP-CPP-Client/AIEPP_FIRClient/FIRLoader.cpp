/**************************************
AIEPP FIR Client cpp
@author tqc
@time 2013年5月9日20:51:37
****************************************/

#include "FIRLoader.h"
#include "MyFirAI.h"
#include <windows.h>

//

FIRLoader::FIRLoader(FIR_AI* firAI) {
	this->loaderAI = firAI;
	this->studentInfo = NULL;
	this->offensiveInfo = NULL;
	this->recvListener = NULL;
	//assert(  );
	if( this->loaderAI != NULL ){
		this->studentInfo = 
			new StudentInfo( loaderAI->getId(), loaderAI->getName(), loaderAI->getNickname() );
		this->recvListener = new FIRReceiveEventListener(this, this->loaderAI, this->studentInfo);
	}
}

FIRLoader::~FIRLoader(){
	forceExit();
}


bool FIRLoader::execute() {
	DataTransmit::getInstance()->initialize();
	this->connId = DataTransmit::getInstance()->createConnection(
			this->hostIp,// hostIp
			this->hostPort,// hostPort
			this->studentInfo, // student info
			this->recvListener);
	if( this->connId > 0 ) {
		//printf( "来自#4d-this->connId=%d\n",this->connId );
		//Sleep(999999999);while(true);
		return true;
	} else if ( this->connId == DataTransmit::SERV_REJECT ){
		printf("Connecting failed(%s:%d)! The host rejects the connecting request!\n", this->hostIp, this->hostPort );
	}  else if ( this->connId == DataTransmit::CONN_TIMEOUT ){
		printf( "Connecting failed(%s:%d)!  Time is running out!\n", this->hostIp, this->hostPort );
	} else {
		printf( "Conn Id = %d.\n", connId );
		printf( "Connecting failed(%s:%d)! An unknown fatal error occurs!\n", this->hostIp, this->hostPort );
	}
	return false;
}

void FIRLoader::forceExit(){
	if( offensiveInfo != NULL ){
		delete offensiveInfo;
		offensiveInfo = NULL;
	}
	if( this->studentInfo != NULL ){
		delete (this->studentInfo);
		this->studentInfo = NULL;
	}
	if( this->recvListener != NULL ){
		delete (this->recvListener);
		this->recvListener = NULL;
	}
	//close the connid
}