package com.njucs.aiep.sandbox;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import javax.swing.JOptionPane;

import com.njucs.aiep.plugin.AILoader;

/**
 * AI Running SandBox
 * @author ygsx
 * 
 * @created 2013Äê6ÔÂ1ÈÕ20:49:03
 * */
public class AISandBox {
	
	private AILoader<?> aiLoader;
	
	
	private FutureTask<String> aisbTask = null;
	private Thread aisbThread;
	
	private static AISBSecurityManager AISB_SECURITY_MANAGER = new AISBSecurityManager();
	
	public AISandBox(AILoader<?> aiLoader){
		this.aiLoader = aiLoader;
		if( this.aiLoader != null ){
			this.aiLoader.setAISandBox(this);
		}
	}
	
	public void execute() {
		aisbTask = new FutureTask<String>( new SandBoxCallable() );
		aisbThread = new Thread( aisbTask );
		aisbThread.start();
	}
	
	private synchronized static void setSecurityCheck( boolean checkSecurity ){
		if( checkSecurity ){
			if (System.getSecurityManager() == null){
				System.setSecurityManager( AISB_SECURITY_MANAGER );
			}
		} else {
			System.setSecurityManager( null );
		}
	}

	private class SandBoxCallable implements Callable<String> {
		@Override
		public String call() throws Exception {
			System.out.println( "Running SandBoxCallable" );
			if( ! initializeLoader() ){
				return "init failed";
			}
			aiLoader.execute();
			return "exec end";
		}
	}
	public boolean initializeLoader(){
		setSecurityCheck( false );
		boolean b = aiLoader.initialize();
		setSecurityCheck( true );
		return b;
	}
	
	public static class AISBExceptionHandler implements UncaughtExceptionHandler {

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			e.printStackTrace();
			System.out.println( "Exception("+e.getClass().getName()+"): "+e.getMessage() );
			StackTraceElement[]  stackArray = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append( e.getClass().getName() );
			sb.append("\n");
			for( StackTraceElement elem : stackArray ){
				sb.append("    ");
				sb.append( elem.getFileName() );
				sb.append(": ");
				sb.append( elem.getLineNumber() );
				sb.append("\n");
			}
			JOptionPane.showMessageDialog(null, sb.toString());
			System.out.println( sb.toString() );
		}
		
	}
	
}







