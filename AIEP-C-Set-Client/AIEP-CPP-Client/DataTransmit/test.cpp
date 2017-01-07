
#include "DataTransmit.h"
#include <iostream>
using namespace std;

class MyRecvListener : public ReceiveEventListener{
	void receive(ReceiveEvent* e){
		printf( "myrecv: "  );
		cout << e->getData() << endl;
	}
};


void main(){
	MyRecvListener* recvlistener = new MyRecvListener();
	StudentInfo info = StudentInfo( "b101220105", "谭琦存", "夜光随行" );
	DataTransmit* transmitter = DataTransmit::getInstance();
	transmitter->initialize();
	int id = transmitter->createConnection("127.0.0.1", 8890, &info, recvlistener);
	printf( "connid=%d\n", id );
	transmitter->pushData( "{\"我们\":\"we\"}", id );
//	cout<< endl;

	while( true );
/*	char* cinfo = "{__type:0,__sendtime:\"Wed May 08 19:27:17 CST 2013\",__id=\"b101220105\",__name=\"tqc\",__nickname=\"夜光随行\"}";
	
	wchar_t* info = L"{__type:1,__sendtime:\"Wed May 08 19:27:17 CST 2013\",__id=\"b101220105\",__name=\"tqc\",__nickname=\"夜光随行\"}";
	size_t len = wcslen(info) + 1;
	size_t converted = 0;
	char *cinfo = (char*)malloc(len*sizeof(char));
	wcstombs_s(&converted, cinfo, len, info, _TRUNCATE);//*/
	
	/*用WSASTARTUP来启动有关socket的动态链接库*/
	/*WORD wVersionRequested;
	WSADATA wsaData;
	int err;
	wVersionRequested = MAKEWORD( 2, 2 );
	err = WSAStartup( wVersionRequested, &wsaData );
	if ( err != 0 ) {
	return;
	}
	if ( LOBYTE( wsaData.wVersion ) != 2 ||
			HIBYTE( wsaData.wVersion ) != 2 ) {
	  WSACleanup( );
	  return; 
	}
	SOCKET sockClient=socket(AF_INET,SOCK_DGRAM,0);

	SOCKADDR_IN addrSrv;
	addrSrv.sin_addr.S_un.S_addr=inet_addr("114.212.132.36");
	addrSrv.sin_family=AF_INET;
	addrSrv.sin_port=htons(8890);//设置要请求的服务器的 IP地址及端口信息

	sendto(sockClient,cinfo,strlen(cinfo)+1,0,(SOCKADDR*)&addrSrv,sizeof(SOCKADDR));

	closesocket(sockClient);//关闭套接字
	WSACleanup();//关闭WSA*/
}



/*
#include<WinSock2.h>
#include<stdio.h>
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//服务器程序，向服务器端发送请求//
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
void main()
{
    WORD wVersionRequested;
    WSADATA wsaData;
    int err;
    wVersionRequested=MAKEWORD(1,1);
    err=WSAStartup(wVersionRequested,&wsaData);
    if(err!=0)
    {
        return;
    }
    if(LOBYTE(wsaData.wVersion)!=1||HIBYTE(wsaData.wVersion)!=1)
    {
        WSACleanup();
        return;
    }
//以上部分是初始化部分，打开SOCKET库
    SOCKET socketClient=socket(AF_INET,SOCK_STREAM,0);
    SOCKADDR_IN addrSrv;
    addrSrv.sin_addr.S_un.S_addr=inet_addr("127.0.0.1");
    addrSrv.sin_family=AF_INET;
    addrSrv.sin_port=htons(6000);//设置要请求的服务器的地址及端口号
connect(socketClient,(sockaddr*)&addrSrv,sizeof(SOCKADDR));//建立连接请求
    char recvBuf[100];
    recv(socketClient,recvBuf,100,0);//接收服务器的信息
    printf("%s\n",recvBuf);
    send(socketClient,"this is zhangsan",strlen("this is zhangsan")+1,0);//像服务器发送信息
    closesocket(socketClient);//关闭套接字
    WSACleanup();//关闭WSA
}

*/