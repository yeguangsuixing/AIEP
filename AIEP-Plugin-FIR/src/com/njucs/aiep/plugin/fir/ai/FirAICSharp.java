package com.njucs.aiep.plugin.fir.ai;

import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.plugin.fir.frame.Step;

public class FirAICSharp  implements FIR_AI {

	//accessed in the package & subclass
	protected native static boolean addFirAIDll(String firAIDllFileName);
	
	//accessed in the package & subclass
	protected native static void setCurrentAIIndex( int index );
	
	//accessed in the package & subclass
	protected FirAICSharp(){}
	
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

		//System.load("F:\\Workspace\\AIEPServer\\AIEP-CSharp.dll");
		//System.load("F:\\Workspace\\AIEPServer\\FIR_AICS.dll");
		//System.load("F:\\Workspace\\AIEPServer\\FIR_AICSharpUtilLoader.dll");
		System.load("F:\\Workspace\\AIEP-Plugin-FIR\\FIR_AICSharpLoader.dll");
		
		FirAICSharp myFirAICSharpX = FirAICSharpFactory.
			newFirAICSharp("F:\\Workspace\\AIEPServer\\res\\aitest\\MyFirAI_X.dll");
		if( myFirAICSharpX == null ){
			System.out.println( "setFirAIDll X failed!" ); return;
		}

		FirAICSharp myFirAICSharpY = FirAICSharpFactory.
			newFirAICSharp("F:\\Workspace\\AIEPServer\\res\\aitest\\MyFirAI_Y.dll");
		if( myFirAICSharpY == null ){
			System.out.println( "setFirAIDll Y failed!" ); return;
		}
		//myFirAICpp.unloadNativeLibs();
		//System.load( new File("XF2.dll").getAbsolutePath() );
		//myFirAICpp = new FirAICpp();
		System.out.println( "myFirAICSharpX="+myFirAICSharpX.getId() );
		System.out.println( "myFirAICSharpY="+myFirAICSharpY.getId() );
		
		
		Status[] piecesArray = new Status[15*15];
		for( int i = 0; i < 15*15; i ++ ){
			piecesArray[i] = Status.EMPTY;
		}
		myFirAICSharpX.setInningInfo(Status.OFFENSIVE, 3000, 
				new StudentInfo("www", "", ""),  piecesArray );
		myFirAICSharpY.setInningInfo(Status.OFFENSIVE, 3000, 
				new StudentInfo("www", "", ""),  piecesArray );

		Step lastStep = new Step(4, 6);
		Step aiStep = myFirAICSharpX.itsmyturn(lastStep);
		System.out.println( "X aiStep:x="+aiStep.getX()+", y="+aiStep.getY() );
		aiStep = myFirAICSharpY.itsmyturn(aiStep);
		System.out.println( "Y aiStep:x="+aiStep.getX()+", y="+aiStep.getY() );
		//*/
	}
	
}



