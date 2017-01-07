// FirAI.cpp : CFirAI 的实现

#include "stdafx.h"
#include "FirAI.h"


// CFirAI
#using <mscorlib.dll>
#using <System.dll>
#using <System.Windows.Forms.dll>
#using "../../AIEP-CSharp-Client/AIEP-CSharp/bin/Release/AIEP-CSharp.dll"
#using "../../AIEP-CSharp-Client/FIR_AICSharpUtilLoader/bin/Release/FIR_AICSharpUtilLoader.dll"
using namespace System;
using namespace System::Runtime::InteropServices;


STDMETHODIMP CFirAI::setCurrentAIIndex(LONG aiIndex)
{
	// TODO: 在此添加实现代码
	this->currentIndex = aiIndex;
	return S_OK;
}

//static HINSTANCE g_hFirAIDll;
//static GetId g_getId;

char *w2c(char *pcstr,const wchar_t *pwstr, size_t len)
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


STDMETHODIMP CFirAI::addFirAIDll(BSTR firAIDllFileName, LONG* errorId, LONG* returnValue)
{
	// TODO: 在此添加实现代码
	if( this->language == ProLang::CS ){
		FIR_AICSharpUtilLoader::FIR_AICSharpUtilLoader ^loader = gcnew FIR_AICSharpUtilLoader::FIR_AICSharpUtilLoader();
		String^ fileNameString= System::Runtime::InteropServices::Marshal::PtrToStringAnsi((IntPtr)firAIDllFileName); 
		bool rsl = loader->addFirAIDll( "F:\\Workspace\\AIEPServer\\res\\aitest\\MyFirAI.dll" );//fileNameString
		if( rsl ){
			*errorId = 555555;
			*returnValue = csDllCount++;
		} else {
			*errorId = GetLastError();
			*returnValue = -100;
		}
		return S_OK;
	}
	HINSTANCE hFirAIDll = LoadLibrary( firAIDllFileName );//
	if( hFirAIDll == NULL ){
		FreeLibrary(hFirAIDll);
		//printf( "Cannot load the Library(%s)!Error:%d\n", firAIDllFileName, GetLastError() );
		//return JNI_FALSE;
		*errorId = GetLastError();
		*returnValue = -200;
		return S_FALSE;
	} else {
		*errorId = 2345;
		*returnValue = -201;
	}
	
	//*
	Itsmyturn t_itsmyturn = (Itsmyturn)GetProcAddress( hFirAIDll, "itsmyturn" );
	Itsmyturn2 t_itsmyturn2 = (Itsmyturn2)GetProcAddress( hFirAIDll, "itsmyturn2" );
	GetId t_getId = (GetId)GetProcAddress(hFirAIDll,"getId");
	GetName t_getName = (GetName)GetProcAddress(hFirAIDll,"getName");
	GetNickname t_getNickname = (GetNickname)GetProcAddress(hFirAIDll,"getNickname");
	IsPrintInfo t_isPrintInfo = (IsPrintInfo)GetProcAddress( hFirAIDll, "isPrintInfo" );
	GetAIVersion t_getVersion = (GetAIVersion)GetProcAddress(hFirAIDll,"getVersion");

	SetInningInfo t_setInningInfo = (SetInningInfo)GetProcAddress( hFirAIDll, "setInningInfo" );

	 if( t_itsmyturn == NULL || t_itsmyturn2 == NULL || t_setInningInfo == NULL 
		  || t_getId == NULL
		  || t_getName == NULL || t_getNickname == NULL
		  || t_isPrintInfo == NULL || t_getVersion == NULL// || t_getLastGameTree == NULL
		 ){
			FreeLibrary(hFirAIDll);
			//printf( "Some function(s) cannot be found in the given DLL file.\n" );
			*errorId = GetLastError();
			*returnValue = -401;
			return S_FALSE;
	  }
	
	 m_hFirAIDllVector.push_back(hFirAIDll);
	 m_itsmyturnVector.push_back( t_itsmyturn );
	m_itsmyturn2Vector.push_back( t_itsmyturn2 );
	m_setInningInfoVector.push_back(t_setInningInfo);
	m_getIdVector.push_back(t_getId);
	m_getNameVector.push_back(t_getName);
	m_getNicknameVector.push_back(t_getNickname);
	m_isPrintInfoVector.push_back(t_isPrintInfo);
	m_getVersionVector.push_back(t_getVersion);
	//g_getLastGameTreeVector.push_back(t_getLastGameTree);
	//*/
	*returnValue = this->m_hFirAIDllVector.size() - 1;
	*errorId = 555666;
	return S_OK;
}


STDMETHODIMP CFirAI::getId(LONG* errorId, BSTR* aiId)
{
	// TODO: 在此添加实现代码
	char* aiid;
	if( this->language == ProLang::CS ){
		FIR_AICSharpUtilLoader::FIR_AICSharpUtilLoader ^loader = gcnew FIR_AICSharpUtilLoader::FIR_AICSharpUtilLoader();
		System::String^ pstrId = loader->getId();
		aiid = (char*)(void*)Marshal::StringToHGlobalAnsi(pstrId);
	} else {//CPP
		aiid = m_getIdVector[currentIndex]();
	}
	CComBSTR aidllStr = aiid;
	*aiId = SysAllocString( aidllStr.Copy() );
	*errorId = GetLastError();
	return S_OK;
}


STDMETHODIMP CFirAI::test(LONG* value)
{
	// TODO: 在此添加实现代码
	*value = 12354;
	return S_OK;
}




STDMETHODIMP CFirAI::itsmyturn(LONG lastStepX, LONG lastStepY, LONG lastStepStatus, LONG* newStepX, LONG* newStepY, LONG* newStepStatus)
{
	// TODO: 在此添加实现代码
	Status lastStatus = Status::EMPTY;
	if( lastStepStatus == Status::OFFENSIVE ){
		lastStatus = Status::OFFENSIVE ;
	} else if ( lastStepStatus == Status::DEFENSIVE ){
		lastStatus = Status::DEFENSIVE;
	}
	Step step( lastStatus, lastStepX, lastStepY );
	Step* newStep = m_itsmyturnVector[currentIndex](&step);
	*newStepX = newStep->getX();
	*newStepY = newStep->getY();
	*newStepStatus = newStep->getStatus();
	return S_OK;
}



STDMETHODIMP CFirAI::itsmyturn2(LONG lastStepX, LONG lastStepY, LONG lastStepStatus, LONG* newStepX, LONG* newStepY, LONG* newStepStatus)
{
	// TODO: 在此添加实现代码
	Status lastStatus = Status::EMPTY;
	if( lastStepStatus == Status::OFFENSIVE ){
		lastStatus = Status::OFFENSIVE ;
	} else if ( lastStepStatus == Status::DEFENSIVE ){
		lastStatus = Status::DEFENSIVE;
	}
	Step step( lastStatus, lastStepX, lastStepY );
	Step* newStep = m_itsmyturn2Vector[currentIndex](&step);
	*newStepX = newStep->getX();
	*newStepY = newStep->getY();
	*newStepStatus = newStep->getStatus();
	return S_OK;
}


STDMETHODIMP CFirAI::setInningInfo(LONG myStatus, LONG limitedTime, BSTR opponentId, 
	BSTR opponentName, BSTR opponentNickname, LONG* piecesIntArray)
{
	// TODO: 在此添加实现代码
	Status mystatus = Status::EMPTY;
	if( myStatus == Status::OFFENSIVE ) mystatus = Status::OFFENSIVE;
	else if( myStatus == Status::DEFENSIVE ) mystatus = Status::DEFENSIVE;
	StudentInfo *opponentInfo = new StudentInfo( "", "", "" );
	Status* piecesStatusArray = new Status[AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION];
	for( int i = 0; i < AIEPP_FIR::DIMENSION*AIEPP_FIR::DIMENSION; i ++ ){
		if( piecesIntArray[i] == 0 ) piecesStatusArray[i] = Status::OFFENSIVE;
		else if( piecesIntArray[i] == 1 ) piecesStatusArray[i] = Status::EMPTY;
		else piecesStatusArray[i] = Status::EMPTY;
	}
	m_setInningInfoVector[currentIndex]( mystatus, limitedTime, opponentInfo, piecesStatusArray );
	return S_OK;
}


STDMETHODIMP CFirAI::getName(BSTR* aiName)
{
	// TODO: 在此添加实现代码
	char* ainame = m_getNameVector[currentIndex]();
	CComBSTR aidllStr = ainame;
	*aiName = SysAllocString( aidllStr.Copy() );
	return S_OK;
}


STDMETHODIMP CFirAI::getNickname(BSTR* aiNickname)
{
	// TODO: 在此添加实现代码
	char* ainickname = m_getNicknameVector[currentIndex]();
	CComBSTR aidllStr = ainickname;
	*aiNickname = SysAllocString( aidllStr.Copy() );
	return S_OK;
}


STDMETHODIMP CFirAI::getVersion(BSTR* aiVersion)
{
	// TODO: 在此添加实现代码
	char* aiversion = m_getVersionVector[currentIndex]();
	CComBSTR aidllStr = aiversion;
	*aiVersion = SysAllocString( aidllStr.Copy() );
	return S_OK;
}


STDMETHODIMP CFirAI::isPrintInfo(LONG* print)
{
	// TODO: 在此添加实现代码
	bool t = m_isPrintInfoVector[currentIndex]();
	*print = t?TRUE:FALSE;
	return S_OK;
}


STDMETHODIMP CFirAI::setProgramLanguage(LONG language)
{
	// TODO: 在此添加实现代码
	if( language == ProLang::CPP ){
		this->language = ProLang::CPP;
	} else if( language == ProLang::CS ){
		this->language = ProLang::CS;
	}
	return S_OK;
}
