using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace AICS
{
    /// <summary>
    /// the student info
    /// </summary>
    public class StudentInfo
    {
        private String id, name, nickname;

        public StudentInfo( String id, String name, String nickname )
        {
            this.id = id;
            this.name = name;
            this.nickname = nickname;
        }

        /// <summary>
        /// Get the nickname of the student
        /// </summary>
        /// <returns>the nickname of the student</returns>
        public String getNickname()
        {
            return nickname;
        }
        
        /// <summary>
        /// Get the name of the student
        /// </summary>
        /// <returns>the name of the student</returns>
        public String getName()
        {
            return name;
        }
        
        /// <summary>
        /// Get the ID of the student
        /// </summary>
        /// <returns>the ID of the student</returns>
        public String getId()
        {
            return id;
        }
        
        /// <summary>
        /// Set the nickname of the student
        /// </summary>
        /// <param name="nickname">the new nickname of the student</param>
        public void setNickname(String nickname)
        {
            this.nickname = nickname;
        }
        
        /// <summary>
        /// Set the name of the student
        /// </summary>
        /// <param name="name">the new name of the student</param>
        public void setName(String name)
        {
            this.name = name;
        }

        /// <summary>
        /// Set the id  of the student
        /// </summary>
        /// <param name="id">the new id of the student</param>
        public void setId(String id)
        {
            this.id = id;
        }
    }
}
