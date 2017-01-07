using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AICS
{
    public class FIR_AICSGenerator
    {
        public static FIR_AI firAI;

        public static FIR_AI getFIR_AI() {
            return firAI;
        }

        public static void setFIR_AI(FIR_AI firAI) {
            FIR_AICSGenerator.firAI = firAI;
        }
    }
}
