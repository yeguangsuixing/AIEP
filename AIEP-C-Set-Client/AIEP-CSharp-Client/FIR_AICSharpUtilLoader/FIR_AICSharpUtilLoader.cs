using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AICS;
using System.Reflection;
using System.Runtime.InteropServices;

namespace FIR_AICSharpUtilLoader
{
    //*
    [ComVisible(true)]
    [Guid("FA938286-19FF-434A-BA84-85231CC8A42A")]
    [InterfaceTypeAttribute(ComInterfaceType.InterfaceIsIDispatch)]
    public interface IFIR_AICSharpUtilLoader
    {
    }//*/
    //375539e6-4cc6-4e7e-86ac-6058d00aea8e
    [ComVisible(true)]
    [Guid("FA938286-19FF-434A-BA84-85231CC8A42A")]
    [ProgId("FIR_AICSharpUtilLoader.FIR_AICSharpUtilLoader")]
    public class FIR_AICSharpUtilLoader : IFIR_AICSharpUtilLoader
    {
         IList<String> firAIDllFileNameList = new List<String>();
         IList<FIR_AIWrapper> firAIList = new List<FIR_AIWrapper>();
         int currentIndex = 0;

        [DispId(1)]
        public void setCurrentAIIndex(int index)
        {
            currentIndex = index;
        }

        [DispId(2)]
        public bool addFirAIDll(String firAIDllFileName)//[In, MarshalAs(UnmanagedType.BStr)] 
        {
            //firAIDllFileName = @"F:\VS2010 Project\AIEP-CSharp-Client\FIR_AICS\obj\Release\MyFirAI.dll";
            //File.ReadAllBytes(Path.GetDirectoryName(Application.ExecutablePath)
            if( firAIDllFileName == null ) return false;
            foreach( String dll in  firAIDllFileNameList ){
                if (dll.Equals(firAIDllFileName)) {
                    Console.WriteLine( "[C#]The Same Dll File!" );
                    return false;
                }
            }
            Assembly assembly = Assembly.LoadFrom(firAIDllFileName);
            if (assembly == null) 
            {
                Console.WriteLine("[C#]Cannot load the library(%s)!", firAIDllFileName);
                return false;
            }
            int beginindex = firAIDllFileName.LastIndexOf('\\')+1;
            int endindex = firAIDllFileName.LastIndexOf('.');
            String firAIClassName = firAIDllFileName.Substring(beginindex, endindex - beginindex);
            //Console.WriteLine("[C#]firAIClassName=" + firAIClassName);
            Type firAIType = assembly.GetType("AICS."+firAIClassName, true);
            if (firAIType == null)
            {
                Console.WriteLine( "[C#]DLL File is Dirty!" );
                return false;
            }
            FIR_AIWrapper wrapper = new FIR_AIWrapper();
            if (wrapper.initialize(firAIType))
            {
                firAIDllFileNameList.Add(firAIDllFileName);
                firAIList.Add(wrapper);
                return true;
            }
            else
            {
                return false;
            }//*/
        }

        [DispId(3)]
        public GameTree getLastGameTree() 
        {
            return firAIList[currentIndex].getLastGameTree();
        }

        [DispId(4)]
        public Step itsmyturn(int status, int x, int y)
        {
            Step opponentStep = new Step((Status)status, x, y);
            //Console.WriteLine( "[C#]x = "+x+", y = " + y);
            Step newStep = firAIList[currentIndex].itsmyturn(opponentStep);
            //Console.WriteLine("[C#]x = " + newStep.getX() + ", y = " + newStep.getY());
            return newStep;
        }


        [DispId(5)]
        public Step itsmyturn2(int status, int x, int y)
        {
            Step opponentStep = new Step((Status)status, x, y);
            return firAIList[currentIndex].itsmyturn2(opponentStep);
        }

        [DispId(6)]
        public void setInningInfo(int myStatus, int limitedTime,
            String opponentId, String opponentName, String opponentNickname, Status[] piecesArray)
        {
            StudentInfo opponentInfo = new StudentInfo(opponentId, opponentName, opponentNickname);
            firAIList[currentIndex].setInningInfo((Status)myStatus, limitedTime, opponentInfo, piecesArray);
        }

        [DispId(7)]
        public String getId() 
        {
            return firAIList[currentIndex].getId();
        }

        [DispId(8)]
        public String getName()
        {
            return firAIList[currentIndex].getName();
        }

        [DispId(9)]
        public String getNickname() 
        {
            return firAIList[currentIndex].getNickname();
        }

        [DispId(10)]
        public String getVersion() 
        {
            return firAIList[currentIndex].getVersion();
        }

        [DispId(11)]
        public bool isPrintInfo()
        {
            return firAIList[currentIndex].isPrintInfo();
        }


        [DispId(12)]
        public Step testUnexportClass()
        {
            return new Step(Status.DEFENSIVE, 3, 4);
        }

        public static void Main(String[] args) {
            FIR_AICSharpUtilLoader loader = new FIR_AICSharpUtilLoader();
            loader.addFirAIDll("F:\\VS2010 Project\\FIR_AICSharp\\FIR_AICSharp\\bin\\Release\\MyFirAI.dll");
            //loader.addFirAIDll(@"F:\VS2010 Project\AIEP-CSharp-Client\FIR_AICS\obj\Release\MyFirAI.dll");
            loader.setCurrentAIIndex(0);


            String id = loader.getId();
            Console.WriteLine( "id = "+id );


            Step newStep = loader.itsmyturn(0, 4, 6);
            Console.WriteLine("newStep = x: "+newStep.getX() +", y = "+ newStep.getY());
            Console.ReadLine();
        }

    }
}
