using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AICS
{
    public class Test
    {
        public static void Main(String[] args)
        {
            FIR_AI ai = new MyFirAI();
            FIRLoader loader = new FIRLoader(ai);
            loader.setHostIp("127.0.0.1");
            loader.setHostPort(8890);
            loader.execute();
            Console.ReadLine();
        }
    }
}
