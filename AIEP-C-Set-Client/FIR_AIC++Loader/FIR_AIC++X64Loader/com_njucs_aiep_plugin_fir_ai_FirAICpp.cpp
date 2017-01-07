/**************************************
My FIR AI CPP for Java
@author ygsx
@time 2013年6月9日13:51:56
****************************************/
#ifdef _WINDLL
#define _WIN32_DCOM
#include "java\com_njucs_aiep_plugin_fir_ai_FirAICpp.h"
#include "MyFirAI.h"
#include<Windows.h>
#include <vector>

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

#define  __X64_DLL__
/*
typedef Step* (*Itsmyturn)(Step*);
typedef Step* (*Itsmyturn2)(Step*);
typedef void (*SetInningInfo)( Status, int, StudentInfo*, Status[]);
typedef char* (*GetId)();
typedef char* (*GetName)();
typedef char* (*GetNickname)();
typedef bool (*IsPrintInfo)();

//typedef char* (*GetVersion)();
typedef char* (*GetAIVersion)();
typedef GameTree* (*GetLastGameTree)();


//static MyFirAI myFirAI;
vector<char*> dllFileNameCharsVector;
vector<HINSTANCE> hFirAIDllVector;
vector<Itsmyturn> g_itsmyturnVector;
vector<Itsmyturn2> g_itsmyturn2Vector;
vector<SetInningInfo> g_setInningInfoVector;
vector<GetId> g_getIdVector;
vector<GetName> g_getNameVector;
vector<GetNickname> g_getNicknameVector;
vector<IsPrintInfo> g_isPrintInfoVector;
vector<GetAIVersion> g_getVersionVector;
vector<GetLastGameTree> g_getLastGameTreeVector;


int currentIndex = 0;
//*/
extern void c2w(wchar_t *pwstr,size_t len,const char *str);
void c2w(wchar_t *pwstr,size_t len,const char *str) {
	if(str)  {
		size_t nu = strlen(str);
		size_t n =(size_t)MultiByteToWideChar(CP_ACP,0,(const char *)str,(int)nu,NULL,0);
		if(n>=len) n=len-1;
		MultiByteToWideChar(CP_ACP,0,(const char *)str,(int)nu,pwstr,(int)n);
		pwstr[n]=0;
    }
}
static bool initialized = false;
static HRESULT hResult;
static IFirAI* pT = NULL;
bool initialize(){
	if( initialized ) return true;
	//CoInitialize( NULL );
	CoInitializeEx( NULL, COINIT_MULTITHREADED);
#ifndef  __X64_DLL__
	hResult = CoInitializeSecurity( NULL, 
				-1, 
				NULL,
				NULL,
				RPC_C_AUTHN_LEVEL_NONE, 
				RPC_C_IMP_LEVEL_IDENTIFY, 
				NULL, 
				EOAC_NONE, 
				NULL );
	if( !SUCCEEDED( hResult ) ) {
		printf( "init right failed!\n" );
		printf( "hr = %d\n", hResult );
		return false;
	} else {
#endif //  __X64_DLL__
		COSERVERINFO si;
		MULTI_QI     qi;
		ZeroMemory( &si, sizeof( si ) );
		ZeroMemory( &qi, sizeof( qi ) );
		si.pwszName = L"127.0.0.1";//remote host = local host
		si.pAuthInfo = NULL;
		qi.pIID = &IID_IFirAI;
		qi.pItf = NULL;
		hResult = CoCreateInstanceEx(CLSID_FirAI, NULL, CLSCTX_REMOTE_SERVER, &si, 1, &qi);
		if( FAILED( hResult ) ) {
			printf( "Can not create myobject : %d\n", GetLastError() );
			printf( "hr = %d\n", hResult );
			return false;
		}  else if (FAILED(qi.hr)) {
			printf( "Can not create myobject : %d\n", GetLastError() );
			printf( "hr = %d\n", hResult );
			return false;
		} else {
			qi.pItf->QueryInterface( &pT );//get the interfaces
			qi.pItf->Release();
			printf( "[initialize]初始化成功！\n" );
			initialized = true;
			return true;
		}
#ifndef __X64_DLL__
	}
#endif
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
}


/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    setCurrentAIIndex
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_setCurrentAIIndex
  (JNIEnv *env, jclass firAIClass, jint index){
	  //currentIndex = (int)index;
	  initialize();
	  if( pT == NULL ) return;
	  pT->setCurrentAIIndex( index );
	  printf( "设置成索引：index=%d\n", index );
	printf( "[try]x=3,y=3\n" );
	Step step( EMPTY, 3, 3 );
	long newStepX, newStepY, newStepIntStatus;
	pT->itsmyturn( 3,3, EMPTY, &newStepX, &newStepY, &newStepIntStatus );
	printf( "[try]x=%d,y=%d\n", newStepX, newStepY );
}
/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    addFirAIDll
 * Signature: (Ljava/lang/String;)Z;
 */
JNIEXPORT jboolean JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_addFirAIDll
  (JNIEnv * env, jclass firAIClass, jstring firAIDllFileName){
	  if(! initialize()) {
		  printf( "初始化失败！\n" );
		  return JNI_FALSE;
	  } else {
		  printf( "初始化成功！\n" );
	  }
	  if( pT == NULL ) return JNI_FALSE;
	  string fileNameString = jstring2str( env, firAIDllFileName );
	  printf( "转换到string结束！\n" );
	  const char* fileNameChars = fileNameString.data();
	  wchar_t* pwstr = new wchar_t[512];
	  c2w(pwstr, 512, fileNameChars);
	  long errorId, returnValue = 0;
	  printf( "转化到wchar_t结束！\n" );
	  BSTR d = SysAllocString( pwstr );
	  pT->addFirAIDll( d, &errorId, &returnValue );
	  printf( "添加结束！当前AI DLL 数量：%d\n", returnValue );
	  //SysFreeString(d);
	  if( returnValue >= 0 ) return JNI_TRUE;
	  else return JNI_FALSE;
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getLastGameTree
 * Signature: ()Lcom/njucs/aiep/game/GameTree;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getLastGameTree
	(JNIEnv * env, jobject  firAI){
		//env->CallObjectMethod( firAI,  );
		/*
		GameTree* gameTree = g_getLastGameTreeVector[currentIndex]();
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
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    itsmyturn
 * Signature: (Lcom/njucs/aiep/plugin/fir/frame/Step;)Lcom/njucs/aiep/plugin/fir/frame/Step;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_itsmyturn
	(JNIEnv * env, jobject firAI, jobject lastStep ){
		initialize();
		if( pT == NULL ) return NULL;
		printf( "调用Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_itsmyturn\n");
		jclass c_step = env->GetObjectClass(lastStep);
		jmethodID m_step_init = env->GetMethodID( c_step, "<init>", "(II)V" );
		jmethodID m_step_getX = env->GetMethodID( c_step, "getX", "()I" );
		jmethodID m_step_getY = env->GetMethodID( c_step, "getY", "()I" );
		jint new_x = (jint)env->CallObjectMethod( lastStep, m_step_getX );
		jint new_y = (jint)env->CallObjectMethod( lastStep, m_step_getY );
		printf( "调用Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_itsmyturn:x=%d,y=%d\n",new_x, new_y );
		Step step(EMPTY, new_x, new_y);
		//Step *newStep;
		long newStepX, newStepY, newStepIntStatus;
		pT->itsmyturn( new_x,new_y, EMPTY, &newStepX, &newStepY, &newStepIntStatus );
		Status newStepStatus = Status::EMPTY;
		if( newStepIntStatus == Status::OFFENSIVE ) newStepStatus = Status::OFFENSIVE;
		else if( newStepIntStatus == Status::DEFENSIVE ) newStepStatus = Status::DEFENSIVE;
		//newStep = new Step( newStepStatus, newStepX,newStepY );
		printf( "新的一步：x=%d,y=%d\n", newStepX,newStepY );
		return env->NewObject( c_step, m_step_init, 
			(jint)(newStepX), (jint)(newStepY) );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    itsmyturn2
 * Signature: (Lcom/njucs/aiep/plugin/fir/frame/Step;)Lcom/njucs/aiep/plugin/fir/frame/Step;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_itsmyturn2
	(JNIEnv * env, jobject firAI, jobject lastStep ){
		initialize();
		if( pT == NULL ) return NULL;
		printf( "调用Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_itsmyturn2\n");
		jclass c_step = env->GetObjectClass(lastStep);
		jmethodID m_step_init = env->GetMethodID( c_step, "<init>", "(II)V" );
		jmethodID m_step_getX = env->GetMethodID( c_step, "getX", "()I" );
		jmethodID m_step_getY = env->GetMethodID( c_step, "getY", "()I" );
		jint new_x = (jint)env->CallObjectMethod( lastStep, m_step_getX );
		jint new_y = (jint)env->CallObjectMethod( lastStep, m_step_getY );
		printf( "调用Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_itsmyturn2:x=%d,y=%d\n",new_x, new_y );
		Step step(EMPTY, new_x, new_y);
		//Step *newStep;
		long newStepX, newStepY, newStepIntStatus;
		pT->itsmyturn2( new_x,new_y, EMPTY, &newStepX, &newStepY, &newStepIntStatus );
		Status newStepStatus = Status::EMPTY;
		if( newStepIntStatus == Status::OFFENSIVE ) newStepStatus = Status::OFFENSIVE;
		else if( newStepIntStatus == Status::DEFENSIVE ) newStepStatus = Status::DEFENSIVE;
		//newStep = new Step( newStepStatus, newStepX,newStepY );
		printf( "新的一步：x=%d,y=%d\n", newStepX,newStepY );
		return env->NewObject( c_step, m_step_init,
			(jint)(newStepX), (jint)(newStepY) );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    setInningInfo
 * Signature: (Lcom/njucs/aiep/game/Status;ILcom/njucs/aiep/net/DataTransmit/StudentInfo;[Lcom/njucs/aiep/game/Status;)V
 */
JNIEXPORT void JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_setInningInfo
	(JNIEnv * env, jobject firAI, jint myStatus, jint limitedTime, 
	jobject opponentInfo, jintArray piecesArray) {
		initialize();
		if( pT == NULL ) return;
		//jclass e_status = env->FindClass( "Lcom/njucs/aiep/game/Status;" );
		printf( "调用Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_setInningInfo\n" );
		long v_my_status = myStatus;
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
		//StudentInfo v_opponent_info( v_opponent_id,v_opponent_name, v_opponent_nickname );
		wchar_t* pwstr_opponent_id = new wchar_t[MAX_LEN];
		wchar_t* pwstr_opponent_name = new wchar_t[MAX_LEN];
		wchar_t* pwstr_opponent_nickname = new wchar_t[MAX_LEN];
		c2w(pwstr_opponent_id, MAX_LEN, v_opponent_id);
		c2w(pwstr_opponent_name, MAX_LEN, v_opponent_name);
		c2w(pwstr_opponent_nickname, MAX_LEN, v_opponent_nickname);
		BSTR d_pwstr_opponent_id = SysAllocString( pwstr_opponent_id );
		BSTR d_pwstr_opponent_name = SysAllocString( pwstr_opponent_name );
		BSTR d_pwstr_opponent_nickname = SysAllocString( pwstr_opponent_nickname );

		long v_pieces_array[AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION];
		jint *ji_piecesArray = env->GetIntArrayElements( piecesArray, JNI_FALSE );
		for( int i = 0; i < AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION; i ++ ){
			v_pieces_array[i] = ji_piecesArray[i];
		}
		//g_setInningInfoVector[currentIndex]( v_my_status, v_limited_time, &v_opponent_info, v_pieces_array );
		pT->setInningInfo( v_my_status, v_limited_time, d_pwstr_opponent_id,
			d_pwstr_opponent_name, d_pwstr_opponent_nickname, v_pieces_array );
		printf( "设置初始化棋盘信息完毕\n" );
		return;
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getId
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getId
	(JNIEnv * env, jobject){
		BSTR aiinfo;
		long errorId;
		pT->getId( &errorId, &aiinfo );
		char* pAiInfo = _com_util::ConvertBSTRToString( aiinfo );
		return str2jstring( env, pAiInfo );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getName
	(JNIEnv * env, jobject){
		BSTR aiinfo;
		pT->getName( &aiinfo );
		char* pAiInfo = _com_util::ConvertBSTRToString( aiinfo );
		return str2jstring( env, pAiInfo );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getNickname
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getNickname
	(JNIEnv * env, jobject){
		BSTR aiinfo;
		pT->getNickname( &aiinfo );
		char* pAiInfo = _com_util::ConvertBSTRToString( aiinfo );
		return str2jstring( env, pAiInfo );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getVersion
	(JNIEnv * env, jobject){
		BSTR aiinfo;
		pT->getVersion( &aiinfo );
		char* pAiInfo = _com_util::ConvertBSTRToString( aiinfo );
		return str2jstring( env, pAiInfo );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    isPrintInfo
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_isPrintInfo
	(JNIEnv *, jobject){
		long print;
		pT->isPrintInfo( &print );
		if( print > 0 ) return JNI_TRUE;
		else return JNI_FALSE;
}

/*

*/

#endif