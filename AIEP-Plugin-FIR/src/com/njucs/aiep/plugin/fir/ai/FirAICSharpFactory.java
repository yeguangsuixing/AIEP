package com.njucs.aiep.plugin.fir.ai;

import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.plugin.fir.frame.Step;

public class FirAICSharpFactory {
	
	public final static int ILLEGAL_INDEX = -1;
	
	private static int INDEX = 0;
	
	public static FirAICSharp newFirAICSharp( String firAIDllFileName ){
		if( FirAICSharp.addFirAIDll( firAIDllFileName ) ){
			return new FirAICSharpWrapper(INDEX ++);
		} else {
			System.out.println( "Creating FIR AI CSharp Object failed!\nfile name="+firAIDllFileName );
			return null;
		}
	}
}

class FirAICSharpWrapper extends FirAICSharp {
	
	int aiIndex = FirAICSharpFactory.ILLEGAL_INDEX;
	
	public FirAICSharpWrapper(){  }
	
	public FirAICSharpWrapper(int aiIndex) {
		super();
		this.aiIndex = aiIndex;
	}
	

	@Override
	public GameTree getLastGameTree(){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		return super.getLastGameTree();
	}

	@Override
	public Step itsmyturn(Step opponentStep){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		return super.itsmyturn(opponentStep);
	}

	@Override
	public  Step itsmyturn2(Step opponentStep){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		return super.itsmyturn2(opponentStep);
	}
	
	@Override
	public void setInningInfo(int myStatus, int limitedTime,
			StudentInfo opponentInfo, int[] piecesArray){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		super.setInningInfo(myStatus, limitedTime, opponentInfo, piecesArray);
	}

	@Override
	public String getId(){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		return super.getId();
	}

	@Override
	public String getName(){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		return super.getName();
	}

	@Override
	public String getNickname(){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		return super.getNickname();
	}

	@Override
	public String getVersion(){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		return super.getVersion();
	}

	@Override
	public boolean isPrintInfo(){
		FirAICSharp.setCurrentAIIndex(aiIndex);
		return super.isPrintInfo();
	}
}
