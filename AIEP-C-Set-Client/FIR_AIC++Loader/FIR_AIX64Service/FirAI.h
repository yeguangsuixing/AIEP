// FirAI.h : CFirAI ������

#pragma once
#include "resource.h"       // ������



#include "FIR_AIX64Service_i.h"
#include "MyFirAI.h"

#include <vector>
typedef Step* (*Itsmyturn)(Step*);
typedef Step* (*Itsmyturn2)(Step*);
typedef void (*SetInningInfo)( Status, int, StudentInfo*, Status[]);
typedef char* (*GetId)();
typedef char* (*GetName)();
typedef char* (*GetNickname)();
typedef bool (*IsPrintInfo)();
//typedef GameTree* (*GetLastGameTree)();

typedef char* (*GetAIVersion)();

#if defined(_WIN32_WCE) && !defined(_CE_DCOM) && !defined(_CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA)
#error "Windows CE ƽ̨(�粻�ṩ��ȫ DCOM ֧�ֵ� Windows Mobile ƽ̨)���޷���ȷ֧�ֵ��߳� COM ���󡣶��� _CE_ALLOW_SINGLE_THREADED_OBJECTS_IN_MTA ��ǿ�� ATL ֧�ִ������߳� COM ����ʵ�ֲ�����ʹ���䵥�߳� COM ����ʵ�֡�rgs �ļ��е��߳�ģ���ѱ�����Ϊ��Free����ԭ���Ǹ�ģ���Ƿ� DCOM Windows CE ƽ̨֧�ֵ�Ψһ�߳�ģ�͡�"
#endif

using namespace ATL;


// CFirAI

class ATL_NO_VTABLE CFirAI :
	public CComObjectRootEx<CComSingleThreadModel>,
	public CComCoClass<CFirAI, &CLSID_FirAI>,
	public IDispatchImpl<IFirAI, &IID_IFirAI, &LIBID_FIR_AIX64ServiceLib, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
private:
	std::vector<HINSTANCE> m_hFirAIDllVector;
	std::vector<char*> m_dllFileNameCharsVector;
	//*
	std::vector<Itsmyturn> m_itsmyturnVector;
	std::vector<Itsmyturn2> m_itsmyturn2Vector;
	std::vector<SetInningInfo> m_setInningInfoVector;
	std::vector<GetId> m_getIdVector;
	std::vector<GetName> m_getNameVector;
	std::vector<GetNickname> m_getNicknameVector;
	std::vector<IsPrintInfo> m_isPrintInfoVector;

	std::vector<GetAIVersion> m_getVersionVector;
	//std::vector<GetLastGameTree> m_getLastGameTreeVector;

	int currentIndex, csDllCount;
	enum ProLang { CPP, CS }  language;
public:
	CFirAI()
	{
		csDllCount = 0;
	}

DECLARE_REGISTRY_RESOURCEID(IDR_FIRAI)


BEGIN_COM_MAP(CFirAI)
	COM_INTERFACE_ENTRY(IFirAI)
	COM_INTERFACE_ENTRY(IDispatch)
END_COM_MAP()



	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}

public:



	STDMETHOD(setCurrentAIIndex)(LONG aiIndex);
	STDMETHOD(addFirAIDll)(BSTR firAIDllFileName, LONG* errorId, LONG* returnValue);
	STDMETHOD(getId)(LONG* errorId, BSTR* aiId);
	STDMETHOD(test)(LONG* value);
	STDMETHOD(itsmyturn)(LONG lastStepX, LONG lastStepY, LONG lastStepStatus, LONG* newStepX, LONG* newStepY, LONG* newStepStatus);
	STDMETHOD(itsmyturn2)(LONG lastStepX, LONG lastStepY, LONG lastStepStatus, LONG* newStepX, LONG* newStepY, LONG* newStepStatus);
	STDMETHOD(setInningInfo)(LONG myStatus, LONG limitedTime, BSTR opponentId, BSTR opponentName, BSTR opponentNickname, LONG* piecesIntArray);
	STDMETHOD(getName)(BSTR* aiName);
	STDMETHOD(getNickname)(BSTR* aiNickname);
	STDMETHOD(getVersion)(BSTR* aiVersion);
	STDMETHOD(isPrintInfo)(LONG* print);
	STDMETHOD(setProgramLanguage)(LONG language);
};

OBJECT_ENTRY_AUTO(__uuidof(FirAI), CFirAI)
