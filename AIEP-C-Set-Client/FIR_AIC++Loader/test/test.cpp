// test.cpp : WinMain ��ʵ��


#include "stdafx.h"
#include "resource.h"
#include "test_i.h"


#include <stdio.h>

class CtestModule : public ATL::CAtlServiceModuleT< CtestModule, IDS_SERVICENAME >
	{
public :
	DECLARE_LIBID(LIBID_testLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_TEST, "{14236AB6-80D9-4C38-986E-BBF7BD002E13}")
		HRESULT InitializeSecurity() throw()
	{
		// TODO : ���� CoInitializeSecurity ��Ϊ�����ṩ�ʵ��İ�ȫ����
		// ���� - PKT ����������֤��
		// RPC_C_IMP_LEVEL_IDENTIFY ��ģ�⼶��
		// �Լ��ʵ��ķ� null ��ȫ˵������

		return S_OK;
	}
	};

CtestModule _AtlModule;



//
extern "C" int WINAPI _tWinMain(HINSTANCE /*hInstance*/, HINSTANCE /*hPrevInstance*/, 
								LPTSTR /*lpCmdLine*/, int nShowCmd)
{
	return _AtlModule.WinMain(nShowCmd);
}

