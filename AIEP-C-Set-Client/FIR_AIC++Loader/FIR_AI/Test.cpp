/**************************************
AIEPP FIR AI Test
@author tqc
@time 2013Äê6ÔÂ26ÈÕ15:34:47
****************************************/



#include <stdio.h>
#include<Windows.h>

#ifdef _WINDLL
#else


typedef char* (*GetId)();

void main(int argc, char** args){
	GetId getId;
	HINSTANCE hdll;
	if( argc < 1 ){
		printf( "Please input you DLL file name.\n" );
	} else {
		hdll=LoadLibrary(args[1]);//"F:\\VS2010 Project\\FIR_AIC++Loader\\FIR_AI\\FIR_AI.dll"
		if(hdll==NULL)  {
			printf( "Loading the library(%s) failed!Error:%d\n", args[1], GetLastError() );
			FreeLibrary(hdll);
		} else {
			getId = (GetId)GetProcAddress(hdll,"getId");
			if( getId == NULL ){
				printf( "Cannot get the funtion \"getId\".\n" );
			} else {
				printf( "id=%s\n", getId() );
			}
			FreeLibrary(hdll);
		}
	}
	system("pause");
}

#endif