
#include "FIR_AICLR.h"


namespace AICLR {
	public ref class FIRLoaderCLR {
		FIRLoader* firLoader;
		FIR_AICLR* firAICLR;
	public:
		FIRLoaderCLR();
		String^ getHostIp();
		int getHostPort();

		void setHostIp(String^ hostIp);
		void setHostPort(int hostPort);

		void forceExit();
		void execute();
	};
}

