package com.njucs.aiep.plugin.fir.ai;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Vector;

import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.plugin.fir.frame.Step;


public class FirAICpp  implements FIR_AI {
	
	//accessed in the package & subclass
	protected native static boolean addFirAIDll(String firAIDllFileName);
	
	//accessed in the package & subclass
	protected native static void setCurrentAIIndex( int index );
	
	//accessed in the package & subclass
	protected FirAICpp(){}
	
	@Override
	public native GameTree getLastGameTree();

	@Override
	public native Step itsmyturn(Step opponentStep);

	@Override
	public native Step itsmyturn2(Step opponentStep);

	@Override
	public void setInningInfo(Status myStatus, int limitedTime,
			StudentInfo opponentInfo, Status[] piecesArray){
		int myIntStatus = myStatus.ordinal();
		int[] piecesIntArray = new int[piecesArray.length];
		for( int i = 0; i < piecesArray.length; i ++ ){
			piecesIntArray[i] = piecesArray[i].ordinal();
		}
		setInningInfo(myIntStatus, limitedTime, opponentInfo, piecesIntArray );
	}
	
	public native void setInningInfo(int myStatus, int limitedTime,
			StudentInfo opponentInfo, int[] piecesArray);

	@Override
	public native String getId();

	@Override
	public native String getName();

	@Override
	public native String getNickname();

	@Override
	public native String getVersion();

	@Override
	public native boolean isPrintInfo();
	
	public static void main(String[] args){

		System.load("F:\\Workspace\\AIEPServer\\FIR_AICppLoader(x86).dll");
		
		FirAICpp myFirAICppX = FirAICppFactory.
			newFirAICpp("F:\\Workspace\\AIEPServer\\upload\\2013_07_08_21_35_18_-1404154507_FIR_AI.dll");
		if( myFirAICppX == null ){
			System.out.println( "setFirAIDll X failed!" ); return;
		}

		FirAICpp myFirAICppY = FirAICppFactory.
			newFirAICpp("F:\\Workspace\\AIEPServer\\res\\aitest\\FIR_AI_Y.dll");
		if( myFirAICppY == null ){
			System.out.println( "setFirAIDll Y failed!" ); return;
		}
		//myFirAICpp.unloadNativeLibs();
		//System.load( new File("XF2.dll").getAbsolutePath() );
		//myFirAICpp = new FirAICpp();
		
		//*
		Status[] piecesArray = new Status[15*15];
		for( int i = 0; i < 15*15; i ++ ){
			piecesArray[i] = Status.EMPTY;
		}
		myFirAICppX.setInningInfo(Status.OFFENSIVE, 3000, 
				new StudentInfo("www", "", ""),  piecesArray );
		myFirAICppY.setInningInfo(Status.OFFENSIVE, 3000, 
				new StudentInfo("www", "", ""),  piecesArray );

		Step lastStep = new Step(2,0);
		Step aiStep = myFirAICppX.itsmyturn(lastStep);
		System.out.println( "X aiStep:x="+aiStep.getX()+", y="+aiStep.getY() );
		aiStep = myFirAICppY.itsmyturn(aiStep);
		System.out.println( "Y aiStep:x="+aiStep.getX()+", y="+aiStep.getY() );
		
		//myFirAICpp.unloadNativeLibs();//*/
		

		/*
		AILoader<FIR_AI> aiLoader = new FIRLoader();
		aiLoader.setAI(myFirAICpp);
		aiLoader.setHostIp("127.0.0.1"); 
		aiLoader.setHostPort( 8890 );
		aiLoader.initialize();
		aiLoader.execute();//*/
	}
	//@SuppressWarnings("unused")
	private void unloadNativeLibs() {
        try {
            ClassLoader classLoader = this.getClass().getClassLoader();
            Field field = ClassLoader.class.getDeclaredField("nativeLibraries");
            field.setAccessible(true);
            Vector<?> libs = (Vector<?>) field.get(classLoader);
            Iterator<?> it = libs.iterator();
            Object o;
            while (it.hasNext()) {
                o = it.next();
                Method finalize = o.getClass().getDeclaredMethod("finalize", new Class[0]);
                finalize.setAccessible(true);
                finalize.invoke(o, new Object[0]);
            }
        } catch (Exception ex) {
            System.out.println("卸载dll文件出错，需要重启服务器！");
            throw new RuntimeException(ex);
        }
    }
	
}
