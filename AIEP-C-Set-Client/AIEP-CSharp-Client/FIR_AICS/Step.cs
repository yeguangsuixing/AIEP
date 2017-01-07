using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Runtime.InteropServices;

namespace AICS
{
    [ComVisible(true)]
    [Guid("168267E6-8367-45F8-B7A1-F7BA1A104629")]
    [InterfaceTypeAttribute(ComInterfaceType.InterfaceIsIDispatch)]
    public interface IStep
    {
        int getX();
        int getY();
    }

    public class Step : IStep
    {
        private Status status = Status.EMPTY;
        private int x, y;

        public Step( Status status, int x, int y ) 
        {
            this.status = status;
            this.x = x;
            this.y = y;
        }

        public int getX() { return  x; }
        public int getY() { return y; }


    }
}
