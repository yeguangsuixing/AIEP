/**************************************
My FIR AI CPP for Java
@author ygsx
@time 2013年6月9日13:51:56
****************************************/


#include "java\com_njucs_aiep_plugin_fir_ai_FirAICpp.h"
#include "MyFirAI.h"
#include<Windows.h>
#include <vector>

typedef Step* (*Itsmyturn)(Step*);
typedef Step* (*Itsmyturn2)(Step*);
typedef void (*SetInningInfo)( Status, int, StudentInfo*, Status[]);
typedef char* (*GetId)();
typedef char* (*GetName)();
typedef char* (*GetNickname)();
typedef bool (*IsPrintInfo)();
/*
com_njucs_aiep_plugin_fir_ai_FirAICpp.cpp(24): error C2365: “GetVersion”: 重定义；以前的定义是“函数”
          C:\Program Files\Microsoft SDKs\Windows\v7.0A\include\winbase.h(2643) : 参见“GetVersion”的声明
*/
//typedef char* (*GetVersion)();
typedef char* (*GetAIVersion)();
typedef GameTree* (*GetLastGameTree)();


//static MyFirAI myFirAI;
vector<char*> dllFileNameCharsVector;
vector<HINSTANCE> hFirAIDllVector;
//*
vector<Itsmyturn> g_itsmyturnVector;
vector<Itsmyturn2> g_itsmyturn2Vector;
vector<SetInningInfo> g_setInningInfoVector;
vector<GetId> g_getIdVector;
vector<GetName> g_getNameVector;
vector<GetNickname> g_getNicknameVector;
vector<IsPrintInfo> g_isPrintInfoVector;

vector<GetAIVersion> g_getVersionVector;
vector<GetLastGameTree> g_getLastGameTreeVector;
//*/

int currentIndex = 0;


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
	  currentIndex = (int)index;
}
/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    addFirAIDll
 * Signature: (Ljava/lang/String;)Z;
 */
JNIEXPORT jboolean JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_addFirAIDll
  (JNIEnv * env, jclass firAIClass, jstring firAIDllFileName){
	  string fileNameString = jstring2str( env, firAIDllFileName );
	  const char* fileNameChars = fileNameString.data();
	  HINSTANCE hFirAIDll = LoadLibrary( fileNameChars );
	  if( hFirAIDll == NULL ){
		  FreeLibrary(hFirAIDll);
		  printf( "Cannot load the Library(%s)!Error:%d\n", fileNameChars, GetLastError() );
		  return JNI_FALSE;
	  }
	  
	  //*
	  Itsmyturn t_itsmyturn = (Itsmyturn)GetProcAddress( hFirAIDll, "itsmyturn" );
	  Itsmyturn2 t_itsmyturn2 = (Itsmyturn2)GetProcAddress( hFirAIDll, "itsmyturn2" );
	  SetInningInfo t_setInningInfo = (SetInningInfo)GetProcAddress( hFirAIDll, "setInningInfo" );
	  
	  GetId t_getId = (GetId)GetProcAddress(hFirAIDll,"getId");
	  GetName t_getName = (GetName)GetProcAddress(hFirAIDll,"getName");
	  GetNickname t_getNickname = (GetNickname)GetProcAddress(hFirAIDll,"getNickname");
	  IsPrintInfo t_isPrintInfo = (IsPrintInfo)GetProcAddress( hFirAIDll, "isPrintInfo" );
	  
	  GetAIVersion t_getVersion = (GetAIVersion)GetProcAddress(hFirAIDll,"getVersion");
	  GetLastGameTree t_getLastGameTree = (GetLastGameTree)GetProcAddress( hFirAIDll, "getLastGameTree" );

	  if( t_itsmyturn == NULL || t_itsmyturn2 == NULL 
		  || t_setInningInfo == NULL || t_getId == NULL
		  || t_getName == NULL || t_getNickname == NULL
		  || t_isPrintInfo == NULL || t_getVersion == NULL || t_getLastGameTree == NULL ){
			  printf( "Some function(s) cannot be found in the given DLL file.\n" );
			  return JNI_FALSE;
	  }
	  hFirAIDllVector.push_back(hFirAIDll);
	  g_itsmyturnVector.push_back( t_itsmyturn );
	  g_itsmyturn2Vector.push_back( t_itsmyturn2 );
	  g_setInningInfoVector.push_back(t_setInningInfo);
	  g_getIdVector.push_back(t_getId);
	  g_getNameVector.push_back(t_getName);
	  g_getNicknameVector.push_back(t_getNickname);
	  g_isPrintInfoVector.push_back(t_isPrintInfo);
	  g_getVersionVector.push_back(t_getVersion);
	  g_getLastGameTreeVector.push_back(t_getLastGameTree);
	  return JNI_TRUE;
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getLastGameTree
 * Signature: ()Lcom/njucs/aiep/game/GameTree;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getLastGameTree
	(JNIEnv * env, jobject  firAI){
		//env->CallObjectMethod( firAI,  );
		GameTree* gameTree = g_getLastGameTreeVector[currentIndex]();
		if( gameTree == NULL ) return NULL;
		jclass c_GameTree = env->FindClass( "Lcom/njucs/aiep/game/GameTree;" );
		jmethodID m_GameTree_ctor = env->GetMethodID( c_GameTree, 
			"<init>", "(Ljava/lang/String;Ljava/lang/String;)V" );
		jmethodID m_GameTree_addChild = env->GetMethodID( c_GameTree,
			"addChild", "(Lcom/njucs/aiep/game/GameTree;)V" );

		return getTree( env, c_GameTree, m_GameTree_ctor, m_GameTree_addChild, gameTree );

}



/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    itsmyturn
 * Signature: (Lcom/njucs/aiep/plugin/fir/frame/Step;)Lcom/njucs/aiep/plugin/fir/frame/Step;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_itsmyturn
	(JNIEnv * env, jobject firAI, jobject lastStep ){
		jclass c_step = env->GetObjectClass(lastStep);
		jmethodID m_step_init = env->GetMethodID( c_step, "<init>", "(II)V" );
		jmethodID m_step_getX = env->GetMethodID( c_step, "getX", "()I" );
		jmethodID m_step_getY = env->GetMethodID( c_step, "getY", "()I" );
		jint new_x = (jint)env->CallObjectMethod( lastStep, m_step_getX );
		jint new_y = (jint)env->CallObjectMethod( lastStep, m_step_getY );
		Step step(EMPTY, new_x, new_y);
		Step *newStep = g_itsmyturnVector[currentIndex]( &step );
		return env->NewObject( c_step, m_step_init, 
			(jint)(newStep->getX()), (jint)(newStep->getY()) );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    itsmyturn2
 * Signature: (Lcom/njucs/aiep/plugin/fir/frame/Step;)Lcom/njucs/aiep/plugin/fir/frame/Step;
 */
JNIEXPORT jobject JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_itsmyturn2
	(JNIEnv * env, jobject firAI, jobject lastStep ){
		jclass c_step = env->GetObjectClass(lastStep);
		jmethodID m_step_init = env->GetMethodID( c_step, "<init>", "(II)V" );
		jmethodID m_step_getX = env->GetMethodID( c_step, "getX", "()I" );
		jmethodID m_step_getY = env->GetMethodID( c_step, "getY", "()I" );
		jint last_step_x = (jint)env->CallObjectMethod( lastStep, m_step_getX );
		jint last_step_y = (jint)env->CallObjectMethod( lastStep, m_step_getY );
		Step n_last_step(EMPTY, last_step_x, last_step_y);
		Step *n_new_step = g_itsmyturn2Vector[currentIndex]( &n_last_step );
		return env->NewObject( c_step, m_step_init, 
			(jint)(n_new_step->getX()), (jint)(n_new_step->getY()) );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    setInningInfo
 * Signature: (Lcom/njucs/aiep/game/Status;ILcom/njucs/aiep/net/DataTransmit/StudentInfo;[Lcom/njucs/aiep/game/Status;)V
 */
JNIEXPORT void JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_setInningInfo
	(JNIEnv * env, jobject firAI, jint myStatus, jint limitedTime, 
	jobject opponentInfo, jintArray piecesArray) {
		//jclass e_status = env->FindClass( "Lcom/njucs/aiep/game/Status;" );
		
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
		/*//*/

		Status v_pieces_array[AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION];
		jint *ji_piecesArray = env->GetIntArrayElements( piecesArray, JNI_FALSE );
		for( int i = 0; i < AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION; i ++ ){
			v_pieces_array[i] = Status::EMPTY;
			if( ji_piecesArray[i] == Status::OFFENSIVE ) v_pieces_array[i] = Status::OFFENSIVE;
			else if( ji_piecesArray[i] == Status::DEFENSIVE ) v_pieces_array[i] = Status::DEFENSIVE;
		}
		g_setInningInfoVector[currentIndex]( v_my_status, v_limited_time, &v_opponent_info, v_pieces_array );
		/*//*/
		return;
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getId
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getId
	(JNIEnv * env, jobject){
		return str2jstring( env, g_getIdVector[currentIndex]() );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getName
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getName
	(JNIEnv * env, jobject){
		return str2jstring( env, g_getNameVector[currentIndex]() );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getNickname
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getNickname
	(JNIEnv * env, jobject){
		return str2jstring( env, g_getNicknameVector[currentIndex]() );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    getVersion
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_getVersion
	(JNIEnv * env, jobject){
		return str2jstring( env, g_getVersionVector[currentIndex]() );
}

/*
 * Class:     com_njucs_aiep_plugin_fir_ai_FirAICpp
 * Method:    isPrintInfo
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_com_njucs_aiep_plugin_fir_ai_FirAICpp_isPrintInfo
	(JNIEnv *, jobject){
		if( g_isPrintInfoVector[currentIndex]() ){
			return JNI_TRUE;
		} else {
			return JNI_FALSE;
		}
}

/*

*/