

/* this ALWAYS GENERATED file contains the definitions for the interfaces */


 /* File created by MIDL compiler version 7.00.0555 */
/* at Tue Jul 02 00:38:15 2013
 */
/* Compiler settings for FIR_AIX64Service.idl:
    Oicf, W1, Zp8, env=Win32 (32b run), target_arch=X86 7.00.0555 
    protocol : dce , ms_ext, c_ext, robust
    error checks: allocation ref bounds_check enum stub_data 
    VC __declspec() decoration level: 
         __declspec(uuid()), __declspec(selectany), __declspec(novtable)
         DECLSPEC_UUID(), MIDL_INTERFACE()
*/
/* @@MIDL_FILE_HEADING(  ) */

#pragma warning( disable: 4049 )  /* more than 64k source lines */


/* verify that the <rpcndr.h> version is high enough to compile this file*/
#ifndef __REQUIRED_RPCNDR_H_VERSION__
#define __REQUIRED_RPCNDR_H_VERSION__ 475
#endif

#include "rpc.h"
#include "rpcndr.h"

#ifndef __RPCNDR_H_VERSION__
#error this stub requires an updated version of <rpcndr.h>
#endif // __RPCNDR_H_VERSION__

#ifndef COM_NO_WINDOWS_H
#include "windows.h"
#include "ole2.h"
#endif /*COM_NO_WINDOWS_H*/

#ifndef __FIR_AIX64Service_i_h__
#define __FIR_AIX64Service_i_h__

#if defined(_MSC_VER) && (_MSC_VER >= 1020)
#pragma once
#endif

/* Forward Declarations */ 

#ifndef __IFirAI_FWD_DEFINED__
#define __IFirAI_FWD_DEFINED__
typedef interface IFirAI IFirAI;
#endif 	/* __IFirAI_FWD_DEFINED__ */


#ifndef __FirAI_FWD_DEFINED__
#define __FirAI_FWD_DEFINED__

#ifdef __cplusplus
typedef class FirAI FirAI;
#else
typedef struct FirAI FirAI;
#endif /* __cplusplus */

#endif 	/* __FirAI_FWD_DEFINED__ */


/* header files for imported files */
#include "oaidl.h"
#include "ocidl.h"

#ifdef __cplusplus
extern "C"{
#endif 


#ifndef __IFirAI_INTERFACE_DEFINED__
#define __IFirAI_INTERFACE_DEFINED__

/* interface IFirAI */
/* [unique][nonextensible][dual][uuid][object] */ 


EXTERN_C const IID IID_IFirAI;

#if defined(__cplusplus) && !defined(CINTERFACE)
    
    MIDL_INTERFACE("307AB1F5-1C26-4D7D-9DB9-918F3C32CCCE")
    IFirAI : public IDispatch
    {
    public:
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE setCurrentAIIndex( 
            /* [in] */ LONG aiIndex) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE addFirAIDll( 
            /* [in] */ BSTR firAIDllFileName,
            /* [out] */ LONG *errorId,
            /* [retval][out] */ LONG *returnValue) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE getId( 
            /* [out] */ LONG *errorId,
            /* [retval][out] */ BSTR *aiId) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE test( 
            /* [retval][out] */ LONG *value) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE itsmyturn( 
            /* [in] */ LONG lastStepX,
            /* [in] */ LONG lastStepY,
            /* [in] */ LONG lastStepStatus,
            /* [out] */ LONG *newStepX,
            /* [out] */ LONG *newStepY,
            /* [out] */ LONG *newStepStatus) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE itsmyturn2( 
            /* [in] */ LONG lastStepX,
            /* [in] */ LONG lastStepY,
            /* [in] */ LONG lastStepStatus,
            /* [out] */ LONG *newStepX,
            /* [out] */ LONG *newStepY,
            /* [out] */ LONG *newStepStatus) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE setInningInfo( 
            /* [in] */ LONG myStatus,
            /* [in] */ LONG limitedTime,
            /* [in] */ BSTR opponentId,
            /* [in] */ BSTR opponentName,
            /* [in] */ BSTR opponentNickname,
            /* [in] */ LONG *piecesIntArray) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE getName( 
            /* [retval][out] */ BSTR *aiName) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE getNickname( 
            /* [retval][out] */ BSTR *aiNickname) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE getVersion( 
            /* [retval][out] */ BSTR *aiVersion) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE isPrintInfo( 
            /* [retval][out] */ LONG *print) = 0;
        
        virtual /* [id] */ HRESULT STDMETHODCALLTYPE setProgramLanguage( 
            /* [in] */ LONG language) = 0;
        
    };
    
#else 	/* C style interface */

    typedef struct IFirAIVtbl
    {
        BEGIN_INTERFACE
        
        HRESULT ( STDMETHODCALLTYPE *QueryInterface )( 
            IFirAI * This,
            /* [in] */ REFIID riid,
            /* [annotation][iid_is][out] */ 
            __RPC__deref_out  void **ppvObject);
        
        ULONG ( STDMETHODCALLTYPE *AddRef )( 
            IFirAI * This);
        
        ULONG ( STDMETHODCALLTYPE *Release )( 
            IFirAI * This);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfoCount )( 
            IFirAI * This,
            /* [out] */ UINT *pctinfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetTypeInfo )( 
            IFirAI * This,
            /* [in] */ UINT iTInfo,
            /* [in] */ LCID lcid,
            /* [out] */ ITypeInfo **ppTInfo);
        
        HRESULT ( STDMETHODCALLTYPE *GetIDsOfNames )( 
            IFirAI * This,
            /* [in] */ REFIID riid,
            /* [size_is][in] */ LPOLESTR *rgszNames,
            /* [range][in] */ UINT cNames,
            /* [in] */ LCID lcid,
            /* [size_is][out] */ DISPID *rgDispId);
        
        /* [local] */ HRESULT ( STDMETHODCALLTYPE *Invoke )( 
            IFirAI * This,
            /* [in] */ DISPID dispIdMember,
            /* [in] */ REFIID riid,
            /* [in] */ LCID lcid,
            /* [in] */ WORD wFlags,
            /* [out][in] */ DISPPARAMS *pDispParams,
            /* [out] */ VARIANT *pVarResult,
            /* [out] */ EXCEPINFO *pExcepInfo,
            /* [out] */ UINT *puArgErr);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *setCurrentAIIndex )( 
            IFirAI * This,
            /* [in] */ LONG aiIndex);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *addFirAIDll )( 
            IFirAI * This,
            /* [in] */ BSTR firAIDllFileName,
            /* [out] */ LONG *errorId,
            /* [retval][out] */ LONG *returnValue);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *getId )( 
            IFirAI * This,
            /* [out] */ LONG *errorId,
            /* [retval][out] */ BSTR *aiId);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *test )( 
            IFirAI * This,
            /* [retval][out] */ LONG *value);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *itsmyturn )( 
            IFirAI * This,
            /* [in] */ LONG lastStepX,
            /* [in] */ LONG lastStepY,
            /* [in] */ LONG lastStepStatus,
            /* [out] */ LONG *newStepX,
            /* [out] */ LONG *newStepY,
            /* [out] */ LONG *newStepStatus);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *itsmyturn2 )( 
            IFirAI * This,
            /* [in] */ LONG lastStepX,
            /* [in] */ LONG lastStepY,
            /* [in] */ LONG lastStepStatus,
            /* [out] */ LONG *newStepX,
            /* [out] */ LONG *newStepY,
            /* [out] */ LONG *newStepStatus);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *setInningInfo )( 
            IFirAI * This,
            /* [in] */ LONG myStatus,
            /* [in] */ LONG limitedTime,
            /* [in] */ BSTR opponentId,
            /* [in] */ BSTR opponentName,
            /* [in] */ BSTR opponentNickname,
            /* [in] */ LONG *piecesIntArray);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *getName )( 
            IFirAI * This,
            /* [retval][out] */ BSTR *aiName);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *getNickname )( 
            IFirAI * This,
            /* [retval][out] */ BSTR *aiNickname);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *getVersion )( 
            IFirAI * This,
            /* [retval][out] */ BSTR *aiVersion);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *isPrintInfo )( 
            IFirAI * This,
            /* [retval][out] */ LONG *print);
        
        /* [id] */ HRESULT ( STDMETHODCALLTYPE *setProgramLanguage )( 
            IFirAI * This,
            /* [in] */ LONG language);
        
        END_INTERFACE
    } IFirAIVtbl;

    interface IFirAI
    {
        CONST_VTBL struct IFirAIVtbl *lpVtbl;
    };

    

#ifdef COBJMACROS


#define IFirAI_QueryInterface(This,riid,ppvObject)	\
    ( (This)->lpVtbl -> QueryInterface(This,riid,ppvObject) ) 

#define IFirAI_AddRef(This)	\
    ( (This)->lpVtbl -> AddRef(This) ) 

#define IFirAI_Release(This)	\
    ( (This)->lpVtbl -> Release(This) ) 


#define IFirAI_GetTypeInfoCount(This,pctinfo)	\
    ( (This)->lpVtbl -> GetTypeInfoCount(This,pctinfo) ) 

#define IFirAI_GetTypeInfo(This,iTInfo,lcid,ppTInfo)	\
    ( (This)->lpVtbl -> GetTypeInfo(This,iTInfo,lcid,ppTInfo) ) 

#define IFirAI_GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId)	\
    ( (This)->lpVtbl -> GetIDsOfNames(This,riid,rgszNames,cNames,lcid,rgDispId) ) 

#define IFirAI_Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr)	\
    ( (This)->lpVtbl -> Invoke(This,dispIdMember,riid,lcid,wFlags,pDispParams,pVarResult,pExcepInfo,puArgErr) ) 


#define IFirAI_setCurrentAIIndex(This,aiIndex)	\
    ( (This)->lpVtbl -> setCurrentAIIndex(This,aiIndex) ) 

#define IFirAI_addFirAIDll(This,firAIDllFileName,errorId,returnValue)	\
    ( (This)->lpVtbl -> addFirAIDll(This,firAIDllFileName,errorId,returnValue) ) 

#define IFirAI_getId(This,errorId,aiId)	\
    ( (This)->lpVtbl -> getId(This,errorId,aiId) ) 

#define IFirAI_test(This,value)	\
    ( (This)->lpVtbl -> test(This,value) ) 

#define IFirAI_itsmyturn(This,lastStepX,lastStepY,lastStepStatus,newStepX,newStepY,newStepStatus)	\
    ( (This)->lpVtbl -> itsmyturn(This,lastStepX,lastStepY,lastStepStatus,newStepX,newStepY,newStepStatus) ) 

#define IFirAI_itsmyturn2(This,lastStepX,lastStepY,lastStepStatus,newStepX,newStepY,newStepStatus)	\
    ( (This)->lpVtbl -> itsmyturn2(This,lastStepX,lastStepY,lastStepStatus,newStepX,newStepY,newStepStatus) ) 

#define IFirAI_setInningInfo(This,myStatus,limitedTime,opponentId,opponentName,opponentNickname,piecesIntArray)	\
    ( (This)->lpVtbl -> setInningInfo(This,myStatus,limitedTime,opponentId,opponentName,opponentNickname,piecesIntArray) ) 

#define IFirAI_getName(This,aiName)	\
    ( (This)->lpVtbl -> getName(This,aiName) ) 

#define IFirAI_getNickname(This,aiNickname)	\
    ( (This)->lpVtbl -> getNickname(This,aiNickname) ) 

#define IFirAI_getVersion(This,aiVersion)	\
    ( (This)->lpVtbl -> getVersion(This,aiVersion) ) 

#define IFirAI_isPrintInfo(This,print)	\
    ( (This)->lpVtbl -> isPrintInfo(This,print) ) 

#define IFirAI_setProgramLanguage(This,language)	\
    ( (This)->lpVtbl -> setProgramLanguage(This,language) ) 

#endif /* COBJMACROS */


#endif 	/* C style interface */




#endif 	/* __IFirAI_INTERFACE_DEFINED__ */



#ifndef __FIR_AIX64ServiceLib_LIBRARY_DEFINED__
#define __FIR_AIX64ServiceLib_LIBRARY_DEFINED__

/* library FIR_AIX64ServiceLib */
/* [version][uuid] */ 


EXTERN_C const IID LIBID_FIR_AIX64ServiceLib;

EXTERN_C const CLSID CLSID_FirAI;

#ifdef __cplusplus

class DECLSPEC_UUID("7E59310E-D03B-43F9-958E-E798FAB1F2F5")
FirAI;
#endif
#endif /* __FIR_AIX64ServiceLib_LIBRARY_DEFINED__ */

/* Additional Prototypes for ALL interfaces */

unsigned long             __RPC_USER  BSTR_UserSize(     unsigned long *, unsigned long            , BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserMarshal(  unsigned long *, unsigned char *, BSTR * ); 
unsigned char * __RPC_USER  BSTR_UserUnmarshal(unsigned long *, unsigned char *, BSTR * ); 
void                      __RPC_USER  BSTR_UserFree(     unsigned long *, BSTR * ); 

/* end of Additional Prototypes */

#ifdef __cplusplus
}
#endif

#endif


