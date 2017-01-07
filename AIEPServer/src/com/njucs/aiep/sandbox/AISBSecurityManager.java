package com.njucs.aiep.sandbox;

import java.awt.AWTPermission;
import java.io.FilePermission;
import java.lang.reflect.ReflectPermission;
import java.net.SocketPermission;
import java.security.Permission;
import java.security.SecurityPermission;
import java.util.PropertyPermission;

public class AISBSecurityManager extends SecurityManager {
    
	@Override
    public void checkExit(int status) {
    	throw new SecurityException("Exit On Client Is Not Allowed!");
    }

    @Override
    public void checkPermission(Permission perm) throws SecurityException {
		if (perm instanceof SecurityPermission) {
			if (perm.getName().startsWith("getProperty")) {
				return;
			}
		} else if (perm instanceof PropertyPermission ) {
			if ( perm.getActions().equals("read") ) {
				return;
			}
		} else if ( perm instanceof FilePermission ){
			if ( perm.getActions().equals("read") ) {
				return;
			} else {
				throw new SecurityException( "You are NOT allowed to write files."+perm.toString() );
			}
		} else if (perm instanceof RuntimePermission || perm instanceof ReflectPermission){
			return;
		} else if ( perm instanceof SocketPermission ){
			if( ! perm.getActions().contains("listen") ){
				return;
			} else {
				throw new SecurityException( "You are NOT allowed to create a linstening socket."+perm.toString() );
			}
		} else if ( perm instanceof AWTPermission ){
			return;
		}

		throw new SecurityException(perm.toString());
	}
    
}
