/**************************************
Data Transmit cpp
@author tqc
@time 2013Äê5ÔÂ8ÈÕ22:01:59
****************************************/

#include "DataTransmit.h"


char DataTransmit::TAG_TYPE[] = "__type";
char DataTransmit::TAG_CONN[] = "__conn";
char DataTransmit::TAG_TIME_SEND[] = "__sendtime";
char DataTransmit::TAG_DATA[] = "__data";
char DataTransmit::TAG_ID[] = "__id";
char DataTransmit::TAG_NAME[] = "__name";
char DataTransmit::TAG_NICKNAME[] = "__nickname";
char DataTransmit::TAG_LANG[] = "__lang";
int DataTransmit::LISTEN_ID = 0;
int DataTransmit::CONN_ID = 0;

DataTransmit* DataTransmit::transmitter = NULL;

DataTransmit::DataTransmit(){
	this->isInitialized = false;
}

bool DataTransmit::initialize(){
	if( this->isInitialized ) return true;
	this->isInitialized = true;
	WSADATA wsaData;
	WORD wVersionRequested = MAKEWORD( 2, 2 );
	int err = WSAStartup( wVersionRequested, &wsaData );
	if ( err != 0 ) { return false; }
	if ( LOBYTE( wsaData.wVersion ) != 2 || HIBYTE( wsaData.wVersion ) != 2 ) {
		WSACleanup( );
		return false;
	}
	return true;
}

DataTransmit::~DataTransmit(){
	WSACleanup( );
}


void DataTransmit::pushData(const char* data, int connId){
	//ClientDataChannel*
	std::list<DataTransmit::ClientDataChannel*>::iterator iter;
	for( iter = connectionList.begin(); iter != connectionList.end(); iter++ ){
		if( connId == (*iter)->getStudentInfo()->getConnetionId() ){
			Json::Value infoValue;
			infoValue[ TAG_TYPE ] = TYPE_DATA;
			infoValue[ TAG_TIME_SEND ] = "Thu May 09 14:39:54 CST 2013";
			infoValue[ TAG_DATA ] = data;
			std::string infoValueString = infoValue.toStyledString();
			//const char* in = infoValue.asCString();
			const char* infoChars = infoValueString.c_str() ;
			//printf( "here is will be sent\n%s\n", infoChars );
			(*iter)->pushData( infoChars );
			break;
		}
	}
}



int DataTransmit::createConnection(char* hostIp, int hostPort,StudentInfo* studentInfo,
		ReceiveEventListener* recvListener ){
	ClientDataChannel* clientChannel = new ClientDataChannel( studentInfo, recvListener);

	if( ! clientChannel->connect( hostIp, hostPort ) ) { delete clientChannel; return ILLEGAL_ID; } 

	//send student info
	Json::Value infoValue;
	infoValue[ TAG_TYPE ] = TYPE_INFO;
	infoValue[ TAG_TIME_SEND ] = "Thu May 09 14:39:54 CST 2013";
	infoValue[ TAG_ID ] = studentInfo->getId();
	infoValue[ TAG_NAME ] = studentInfo->getName();
	infoValue[ TAG_NICKNAME ] = studentInfo->getNickname();
	infoValue[TAG_LANG] = CPP;
	//infoValue[TAG_LANG] = JAVA;//for C# or .NET
	//const char* infoValueString = new char(100);
	std::string infoValueString = infoValue.toStyledString();
	//printf( "infoValueString=%s\n", infoValueString.c_str() );

	clientChannel->pushData( infoValueString.c_str() );
	//
	int tempTimeout = clientChannel->getTimeout();
	clientChannel->setTimeout(5);
	char recvBuf[PACKET_MAX_LEN];
	int recvN = clientChannel->receive( recvBuf, PACKET_MAX_LEN );
	if( recvN < 0 ) {
		//printf( "nothing recvd!" );
		return CONN_TIMEOUT;//
	}
			
	//converst
	Json::Reader read;
	Json::Value data;
	if( ! read.parse( recvBuf, data ) ){ 
		printf( "[DataTransmit]Conversting the package failed!\n" ); 
		return ILLEGAL_ID;//error
	}

	int type = data[ TAG_TYPE ].asInt();
	if( type != TYPE_CONN ) {//ignore
		return ILLEGAL_ID;
	}

	int conn = data[TAG_CONN].asInt();
	if( conn == CONN_REJECT ){
		return SERV_REJECT;
	} else if( conn ==  CONN_SUCCESS ){
		clientChannel->setTimeout( tempTimeout );
		clientChannel->start();
		studentInfo->setConnectionId( ++CONN_ID );
		connectionList.push_back( clientChannel );
		return studentInfo->getConnetionId();
	}
	return ILLEGAL_ID;
};

void DataTransmit::closeConnection(int connectionId){
	std::list<DataTransmit::ClientDataChannel*>::iterator iter = connectionList.begin();
	for( ; iter != connectionList.end(); iter ++ ){
		if( (*iter)->getStudentInfo()->getConnetionId() == connectionId ){
			//closesocket( (*iter)->clientSocket );
			(*iter)->close();
			connectionList.remove( (*iter) );
			if( *iter == NULL ){
				delete (*iter);
				*iter = NULL;
			}
			break;
		}
	}
}

void DataTransmit::ClientDataChannel::AmIKiddingMyself(char* userData, char* recvData, int length)
{
	for( int i = 0; i < PACKET_MAX_LEN ;i ++) userData[i] = 0;
	int beginIndex = 1, endIndex = length-1, count = 0;
	for( ; beginIndex < length-1 && recvData[beginIndex] != '{'; beginIndex ++ );//be careful here
	//printf( "begin=%d\n", beginIndex );
	int i = beginIndex;
	for( int i = beginIndex; i < length - 1; i++ ){
		userData[i-beginIndex] = recvData[i];
	}
	return;
}
void DataTransmit::ClientDataChannel::receiveHandler(){
	Json::Reader read;
	char recvBuf[PACKET_MAX_LEN];
	while( isRunning ){
		for( int i = 0; i < PACKET_MAX_LEN; i ++ ) recvBuf[i] = 0;
		//int len = receive( recvBuf, PACKET_MAX_LEN );
		int len = recv( clientSocket, recvBuf, PACKET_MAX_LEN, 0 );
		/*
		printf( "Client recv(len=%d): ", len );
		for( int i = 0; i < len; i ++ ) printf( "%c", recvBuf[i] );
		printf( "\n" );//*/
		if( len == -1 ){
			printf("[DataTransmit]Time is running out!\n"); break;
		}

		Json::Value data;
		if( ! read.parse( recvBuf, data ) ){ 
			//printf( "converst failed!\n" ); 
			continue; 
		}

		int type = data[ TAG_TYPE ].asInt();
		char* sendtime = "Thu May 09 14:39:54 CST 2013";//data[ TAG_TIME_SEND ].asString().c_str();
		if( type == TYPE_DATA ){
			//char* sendtime = "Thu May 09 14:39:54 CST 2013";//data[ TAG_TIME_SEND ].asString().c_str();
			char t[ PACKET_MAX_LEN ];
			AmIKiddingMyself( t, recvBuf, len );
			if( receiveEventListener != NULL ){
				ReceiveEvent  e =  ReceiveEvent( studentInfo, sendtime, t);
				receiveEventListener->receive( &e );
				//delete e;
			}
			//delete []t;
		} else if ( type == TYPE_CONN ) {
			int conn = data[TAG_CONN].asInt();
			//below matches [JAVA]private void handleConn(Date sendTime, int conn, InetAddress address,int port)
			if( conn == Conn::CONN_REQ_RESTART ) {
				if( receiveEventListener != NULL ) {
					ReceiveEvent e = ReceiveEvent( studentInfo, sendtime, ReceiveEvent::RecvCmdType::RESTART );
					receiveEventListener->receive( &e );
					//delete e;
				}
				return;//stop the current thread
			} else {
				printf( "[DataTransmit]Unknown connection type(conn type=%d)!\n", conn );
			}
		} else {
			printf( "[DataTransmit]The packet data is error!\n" ); continue;
		}
	}

}
bool DataTransmit::ClientDataChannel::connect(char* hostIp, int hostPort ){
	
	SOCKET sock = socket(AF_INET, SOCK_DGRAM, 0);
	if (sock == INVALID_SOCKET){
		return false;
	}

	SOCKADDR_IN addrSrv;
	addrSrv.sin_addr.S_un.S_addr=inet_addr( hostIp );
	addrSrv.sin_family=AF_INET;
	addrSrv.sin_port=htons( hostPort );


	this->clientSocket = sock;
	this->serverAddress = addrSrv;
	this->servAddrLength = sizeof(serverAddress);
	
	return true;
}
void DataTransmit::ClientDataChannel::start(){
	if( this->isRunning ) return;
	this->isRunning = true;
	//create  a new thread to recv the packets 
	recvThread = CreateThread(
		NULL //security
		, 0 //default static size
		, recvProc//entry
		, this // entry parameter pointer
		, 0 //flag--run immediately
		, NULL//thread id pointer
	);//*/
}
int DataTransmit::ClientDataChannel::receive( char* recvBuf, int bufLength ){
	struct timeval tv;
	fd_set readfds;
	for( int i = 0; i < this->timeout; i++) {
		FD_ZERO( &readfds );
		FD_SET(clientSocket, &readfds );
		tv.tv_sec = 1;
		tv.tv_usec = 0;
		select( clientSocket + 1, &readfds, NULL, NULL, &tv );
		if( FD_ISSET( clientSocket, &readfds ) ) {
			int n= recvfrom( clientSocket, recvBuf, PACKET_MAX_LEN, 0, 
				(struct sockaddr* )&serverAddress, &servAddrLength );
			if( n >= 0 ) {
				//printf( "n = %d, recv: %s", n, recvBuf );
				return n;
			}
		}
	}
	//printf( "ttttrecv" );
	return -1;
}

void DataTransmit::ClientDataChannel::pushData( const char* data) const {
	int len = strlen(data)+1;
	sendto( clientSocket, data, len, 0, (SOCKADDR*)&serverAddress,  sizeof(SOCKADDR) );
}




DataTransmit* DataTransmit::getInstance(){
	if( DataTransmit::transmitter == NULL ){
		DataTransmit::transmitter = new DataTransmit();
	}
	return DataTransmit::transmitter;
}

//receive thread entry, go back to the ClientDataChannel class
DWORD WINAPI recvProc( LPVOID lpParameter ){
	DataTransmit::ClientDataChannel* cdc = (DataTransmit::ClientDataChannel*)lpParameter;
	cdc->receiveHandler();
	return 0;
}