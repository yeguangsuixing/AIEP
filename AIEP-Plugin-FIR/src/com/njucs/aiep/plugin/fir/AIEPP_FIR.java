package com.njucs.aiep.plugin.fir;

import java.io.File;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEP.SystemPlatform;




/**
 * AIEPP( Artificial Intelligence Experiment Platform Plugin )-FIR( Five In Row )
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ4ÈÕ15:01:57
 * 
 * */
public class AIEPP_FIR {

	public final static int DIMENSION = 15;
	
	public static enum TagType { INNING_INFO, RUNNING, GAME_OVER };
	public final static String TAG_TYPE = "type";
	public final static String TAG_MODE = "mode";
	public final static String TAG_OFFENSIVE = "offensive";
	/**
	 * limited time per step, by second at v0.1beta, by millsecond at others
	 * */
	public final static String TAG_TIME = "time";
	public final static String TAG_OPPONENT_ID = "aid";//
	public final static String TAG_OPPONENT_NAME = "aname";//
	public final static String TAG_OPPONENT_NICKNAME = "anick";//

	public final static String TAG_POSITION_X = "px";
	public final static String TAG_POSITION_Y = "py";
	public final static String TAG_TIME_USED = "usedtime";
	
	public final static String TAG_WINNER = "winner";//valued by Status
	public final static String TAG_WIN_TYPE = "wintype";
	public final static String TAG_POSITION_X0 = "px0";
	public final static String TAG_POSITION_Y0 = "py0";
	public final static String TAG_POSITION_X1 = "px1";
	public final static String TAG_POSITION_Y1 = "py1";
	public final static String TAG_POSITION_X2 = "px2";
	public final static String TAG_POSITION_Y2 = "py2";
	public final static String TAG_POSITION_X3 = "px3";
	public final static String TAG_POSITION_Y3 = "py3";
	public final static String TAG_POSITION_X4 = "px4";
	public final static String TAG_POSITION_Y4 = "py4";
	
	public final static String TAG_HEURISTIC = "heuristic";
	public final static String TAG_CHESS = "chess";

	public static int REMOTE_PORT = 8895;
	
	static {
		if( AIEP.PLATFORM == SystemPlatform.X86 ){
			System.load( new File("FIR_AICppLoader(x86).dll").getAbsolutePath() );	
		} else if( AIEP.PLATFORM == SystemPlatform.X64 ) {
			System.load( new File("FIR_AICppLoader(x64).dll").getAbsolutePath() );
		}
		System.load( new File("FIR_AICSharpLoader.dll").getAbsolutePath());
	}
	
}
