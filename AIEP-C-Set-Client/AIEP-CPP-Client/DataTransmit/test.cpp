
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
	StudentInfo info = StudentInfo( "b101220105", "̷����", "ҹ������" );
	DataTransmit* transmitter = DataTransmit::getInstance();
	transmitter->initialize();
	int id = transmitter->createConnection("127.0.0.1", 8890, &info, recvlistener);
	printf( "connid=%d\n", id );
	transmitter->pushData( "{\"����\":\"we\"}", id );
//	cout<< endl;

	while( true );
/*	char* cinfo = "{__type:0,__sendtime:\"Wed May 08 19:27:17 CST 2013\",__id=\"b101220105\",__name=\"tqc\",__nickname=\"ҹ������\"}";
	
	wchar_t* info = L"{__type:1,__sendtime:\"Wed May 08 19:27:17 CST 2013\",__id=\"b101220105\",__name=\"tqc\",__nickname=\"ҹ������\"}";
	size_t len = wcslen(info) + 1;
	size_t converted = 0;
	char *cinfo = (char*)malloc(len*sizeof(char));
	wcstombs_s(&converted, cinfo, len, info, _TRUNCATE);//*/
	
	/*��WSASTARTUP�������й�socket�Ķ�̬���ӿ�*/
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
	addrSrv.sin_port=htons(8890);//����Ҫ����ķ������� IP��ַ���˿���Ϣ

	sendto(sockClient,cinfo,strlen(cinfo)+1,0,(SOCKADDR*)&addrSrv,sizeof(SOCKADDR));

	closesocket(sockClient);//�ر��׽���
	WSACleanup();//�ر�WSA*/
}



/*
#include<WinSock2.h>
#include<stdio.h>
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//������������������˷�������//
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
//���ϲ����ǳ�ʼ�����֣���SOCKET��
    SOCKET socketClient=socket(AF_INET,SOCK_STREAM,0);
    SOCKADDR_IN addrSrv;
    addrSrv.sin_addr.S_un.S_addr=inet_addr("127.0.0.1");
    addrSrv.sin_family=AF_INET;
    addrSrv.sin_port=htons(6000);//����Ҫ����ķ������ĵ�ַ���˿ں�
connect(socketClient,(sockaddr*)&addrSrv,sizeof(SOCKADDR));//������������
    char recvBuf[100];
    recv(socketClient,recvBuf,100,0);//���շ���������Ϣ
    printf("%s\n",recvBuf);
    send(socketClient,"this is zhangsan",strlen("this is zhangsan")+1,0);//�������������Ϣ
    closesocket(socketClient);//�ر��׽���
    WSACleanup();//�ر�WSA
}

*/