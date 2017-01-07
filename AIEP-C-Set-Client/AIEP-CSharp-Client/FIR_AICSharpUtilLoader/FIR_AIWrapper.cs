using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using AICS;
using System.Reflection;

namespace FIR_AICSharpUtilLoader
{
    public class FIR_AIWrapper : FIR_AI
    {
        Type firAIType;
        object obj;
        MethodInfo itsmyturnMethod, itsmyturn2Method, setInfoMethod, getLastGameTreeMethod;
        MethodInfo getIdMethod, getNameMethod, getNicknameMethod, getVersionMethod, isPrintInfoMethod;
        public bool initialize(Type firAIType) 
        {
            this.firAIType = firAIType;
            this.obj = System.Activator.CreateInstance(firAIType);
            Type steptype = new Step(Status.EMPTY, 0, 0).GetType();
            Type statustype = Status.EMPTY.GetType();
            int a = 0; Type inttype = a.GetType();
            Type stutype = new StudentInfo(null,null,null).GetType();
            Status[] statusarray = new Status[] { };
            Type statusarraytype = statusarray.GetType();
            try
            {
                itsmyturnMethod = firAIType.GetMethod("itsmyturn", new Type[] { steptype });
                itsmyturn2Method = firAIType.GetMethod("itsmyturn2", new Type[] { steptype });
                setInfoMethod = firAIType.GetMethod( "setInningInfo", new Type[]{
                    statustype, inttype, stutype,statusarraytype
                } );
                getLastGameTreeMethod = firAIType.GetMethod("getLastGameTree", new Type[]{ } );

                getIdMethod = firAIType.GetMethod("getId", new Type[] { });
                getNameMethod = firAIType.GetMethod("getName", new Type[] { });
                getNicknameMethod = firAIType.GetMethod("getNickname", new Type[] { });
                getVersionMethod = firAIType.GetMethod("getVersion", new Type[] { });
                isPrintInfoMethod = firAIType.GetMethod("isPrintInfo", new Type[] { });
                return true;
            }
            catch (Exception e) 
            {
                Console.WriteLine( e.ToString() );
                return false;
            }

        }

        public Step itsmyturn(Step opponentStep)
        {
            return (Step)itsmyturnMethod.Invoke(obj, new Object[] { opponentStep });
        }
        public Step itsmyturn2(Step opponentStep)
        {
            return (Step)itsmyturn2Method.Invoke(obj, new Object[] { opponentStep });
        }

        public bool isPrintInfo()
        {
            return (bool)isPrintInfoMethod.Invoke(obj, new Object[]{});
        }

        public void setInningInfo(Status myStatus, int limitedTime, StudentInfo opponentInfo,
            Status[] piecesArray)
        {
            setInfoMethod.Invoke(obj, new Object[] { myStatus, limitedTime, opponentInfo, piecesArray });
        }

        public String getId()
        {
            return (String)getIdMethod.Invoke(obj, new Object[]{});
        }
        public String getName()
        {
            return (String)getNameMethod.Invoke(obj, new Object[] { });
        }
        public String getNickname()
        {
            return (String)getNicknameMethod.Invoke(obj, new Object[] { });
        }
        public String getVersion()
        {
            return (String)getVersionMethod.Invoke(obj, new Object[] { });
        }
        public GameTree getLastGameTree()
        {
            return (GameTree)getLastGameTreeMethod.Invoke( obj, new Object[]{  } );
        }
    }
}
