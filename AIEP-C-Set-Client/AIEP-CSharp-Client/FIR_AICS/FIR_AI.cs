using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

using System.Runtime.InteropServices;

namespace AICS
{
    /// <summary>
    /// FIR(Fice In Row) AI interface
    /// </summary>
    public interface FIR_AI : AI
    {
        /// <summary>
        /// Obtain the  step of the current AI located.<br />
        /// The first heuristic evaluation function.  <br />This method will be called when 
        ///  the server chooses the "Heriustic1" Item.
        /// </summary>
        /// <param name="opponentStep">the last step of the opponent located</param>
        /// <returns>the new step</returns>
        Step itsmyturn(Step opponentStep);

        /// <summary>
        /// Obtain the  step of the current AI located.<br />
        ///  The second heuristic evaluation function.<br /> This method will be called when 
        ///  the Judge chooses the "Heriustic2" Item.
        /// </summary>
        /// <param name="opponentStep">the last step of the opponent located</param>
        /// <returns>the new step</returns>
        Step itsmyturn2(Step opponentStep);

        /// <summary>
        /// Set the inning info. <br />When the data transmitter receives an info message, it will be called!
        /// </summary>
        /// <param name="myStatus">the current status</param>
        /// <param name="limitedTime">the limited time</param>
        /// <param name="opponentInfo">the opponent info</param>
        /// <param name="piecesArray">the initial inning array, length = 15*15 </param>
        void setInningInfo(Status myStatus, int limitedTime, StudentInfo opponentInfo,
            Status[] piecesArray);


        /// <summary>
        /// Get the game tree generated at the last step
        /// </summary>
        /// <returns>the game tree root generated at the last step</returns>
        GameTree getLastGameTree();
    }





}
