/**************************************
My FIR AI CPP for Java
@author ygsx
@time 2013年6月9日13:51:56
****************************************/

#ifdef _WINDLL
//#pragma managed

#define NULL 0

#include "java\com_njucs_aiep_plugin_fir_ai_FirAICSharp.h"
#include <string>
using namespace std;

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

//


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



/**
Copy by internet, 
hope it does work well!
*/
jstring str2jstring(JNIEnv* env,const char* pat)
{
    //定义java String类 strClass
    jclass strClass = (env)->FindClass("Ljava/lang/String;");
    //获取String(byte[],String)的构造器,用于将本地byte[]数组转换为一个新String
    jmethodID ctorID = (env)->GetMethodID(strClass, "<init>", "([BLjava/lang/String;)V");
    //建立byte数组
    jbyteArray bytes = (env)->NewByteArray(strlen(pat));
    //将char* 转换为byte数组
    (env)->SetByteArrayRegion(bytes, 0, strlen(pat), (jbyte*)pat);
    // 设置String, 保存语言类型,用于byte数组转换至String时的参数
    jstring encoding = (env)->NewStringUTF("GB2312"); 
    //将byte数组转换为java String,并输出
    return (jstring)(env)->NewObject(strClass, ctorID, bytes, encoding);
}
std::string jstring2str(JNIEnv* env, jstring jstr)
{   
    char*   rtn   =   NULL;   
    jclass   clsstring   =   env->FindClass("java/lang/String");   
    jstring   strencode   =   env->NewStringUTF("GB2312");   
    jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");   
    jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode);   
    jsize   alen   =   env->GetArrayLength(barr);   
    jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);   
    if(alen   >   0)   
    {   
        rtn   =   (char*)malloc(alen+1);         
        memcpy(rtn,ba,alen);   
        rtn[alen]=0;   
    }   
    env->ReleaseByteArrayElements(barr,ba,0);   
    std::string stemp(rtn);
    free(rtn);
    return   stemp;   
}  

const LPOLESTR szMember_addFirAIDll = OLESTR("addFirAIDll");
const LPOLESTR szMember_isPrintInfo = OLESTR("isPrintInfo");
const LPOLESTR szMember_getId = OLESTR("getId");
const LPOLESTR szMember_getName = OLESTR("getName");
const LPOLESTR szMember_getNickname = OLESTR("getNickname");
const LPOLESTR szMember_getVersion = OLESTR("getVersion");
const LPOLESTR szMember_itsmyturn = OLESTR("itsmyturn");
const LPOLESTR szMember_itsmyturn2 = OLESTR("itsmyturn2");
const LPOLESTR szMember_setCurrentAIIndex = OLESTR("setCurrentAIIndex");

//const static CLSID CLSID_loader = { 0xFA938286, 0x19FF, 0x434A, { 0xBA, 0x84, 0x85, 0x23, 0x1C, 0xC8, 0xA4, 0x2A } };
IDispatch* loader = NULL;
CLSID loaderid;

DISPID dispid_setCurrentAIIndex, dispid_addFirAIDll, dispid_itsmyturn, dispid_itsmyturn2;
DISPID dispid_isPrintInfo, dispid_getId, dispid_getName, dispid_getNickname, dispid_getVersion;


static bool isInitialized =false;
bool initialize(){
	if( isInitialized ) return true;

	//CoInitialize(NULL);
	CoInitializeEx( NULL, COINIT_MULTITHREADED);
	HRESULT hr;
	//*

	hr = CoInitializeSecurity( NULL, 
				-1, 
				NULL,
				NULL,
				RPC_C_AUTHN_LEVEL_NONE, 
				RPC_C_IMP_LEVEL_IDENTIFY, 
				NULL, 
				EOAC_NONE, 
				NULL );
	if( FAILED( hr ) ) {
		printf( "init right failed!\n" );
		printf( "hr = %d\n", hr );
		return false;
	} 
	
	const OLECHAR lpszProgID[] = OLESTR("FIR_AICSharpUtilLoader.FIR_AICSharpUtilLoader");
	hr = CLSIDFromProgID(lpszProgID, &loaderid);
	if( FAILED( hr ) ) {
		printf("CLSIDFromProgID Failed!\n");
		printf("hr = %d\n", hr);
		CoUninitialize();
		return false;
	}
	/*printf("CLSIDFromProgID Succeeded!classid=%X-%X-%X-%X%X-%X-%X-%X-%X-%X-%X\n", 
		loaderid.Data1, loaderid.Data2, loaderid.Data3,
		loaderid.Data4[0], loaderid.Data4[1], loaderid.Data4[2], loaderid.Data4[3], 
		loaderid.Data4[4], loaderid.Data4[5], loaderid.Data4[6], loaderid.Data4[7] );//*/

	//IDispatch* t_loader;
	hr = CoCreateInstance(loaderid, NULL, CLSCTX_ALL, IID_IDispatch, (void**)&loader);//
	if( hr == S_OK ){
		//printf( "指定的Com对象实例被成功创建。\n" );
	} else if( hr == REGDB_E_CLASSNOTREG ){
		//printf( "指定的类没有在注册表中注册. 也可能是指定的dwClsContext没有注册或注册表中的服务器类型损坏。\n" );
	} else if ( hr == CLASS_E_NOAGGREGATION ){
		//printf( "这个类不能创建为聚合型。\n" );
	} else if ( hr == E_NOINTERFACE ){
		//printf( "指定的类没有实现请求的接口, 或者是IUnknown接口没有暴露请求的接口。\n" );
	} else {
		//printf( "其他错误！\n" );
	}
	if( FAILED( hr ) ) {
		printf("CoCreateInstance Failed!\n");
		printf("hr = %d\n", hr); 
		printf( "error = %d\n", GetLastError() );
		return false;
	}
	//printf("CoCreateInstance Succeeded\n");


	HRESULT hr1 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_addFirAIDll,1,LOCALE_SYSTEM_DEFAULT,&dispid_addFirAIDll);
	HRESULT hr2 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_isPrintInfo,1,LOCALE_SYSTEM_DEFAULT,&dispid_isPrintInfo);
	HRESULT hr3 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getId,1,LOCALE_SYSTEM_DEFAULT,&dispid_getId);
	HRESULT hr4 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_itsmyturn,1,LOCALE_SYSTEM_DEFAULT,&dispid_itsmyturn);
	HRESULT hr5 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_itsmyturn2,1,LOCALE_SYSTEM_DEFAULT,&dispid_itsmyturn2);
	HRESULT hr6 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_setCurrentAIIndex,1,LOCALE_SYSTEM_DEFAULT,&dispid_setCurrentAIIndex);
	HRESULT hr7 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getName,1,LOCALE_SYSTEM_DEFAULT,&dispid_getName);
	HRESULT hr8 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getNickname,1,LOCALE_SYSTEM_DEFAULT,&dispid_getNickname);
	HRESULT hr9 = loader->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getVersion,1,LOCALE_SYSTEM_DEFAULT,&dispid_getVersion);
	
	if(FAILED(hr1)||FAILED(hr2)||FAILED(hr3)||FAILED(hr4)||FAILED(hr5)
		||FAILED(hr6)||FAILED(hr7)||FAILED(hr8)||FAILED(hr9)) {
		printf("GetIDsOfNames Faield!\n");
		printf("hr1 = %d\n", hr1);
		printf("hr2 = %d\n", hr2);
		printf("hr3 = %d\n", hr3);
		printf("hr4 = %d\n", hr4);
		printf("hr5 = %d\n", hr5);
		printf("hr6 = %d\n", hr6);
		printf("hr7 = %d\n", hr7);
		printf("hr8 = %d\n", hr8);
		printf("hr9 = %d\n", hr9);
		loader->Release();
		CoUninitialize();
		return false;
	}

	isInitialized = true;
	return true;

}

bool invoke( DISPPARAMS* dispParams, DISPID& dispid, VARIANT* vtResult = NULL, IDispatch* dispatch = loader ){
	VARIANT tvtResult;
	UINT dwArgErr;
	EXCEPINFO FAR  pExcepInfo;

	HRESULT hr_invoke = dispatch->Invoke(dispid,
		IID_NULL,
		LOCALE_SYSTEM_DEFAULT,
		DISPATCH_METHOD,//DISPATCH_PROPERTYPUT
		dispParams, vtResult != NULL? vtResult:&tvtResult, &pExcepInfo,
		&dwArgErr);
	if(FAILED(hr_invoke)) {
		if( hr_invoke == DISP_E_EXCEPTION ){
			char desc[512]; memset( desc, 0, 512*sizeof(char) );
			w2c( desc, pExcepInfo.bstrDescription, 512 );
			printf( "pExcepInfo = %s\n", desc );
		}
		printf( "error = %d\n", GetLastError() );
		printf( "hr = %d\n", hr_invoke );
		printf( "dwArgErr = %d\n", dwArgErr );
		//ppv->Release();
		return false;
	}
	return true;
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    setCurrentAIIndex
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_setCurrentAIIndex
  (JNIEnv *env, jclass firAIClass, jint index){
	if( ! initialize() ){ printf("Initializing failed!\n"); return; }
	VARIANTARG v[1];
	v[0].vt = VT_INT;
	v[0].intVal = index;
	DISPPARAMS dispParams = { v, NULL, 1, 0 };
	invoke( &dispParams, dispid_setCurrentAIIndex );
	return;
}
/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    addFirAIDll
 * Signature: (Ljava/lang/String;)Z;
 */
JNIEXPORT jboolean JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_addFirAIDll
  (JNIEnv * env, jclass firAIClass, jstring firAIDllFileName){
		if( ! initialize() ){ printf("Initializing failed!\n"); return JNI_FALSE; }
		string fileNamestring = jstring2str( env, firAIDllFileName );
		const char* fileNameChars = fileNamestring.data();
		char* t_fileNameChars = new char[strlen(fileNameChars)+1];
		strcpy( t_fileNameChars, fileNameChars );

		wchar_t *pwstr = new wchar_t[512];
		c2w( pwstr, 512, t_fileNameChars );

		VARIANTARG v[1];
		v[0].vt = VT_BSTR;
		v[0].bstrVal = SysAllocString( pwstr );
		DISPPARAMS dispParams = { v, NULL, 1, 0 };
		VARIANT vtResult;
		if( invoke( &dispParams, dispid_addFirAIDll, &vtResult )){
			//delete []t_fileNameChars;
			if( vtResult.boolVal ){ return JNI_TRUE; } 
			else{ return JNI_FALSE; }
		} else {
			return JNI_FALSE;
		}
}
/*
jobject getTree( JNIEnv * env, jclass c_GameTree, jmethodID m_GameTree_ctor, 
	jmethodID m_GameTree_addChild, GameTree*  root ){
	if( root == NULL ) return NULL;
	if( root->getName() == NULL ){
		jobject jroot = env->NewObject( c_GameTree, m_GameTree_ctor, 
			NULL, str2jstring(env, root->getValue()   )
		);
		return jroot;
	} else {
		jobject jroot = env->NewObject( c_GameTree, m_GameTree_ctor, 
			str2jstring(env, root->getName()  ),
			root->getValue() == NULL? NULL: str2jstring(env, root->getValue() )
		);
		list<GameTree*>::iterator iter = root->getTreeChildren()->begin();
		for( ; iter != root->getTreeChildren()->end(); iter ++ ){
			jobject jchild = getTree( env, c_GameTree, m_GameTree_ctor, m_GameTree_addChild, *iter );
			if( jchild != NULL ){
				env->CallObjectMethod( jroot, m_GameTree_addChild, jchild );
			}
		}
		return jroot;
	}
}//*/

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    getLastGameTree
 * Signature: ()Lcom/njucs/aiep/game/GameTree;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_getLastGameTree
	(JNIEnv * env, jobject  firAI){
		/*
		GameTree* gameTree = myFirAI.getLastGameTree();
		if( gameTree == NULL ) return NULL;
		jclass c_GameTree = env->FindClass( "Lcom/njucs/aiep/game/GameTree;" );
		jmethodID m_GameTree_ctor = env->GetMethodID( c_GameTree, 
			"<init>", "(Ljava/lang/String;Ljava/lang/String;)V" );
		jmethodID m_GameTree_addChild = env->GetMethodID( c_GameTree,
			"addChild", "(Lcom/njucs/aiep/game/GameTree;)V" );

		return getTree( env, c_GameTree, m_GameTree_ctor, m_GameTree_addChild, gameTree );
		//*/
		return NULL;
}
/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    itsmyturn
 * Signature: (Lcom/njucs/aiep/plugin/fir/frame/Step;)Lcom/njucs/aiep/plugin/fir/frame/Step;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_itsmyturn
	(JNIEnv * env, jobject firAI, jobject lastStep ){

		jclass c_step = env->GetObjectClass(lastStep);
		jmethodID m_step_init = env->GetMethodID( c_step, "<init>", "(II)V" );
		jmethodID m_step_getX = env->GetMethodID( c_step, "getX", "()I" );
		jmethodID m_step_getY = env->GetMethodID( c_step, "getY", "()I" );
		jint last_step_x = (jint)env->CallObjectMethod( lastStep, m_step_getX );
		jint last_step_y = (jint)env->CallObjectMethod( lastStep, m_step_getY );
		//printf( "last_step_x = %d, last_step_y = %d\n", last_step_x, last_step_y );

		VARIANTARG lastStepV[3];
		lastStepV[0].vt = VT_INT;
		lastStepV[0].intVal = last_step_y;
		lastStepV[1].vt = VT_INT;
		lastStepV[1].intVal = last_step_x;
		lastStepV[2].vt = VT_INT;
		lastStepV[2].intVal = 2;//status
		DISPPARAMS dispParams = { lastStepV, NULL, 3, 0 };
		VARIANT vtResult;
		if( invoke( &dispParams, dispid_itsmyturn, &vtResult )){
			IDispatch FAR* pstep = (vtResult.pdispVal);
			DISPID dispid_getx, dispid_gety;
			const LPOLESTR szMember_getX = OLESTR("getX");
			const LPOLESTR szMember_getY = OLESTR("getY");
			HRESULT hr_invoke1 = pstep->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getX,1,LOCALE_SYSTEM_DEFAULT,&dispid_getx);
			HRESULT hr_invoke2 = pstep->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getY,1,LOCALE_SYSTEM_DEFAULT,&dispid_gety);
			if( SUCCEEDED(hr_invoke1) && SUCCEEDED(hr_invoke2) ) {
				DISPPARAMS dispParamsX = { NULL, NULL, 0, 0 };
				DISPPARAMS dispParamsY = { NULL, NULL, 0, 0 };
				VARIANT vtResultX, vtResultY;
				if( invoke( &dispParamsX, dispid_getx, &vtResultX, pstep ) && invoke( &dispParamsY, dispid_gety, &vtResultY, pstep )  ){
					//printf( "vtResultX.intVal = %d, vtResultY.intVal = %d\n", vtResultX.intVal, vtResultY.intVal );
					return env->NewObject( c_step, m_step_init, (jint)(vtResultX.intVal), (jint)(vtResultY.intVal) );
				}
			} else {
				printf( "GetIDsOfNames failed!\n" );
				printf( "error = %d\n", GetLastError() );
				printf( "hr_invoke1 = %d\n", hr_invoke1 );
				printf( "hr_invoke2 = %d\n", hr_invoke2 );
			}
		}
		return NULL;

		
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    itsmyturn2
 * Signature: (Lcom/njucs/aiep/plugin/fir/frame/Step;)Lcom/njucs/aiep/plugin/fir/frame/Step;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_itsmyturn2
	(JNIEnv * env, jobject firAI, jobject lastStep ){
		
		jclass c_step = env->GetObjectClass(lastStep);
		jmethodID m_step_init = env->GetMethodID( c_step, "<init>", "(II)V" );
		jmethodID m_step_getX = env->GetMethodID( c_step, "getX", "()I" );
		jmethodID m_step_getY = env->GetMethodID( c_step, "getY", "()I" );
		jint last_step_x = (jint)env->CallObjectMethod( lastStep, m_step_getX );
		jint last_step_y = (jint)env->CallObjectMethod( lastStep, m_step_getY );

		VARIANTARG lastStepV[3];
		lastStepV[0].vt = VT_INT;
		lastStepV[0].intVal = 2;
		lastStepV[1].vt = VT_INT;
		lastStepV[1].intVal = last_step_x;
		lastStepV[2].vt = VT_INT;
		lastStepV[2].intVal = last_step_y;
		DISPPARAMS dispParams = { lastStepV, NULL, 3, 0 };
		VARIANT vtResult;
		if( invoke( &dispParams, dispid_itsmyturn2, &vtResult )){
			IDispatch FAR* pstep = (vtResult.pdispVal);
			DISPID dispid_getx, dispid_gety;
			const LPOLESTR szMember_getX = OLESTR("getX");
			const LPOLESTR szMember_getY = OLESTR("getY");
			HRESULT hr_invoke1 = pstep->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getX,1,LOCALE_SYSTEM_DEFAULT,&dispid_getx);
			HRESULT hr_invoke2 = pstep->GetIDsOfNames(IID_NULL, (LPOLESTR*)&szMember_getY,1,LOCALE_SYSTEM_DEFAULT,&dispid_gety);
			if( SUCCEEDED(hr_invoke1) && SUCCEEDED(hr_invoke2) ) {
				DISPPARAMS dispParams = { NULL, NULL, 0, 0 };
				VARIANT vtResultX, vtResultY;
				if( invoke( &dispParams, dispid_getx, &vtResultX, pstep ) && invoke( &dispParams, dispid_gety, &vtResultY, pstep )  ){
					return env->NewObject( c_step, m_step_init, (jint)(vtResultX.intVal), (jint)(vtResultY.intVal) );
				}
			} else {
				printf( "GetIDsOfNames failed!\n" );
				printf( "error = %d\n", GetLastError() );
				printf( "hr_invoke1 = %d\n", hr_invoke1 );
				printf( "hr_invoke2 = %d\n", hr_invoke2 );
			}
		}
		return NULL;

		
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    setInningInfo
 * Signature: (Lcom/njucs/aiep/game/Status;ILcom/njucs/aiep/net/DataTransmit/StudentInfo;[Lcom/njucs/aiep/game/Status;)V
 */
JNIEXPORT void JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_setInningInfo
	(JNIEnv * env, jobject firAI, jint myStatus, jint limitedTime, 
	jobject opponentInfo, jintArray piecesArray) {
		/*
		Status v_my_status = Status::EMPTY;
		if( myStatus == Status::OFFENSIVE )v_my_status = Status::OFFENSIVE;
		else if( myStatus == Status::DEFENSIVE)v_my_status = Status::DEFENSIVE;
		int v_limited_time = (int)limitedTime;

		jclass c_studentInfo = env->FindClass( "Lcom/njucs/aiep/net/DataTransmit$StudentInfo;" );
		jmethodID m_studentInfo_getId = env->GetMethodID( c_studentInfo, "getId", "()Ljava/lang/String;" );
		jmethodID m_studentInfo_getName = env->GetMethodID( c_studentInfo, "getName", "()Ljava/lang/String;" );
		jmethodID m_studentInfo_getNickname = env->GetMethodID( c_studentInfo, "getNickname", "()Ljava/lang/String;" );
		jstring jstr_opponent_id = (jstring)(env->CallObjectMethod( opponentInfo, m_studentInfo_getId ));
		jstring jstr_opponent_name = (jstring)(env->CallObjectMethod( opponentInfo, m_studentInfo_getName ));
		jstring jstr_opponent_nickname = (jstring)(env->CallObjectMethod( opponentInfo, m_studentInfo_getNickname ));
		//char * v_opponent_id = jstring2str( env, jstr_opponent_id );
		const static int MAX_LEN = AIEPP_FIR::MAX_LEN;
		char v_opponent_id[MAX_LEN], v_opponent_name[MAX_LEN], v_opponent_nickname[MAX_LEN];
		if( jstr_opponent_id != NULL && jstr_opponent_name != NULL && jstr_opponent_nickname != NULL ) {
			strncpy_s( v_opponent_id, env->GetStringUTFChars(jstr_opponent_id, JNI_FALSE), MAX_LEN );
			strncpy_s( v_opponent_name, env->GetStringUTFChars(jstr_opponent_name, JNI_FALSE), MAX_LEN );
			strncpy_s( v_opponent_nickname, env->GetStringUTFChars(jstr_opponent_nickname, JNI_FALSE), MAX_LEN );
		}
		StudentInfo v_opponent_info( v_opponent_id,v_opponent_name, v_opponent_nickname );


		Status v_pieces_array[AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION];
		jint *ji_piecesArray = env->GetIntArrayElements( piecesArray, JNI_FALSE );
		for( int i = 0; i < AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION; i ++ ){
			v_pieces_array[i] = Status::EMPTY;
			if( ji_piecesArray[i] == Status::OFFENSIVE ) v_pieces_array[i] = Status::OFFENSIVE;
			else if( ji_piecesArray[i] == Status::DEFENSIVE ) v_pieces_array[i] = Status::DEFENSIVE;
		}
		myFirAI.setInningInfo( v_my_status, v_limited_time, &v_opponent_info, v_pieces_array );
		/*/
		return;
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    getId
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_getId
	(JNIEnv * env, jobject){
		DISPPARAMS dispParams = { NULL, NULL, 0, 0 };
		VARIANT vtResult;
		if(invoke( &dispParams, dispid_getId, &vtResult )){
			char idchars[100];
			memset( idchars, 0, 100*sizeof(char) );
			w2c( idchars, vtResult.bstrVal, 100 );
			return str2jstring( env, idchars );
		} else {
			return str2jstring( env, "ERROR" );
		}
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    getName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_getName
	(JNIEnv * env, jobject){
		DISPPARAMS dispParams = { NULL, NULL, 0, 0 };
		VARIANT vtResult;
		if(invoke( &dispParams, dispid_getName, &vtResult )){
			char namechars[100];
			memset( namechars, 0, 100*sizeof(char) );
			w2c( namechars, vtResult.bstrVal, 100 );
			return str2jstring( env, namechars );
		} else {
			return str2jstring( env, "ERROR" );
		}
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    getNickname
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_getNickname
	(JNIEnv * env, jobject){
		DISPPARAMS dispParams = { NULL, NULL, 0, 0 };
		VARIANT vtResult;
		if(invoke( &dispParams, dispid_getNickname, &vtResult )){
			char nicknamechars[100];
			memset( nicknamechars, 0, 100*sizeof(char) );
			w2c( nicknamechars, vtResult.bstrVal, 100 );
			return str2jstring( env, nicknamechars );
		} else {
			return str2jstring( env, "ERROR" );
		}
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_getVersion
	(JNIEnv * env, jobject){
		DISPPARAMS dispParams = { NULL, NULL, 0, 0 };
		VARIANT vtResult;
		if(invoke( &dispParams, dispid_getVersion, &vtResult )){
			char versionchars[100];
			memset( versionchars, 0, 100*sizeof(char) );
			w2c( versionchars, vtResult.bstrVal, 100 );
			return str2jstring( env, versionchars );
		} else {
			return str2jstring( env, "ERROR" );
		}
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICSharp
 * Method:    isPrintInfo
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICSharp_isPrintInfo
	(JNIEnv *, jobject){
		DISPPARAMS dispParams = { NULL, NULL, 0, 0 };
		VARIANT vtResult;
		if( invoke( &dispParams, dispid_isPrintInfo, &vtResult ) ){
			return vtResult.boolVal;
		} else {
			return false;
		}
}

/*

*/

#endif