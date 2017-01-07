

//2013Äê7ÔÂ5ÈÕ20:05:26
///DELAYSIGN /KEYFILE:"F:\VS2010 Project\AIEP-CSharp-Client\FIR_AICS\fircs.snk" 
#include <atlbase.h>
#include <atlcom.h>
#include <atlctl.h>

#using <mscorlib.dll>
#using <System.dll>
#using "../FIR_AICSGenerator/bin/Release/AIEP-CSharp.dll"
#using "../FIR_AICSGenerator/bin/Release/FIR_AICS.dll"
#using "../FIR_AICSGenerator/bin/Release/FIR_AICSGenerator.dll"
using namespace System;
using namespace System::Runtime::InteropServices;

#include "AIEPP-Plugin-FIR/FIR_AI.h"
#include "AIEPP-Plugin-FIR/FIRLoader.h"
#include "AIEPP-Plugin-FIR/Step.h"

#ifdef _DEBUG
#pragma comment(lib, "lib/Debug/AIEPP-Plugin-FIR.lib")
#else
#pragma comment(lib, "lib/Release/AIEPP-Plugin-FIR.lib")
#endif

namespace AICLR {
	class FIR_AICLR : FIR_AI {
		//AICS::FIR_AI^ firAI;
	public:
		FIR_AICLR();


		char* getId();
		char* getName();
		char* getNickname();
		char* getVersion();
		bool isPrintInfo();
	
		Step* itsmyturn( const Step*  lastStep);
		Step* itsmyturn2( const Step*  opponentStep);
	
	
		void setInningInfo( Status myStatus, int limitedTime, 
			 StudentInfo* opponentInfo,  Status piecesArray[]);
	
		GameTree* getLastGameTree();

	};

}
