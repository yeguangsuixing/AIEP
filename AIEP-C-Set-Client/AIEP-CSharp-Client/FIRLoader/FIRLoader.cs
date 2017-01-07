using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AICLR;

namespace AICS
{
    public class FIRLoader
    {
        static FIRLoaderCLR loaderCLR;

        public FIRLoader() { }

        public FIRLoader( FIR_AI firAi ) {
            FIR_AICSGenerator.setFIR_AI(firAi);
            loaderCLR = new FIRLoaderCLR();
        }

        public void setHostIp(String hostIp) {
            loaderCLR.setHostIp(hostIp);
        }

        public void setHostPort(int hostPort) {
            loaderCLR.setHostPort(hostPort);
        }

        public String getHostIp() {
            return loaderCLR.getHostIp();
        }

        public int getHostPort() {
            return loaderCLR.getHostPort();
        }

        public void forceExit() {
            loaderCLR.forceExit();
        }
        public void execute() {
            loaderCLR.execute();
        }
    }
}
