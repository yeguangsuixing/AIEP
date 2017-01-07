
#include "../DataTransmit/DataTransmit.h"


#ifdef _DEBUG
#pragma comment(lib, "../Debug/DataTransmit.lib")
#else
#pragma comment(lib, "../Release/DataTransmit.lib")
#endif

class MyRecvListener : public ReceiveEventListener{
	void receive(ReceiveEvent* e){
		cout << "myrecv: " << e->getData() << endl;
	}
};


void main(){
	MyRecvListener* recvlistener = new MyRecvListener();
	StudentInfo info = StudentInfo( "b101220105", "谭琦存", "夜光随行" );
	DataTransmit* transmitter = DataTransmit::getInstance();
	int id = transmitter->createConnection("127.0.0.1", 8890, &info, recvlistener);
	transmitter->pushData( "{我们}", id );
	while( true );
}

