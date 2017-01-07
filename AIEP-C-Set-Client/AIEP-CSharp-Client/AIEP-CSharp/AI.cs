using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AICS
{
    /// <summary>
    /// AI Interface, every AI Class should implement the interface
    /// </summary>
    public interface AI
    {
        /// <summary>
        /// Get the ID of the AI
        /// </summary>
        /// <returns>the ID of the AI</returns>
        String getId();

        /// <summary>
        /// Get the name of the AI
        /// </summary>
        /// <returns>the name of the AI</returns>
        String getName();

        /// <summary>
        /// Get the nickname of the AI
        /// </summary>
        /// <returns>the nickname of the AI</returns>
        String getNickname();

        /// <summary>
        /// Whether print the received info
        /// </summary>
        /// <returns></returns>
        Boolean isPrintInfo();

        /// <summary>
        /// Get the version of the AI
        /// </summary>
        /// <returns>the version of the AI</returns>
        String getVersion();
    }
}
