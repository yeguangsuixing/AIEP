/**************************************
AIEPP FIR Receive Listener CPP
@author tqc
@time 2013Äê5ÔÂ9ÈÕ20:51:37
****************************************/
#include "FIRLoader.h"
#include "FIRReceiveEventListener.h"

#include <iostream>
#include <sys/timeb.h>

#include "Step.h"

void FIRReceiveEventListener::receive(ReceiveEvent* e){
	//cout << "client recv: " << e->getData() << endl;
	if( this->firAI == NULL ) {
		if( isPrintErrorInfo == false ) {
			printf( "FIR_AI is null! Did you remember setting it?\n" );
			isPrintErrorInfo = true;
		}
		return;
	}
	
	if( e->getEventType() == ReceiveEvent::RecvEventType::DATA ){
		Json::Value value;
		if( ! read.parse( e->getData(),value ) ){ printf( "parse failed!\n" ); return; }
		if( ! value[ AIEPP_FIR::TAG_TYPE ].isNull() ){
			int type = value[ AIEPP_FIR::TAG_TYPE ].asInt();
			if( type == ACROSS_INFO ){
				this->handleInfo( value );
			} else if( type == RUNNING ){
				this->handleRunning( value );
			} else if( type == GAME_OVER ){
				this->hanleGameOver( value );
			} else {
				//ignore
			}
		} else {
			//printf( "The data received is dirty! Ignore it! \n" );
		}
	} else if( e->getEventType() == ReceiveEvent::RecvEventType::COMMAND ){
		if( e->getCmdType() == ReceiveEvent::RecvCmdType::RESTART ){
			printf("The server requests to restart! \n");
			printf( "Wait for the Server restarting in 1 seconds!\n" );
			Sleep(1000);
			//this->aiLoader->forceExit();
			DataTransmit::getInstance()->closeConnection( this->studentInfo->getConnetionId() );
			this->aiLoader->execute();
		} else {
			//printf("unknown cmd!\n");//ignore
		}
	} else {
		//printf("unknown event!\n");//ignore
	}
	return;
}


void FIRReceiveEventListener::handleInfo(Json::Value value){
	if( (this->firAI)->isPrintInfo() ) {				
		printf( "Received the info of the opponent.\n" );
	} else {
		//printf( "~Received the info of the opponent.\n" );
	}
	clear();
	const char* id = value[ AIEPP_FIR::TAG_ACROSS_ID ].asCString();
	const char* name =  value[ AIEPP_FIR::TAG_ACROSS_NAME ].asCString();
	const char* nickname =  value[ AIEPP_FIR::TAG_ACROSS_NICKNAME ].asCString();
	
	char tid[AIEPP_FIR::MAX_LEN];
	char tname[AIEPP_FIR::MAX_LEN];
	char tnick[AIEPP_FIR::MAX_LEN];
	strncpy_s( tid, id, AIEPP_FIR::MAX_LEN);
	strncpy_s( tname, name, AIEPP_FIR::MAX_LEN);
	strncpy_s( tnick, nickname, AIEPP_FIR::MAX_LEN);
	this->opponentInfo = new StudentInfo(tid, tname, tnick);

	this->limitedTime = value[ AIEPP_FIR::TAG_TIME ].asInt();
	this->heuristic = value[ AIEPP_FIR::TAG_HEURISTIC ].asInt();

	if ( value[ AIEPP_FIR::TAG_OFFENSIVE ].asInt() == 1 ) {
		this->status = OFFENSIVE;
		this->opponentStatus = DEFENSIVE;
	} else {
		this->status = DEFENSIVE;
		this->opponentStatus = OFFENSIVE;
	}
	if( ! value[AIEPP_FIR::TAG_CHESS].isNull() ){
		const char* pieces = value[AIEPP_FIR::TAG_CHESS].asCString();
		//char tpieces[AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION*2+5];//+1 is enough
		//strncpy_s( tpieces, pieces, 
		//	AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION*2+5);
		//this->piecesArray = new int[AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION];

		int count = 0, c = 0;
		while( count < AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION ){
			if( pieces[c] == '0' ){
				this->piecesArray[count++] = EMPTY;
				c+=2;
			} else if( pieces[c] == '1' ){
				this->piecesArray[count++] = OFFENSIVE;
				c+=2;
			} else if( pieces[c] == '-' ){//-1
				this->piecesArray[count++] = DEFENSIVE;
				c+=3;
			} else {
				printf( "Some error occurs! \n" ); return;
			}
		}
	}
	this->firAI->setInningInfo(this->status, this->limitedTime,
		this->opponentInfo, this->piecesArray);
}


void FIRReceiveEventListener::handleRunning(Json::Value value){
	//printf( "running!\n" );
	int x = 0, y = 0;
	if( ! value[AIEPP_FIR::TAG_POSITION_X].isNull() ){
		x = value[ AIEPP_FIR::TAG_POSITION_X ].asInt();
		y = value[ AIEPP_FIR::TAG_POSITION_Y ].asInt();
	}
	if( (this->firAI)->isPrintInfo() ) {	
		printf( "Located at (%d, %d).\n", x, y );
	}

	Step *lastStep = new Step( this->opponentStatus, x, y );
	timeb begin, end;
	ftime(&begin);
	Step* newStep;
	if( heuristic == 1 ){
		newStep = this->firAI->itsmyturn(lastStep);
	} else {
		newStep = this->firAI->itsmyturn2(lastStep);
	}
	if( newStep == NULL ){
		printf( "You get me a NULL value!\n" );
		return;
	}
	ftime(&end);
	int timeDelta = end.time+end.millitm - begin.time - begin.millitm;
	//printf( "timeDelta=%d\n", timeDelta );

	int temp_state = RUNNING;
	Json::Value newValue;
	newValue[ AIEPP_FIR::TAG_TYPE ] = temp_state;
	newValue[ AIEPP_FIR::TAG_POSITION_X] = newStep->getX();
	newValue[ AIEPP_FIR::TAG_POSITION_Y] = newStep->getY();
	newValue[ AIEPP_FIR::TAG_TIME_USED ] = timeDelta;

	std::string infoValueString = newValue.toStyledString();//magic!!!!!
	const char* cvalue = infoValueString.c_str();//you have to part into two statements here
	
	//printf( "student info->connid=%d\n", this->studentInfo->getConnetionId() );
	DataTransmit::getInstance()->pushData( cvalue, this->studentInfo->getConnetionId() );

	if( newStep != NULL ){ 
		//delete newStep; 
	}
}


void FIRReceiveEventListener::hanleGameOver(Json::Value value){
	//printf( "over!\n" );
	int win = value[ AIEPP_FIR::TAG_WINNER ].asInt();
	if( win == EMPTY ){
		printf( "An error occurs!Game is over!\n" );
		return;
	}
	if( win == (status) ){
		printf( "You are the winner! Game is over!\n" );
	} else {
		printf( "You lost! Game is over!\n" );
	}
	int wintype = value[ AIEPP_FIR::TAG_WIN_TYPE ].asInt();
	if( wintype == NORMAL ){
		int x[5], y[5];
		//be careful here--which is not beautiful
		x[0] = value[ "px0" ].asInt(); x[1] = value[ "px1" ].asInt(); x[2] = value[ "px2" ].asInt();
		x[3] = value[ "px3" ].asInt(); x[4] = value[ "px4" ].asInt();
		y[0] = value[ "py0" ].asInt(); y[1] = value[ "py1" ].asInt(); y[2] = value[ "py2" ].asInt();
		y[3] = value[ "py3" ].asInt(); y[4] = value[ "py4" ].asInt();
		printf( "FIR: " );
		for( int i = 0; i < 5; i ++ ){
			printf( "(%d, %d)", x[i], y[i] );
		}
		printf( "\n" );
	} else if ( wintype == POSITION_ERROR ){
		printf( "Position is error!\n" );
	} else if ( wintype == TIME_OUT ){
		printf( "Time is running out!\n" );
	}
	return;
}


void FIRReceiveEventListener::clear(){
	/*if( piecesArray != NULL ){
		delete []piecesArray;
		//piecesArray = NULL;
	}//*/
	if( this->opponentInfo != NULL ){
		delete (this->opponentInfo);
		this->opponentInfo = NULL;
	}
};