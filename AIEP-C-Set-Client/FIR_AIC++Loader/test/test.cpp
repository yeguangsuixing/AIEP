// test.cpp : WinMain 的实现


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
		// TODO : 调用 CoInitializeSecurity 并为服务提供适当的安全设置
		// 建议 - PKT 级别的身份验证、
		// RPC_C_IMP_LEVEL_IDENTIFY 的模拟级别
		// 以及适当的非 null 安全说明符。

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

