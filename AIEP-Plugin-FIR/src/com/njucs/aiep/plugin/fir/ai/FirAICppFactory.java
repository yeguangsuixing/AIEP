package com.njucs.aiep.plugin.fir.ai;

import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.plugin.fir.frame.Step;

public class FirAICppFactory {
	
	public final static int ILLEGAL_INDEX = -1;
	
	private static int INDEX = 0;
	
	private FirAICppFactory(){}
	
	public static FirAICpp newFirAICpp( String firAIDllFileName ){
		if( FirAICpp.addFirAIDll( firAIDllFileName ) ){
			return new FirAICppWrapper(INDEX ++);
		} else {
			System.out.println( "Creating FIR AI Cpp Object failed!\nfile name="+firAIDllFileName );
			return null;
		}
	}
	
}

class FirAICppWrapper extends FirAICpp {
	
	int aiIndex = FirAICppFactory.ILLEGAL_INDEX;
	
	public FirAICppWrapper(int aiIndex) {
		super();
		this.aiIndex = aiIndex;
	}
	

	@Override
	public GameTree getLastGameTree(){
		FirAICpp.setCurrentAIIndex(aiIndex);
		return super.getLastGameTree();
	}

	@Override
	public Step itsmyturn(Step opponentStep){
		FirAICpp.setCurrentAIIndex(aiIndex);
		return super.itsmyturn(opponentStep);
	}

	@Override
	public  Step itsmyturn2(Step opponentStep){
		FirAICpp.setCurrentAIIndex(aiIndex);
		return super.itsmyturn2(opponentStep);
	}
	
	@Override
	public void setInningInfo(int myStatus, int limitedTime,
			StudentInfo opponentInfo, int[] piecesArray){
		FirAICpp.setCurrentAIIndex(aiIndex);
		super.setInningInfo(myStatus, limitedTime, opponentInfo, piecesArray);
	}

	@Override
	public String getId(){
		FirAICpp.setCurrentAIIndex(aiIndex);
		return super.getId();
	}

	@Override
	public String getName(){
		FirAICpp.setCurrentAIIndex(aiIndex);
		return super.getName();
	}

	@Override
	public String getNickname(){
		FirAICpp.setCurrentAIIndex(aiIndex);
		return super.getNickname();
	}

	@Override
	public String getVersion(){
		FirAICpp.setCurrentAIIndex(aiIndex);
		return super.getVersion();
	}

	@Override
	public boolean isPrintInfo(){
		FirAICpp.setCurrentAIIndex(aiIndex);
		return super.isPrintInfo();
	}
}

