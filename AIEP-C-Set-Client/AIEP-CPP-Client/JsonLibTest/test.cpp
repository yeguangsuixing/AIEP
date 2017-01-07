
#include "A.h"

int main() {

	//A* a = new A();
	//a->show();

	char * data = " {\"time\":2,\"offensive\":1,\"aid\":\"2314\",\"type\":0,\"aname\":\"Ì·çù´æ\",\"anick\":\"Ì«Ñô¹«¹«\",\"mode\":1}";
	Json::Reader read;
	Json::Value value;
	char* time = "time";
	if( read.parse(data, value) ){
		printf( "succeed!%d\n", value[time].asInt());
	} else {
		printf( "failed!\n" );
	}


	getchar();
	return 0;
}