package com.njucs.aiep.plugin.fir;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.json.JSONException;
import org.json.JSONObject;

import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.AIEPUserConf.AisBasicConf;
import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.DataTransmit;
import com.njucs.aiep.net.ReceiveEvent;
import com.njucs.aiep.net.ReceiveEventListener;
import com.njucs.aiep.net.DataTransmit.Lang;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.net.ReceiveEvent.RecvCmdType;
import com.njucs.aiep.net.ReceiveEvent.RecvEventType;
import com.njucs.aiep.plugin.AILoader;
import com.njucs.aiep.plugin.AISite;
import com.njucs.aiep.plugin.fir.AIEPP_FIR.TagType;
import com.njucs.aiep.plugin.fir.ai.FIR_AI;
import com.njucs.aiep.plugin.fir.ai.FirAICSharpFactory;
import com.njucs.aiep.plugin.fir.ai.FirAICppFactory;
import com.njucs.aiep.plugin.fir.ai.JythonFactory;
import com.njucs.aiep.plugin.fir.frame.Step;
import com.njucs.aiep.plugin.fir.frame.Inning.WIN_TYPE;
import com.njucs.aiep.sandbox.AISandBox;

/**
 * FIR AI Loader 
 * @author ygsx
 * 
 * @time 2013年5月6日9:32:25
 * */
public class FIRLoader extends AILoader<FIR_AI> {


	private int connId = DataTransmit.ILLEGAL_ID;
	
	private Lang aiLang = Lang.JAVA;
	
	private StudentInfo studentInfo;
	private StudentInfo opponentInfo;
	private int heuristic = 1;
	private int limitedTime;
	private Status status = Status.EMPTY;
	private Status[] piecesArray;

	private FIRSite firSite = null;
	
	private String aiFileName = null;//to record
	
	private ScriptEngineManager manager = new ScriptEngineManager();
	private ScriptEngine jsEngine = manager.getEngineByName("js");
	
	public FIRLoader() { }

	@Override
	public void setAISite(AISite<FIR_AI> aiSite) {
		if( aiSite instanceof FIRSite  ){
			this.firSite = (FIRSite) aiSite;
		}
	}

	@Override
	public   void setAI(FIR_AI aiplugin) {
		this.loaderAI =  aiplugin;
		if( this.loaderAI != null ) {
			this.studentInfo = new StudentInfo( loaderAI.getId(), loaderAI.getName(), loaderAI.getNickname() );
		}
	}
	//*
	@Override
	public boolean setAI(String aiJarOrDllOrJSFileName) throws Exception {
		if( aiJarOrDllOrJSFileName == null || aiJarOrDllOrJSFileName.length() <= ".jar".length() ) return false;
		if( aiFileName != null && aiFileName.equals(aiJarOrDllOrJSFileName) ) return true;//loaded
		File aiFile = new File( aiJarOrDllOrJSFileName );//*
		if( ! aiFile.exists() ){
			throw new Exception( "File(\""+aiJarOrDllOrJSFileName+"\") does not exist. " );
		}//*/
		if( aiJarOrDllOrJSFileName.endsWith(".jar") || aiJarOrDllOrJSFileName.endsWith(".JAR")  ) {
			Class<?> aiclass = Class.forName( loadAIJarFile( aiJarOrDllOrJSFileName ) );
			if( FIR_AI.class.isAssignableFrom(aiclass) ){
				this.loaderAI = (FIR_AI) aiclass.newInstance();
				this.aiLang = Lang.JAVA;
				//System.out.printf( "%s,%s,%s\n", firAI.getId(), firAI.getName(), firAI.getNickname() );
				this.studentInfo = new StudentInfo( loaderAI.getId(), loaderAI.getName(), loaderAI.getNickname() );
				aiFileName = aiJarOrDllOrJSFileName;
				return true;
			} else {
				throw new Exception( "AI Class File is in Error!" );
			}
		} else  if( aiJarOrDllOrJSFileName.endsWith(".dll") || aiJarOrDllOrJSFileName.endsWith(".DLL") ) {
			/*if( aiJarOrDllOrJSFileName.indexOf(":") != -1 ){
				throw new RuntimeException( Language.TIP_NOT_ABSOLUTE_PATH );
			}
			
			if( aiJarOrDllOrJSFileName.indexOf("/") != -1 || 
					aiJarOrDllOrJSFileName.indexOf("\\") != -1 ||
					aiJarOrDllOrJSFileName.indexOf(":") != -1  ){
				throw new RuntimeException("Directory separator should not appear in library name. ");
			}//*/
			//System.out.println( "aiFile.getCanonicalPath()="+aiFile.getCanonicalPath() );
			//aiJarOrDllOrJSFileName = aiJarOrDllOrJSFileName.replace("\\", "/");
			//System.out.println("System.getProperty(\"java.library.path\")="+System.getProperty("java.library.path"));
			//System.loadLibrary( aiJarOrDllOrJSFileName.substring(0, aiJarOrDllOrJSFileName.length() - 4) );
			//System.load( aiFile.getAbsolutePath() );
			//this.loaderAI = new FirAICpp();
			this.loaderAI = FirAICppFactory.newFirAICpp(aiFile.getAbsolutePath());
			if( this.loaderAI != null ){
				this.aiLang = Lang.CPP;
				this.studentInfo = new StudentInfo( loaderAI.getId(), loaderAI.getName(), loaderAI.getNickname() );
				aiFileName = aiJarOrDllOrJSFileName;
				return true;
			}
			this.loaderAI = FirAICSharpFactory.newFirAICSharp(aiFile.getAbsolutePath());
			if( this.loaderAI != null ){
				this.aiLang = Lang.JAVA;
				this.studentInfo = new StudentInfo( loaderAI.getId(), loaderAI.getName(), loaderAI.getNickname() );
				aiFileName = aiJarOrDllOrJSFileName;
				return true;
			}
		} else  if( aiJarOrDllOrJSFileName.endsWith(".js") || aiJarOrDllOrJSFileName.endsWith(".JS") ) {
			FileReader reader = new FileReader( aiJarOrDllOrJSFileName );
			jsEngine.eval(reader);
			jsEngine.eval(" var myFirAi = new MyFirAI(); ");
			reader.close();
			Object myFirAIObject = jsEngine.get("myFirAi");
			Invocable ivncoable = (Invocable)jsEngine;
			this.loaderAI  = ivncoable.getInterface(myFirAIObject, FIR_AI.class);
			this.aiLang = Lang.JAVA;
			this.studentInfo = new StudentInfo( loaderAI.getId(), loaderAI.getName(), loaderAI.getNickname() );
			aiFileName = aiJarOrDllOrJSFileName;
			return true;
		}  else  if( aiJarOrDllOrJSFileName.endsWith(".py") || aiJarOrDllOrJSFileName.endsWith(".PY") ) {
			this.loaderAI = (FIR_AI) JythonFactory.getInstance().
				getJavaObjectFromJythonFile(FIR_AI.class.getName(), aiJarOrDllOrJSFileName, "MyFirAI" );//
			this.aiLang = Lang.JAVA;
			this.studentInfo = new StudentInfo( loaderAI.getId(), loaderAI.getName(), loaderAI.getNickname() );
			aiFileName = aiJarOrDllOrJSFileName;
			return true;
		}
		return false;
	}//*/

	@Override
	public FIR_AI getAI() {
		return this.loaderAI;
	}

	@Override
	public void forceExit() {
		if( this.ailMode == AilMode.NET ){
			DataTransmit.getInstance().closeConnection(connId);
		}
	}

	public boolean initialize(){
		if( this.loaderAI == null ){
			printString( "FIR_AI is null!", true );
			return false;
		}
		if( this.ailMode == AilMode.NET ){
			//System.out.println("E:"+AIEPUserConf.getInstance().getInt(AisBasicConf.AIJ_PORT));
			String hostIp = AIEPUserConf.getInstance().getString(AisBasicConf.AIJ_IP);
			int hostPort = AIEPUserConf.getInstance().getInt(AisBasicConf.AIJ_PORT);
			connId = DataTransmit.getInstance().createConnection(
					hostIp,hostPort, studentInfo,
					new FIRReceiveEventListener(),// null);
					new AISandBox.AISBExceptionHandler());
			String tip = null;
			if( connId > 0 ) {
				return true;
			} else if ( connId == DataTransmit.SERV_REJECT ){
				tip = String.format(Language.TIP_CONN_SERV_REJECT, hostIp, hostPort );
			}  else if ( connId == DataTransmit.CONN_TIMEOUT ){
				//printString( Language.TIP_CONN_TIME_OUT, true );
				tip = String.format(Language.TIP_CONN_TIME_OUT, hostIp, hostPort );
			} else {
				//printString( Language.TIP_CONN_FATAL_ERROR, true );
				tip = String.format(Language.TIP_CONN_FATAL_ERROR, hostIp, hostPort );
			}
			printString( tip, true );
		}
		return false;
	}

	public boolean execute() {
		return true;
	}
	
	protected static Status[] transferChessString( String chessString ){
		String[] t = chessString.split(",");
		Status[] piecesarray = new Status[AIEPP_FIR.DIMENSION*AIEPP_FIR.DIMENSION];
		for( int i = 0; i < AIEPP_FIR.DIMENSION*AIEPP_FIR.DIMENSION; i ++ ){
			int temp = Integer.parseInt(t[i]);
			if( temp == 0 ) piecesarray[i] = Status.EMPTY;
			else if(temp == 1)piecesarray[i] = Status.OFFENSIVE;
			else piecesarray[i] = Status.DEFENSIVE;
		}
		return piecesarray;
	}

	private class FIRReceiveEventListener implements ReceiveEventListener<JSONObject> {

		private Status opponentStatus;
		
		@Override
		public void receive(ReceiveEvent<JSONObject> event) {
			RecvEventType eventType = event.getEventType();
			if( eventType == RecvEventType.DATA ) {
				JSONObject jsonObject = event.getData();
				//System.out.println("client recv:" + jsonObject.toString());
				try {
					int type = jsonObject.getInt(AIEPP_FIR.TAG_TYPE);
					if (type == TagType.INNING_INFO.ordinal()) {
						handleInfo(jsonObject);
					} else if (type == TagType.RUNNING.ordinal()) {
						handleRunning(jsonObject);
					} else if (type == TagType.GAME_OVER.ordinal()) {
						hanleGameOver(jsonObject);
					}
				} catch (JSONException e) {
					e.printStackTrace();
					return;
				}
			} else if ( eventType == RecvEventType.COMMAND ){
				RecvCmdType type = event.getCmdType();
				if( type == RecvCmdType.RESTART ){//restart
					printString( "The server requests to restart!", true );
					DataTransmit.getInstance().closeConnection(connId);
					//printString( .getName(), true);
					try {
						loaderAI = loaderAI.getClass().newInstance();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
					printString( "Wait for the Server restarting in 1 second!", true );
					try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
					if( aiSandBox != null ){
						aiSandBox.initializeLoader();
					} else {
						initialize();
					}
				}
			}
		}

		private void handleInfo(JSONObject jsonObject) throws JSONException {
			printString( "Received the info of the inning.", false );
			piecesArray = null;//initialize
			opponentInfo = new StudentInfo(jsonObject
					.getString(AIEPP_FIR.TAG_OPPONENT_ID), jsonObject
					.getString(AIEPP_FIR.TAG_OPPONENT_NAME), jsonObject
					.getString(AIEPP_FIR.TAG_OPPONENT_NICKNAME));
			limitedTime = jsonObject.getInt(AIEPP_FIR.TAG_TIME);
			//System.out.println( "limitedtime="+limitedTime );
			heuristic = jsonObject.getInt(AIEPP_FIR.TAG_HEURISTIC);
			if ( jsonObject.getInt(AIEPP_FIR.TAG_OFFENSIVE) == 1 ) {
				setStatus(Status.OFFENSIVE);
				opponentStatus = Status.DEFENSIVE;
			} else {
				setStatus(Status.DEFENSIVE);
				opponentStatus = Status.OFFENSIVE;
			}
			if( ! jsonObject.isNull(AIEPP_FIR.TAG_CHESS) ){
				String str = jsonObject.getString(AIEPP_FIR.TAG_CHESS);
				piecesArray = transferChessString( str );
				/*
				String[] t = str.split(",");
				piecesArray = new int[AIEPP_FIR.DIMENSION*AIEPP_FIR.DIMENSION];
				for( int i = 0; i < AIEPP_FIR.DIMENSION*AIEPP_FIR.DIMENSION; i ++ ){
					piecesArray[i] = Integer.parseInt(t[i]);
				}//*/
			}
			FIRLoader.this.loaderAI.setInningInfo(FIRLoader.this.getStatus(), limitedTime,
					 opponentInfo, piecesArray);
		}

		private void handleRunning(JSONObject jsonObject) throws JSONException {
			int x, y;
			if (jsonObject.isNull(AIEPP_FIR.TAG_POSITION_X)) {
				x = 0;
				y = 0;
			} else {
				x = jsonObject.getInt(AIEPP_FIR.TAG_POSITION_X);
				y = jsonObject.getInt(AIEPP_FIR.TAG_POSITION_Y);
			}
			Step lastStep = new Step( opponentStatus, x, y );
			printString( String.format("Located at (%d, %d).", x, y), false );
			//System.out.println( "[Java]x= "+x+", y = "+y );
			long t1 = System.currentTimeMillis();
			Step step;
			if( heuristic == 1 ){
				step = FIRLoader.this.loaderAI.itsmyturn(lastStep);
			} else {
				step = FIRLoader.this.loaderAI.itsmyturn2(lastStep);
			}
			long t2 = System.currentTimeMillis();
			if( step == null ){
				//printString( "You get me a NULL value!\n", true );
				return;
			}  else {
				//printString( String.format("You located at (%d, %d).", step.getX(), step.getY()), false );
			}
			JSONObject jsonObject2 = new JSONObject();
			jsonObject2.put(AIEPP_FIR.TAG_TYPE, TagType.RUNNING.ordinal());
			jsonObject2.put(AIEPP_FIR.TAG_POSITION_X, step.getX());
			jsonObject2.put(AIEPP_FIR.TAG_POSITION_Y, step.getY());
			jsonObject2.put(AIEPP_FIR.TAG_TIME_USED, t2 - t1);
			try {
				DataTransmit.getInstance().pushData(jsonObject2, connId);
				//System.out.println( "send to the server!" );
			} catch (IOException e) {
				e.printStackTrace();
				printString("sending data failed!", true);
			}//show the game tree
			if(  firSite != null ){
				GameTree gt = loaderAI.getLastGameTree();
				if( gt != null ) {
					gt.setStatus(status);
					FIRLoader.this.firSite.showGameTree( gt );
				}
			}
		}

		private void hanleGameOver(JSONObject jsonObject) throws JSONException {
			int win = jsonObject.getInt(AIEPP_FIR.TAG_WINNER);
			if (win == Status.EMPTY.ordinal()) {
				printString("An error occurs! Game is over!", true);
			} else {
				if (status.ordinal() == win) {
					printString("You are the winner! Game is over!", true);
				} else {
					printString("You lost! Game is over!", true);
				}
				int wintype = jsonObject.getInt(AIEPP_FIR.TAG_WIN_TYPE);
				if (wintype == WIN_TYPE.NORMAL.ordinal()) {
					int[] x = new int[5], y = new int[5];
					for (int i = 0; i < 5; i++) {
						x[i] = jsonObject.getInt(AIEPP_FIR.TAG_POSITION_X + i);
						y[i] = jsonObject.getInt(AIEPP_FIR.TAG_POSITION_Y + i);
					}
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < 5; i++) {
						sb.append("(");
						sb.append(x[i]);
						sb.append(",");
						sb.append(y[i]);
						sb.append(")");
					}
					printString("FIR: " + sb.toString(), true);
				} else if (wintype == WIN_TYPE.POSITION_ERROR.ordinal()) {
					printString("Position is error!", true);
				} else if (wintype == WIN_TYPE.TIME_OUT.ordinal()) {
					printString("Time is running out!", true);
				}
			}
		}

	}

	
	private void printString(String context, boolean isForce){
		if( isForce || FIRLoader.this.loaderAI != null && FIRLoader.this.loaderAI.isPrintInfo() ){
			System.out.println( context );
		}
		if( this.firSite != null ){
			this.firSite.addPrompt(context);
		}
	}
	
	/**
	 * @return the offensiveInfo
	 */
	public StudentInfo getOpponentInfo() {
		return opponentInfo;
	}

	/**
	 * @return the limitedTime
	 */
	public int getLimitedTime() {
		return limitedTime;
	}

	
	/**
	 * copy by internet
	 * */
	@SuppressWarnings("unused")
	private static void test() {
		int[][] chessboard = new int[AIEPP_FIR.DIMENSION][];
		for (int i = 0; i < AIEPP_FIR.DIMENSION; i++) {
			chessboard[i] = new int[AIEPP_FIR.DIMENSION];
			for (int j = 0; j < AIEPP_FIR.DIMENSION; j++) {
				chessboard[i][j] = 0;
			}
		}
		//chessboard[4][9] = 3;
		chessboard[5][10] = 3;
		//chessboard[5][11] = 3;
		chessboard[5][12] = 3;
		chessboard[5][13] = 3;
		chessboard[5][14] = 3;
		for (int i = 0; i < AIEPP_FIR.DIMENSION; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION; j++) {
				System.out.print(chessboard[i][j]);
			}
			System.out.println();
		}

		int k, tmp;
		/* 判断所有横行 */
		for (int i = 0; i < AIEPP_FIR.DIMENSION; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION - 4; j++) {
				tmp = chessboard[i][j];
				if (tmp != 0) {
					for (k = 1; k < 5; k++){
						if (chessboard[i][j + k] != tmp) break;
					}
					if (k == 5) {
						return;
					}
				}
			}
		}
		/* 判断所有纵行 */
		for (int i = 0; i < AIEPP_FIR.DIMENSION - 4; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION; j++) {
				tmp = chessboard[i][j];
				if (tmp != 0) {
					for (k = 1; k < 5; k++){
						if (chessboard[i + k][j] != tmp) break;
					}
					if (k == 5){
						return;
					}
				}
			}
		}
		/* 判断所有斜行（撇） */
		for (int i = 4; i < AIEPP_FIR.DIMENSION; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION - 4; j++) {
				tmp = chessboard[i][j];
				if (tmp != 0) {
					for (k = 1; k < 5; k++)
						if (chessboard[i - k][j + k] != tmp)
							break;
					if (k == 5)
						return;
				}
			}
		}
		/* 判断所有斜行（捺） */
		for (int i = 0; i < AIEPP_FIR.DIMENSION - 4; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION - 4; j++) {
				tmp = chessboard[i][j];
				if (tmp != 0) {
					for (k = 1; k < 5; k++){
						if (chessboard[i + k][j + k] != tmp)
							break;
					}
					if (k == 5)
						return;
				}
			}
		}
		System.out.println("Test Over!");
	}

	
	

	/**
	 * @param status
	 *            the status to set
	 */
	private void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	public static void main(String[] args) {	
		//*
	//	System.loadLibrary("XF2");
		//FIR_AI ai = new FirAICpp();

		
		//try { Thread.sleep(300); } catch (InterruptedException e) { 	e.printStackTrace(); }
		
		FIRLoader loader = new FIRLoader();
		try {
			loader.setAI("STY_ENG.dll");
		} catch (Exception e) {
			e.printStackTrace();
		}
		loader.setHostIp("127.0.0.1");
		loader.setHostPort( 8890 );
		loader.initialize();
		loader.execute();
//*/
	//	test();
	}

	@Override
	public Lang getAILang() {
		return this.aiLang;
	}

}
