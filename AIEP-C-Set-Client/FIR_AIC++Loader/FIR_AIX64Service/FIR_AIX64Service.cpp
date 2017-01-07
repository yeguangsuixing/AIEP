// FIR_AIX64Service.cpp : WinMain ��ʵ��


#include "stdafx.h"
#include "resource.h"
#include "FIR_AIX64Service_i.h"


#include <stdio.h>

class CFIR_AIX64ServiceModule : public ATL::CAtlServiceModuleT< CFIR_AIX64ServiceModule, IDS_SERVICENAME >
	{
public :
	DECLARE_LIBID(LIBID_FIR_AIX64ServiceLib)
	DECLARE_REGISTRY_APPID_RESOURCEID(IDR_FIR_AIX64SERVICE, "{071B5EEA-FD41-4BA8-B666-B1AFF3CF490D}")
		HRESULT InitializeSecurity() throw()
	{
		// TODO : ���� CoInitializeSecurity ��Ϊ�����ṩ�ʵ��İ�ȫ����
		// ���� - PKT ����������֤��
		// RPC_C_IMP_LEVEL_IDENTIFY ��ģ�⼶��
		// �Լ��ʵ��ķ� null ��ȫ˵������
		return CoInitializeSecurity( NULL, -1, NULL, NULL, RPC_C_AUTHN_LEVEL_NONE,
			RPC_C_IMP_LEVEL_IDENTIFY, NULL, EOAC_NONE, NULL );
		//return S_OK;
	}

		HRESULT RegisterAppId(bool bService);
	};

CFIR_AIX64ServiceModule _AtlModule;



//
extern "C" int WINAPI _tWinMain(HINSTANCE /*hInstance*/, HINSTANCE /*hPrevInstance*/, 
								LPTSTR /*lpCmdLine*/, int nShowCmd)
{
	return _AtlModule.WinMain(nShowCmd);
}


HRESULT CFIR_AIX64ServiceModule::RegisterAppId(bool bService = false) throw()
{
	HRESULT hr = S_OK;
	BOOL res = __super::RegisterAppId(bService); 
	if (bService)
	{
		if (IsInstalled())
		{
			SC_HANDLE hSCM = ::OpenSCManagerW(NULL, NULL, SERVICE_CHANGE_CONFIG);
			SC_HANDLE hService = NULL;
			if (hSCM == NULL){
				hr = HRESULT_FROM_WIN32(GetLastError());
					//hr = AtlHresultFromLastError();
			} else
			{
					hService = ::OpenService(hSCM, m_szServiceName, SERVICE_CHANGE_CONFIG);
					if (hService != NULL)
					{
							::ChangeServiceConfigW(hService, SERVICE_NO_CHANGE, 
															SERVICE_AUTO_START,	//auto start
															NULL, NULL, NULL, NULL, NULL, NULL, NULL,
															m_szServiceName); // ͨ���޸���ԴIDS_SERVICENAME �޸ķ������ʾ����

							SERVICE_DESCRIPTION Description;
							TCHAR	 szDescription[1024];
							ZeroMemory(szDescription, 1024);
							ZeroMemory(&Description, sizeof(SERVICE_DESCRIPTION));
							lstrcpy(szDescription, _T("This is the service that making NJUCS-AIEP-FIR compatible with 64-bit Windows OS.��64λDLL������32λDLL���Ǹ���ӣ���"));
							Description.lpDescription = szDescription;
							::ChangeServiceConfig2(hService, SERVICE_CONFIG_DESCRIPTION, &Description);

							::CloseServiceHandle(hService);
					}
					else{
						hr = HRESULT_FROM_WIN32(GetLastError());
						//hr = AtlHresultFromLastError();
					}
					::CloseServiceHandle(hSCM);
			}
		}
	}
	return hr;
}