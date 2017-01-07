
//2013Äê7ÔÂ5ÈÕ19:39:43
#include "FIR_AICLR.h"
using namespace AICLR;

FIR_AICLR::FIR_AICLR(){
	//this->firAI =  firAI;
}


char* FIR_AICLR::getId(){
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	return (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi( firAI->getId() );
}
char* FIR_AICLR::getName(){
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	return (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi( firAI->getName() );
}
char* FIR_AICLR::getNickname(){
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	return (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi( firAI->getNickname() );
}
char* FIR_AICLR::getVersion(){
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	return (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi( firAI->getVersion() );
}
bool FIR_AICLR::isPrintInfo(){
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	return firAI->isPrintInfo();
}



Step* FIR_AICLR::itsmyturn( const Step*  lastStep){
	//printf( "itsmyturn.step = (%d, %d)", lastStep->getX(), lastStep->getY()  );
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	AICS::Status status = AICS::Status::EMPTY;
	AICS::Step^ tlastStep = gcnew AICS::Step( status, lastStep->getX(), lastStep->getY() );
	AICS::Step^ tnewStep = firAI->itsmyturn( tlastStep );
	return new Step( Status::EMPTY, tnewStep->getX(), tnewStep->getY() );
}
Step* FIR_AICLR::itsmyturn2( const Step*  lastStep){
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	AICS::Status status = AICS::Status::EMPTY;
	AICS::Step^ tlastStep = gcnew AICS::Step( status, lastStep->getX(), lastStep->getY() );
	AICS::Step^ tnewStep = firAI->itsmyturn2( tlastStep );
	return new Step( Status::EMPTY, tnewStep->getX(), tnewStep->getY() );
}
	
	
void FIR_AICLR::setInningInfo( Status myStatus, int limitedTime, 
	StudentInfo* opponentInfo,  Status piecesArray[]){
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	AICS::Status mystatus = AICS::Status::EMPTY;
	if( myStatus == Status::OFFENSIVE ) mystatus =  AICS::Status::OFFENSIVE;
	else if( myStatus == Status::DEFENSIVE ) mystatus =  AICS::Status::DEFENSIVE;
	AICS::StudentInfo^ stdinfo = gcnew AICS::StudentInfo(
		Marshal::PtrToStringAnsi((IntPtr)( opponentInfo->getId() )),
		Marshal::PtrToStringAnsi((IntPtr)( opponentInfo->getName() )),
		Marshal::PtrToStringAnsi((IntPtr)( opponentInfo->getNickname() ))
	);
	cli::array<AICS::Status, 1>^ piecesarray = gcnew cli::array<AICS::Status, 1>( AIEPP_FIR::DIMENSION * AIEPP_FIR::DIMENSION );
	for( int i = 0; i < AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION; i ++ ){
		piecesarray[i] = AICS::Status::EMPTY;
		if( piecesArray[i] == Status::OFFENSIVE ) piecesarray[i] =  AICS::Status::OFFENSIVE;
		else if( piecesArray[i] == Status::DEFENSIVE ) piecesarray[i] =  AICS::Status::DEFENSIVE;
	}
	firAI->setInningInfo( mystatus, limitedTime, stdinfo, piecesarray );
}

GameTree* FIR_AICLR::getLastGameTree(){
	AICS::FIR_AI^ firAI = (gcnew AICS::FIR_AICSGenerator())->getFIR_AI();
	return NULL;
}