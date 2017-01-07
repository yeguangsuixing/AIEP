
#include "FIRLoaderCLR.h"
using namespace AICLR;

FIRLoaderCLR::FIRLoaderCLR(){
	//this->firAI = firAI;
	this->firAICLR = new FIR_AICLR();
	this->firLoader = new FIRLoader( (FIR_AI*)(this->firAICLR) );
}

String^ FIRLoaderCLR::getHostIp(){
	char* cip = this->firLoader->getHostIp();
	String^ sip= System::Runtime::InteropServices::Marshal::PtrToStringAnsi((IntPtr)cip);
	return sip;
}

int FIRLoaderCLR::getHostPort(){
	return this->firLoader->getHostPort();
}

void FIRLoaderCLR::setHostIp(String^ hostIp){
	this->firLoader->setHostIp( (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi( hostIp ) );
}

void FIRLoaderCLR::setHostPort(int hostPort){
	this->firLoader->setHostPort( hostPort );
}

void FIRLoaderCLR::forceExit(){
	this->firLoader->forceExit();
}

void FIRLoaderCLR::execute(){
	this->firLoader->execute();
}
