

#include "AIEPP_FIR.h"
#include "FIRLoader.h"
#include "MyFirAI.h"

void main(){
	MyFirAI* ai = new MyFirAI();
	AILoader<FIR_AI>* aiLoader = new FIRLoader( ai );
	aiLoader->setHostIp("127.0.0.1"); 
	aiLoader->setHostPort( 8890 );
	aiLoader->execute();
	Sleep(999999999);while(true);
	//system("pause");
}
