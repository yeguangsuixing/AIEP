/**************************************
AIEPP FIR AI Test
@author tqc
@time 2013Äê6ÔÂ9ÈÕ3:58:56
****************************************/

#include "AIEPP-Plugin-FIR/FIRLoader.h"
#include "DataTransmit/StudentInfo.h"
#include "MyFirAI.h"

#ifdef _DEBUG
#pragma comment(lib, "lib/Debug/AIEPP-Plugin-FIR.lib")
#else
#pragma comment(lib, "lib/Release/AIEPP-Plugin-FIR.lib")
#endif

#ifdef _WINDLL
#else



void main(){
	MyFirAI* ai = new MyFirAI();
	AILoader<FIR_AI>* client = new FIRLoader( ai );
	client->setHostIp("127.0.0.1"); 
	client->setHostPort( 8890 );
	client->execute();
	Sleep(999999999);while(true);
}

#endif