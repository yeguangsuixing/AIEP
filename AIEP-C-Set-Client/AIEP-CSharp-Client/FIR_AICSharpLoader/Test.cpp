/**************************************
AIEPP FIR AI Test
@author tqc
@time 2013年6月9日3:58:56
****************************************/
#ifdef _WINDLL
#include "AIEPP-Plugin-FIR/FIRLoader.h"
#include "DataTransmit/StudentInfo.h"
//#include "MyFirAI.h"

#ifdef _DEBUG
#pragma comment(lib, "lib/Debug/AIEPP-Plugin-FIR.lib")
#else
#pragma comment(lib, "lib/Release/AIEPP-Plugin-FIR.lib")
#endif




#else

#ifndef __INCLUDE_COMDEF_H_
#define __INCLUDE_COMDEF_H_

#include "../../FIR_AIC++Loader/FIR_AIX64Service/stdafx.h"
#include "../../FIR_AIC++Loader/FIR_AIX64Service/FIR_AIX64Service_i.h"
#include "../../FIR_AIC++Loader/FIR_AIX64Service/FIR_AIX64Service_i.c"

#include <tchar.h>
#include "../../FIR_AIC++Loader/FIR_AI/MyFirAI.h"

#include<comdef.h>
#include "comutil.h"
#pragma comment(lib, "comsupp.lib")


#endif


#include <iostream>
#include <Windows.h>


void c2w(wchar_t *pwstr,size_t len,const char *str) {
	if(str)  {
		size_t nu = strlen(str);
		size_t n =(size_t)MultiByteToWideChar(CP_ACP,0,(const char *)str,(int)nu,NULL,0);
		if(n>=len) n=len-1;
		MultiByteToWideChar(CP_ACP,0,(const char *)str,(int)nu,pwstr,(int)n);
		pwstr[n]=0;
    }
}
char *w2c(char *pcstr, const wchar_t *pwstr, size_t len)
{
	int nlength=wcslen(pwstr);
	//获取转换后的长度
	int nbytes = WideCharToMultiByte( 0, // specify the code page used to perform the conversion
		0,         // no special flags to handle unmapped characters
		pwstr,     // wide character string to convert
		nlength,   // the number of wide characters in that string
		NULL,      // no output buffer given, we just want to know how long it needs to be
		0,
		NULL,      // no replacement character given
		NULL );    // we don't want to know if a character didn't make it through the translation

	// make sure the buffer is big enough for this, making it larger if necessary
	if(nbytes>len)   nbytes=len;

	// 通过以上得到的结果，转换unicode 字符为ascii 字符
	WideCharToMultiByte( 0, // specify the code page used to perform the conversion
		0,         // no special flags to handle unmapped characters
		pwstr,   // wide character string to convert
		nlength,   // the number of wide characters in that string
		pcstr, // put the output ascii characters at the end of the buffer
		nbytes,                           // there is at least this much space there
		NULL,      // no replacement character given
		NULL );

	return pcstr ;

}




#using <mscorlib.dll>
#using <System.dll>
#using <System.Windows.Forms.dll>
#using "../FIR_AICSharpUtilLoader/bin/Release/AIEP-CSharp.dll"
#using "../FIR_AICSharpUtilLoader/bin/Release/FIR_AICSharpUtilLoader.dll"
using namespace System;
//using namespace FIR_AICSharpUtilLoader;

static CLSID CLSID_loader = { 0xFA938286, 0x19FF, 0x434A, { 0xBA, 0x84, 0x85, 0x23, 0x1C, 0xC8, 0xA4, 0x2A } };
typedef   HRESULT   (__stdcall* PGetLoaderClassObject)   (REFCLSID,   REFIID,   void**);

int _tmain(int argc, _TCHAR* argv[]){
	//Windows::Forms::MessageBox::Show(L"test");
	//将char*转换为System::String^
	//char* ch1 = "this is chars 我们"; 
	//String^ str1= System::Runtime::InteropServices::Marshal::PtrToStringAnsi((IntPtr)ch1); // 
	//Console::WriteLine(str1);

	/*
	FIR_AICSharpUtilLoader::FIR_AICSharpUtilLoader ^loader = gcnew FIR_AICSharpUtilLoader::FIR_AICSharpUtilLoader();
	bool b = loader->addFirAIDll("F:\\VS2010 Project\\AIEP-CSharp-Client\\FIR_AICS\\obj\\Release\\MyFirAI.dll");
	if( !b ){ printf( "[C++]Adding Dll File Failed!\n" );return; }
	else printf( "[C++]Adding Dll File Succeed!\n" );
	
	char* id = (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi( loader->getId() );
	char* name = (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi( loader->getName() );
	char* nick = (char*)(void*)System::Runtime::InteropServices::Marshal::StringToHGlobalAnsi( loader->getNickname() );
	printf( "AI Id=%s\n",id );
	printf( "AI name=%s\n",name );
	printf( "AI nickname=%s\n",nick );

	
	AICS::Status status = AICS::Status::EMPTY;
	AICS::Step ^lastStep = gcnew AICS::Step( status, 2, 3 );
	AICS::Step ^newStep = loader->itsmyturn( lastStep );
	printf( "newStep: x = %d, y=%d\n", newStep->getX() , newStep->getY() );
	//*/

/*
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

			pT->setProgramLanguage( 1 );

			long dllSize;
			wchar_t* pwstr = new wchar_t[512];
			args[1]="F:\\Workspace\\AIEPServer\\res\\aitest\\MyFirAI.dll";
			c2w(pwstr, 512, args[1]);
			BSTR d = SysAllocString( pwstr );
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
		}
	}//*/

	//*
	CoInitialize(NULL);

	// Get CLSID for CoCreateInstance
	const OLECHAR lpszProgID[] = OLESTR("FIR_AICSharpUtilLoader.FIR_AICSharpUtilLoader");
	CLSID clsid;
	HRESULT hr;
	hr = CLSIDFromProgID(lpszProgID, &clsid);
	if(FAILED(hr))
	{
		printf("CLSIDFromProgID Failed!\n");
		printf("hr = %d\n", hr); 
		system("pause"); return 0;
	}
	printf("CLSIDFromProgID Succeeded!classid=%X-%X-%X-%X%X-%X-%X-%X-%X-%X-%X\n", 
		clsid.Data1, clsid.Data2, clsid.Data3,
		clsid.Data4[0], clsid.Data4[1], clsid.Data4[2], clsid.Data4[3], 
		clsid.Data4[4], clsid.Data4[5], clsid.Data4[6], clsid.Data4[7] );
	//*/


	IDispatch* ppv = NULL;
	hr = CoCreateInstance(clsid, NULL, CLSCTX_ALL, IID_IDispatch, (void**)&ppv);
	if(FAILED(hr))
	{
		printf("CoCreateInstance Failed!\n");
		printf("hr = %d\n", hr); 
		system("pause"); return 0;
	}
	printf("CoCreateInstance Succeeded\n");

	// Get DispId for Invoke
	DISPID dispid_addFirAIDll, dispid_isPrintInfo, dispid_getId, dispid_itsmyturn;
	const LPOLESTR szMember_addFirAIDll = OLESTR("addFirAIDll");
	const LPOLESTR szMember_isPrintInfo = OLESTR("isPrintInfo");
	const LPOLESTR szMember_getId = OLESTR("getId");
	const LPOLESTR szMember_itsmyturn = OLESTR("itsmyturn");
	HRESULT hr1 = ppv->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_addFirAIDll,1,LOCALE_SYSTEM_DEFAULT,&dispid_addFirAIDll);
	HRESULT hr2 = ppv->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_isPrintInfo,1,LOCALE_SYSTEM_DEFAULT,&dispid_isPrintInfo);
	HRESULT hr3 = ppv->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getId,1,LOCALE_SYSTEM_DEFAULT,&dispid_getId);
	HRESULT hr4 = ppv->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_itsmyturn,1,LOCALE_SYSTEM_DEFAULT,&dispid_itsmyturn);
	if(FAILED(hr1)||FAILED(hr2)||FAILED(hr3)||FAILED(hr4))
	{
		printf("GetIDsOfNames Faield!\n");
		printf("hr1 = %d\n", hr1);
		printf("hr2 = %d\n", hr2);
		printf("hr3 = %d\n", hr3);
		printf("hr4 = %d\n", hr4);
		ppv->Release();
		system("pause"); return 0;
	}
	printf("dispid_addFirAIDll=%d\n", dispid_addFirAIDll);
	printf("dispid_isPrintInfo=%d\n", dispid_isPrintInfo);
	printf("dispid_getId=%d\n", dispid_getId);
	printf("dispid_itsmyturn=%d\n", dispid_itsmyturn);
	
	
	VARIANT vtResult;
	UINT dwArgErr;
	EXCEPINFO FAR  pExcepInfo;

	VARIANTARG v[1];
	v[0].vt = VT_BSTR;
	v[0].bstrVal = SysAllocString(L"F:\\VS2010 Project\\FIR_AICSharp\\FIR_AICSharp\\bin\\Release\\MyFirAI.dll");
	DISPPARAMS dispParams = { v, NULL, 1, 0 };
	
	/*
	DISPPARAMS dispParams;
	dispParams.cArgs=1;
	dispParams.cNamedArgs=1;//必须，原因不明
	DISPID dispidPut = DISPID_PROPERTYPUT;//必须，原因不明
	dispParams.rgdispidNamedArgs=&dispidPut;//必须，原因不明
	dispParams.rgvarg=new VARIANTARG[1];
	dispParams.rgvarg[0].vt=VT_BSTR;
	dispParams.rgvarg[0].bstrVal=SysAllocString(L"F:\\Workspace\\AIEPServer\\res\\aitest\\MyFirAI.dll");
	//*/

	HRESULT hr_invoke = ppv->Invoke(dispid_addFirAIDll,
		IID_NULL,
		LOCALE_SYSTEM_DEFAULT,
		DISPATCH_METHOD,//DISPATCH_PROPERTYPUT
		&dispParams,&vtResult,&pExcepInfo,
		&dwArgErr);
	if(FAILED(hr_invoke))
	{
		if( hr_invoke == DISP_E_EXCEPTION ){
			printf( "DISP_E_EXCEPTION\n" );
			char desc[512];
			w2c( desc, pExcepInfo.bstrDescription, 512 );
			printf( "pExcepInfo = %s\n", desc );
		}
		printf( "error = %d\n", GetLastError() );
		printf( "hr = %d\n", hr_invoke );
		printf( "dwArgErr = %d\n", dwArgErr );
		ppv->Release();
		system("pause");
		return 0;
	}
	printf("Invoke \"addDllFile\" Succeeded\n");

	DISPPARAMS dispParams2 = { NULL, NULL, 0, 0 };
	hr_invoke = ppv->Invoke(dispid_isPrintInfo,
		IID_NULL,
		LOCALE_SYSTEM_DEFAULT,
		DISPATCH_METHOD,//DISPATCH_PROPERTYPUT
		&dispParams2, &vtResult, &pExcepInfo,
		&dwArgErr);
	if(FAILED(hr_invoke))
	{
		if( hr_invoke == DISP_E_EXCEPTION ){
			printf( "DISP_E_EXCEPTION\n" );
			char desc[512];
			w2c( desc, pExcepInfo.bstrDescription, 512 );
			printf( "pExcepInfo = %s\n", desc );
		}
		printf( "error = %d\n", GetLastError() );
		printf( "hr = %d\n", hr_invoke );
		printf( "dwArgErr = %d\n", dwArgErr );
		ppv->Release();
		system("pause");
		return 0;
	}
	printf("Invoke \"isPrintInfo\" Succeeded, result = %s\n", vtResult.boolVal?"true":"false" );

	
	DISPPARAMS dispParams3 = { NULL, NULL, 0, 0 };
	//memset( &vtResult, 0, sizeof( vtResult ) );
	hr_invoke = ppv->Invoke(dispid_getId,
		IID_NULL,
		LOCALE_SYSTEM_DEFAULT,
		DISPATCH_METHOD,//DISPATCH_PROPERTYPUT
		&dispParams3, &vtResult, &pExcepInfo,
		&dwArgErr);
	if(FAILED(hr_invoke))
	{
		if( hr_invoke == DISP_E_EXCEPTION ){
			printf( "DISP_E_EXCEPTION\n" );
			char desc[512];
			w2c( desc, pExcepInfo.bstrDescription, 512 );
			printf( "pExcepInfo = %s\n", desc );
		}
		printf( "error = %d\n", GetLastError() );
		printf( "hr = %d\n", hr_invoke );
		printf( "dwArgErr = %d\n", dwArgErr );
		ppv->Release();
		system("pause");
		return 0;
	}
	char idchars[100];
	memset( idchars, 0, 100*sizeof(char) );
	w2c( idchars, vtResult.bstrVal, 100 );
	printf("Invoke \"getId\" Succeeded, result = %s\n", idchars );

	
	VARIANTARG step[3];
	step[0].vt = VT_INT;
	step[0].intVal = 2;
	step[1].vt = VT_INT;
	step[1].intVal = 3;
	step[2].vt = VT_INT;
	step[2].intVal = 4;
	DISPPARAMS dispParams4 = { step, NULL, 3, 0 };
	hr_invoke = ppv->Invoke(dispid_itsmyturn,
		IID_NULL,
		LOCALE_SYSTEM_DEFAULT,
		DISPATCH_METHOD,//DISPATCH_PROPERTYPUT
		&dispParams4, &vtResult, &pExcepInfo,
		&dwArgErr);
	if(FAILED(hr_invoke))
	{
		if( hr_invoke == DISP_E_EXCEPTION ){
			printf( "DISP_E_EXCEPTION\n" );
			char desc[512];
			w2c( desc, pExcepInfo.bstrDescription, 512 );
			printf( "pExcepInfo = %s\n", desc );
		}
		printf( "error = %d\n", GetLastError() );
		printf( "hr = %d\n", hr_invoke );
		printf( "dwArgErr = %d\n", dwArgErr );
		ppv->Release();
		system("pause");
		return 0;
	}
	printf("Invoke \"itsmyturn\" Succeeded\n" );

	IDispatch FAR* pstep = (vtResult.pdispVal);
	DISPID dispid_getx;
	const LPOLESTR szMember_getX = OLESTR("getX");
	HRESULT hr5 = pstep->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getX,1,LOCALE_SYSTEM_DEFAULT,&dispid_getx);
	if(FAILED(hr5))
	{
		printf("GetIDsOfNames Faield!\n");
		printf("hr5 = %d\n", hr5);
		ppv->Release();
		system("pause"); return 0;
	}
	hr_invoke = pstep->Invoke(dispid_getx,
		IID_NULL,
		LOCALE_SYSTEM_DEFAULT,
		DISPATCH_METHOD,//DISPATCH_PROPERTYPUT
		&dispParams3, &vtResult, &pExcepInfo,
		&dwArgErr);
	if(FAILED(hr_invoke))
	{
		if( hr_invoke == DISP_E_EXCEPTION ){
			printf( "DISP_E_EXCEPTION\n" );
			char desc[512];
			w2c( desc, pExcepInfo.bstrDescription, 512 );
			printf( "pExcepInfo = %s\n", desc );
		}
		printf( "error = %d\n", GetLastError() );
		printf( "hr = %d\n", hr_invoke );
		printf( "dwArgErr = %d\n", dwArgErr );
		ppv->Release();
		system("pause");
		return 0;
	}
	printf("Invoke \"getX\" Succeeded, result = %d\n", vtResult.intVal );

	ppv->Release();
	system("pause");
	return 0;
}

#endif