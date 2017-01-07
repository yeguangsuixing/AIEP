/**************************************
Data Transmit class
@author tqc
@time 2013Äê5ÔÂ8ÈÕ21:04:19
****************************************/

#ifndef _DATA_TRANSMIT_H_
#define _DATA_TRANSMIT_H_

#include <Windows.h>
//#include<winsock2.h>
#include <list>

#include "DataTransmit/std.h"
#include "DataTransmit/ReceiveEvent.h"
#include "DataTransmit/ReceiveEventListener.h"
#include "DataTransmit/StudentInfo.h"
#include "json/json.h"

#ifdef VS2010

#ifdef _DEBUG
#pragma comment(lib, "lib/Debug/JsonLib.lib")
#else
#pragma comment(lib, "lib/Release/JsonLib.lib")
#endif

#endif

#ifdef VS2012

#ifdef _DEBUG
#pragma comment(lib, "lib/Debug/JsonLibVS2012.lib")
#else
#pragma comment(lib, "lib/Release/JsonLibVS2012.lib")
#endif

#endif

#pragma comment(lib,"ws2_32.lib")

//extern DLL_EXPORT DataTransmit*  getDTInstance();

class ClientDataChannel;//declear
extern DWORD WINAPI recvProc( LPVOID lpParameter );
enum Conn { CONN_REJECT, CONN_SUCCESS, CONN_REQ_RESTART, CONN_READY };

class  DataTransmit {
private:
	//class ClientDataChannel;//declear
	const static int PACKET_MAX_LEN = 1024;
public:
	const static int ILLEGAL_ID = 0, SERV_REJECT = -1, CONN_TIMEOUT = -2 ;
private:
	const static int TYPE_INFO = 0, TYPE_DATA = 1, TYPE_CONN = 2;
	const static int MAX_LEN = 20;
	const static int TIME_OUT = 10000;

	static DataTransmit* transmitter;
	static char TAG_TYPE[MAX_LEN];
	static char TAG_CONN[MAX_LEN];
	static char TAG_TIME_SEND[MAX_LEN];
	static char TAG_DATA[MAX_LEN];
	static char TAG_NICKNAME[MAX_LEN];
	static char TAG_NAME[MAX_LEN];
	static char TAG_ID[MAX_LEN];
	static char TAG_LANG[MAX_LEN];

	static int LISTEN_ID, CONN_ID;

	
	bool isInitialized;
public:
	class ClientDataChannel {
	private:
		int connId;
		SOCKADDR_IN serverAddress;
		SOCKET clientSocket;
		int serverPort;
		int servAddrLength;
		StudentInfo* studentInfo;
		ReceiveEventListener* receiveEventListener;
		HANDLE recvThread;
		bool isRunning;
		int timeout;
		const static int DEFAULT_TIME_OUT = 5;

		friend DWORD WINAPI recvProc( LPVOID lpParameter );
		//kidding function - for fetching the user data from origin package
		void AmIKiddingMyself(char* userData, char* recvData, int length);
		void receiveHandler();
	public:
		ClientDataChannel( StudentInfo* studentInfo, ReceiveEventListener* recvListener ){
			this->servAddrLength = 0;
			this->recvThread = NULL;
			this->isRunning = false;
			this->studentInfo = studentInfo;
			this->receiveEventListener = recvListener;
			this->timeout = DEFAULT_TIME_OUT;
		}
		~ClientDataChannel(){
			close();
			//if( serverAddress != NULL ){ delete serverAddress; serverAddress = NULL; }
		}
		void close(){
			this->isRunning = false;
			if(clientSocket != NULL ){ closesocket(clientSocket); clientSocket = NULL; }
			//if( recvThread != NULL ) CloseHandle( recvThread );
		}
		bool connect(char* hostIp, int hostPort );
		void start();
		int receive(char* recvBuf, int bufLength);
		/**
		 * @return the studentInfo
		*/
		StudentInfo* getStudentInfo() {
			return studentInfo;
		}
		/**
		 * @return the serverPort
		 */
		int getServerPort() {
			return serverPort;
		}
		/**
		 * @return the clientSocket
		 */
		SOCKET getClientSocket() {
			return clientSocket;
		}
		ReceiveEventListener* getReceiveEventListener() {
			return receiveEventListener;
		}
		/**
		 * @return the serverAddress
		 */
		SOCKADDR_IN* getServerAddress() {
			return &serverAddress;
		}
		void pushData( const char* data) const ;
		int getTimeout(){ return this->timeout; }
		void setTimeout(int timeout){ this->timeout = timeout; }//by second
	};

private:
	std::list<DataTransmit::ClientDataChannel*> connectionList;

	//forbid creating instances
	DataTransmit();


public:
	static DLL_EXPORT DataTransmit* getInstance();
	~DataTransmit();
	//DataTransmit(const DataTransmit&){};
	//DataTransmit& operator= (const DataTransmit&){};
	bool DLL_EXPORT initialize();
	int DLL_EXPORT createConnection(char* hostIp, int hostPort, StudentInfo* studentInfo,
		ReceiveEventListener* recvListener );
	void DLL_EXPORT pushData(const char* data, int connId);
	void DLL_EXPORT closeConnection( int connectionId );
};


#endif //_DATA_TRANSMIT_H_