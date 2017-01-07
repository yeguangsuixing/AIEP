using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;


namespace AICS
{
    public class MyFirAI : FIR_AI
    {

        /// <summary>
        /// Get the game tree generated at the last step
        /// </summary>
        /// <returns>the game tree root generated at the last step</returns>
        public GameTree getLastGameTree()
        {
            return null;
        }

        /// <summary>
        /// Obtain the  step of the current AI located.
        /// The first heuristic evaluation function.  This method will be called when 
        ///  the server chooses the "Heriustic1" Item.
        /// </summary>
        /// <param name="opponentStep">the last step of the opponent located</param>
        /// <returns>the new step</returns>
        public Step itsmyturn(Step opponentStep)
        {
            Console.WriteLine("\"itsmyturn\" is called by the system.");
            return new Step( Status.EMPTY, opponentStep.getX() + 1, opponentStep.getY() );
        }

        /// <summary>
        /// Obtain the  step of the current AI located.
        ///  The second heuristic evaluation function. This method will be called when 
        ///  the Judge chooses the "Heriustic2" Item.
        /// </summary>
        /// <param name="opponentStep">the last step of the opponent located</param>
        /// <returns>the new step</returns>
        public Step itsmyturn2(Step opponentStep)
        {
            return itsmyturn(opponentStep);
        }

        /// <summary>
        /// Set the inning info. When the data transmitter receives an info message, it will be called!
        /// </summary>
        /// <param name="myStatus">the current status</param>
        /// <param name="limitedTime">the limited time</param>
        /// <param name="opponentInfo">the opponent info</param>
        /// <param name="piecesArray">the initial inning array, length = 15*15 </param>
        public void setInningInfo(Status myStatus, int limitedTime, StudentInfo opponentInfo, Status[] piecesArray)
        {
            Console.WriteLine(myStatus);
            Console.WriteLine(limitedTime);
            Console.WriteLine(opponentInfo.getId());
            Console.WriteLine(piecesArray[0]);
            Console.WriteLine("\"setInningInfo\" is called by the system.");
        }

        /// <summary>
        /// Return the AI ID.
        /// </summary>
        /// <returns>the AI ID</returns>
        public String getId()
        {
            return "b101220004";
        }

        /// <summary>
        /// Return the AI name.
        /// </summary>
        /// <returns>the AI name</returns>
        public String getName()
        {
            return "EEEE";
        }

        /// <summary>
        /// Return the AI nickname.
        /// </summary>
        /// <returns>the AI nickname</returns>
        public String getNickname()
        {
            return "eeee";
        }

        /// <summary>
        /// Return the AI version.
        /// </summary>
        /// <returns>the AI version</returns>
        public String getVersion()
        {
            return "v0.1";
        }

        /// <summary>
        /// Do u want to print the receive info? Please return false if not. 
        /// </summary>
        /// <returns>true, if u want to print the recv info, false otherwize.</returns>
        public Boolean isPrintInfo()
        {
            return true;
        }

    }
}
