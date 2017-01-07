/**************************************
AI X64 Loader Test
@author tqc
@time 2013Äê6ÔÂ29ÈÕ17:53:03
****************************************/



#ifndef _WINDLL


#define _WIN32_DCOM 

#include <stdio.h>

#include <windows.h>
#include <iostream>

#ifndef __INCLUDE_COMDEF_H_
#define __INCLUDE_COMDEF_H_

#include "../FIR_AIX64Service/stdafx.h"
#include "../FIR_AIX64Service/FIR_AIX64Service_i.h"
#include "../FIR_AIX64Service/FIR_AIX64Service_i.c"

#include <tchar.h>
#include "MyFirAI.h"

#include<comdef.h>
#include "comutil.h"
#pragma comment(lib, "comsupp.lib")
#endif





void c2w(wchar_t *pwstr,size_t len,const char *str) {
	if(str)  {
		size_t nu = strlen(str);
		size_t n =(size_t)MultiByteToWideChar(CP_ACP,0,(const char *)str,(int)nu,NULL,0);
		if(n>=len) n=len-1;
		MultiByteToWideChar(CP_ACP,0,(const char *)str,(int)nu,pwstr,(int)n);
		pwstr[n]=0;
    }
}



//typedef char* (*GetId)();

void main(int argc, char** args){
	if( argc < 1 ){
		printf( "Please input you DLL file name.\n" );
		//return;
	} else {
		printf( "DLL file: %s\n", args[1] );
	}
	CoInitialize( NULL );
	
	HRESULT hr;
	hr = CoInitializeSecurity( NULL, 
				-1, 
				NULL, 
				NULL,
				RPC_C_AUTHN_LEVEL_NONE, 
				RPC_C_IMP_LEVEL_IDENTIFY, 
				NULL, 
				EOAC_NONE, 
				NULL );
	if( !SUCCEEDED( hr ) ) {
		printf( "init right failed!\n" );
		printf( "hr = %d\n", hr );
	} else {
	
		COSERVERINFO si;
		MULTI_QI     qi;
		ZeroMemory( &si, sizeof( si ) );
		ZeroMemory( &qi, sizeof( qi ) );
		si.pwszName = L"127.0.0.1";//remote host = local host
		si.pAuthInfo = NULL;
		qi.pIID = &IID_IFirAI;
		qi.pItf = NULL;
		hr = CoCreateInstanceEx(CLSID_FirAI, NULL, CLSCTX_REMOTE_SERVER, &si, 1, &qi);
		if( FAILED( hr ) ) {
			printf( "Can not create myobject : %d\n", GetLastError() );
			printf( "hr = %d\n", hr );
		}  else if (FAILED(qi.hr)) {
			printf( "Can not create myobject : %d\n", GetLastError() );
			printf( "hr = %d\n", hr );
		} else {
			IFirAI* pT = NULL;
			qi.pItf->QueryInterface( &pT );//get the interfaces
			qi.pItf->Release();

			BSTR aiId;
			char* pAiId;
			long errorId;

			
			pT->test(&errorId);
			printf( "test = %d\n", errorId );

			long dllSize;
			wchar_t* pwstr = new wchar_t[512];
			args[1]="F:\\Workspace\\AIEPServer\\res\\aitest\\FIR_AI_X.dll";
			c2w(pwstr, 512, args[1]);
			BSTR d = SysAllocString( pwstr );
			pT->addFirAIDll( d, &errorId, &dllSize );
			printf( "errorId = %d, dllSize = %d\n", errorId, dllSize );

			
			args[1]="F:\\Workspace\\AIEPServer\\res\\aitest\\FIR_AI_Y.dll";
			c2w(pwstr, 512, args[1]);
			d = SysAllocString( pwstr );
			pT->addFirAIDll( d, &errorId, &dllSize );
			printf( "errorId = %d, dllSize = %d\n", errorId, dllSize );

			pT->setCurrentAIIndex( 0 );
			pT->getId( &errorId, &aiId );
			printf( "errorId = %d\n", errorId );
			pAiId = _com_util::ConvertBSTRToString( aiId );
			printf( "aiId = %s\n", pAiId );

			
			pT->setCurrentAIIndex( 1 );
			pT->getId( &errorId, &aiId );
			printf( "errorId = %d\n", errorId );
			pAiId = _com_util::ConvertBSTRToString( aiId );
			printf( "aiId = %s\n", pAiId );

/*
			args[1]="F:\\Workspace\\AIEPServer\\res\\aitest\\FIR_AI_Y.dll";
			c2w(pwstr, 512, args[1]);
			d = SysAllocString( pwstr );
			
			pT->test(&errorId);
			printf( "test = %d\n", errorId );
			pT->addFirAIDll( d, &errorId, &dllSize );
			printf( "errorId = %d, dllSize = %d\n", errorId, dllSize );
			pT->setCurrentAIIndex( 1 );
			pT->getId( &errorId, &aiId );
			printf( "errorId = %d\n", errorId );
			pAiId = _com_util::ConvertBSTRToString( aiId );
			printf( "aiId = %s\n", pAiId );//*/

			//*
			pT->setCurrentAIIndex( 0 );
			Step step(Status::EMPTY,2, 4);
			long newStatus = 0, newStepX = 0, newStepY = 0;
			Step *newStep;
			pT->itsmyturn( 2, 4, 0, &newStepX, &newStepY, &newStatus );
			printf( "newStep = x:%d, y:%d\n", newStepX, newStepY );

			pT->setCurrentAIIndex( 1 );
			pT->itsmyturn2( 3, 4, 0, &newStepX, &newStepY, &newStatus );
			printf( "newStep = x:%d, y:%d\n", newStepX, newStepY );
			//*/
			pT->Release();
			SysFreeString(d);
		}
	}
	CoUninitialize();
	system("pause");
}

#endif
