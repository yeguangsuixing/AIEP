package com.njucs.aiep.plugin.fir;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.json.JSONException;
import org.json.JSONObject;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.AIEP.Country;
import com.njucs.aiep.AIEPUserConf.AijBasicConf;
import com.njucs.aiep.base.FileIO;
import com.njucs.aiep.flow.Flow;
import com.njucs.aiep.flow.FlowEvent;
import com.njucs.aiep.flow.FlowEventListener;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.DataTransmit;
import com.njucs.aiep.net.NewConnEvent;
import com.njucs.aiep.net.NewConnListener;
import com.njucs.aiep.net.DataTransmit.Lang;
import com.njucs.aiep.net.DataTransmit.StudentInfo;
import com.njucs.aiep.plugin.AIArena;
import com.njucs.aiep.plugin.AIJudge;
import com.njucs.aiep.plugin.AILoader;
import com.njucs.aiep.plugin.Result;
import com.njucs.aiep.plugin.ResultMessage;
import com.njucs.aiep.plugin.Result.ResultCode;
import com.njucs.aiep.plugin.ResultMessage.ResultType;
import com.njucs.aiep.plugin.fir.ai.FIR_AI;
import com.njucs.aiep.plugin.fir.frame.Inning;
import com.njucs.aiep.plugin.fir.frame.InningInfo;
import com.njucs.aiep.plugin.fir.frame.Mode;
import com.njucs.aiep.plugin.fir.frame.Step;
import com.njucs.aiep.plugin.fir.frame.Inning.PartInfo;
import com.njucs.aiep.plugin.fir.frame.Inning.WIN_TYPE;
import com.njucs.aiep.plugin.fir.ui.FIRBoard;
import com.njucs.aiep.plugin.fir.ui.SetDialog;
import com.twzcluster.net.INetManager;
import com.twzcluster.net.NetManager;


/**
 *  * AIEPSP( Artificial Intelligence Experiment Platform Server Plugin )-FIR( Five In Row )
 *  
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ6ÈÕ9:32:37
 * */
public class FIRJudge  extends AIJudge<FIR_AI> {
	

	//public final static int DEFAULT_LIMITED_TIME = 2;//2s
	
	public final static int WIDTH = 751, HEIGHT = 649;
	
	private Inning currentInning;
	private PartInfo offensivePartInfo, defensivePartInfo;
	
	//pointer for HUMAN VS AI Mode
	private PartInfo humanPartInfo, aiPartInfo;
	private Status humanStatus, aiStatus;

	private final static String CONF_LIMITED = "LimitedTime";
	private final static String CONF_RMI_PORT = "RemotePort";
	private final static String CONF_FILE_NAME = "fir_conf.txt";
	
	private SetDialog setDialog;
	private FIRBoard firBoard;
	//buffer for reexecute
	private Mode mode = Mode.AI_VS_HUMAN;
	private int offensiveHeuristic = 1, defensiveHeuristic = 1, level = 1;
	private String chessFile;
	private String hiId, hiName, hiNickname;
	
	private HumanAINewConnListener humanAINewConnListener;
	private AIAINewConnListener aiAINewConnListener;
	
	private int listenId = DataTransmit.ILLEGAL_ID;
	
	private int[] cppLevelTime = { 2, 8, 15 }, javaLevelTime = { 3, 10, 20 };

	private Flow<JSONObject> globalFlow;
	
	private JMenuItem startItem = new JMenuItem( Language.MENU_OPEN_STEP ), 
		stopItem = new JMenuItem( Language.MENU_CLOSE_STEP ),
		nextItem = new JMenuItem( Language.MENU_NEXT_STEP ),
		saveItem = new JMenuItem( Language.MENU_SAVE_INNING );
	
	private int connArenaId = NetManager.ILLEGAL_ID;
	
	public FIRJudge( ){ }
	
	
	public boolean initialize( AIEP aiep ) {
		super.initialize(aiep);
		
		initializeMenu();
		
		if( this.aijRole == AijRole.CONN_SITE ) {
			this.aiep.setWindowSize(WIDTH, HEIGHT);
			this.aiep.setResizable(false);
			
			this.firBoard = new FIRBoard( this );
			this.aiep.getCavas().setLayout(new BorderLayout());
			this.aiep.getCavas().add( firBoard, BorderLayout.CENTER );
			this.aiep.getCavas().update(aiep.getCavas().getGraphics());
			String errmsg = null;
			if( (errmsg = readConfFile( cppLevelTime, javaLevelTime )) != null ){
				JOptionPane.showMessageDialog(null, errmsg);
				aiep.setStatusBarText(errmsg);
			}
	
			//show ui
			this.setDialog = new SetDialog( null, this );
		}
		return true;
	}
	/*default*/ static String readConfFile(int[] cppLevelTime, int[] javaLevelTime){
		try {
			BufferedReader reader = FileIO.getBufferedReader( CONF_FILE_NAME );
			
			String str;
			while( (str = reader.readLine())!= null ){
				String[] temp = str.split("=");
				if( temp.length < 2 ) {
					throw new Exception( "Configuration File Error!" );
				} else {
					if( CONF_LIMITED.equals(temp[0]) ){
						String[] timeStringArray = temp[1].split(",");
						if( timeStringArray.length < 6 ){
							throw new Exception( "Configuration File Error!" );
						} else {
							cppLevelTime[0] = Integer.parseInt(timeStringArray[0]);
							cppLevelTime[1] = Integer.parseInt(timeStringArray[1]);
							cppLevelTime[2] = Integer.parseInt(timeStringArray[2]);
							javaLevelTime[0] = Integer.parseInt(timeStringArray[3]);
							javaLevelTime[1] = Integer.parseInt(timeStringArray[4]);
							javaLevelTime[2] = Integer.parseInt(timeStringArray[5]);
						}
					} else if ( CONF_RMI_PORT.equals(temp[0]) ){
						AIEPP_FIR.REMOTE_PORT = Integer.parseInt( temp[1] );
					}
				}
			}
			//System.out.println( "Reading Conf File!"+javaLevelTime[0] );
			FileIO.close(reader);
			return null;
		} catch (IOException e) {//igonre
			e.printStackTrace(); return e.getMessage();
		} catch (Exception e) {
			e.printStackTrace(); return e.getMessage();
		}
	}
	

	public void forceExit() {
		DataTransmit.getInstance().closeAll();
		listenId = DataTransmit.ILLEGAL_ID;
		if( globalFlow != null ){
			globalFlow.stop();
			globalFlow = null;
		}
	}

	@Override
	protected int executeSiteRole() {
		DataTransmit.getInstance().sendRestartRequest(listenId);
		forceExit();
		//setDialog.setVisible(true);

		startItem.setEnabled(false);
		stopItem.setEnabled(false);
		nextItem.setEnabled(false);
		saveItem.setEnabled(false);
		
		try {
			setMode( mode, offensiveHeuristic, defensiveHeuristic, level, chessFile );
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, Language.MSG_ERROR_FAILED_SET );
			setStatusBarText(Language.MSG_ERROR_FAILED_SET);
		}
		setHumanInfo(hiId, hiName, hiNickname);
		
		if( this.mode == Mode.AI_VS_AI ){
			executeAIAIMode();
		} else if( this.mode == Mode.AI_VS_HUMAN || this.mode == Mode.HUMAN_VS_AI){
			executeHumanAIMode();
		} else {
			JOptionPane.showMessageDialog(null, "Please set the mode!", "title", 
					JOptionPane.ERROR_MESSAGE );
		}
		return 0;
	}

	private void executeHumanAIMode(){
		if( mode == null ) {
			JOptionPane.showMessageDialog(null, "Please set the mode!", "title", JOptionPane.ERROR_MESSAGE );
		}
		this.setStatusBarText(Language.TIP_WAIT);//UI
		this.updateUI();

		this.humanAINewConnListener = new HumanAINewConnListener( aiPartInfo );
		this.listenId = DataTransmit.getInstance().createListener(
				AIEPUserConf.getInstance().getInt(AijBasicConf.AIJ_PORT), 
				humanAINewConnListener, null/*do nothing*/ );
		if( listenId == DataTransmit.ILLEGAL_ID ){
			this.setStatusBarText(Language.MSG_ERROR_FAILED_LISTEN);
			JOptionPane.showMessageDialog(null, Language.MSG_ERROR_FAILED_LISTEN,
					null, JOptionPane.ERROR_MESSAGE);
		}
	}
	private void executeAIAIMode(){
		
		this.setStatusBarText(Language.TIP_WAIT);//UI
		this.updateUI();
		
		aiAINewConnListener = new AIAINewConnListener();
		this.listenId = DataTransmit.getInstance().createListener(
				AIEPUserConf.getInstance().getInt(AijBasicConf.AIJ_PORT), 
				aiAINewConnListener, null/*do nothing*/ );
		if( listenId == DataTransmit.ILLEGAL_ID ){
			JOptionPane.showMessageDialog(null, Language.MSG_ERROR_FAILED_LISTEN);
		} else {
			DataTransmit.getInstance().setMaxCount(listenId, 2);//2 AIs
		}
	}

	
	
	//called by SetDialog
	public void setMode(Mode mode, int offensiveHeuristic, int defensiveHeuristic,
			int level/* level = { 1,2,3 } */, 
			String chessFile) throws Exception {
		//System.out.println( "setMode" );
		this.offensiveHeuristic = offensiveHeuristic;
		this.defensiveHeuristic = defensiveHeuristic;
		this.level = level;
		this.chessFile = chessFile;
		
		if( this.firBoard != null ){
			this.firBoard.clear();
		}
		
		this.mode = mode;
		this.offensivePartInfo = new PartInfo();
		this.defensivePartInfo = new PartInfo();

		//create a new innings
		this.currentInning = new Inning(mode, offensivePartInfo, defensivePartInfo);
		if( level < 1 ) level = 1;
		else if( level > 3 ) level = 3;
		this.currentInning.setCppTime(cppLevelTime[level-1]);
		this.currentInning.setJavaTime(javaLevelTime[level-1]);
		
		this.currentInning.setChessFile(chessFile);
		if( this.firBoard != null ){
			this.firBoard.setInning(currentInning);
		}
		
		if( this.mode == Mode.AI_VS_AI ){
			this.offensivePartInfo.setHeuristic(offensiveHeuristic);
			this.defensivePartInfo.setHeuristic(defensiveHeuristic);
			this.setStatusBarText( Language.TIP_MODE_AI_AI );
		} else if( this.mode == Mode.AI_VS_HUMAN ){
			this.aiPartInfo = offensivePartInfo;
			this.aiStatus = Status.OFFENSIVE;
			this.humanPartInfo = defensivePartInfo;
			this.humanStatus = Status.DEFENSIVE;
			this.aiPartInfo.setHeuristic(offensiveHeuristic);
			this.setStatusBarText( Language.TIP_MODE_AI_HUMAN );
		} else if( this.mode == Mode.HUMAN_VS_AI ){
			this.aiPartInfo = defensivePartInfo;
			this.aiStatus = Status.DEFENSIVE;
			this.humanPartInfo = offensivePartInfo;
			this.humanStatus = Status.OFFENSIVE;
			this.aiPartInfo.setHeuristic(defensiveHeuristic);
			this.setStatusBarText( Language.TIP_MODE_HUMAN_AI );
		}
		
		this.updateUI();
		
	}
	
	public void setHumanInfo(String id, String name, String nickname){
		hiId = id;
		hiName = name;
		hiNickname = nickname;
		if( this.humanPartInfo == null ) return;
		if( id == null || id.trim().length() == 0 ) {
			id = "unfilled";
		} else if( id.length() >= 20 ){
			id = id.substring(0, 19);
		}
		if( name == null || name.trim().length() == 0 ) {
			name = "unfilled";
		} else if( name.length() >= 20 ){
			name = name.substring(0, 19);
		}
		if( nickname == null || nickname.trim().length() == 0 ) {
			nickname = "unfilled";
		} else if( nickname.length() >= 20 ){
			nickname = nickname.substring(0, 19);
		}
		this.humanPartInfo.set( id, name, nickname );
	}
	public void setStatusBarText(String text){
		this.aiep.setStatusBarText(text);
	}
	public void executeByOK(){
		/*
		if( this.mode == Mode.AI_VS_AI ){
			executeAIAIMode();
		} else if( this.mode == Mode.AI_VS_HUMAN || this.mode == Mode.HUMAN_VS_AI){
			executeHumanAIMode();
		}//*/
		//execute();
		this.aiep.pluginExecute();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	/**
	 * This function will be called by the NewConnListener
	 * */
	private void runHumanAIMode(){
		try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
		//send the innings info to the client(AI)

		packetInningsInfoAndSend( this.mode == Mode.AI_VS_HUMAN, 
				this.currentInning.getLimitedTime(this.aiPartInfo.getLanguage()), 
				this.aiPartInfo.getHeuristic(),this.currentInning,
				this.humanPartInfo, this.aiPartInfo.getConnId() );
		//begin to fighting!
		if( mode ==Mode.AI_VS_HUMAN ){
			if( this.currentInning.getNext() == Status.OFFENSIVE ){
				turnToAI( null );
			} else if( this.currentInning.getNext() == Status.DEFENSIVE ){
				turnToHuman( null );
			}
		} else if( mode ==Mode.HUMAN_VS_AI ){
			if( this.currentInning.getNext() == Status.OFFENSIVE ){
				turnToHuman( null );
			} else if( this.currentInning.getNext() == Status.DEFENSIVE ){
				turnToAI( null );
			}
		} else {
			System.out.println( "Unknown Fatal Error!" );
		}
	}
	/**
	 * This function will be called by the NewConnListener
	 * */
	private void runAIAIMode(){
		try { Thread.sleep(500); } catch (InterruptedException e) { e.printStackTrace(); }
		//send the innings info to the client(AI)
		packetInningsInfoAndSend( true, 
				this.currentInning.getLimitedTime(this.offensivePartInfo.getLanguage()), 
				this.offensivePartInfo.getHeuristic(),this.currentInning,
				this.defensivePartInfo, 
				this.offensivePartInfo.getConnId() );
		packetInningsInfoAndSend( false, 
				this.currentInning.getLimitedTime(this.offensivePartInfo.getLanguage()), 
				this.defensivePartInfo.getHeuristic(),this.currentInning,
				this.offensivePartInfo, 
				this.defensivePartInfo.getConnId() );
		
		turnToAI( defensivePartInfo, offensivePartInfo, null );
		
	}
	
	//called by UI( class FIRBoard )
	public void turnToAI( Step step ){
		if( step != null ) {
			boolean b = this.currentInning.addStep(step);
			if( ! b ) return;
			this.updateUI();
			PartInfo winner = currentInning.getWinner();
			if( winner != null ){
				win( winner, WIN_TYPE.NORMAL, this.currentInning.getWinStepList() );
				System.out.println( "winner : "+winner.getNickname() );
				return;
			}
		}
		this.firBoard.turn( this.aiStatus );
		//beginTiming
		Calendar c = Calendar.getInstance();
		int lt = this.currentInning.getLimitedTime( this.aiPartInfo.getLanguage() );
		//System.out.println( "lt="+lt+",lang="+this.aiPartInfo.getLanguage() );
		c.add(Calendar.MILLISECOND, lt);
		globalFlow = new Flow<JSONObject>(c.getTime(), new FlowEventListener<JSONObject>(){
			@Override
			public void flow(FlowEvent<JSONObject> event) {
				if( event.getType() == FlowEvent.TIME_OUT ){
					System.out.println( "#284-TIME_OUT" );
					error( aiPartInfo, WIN_TYPE.TIME_OUT );
				} else if( event.getType() == FlowEvent.RECEIVE ){
					System.out.println( "Server recv:"+event.getData() );
					turnToHuman( event.getData() );
				}
			}
		}, listenId,  this.aiPartInfo.getConnId() );
		globalFlow.run();
		//send step info
		packetStepAndSend( step, this.aiPartInfo.getConnId() );
	}
	private void turnToHuman( JSONObject dataReceived ){
		if( dataReceived == null ){
			this.firBoard.turn( this.humanStatus );
			return;
		}
		int x, y;
		Step lastStep;
		try { x = dataReceived.getInt(AIEPP_FIR.TAG_POSITION_X);
			y = dataReceived.getInt(AIEPP_FIR.TAG_POSITION_Y);
			lastStep = new Step(aiPartInfo.getStatus(), x, y, 
					dataReceived.getInt(AIEPP_FIR.TAG_TIME_USED));
			//lastStep.setUsedTime();
		} catch (JSONException e) { 	e.printStackTrace();
			error( aiPartInfo, WIN_TYPE.POSITION_ERROR );
			return;
		}
		boolean b = currentInning.addStep( lastStep );
		if( lastStep != null ) {
			this.setStatusBarText(String.format(Language.TIP_TAKE_A_STEP, 
					aiPartInfo.getNickname(),
					lastStep.getX(),
					lastStep.getY(),
					lastStep.getUsedTime()
			));
		}
		this.updateUI();
		//System.out.println( "currentInnings.addStep="+b );
		if( ! b ){
			error(aiPartInfo, WIN_TYPE.POSITION_ERROR);
		} else {
			PartInfo winner = this.currentInning.getWinner();
			if( winner == null ) {
				this.firBoard.turn( this.humanStatus );
			} else {
				win( winner, WIN_TYPE.NORMAL, this.currentInning.getWinStepList() );
			}
		}
	}
	
	
	public void turnToAI( final PartInfo curPartInfo, final PartInfo nextPartInfo, JSONObject dataReceived ){
		Step lastStep = null;
		if( dataReceived != null ){
			int x, y;
			try { x = dataReceived.getInt(AIEPP_FIR.TAG_POSITION_X);
				y = dataReceived.getInt(AIEPP_FIR.TAG_POSITION_Y);
				lastStep = new Step(curPartInfo.getStatus(), x, y,
						dataReceived.getInt(AIEPP_FIR.TAG_TIME_USED));
			} catch (JSONException e) { 	e.printStackTrace();
				error( curPartInfo, WIN_TYPE.POSITION_ERROR );
				return;
			}
			boolean b = this.currentInning.addStep(lastStep);
			if( ! b ) {
				error( curPartInfo, WIN_TYPE.POSITION_ERROR );
				return;
			};
			if( lastStep != null ) {
				this.setStatusBarText(String.format(Language.TIP_TAKE_A_STEP, 
						curPartInfo.getNickname(),
						lastStep.getX(),
						lastStep.getY(),
						lastStep.getUsedTime()
				));
			}
			this.updateUI();
			PartInfo winner = currentInning.getWinner();
			if( winner != null ){
				win( winner, WIN_TYPE.NORMAL, this.currentInning.getWinStepList() );
				System.out.println( "winner : "+winner.getNickname() );
				return;
			}
		}
		this.firBoard.turn( nextPartInfo.getStatus() );
		
		//beginTiming
		Calendar c = Calendar.getInstance();
		int time = this.currentInning.getLimitedTime(nextPartInfo.getLanguage());
		//System.out.println("time:"+time );
		c.add(Calendar.MILLISECOND, time );
		globalFlow = new Flow<JSONObject>(c.getTime(), new FlowEventListener<JSONObject>(){
			@Override
			public void flow(FlowEvent<JSONObject> event) {
				if( event.getType() == FlowEvent.TIME_OUT ){
					System.out.println( "#358-TIME_OUT" );
					error( nextPartInfo, WIN_TYPE.TIME_OUT );
				} else if( event.getType() == FlowEvent.RECEIVE ){
					//System.out.println( "Server recv:"+event.getData() );
					turnToAI( nextPartInfo, curPartInfo, event.getData() );
				}
			}
		}, listenId,  nextPartInfo.getConnId() );
		globalFlow.run();
		//send step info
		packetStepAndSend( lastStep, nextPartInfo.getConnId() );
		
	}
	
	private void error( PartInfo partInfo, WIN_TYPE winType ){
		if( partInfo == this.offensivePartInfo ) {
			win( this.defensivePartInfo, winType, null );
		} else {
			win( this.offensivePartInfo, winType, null );
		}
	}
	
	public void win( PartInfo winnerInfo, WIN_TYPE winType, AbstractList<Step> stepList ){
		packetGameOverAndSend( winnerInfo.getStatus(), winType, stepList, 
				this.offensivePartInfo.getConnId(), 
				this.defensivePartInfo.getConnId());
		this.firBoard.win(winnerInfo);
		String winText = "Player " + winnerInfo.getNickname() + " is Winner!";
		this.setStatusBarText( winText );
		JOptionPane.showMessageDialog(null, winText);
	}

	public void updateUI(){
		if( this.firBoard == null ){
			System.out.println( "this.firBoard == null!!!" );return;
		}
		this.firBoard.invalidate();
		this.firBoard.repaint();
	//	this.firBoard.update(this.firBoard.getGraphics());
	//	this.firBoard.updateUI();
	}
	
	private void packetInningsInfoAndSend(boolean offensive, int limitedTime, int heuristic, 
			Inning inning,
			PartInfo opponentInfo, int connId){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(AIEPP_FIR.TAG_TYPE, AIEPP_FIR.TagType.INNING_INFO.ordinal());
			jsonObject.put(AIEPP_FIR.TAG_MODE, mode.ordinal());
			jsonObject.put(AIEPP_FIR.TAG_OFFENSIVE,  offensive?1:0 );
			jsonObject.put(AIEPP_FIR.TAG_TIME, limitedTime);
			jsonObject.put(AIEPP_FIR.TAG_OPPONENT_ID, opponentInfo.getId());
			jsonObject.put(AIEPP_FIR.TAG_OPPONENT_NAME, opponentInfo.getName());
			jsonObject.put(AIEPP_FIR.TAG_OPPONENT_NICKNAME, opponentInfo.getNickname());
			jsonObject.put(AIEPP_FIR.TAG_HEURISTIC, heuristic);
			jsonObject.put(AIEPP_FIR.TAG_CHESS, inning.toChessString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendPacket( jsonObject, connId ); return;
	}
	private void packetStepAndSend(Step step, int connId) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(AIEPP_FIR.TAG_TYPE, AIEPP_FIR.TagType.RUNNING.ordinal());
			if( step != null ) {//it means that  a StepPacket cannot include a step info.
				jsonObject.put(AIEPP_FIR.TAG_POSITION_X, step.getX());
				jsonObject.put(AIEPP_FIR.TAG_POSITION_Y, step.getY());
				jsonObject.put(AIEPP_FIR.TAG_TIME_USED, step.getUsedTime());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendPacket( jsonObject, connId ); return;
	}
	private void packetGameOverAndSend( Status winner, WIN_TYPE winType,
			AbstractList<Step> winStepList, int connid, int connid2){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(AIEPP_FIR.TAG_TYPE, AIEPP_FIR.TagType.GAME_OVER.ordinal());
			jsonObject.put(AIEPP_FIR.TAG_WINNER, winner.ordinal());
			jsonObject.put(AIEPP_FIR.TAG_WIN_TYPE, winType.ordinal());
			if( winType == WIN_TYPE.NORMAL ){
				if( winStepList == null ){
					System.out.println( "Some Error(s) occured!" );
				}
				Step step = winStepList.get(0);
				jsonObject.put(AIEPP_FIR.TAG_POSITION_X0, step.getX());
				jsonObject.put(AIEPP_FIR.TAG_POSITION_Y0, step.getY());
				step = winStepList.get(1);
				jsonObject.put(AIEPP_FIR.TAG_POSITION_X1, step.getX());
				jsonObject.put(AIEPP_FIR.TAG_POSITION_Y1, step.getY());
				step = winStepList.get(2);
				jsonObject.put(AIEPP_FIR.TAG_POSITION_X2, step.getX());
				jsonObject.put(AIEPP_FIR.TAG_POSITION_Y2, step.getY());
				step = winStepList.get(3);
				jsonObject.put(AIEPP_FIR.TAG_POSITION_X3, step.getX());
				jsonObject.put(AIEPP_FIR.TAG_POSITION_Y3, step.getY());
				step = winStepList.get(4);
				jsonObject.put(AIEPP_FIR.TAG_POSITION_X4, step.getX());
				jsonObject.put(AIEPP_FIR.TAG_POSITION_Y4, step.getY());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		sendPacket( jsonObject, connid ); 
		sendPacket( jsonObject, connid2 );
		return;
	}
	private void sendPacket( JSONObject data, int connId ){
		try {
			DataTransmit.getInstance().sendData( data, listenId, connId );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	
	
	private class HumanAINewConnListener implements NewConnListener {

		private PartInfo partInfo;
		private int connCount = 0;
		
		public HumanAINewConnListener( PartInfo AIPartInfo ){
			this.partInfo = AIPartInfo;
		}
		
		@Override
		public void newConn(NewConnEvent event) {
			connCount++;
			if( connCount == 1 ){
				String id = event.getStudentInfo().getId();
				String name = event.getStudentInfo().getName();
				String nick = event.getStudentInfo().getNickname();
				
				FIRJudge.this.setStatusBarText( String.format(Language.TIP_CONN, id, name, nick));//UI
				
				partInfo.set(id, name, nick );
				partInfo.setConnId(event.getConnId());
				partInfo.setLanguage( event.getLang() );
				//System.out.println( "lang="+ event.getLang() );
				
				FIRJudge.this.runHumanAIMode();
			} else {
				System.out.println( "error conn!" );
			}
		}
	}
	
	private class AIAINewConnListener implements NewConnListener {
		
		private int connCount = 0;
		
		@Override
		public void newConn(NewConnEvent event) {
			
			String id = event.getStudentInfo().getId();
			String name = event.getStudentInfo().getName();
			String nick = event.getStudentInfo().getNickname();
			
			FIRJudge.this.setStatusBarText( String.format(Language.TIP_CONN, id, name, nick));//UI
			
			if( connCount == 0 ){
				offensivePartInfo.set(id, name, nick);
				offensivePartInfo.setConnId(event.getConnId());
				offensivePartInfo.setLanguage( event.getLang() );
			} else if( connCount == 1 ){
				defensivePartInfo.set(id, name, nick);
				defensivePartInfo.setConnId(event.getConnId());
				defensivePartInfo.setLanguage( event.getLang() );
				FIRJudge.this.runAIAIMode();
			}//igonre the other conns
			connCount++;
		}
		
	}
	
	


	@Override
	public String getAuthor() {
		return "ygsx";
	}


	@Override
	public String getName(Country countryNo) {
		return "Five In Row[Judge]";
	}

	private void initializeMenu(){
		startItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "start");
			}
		});
		stopItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "stop");
			}
		});
		nextItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "next");
			}
		});
		saveItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "save");
			}
		});
		startItem.setEnabled(false);
		stopItem.setEnabled(false);
		nextItem.setEnabled(false);
		saveItem.setEnabled(false);
	}
	
	@Override
	public AbstractList<? extends JComponent> getSetMenu() {
		AbstractList<JMenuItem> menuList= new ArrayList<JMenuItem>();
		/*
		menuList.add(startItem);
		menuList.add(stopItem);
		menuList.add(nextItem);
		menuList.add(saveItem);//*/
		return menuList;
	}

	@Override
	public String getVersion() {
		return "v0.1.2.36";
	}


	public static void main(String[] args){
		AIEP.main(args);
		//AIEPP_FIRServer server = new AIEPP_FIRServer();
		//server.initialize(null);
	}

	@Override
	public JDialog getExtConfDialog() {
		return this.setDialog;
	}


	protected Result finalResult, tempResult;
	protected Date lastUpdate;
	@Override
	protected Result getResult(final AILoader<FIR_AI> aiLoader1, final AILoader<FIR_AI> aiLoader2) {
		if( aiLoader1 == null || aiLoader2 == null || 
				aiLoader1.getAI() == null || aiLoader2.getAI() == null )  return null;
		InningInfo inninginfo = null;
		try {
			inninginfo =(InningInfo) Naming.lookup("rmi://localhost:"+AIEPP_FIR.REMOTE_PORT+"/InningInfo");
			//System.out.println( "firArena.getOffensiveHeuristic="+inninginfo.offensiveHeuristic);
		} catch (MalformedURLException e1) {
			e1.printStackTrace(); return null;
		} catch (RemoteException e1) {
			e1.printStackTrace();return null;
		} catch (NotBoundException e1) {
			e1.printStackTrace();return null;
		}
		final FIR_AI ai1 = aiLoader1.getAI();
		final FIR_AI ai2 = aiLoader2.getAI();
		tempResult = new Result();
		AIInfo ai1info = new AIInfo(ai1.getId(), ai1.getName(), ai1.getNickname(), ai1.getVersion());
		AIInfo ai2info = new AIInfo(ai2.getId(), ai2.getName(), ai2.getNickname(), ai2.getVersion());
		System.out.printf( "#1:%s,%s,%s,%s\n", ai1.getId(), ai1.getName(), ai1.getNickname(), ai1.getVersion() );
		System.out.printf( "#2:%s,%s,%s,%s\n", ai2.getId(), ai2.getName(), ai2.getNickname(), ai2.getVersion() );
		tempResult.setOffensiveAIInfo(ai1info);
		tempResult.setDefensiveAIInfo(ai2info);
		tempResult.setResultCode(ResultCode.ERROR);

		readConfFile( cppLevelTime, javaLevelTime );
		try { setMode( Mode.AI_VS_AI, inninginfo.offensiveHeuristic, 
				inninginfo.defensiveHeuristic, inninginfo.level, inninginfo.chessFileName );
		} catch (Exception e) { e.printStackTrace(); }
		this.offensivePartInfo.setLanguage(aiLoader1.getAILang());
		this.defensivePartInfo.setLanguage(aiLoader2.getAILang());
		StudentInfo si1 = new StudentInfo(ai1.getId(), ai1.getName(), ai1.getNickname());
		StudentInfo si2 = new StudentInfo(ai2.getId(), ai2.getName(), ai2.getNickname());
		String chessString = this.currentInning.toChessString();
		ai1.setInningInfo(Status.OFFENSIVE, 
				this.currentInning.getLimitedTime(this.offensivePartInfo.getLanguage()), 
				si2,  FIRLoader.transferChessString(chessString) );
		ai2.setInningInfo(Status.DEFENSIVE, 
				this.currentInning.getLimitedTime(this.defensivePartInfo.getLanguage()), 
				si1,  FIRLoader.transferChessString(chessString) );
		
		finalResult = new Result();
		finalResult.setOffensiveAIInfo(ai1info);
		finalResult.setDefensiveAIInfo(ai2info);
		finalResult.setResultCode(null);
		INetManager manager = NetManager.newInstance();
		manager.init(null);
		connArenaId = manager.createConnection(
				userConf.getString(AijBasicConf.AIJ_RESULT_SEND_IP), 
				userConf.getInt(AijBasicConf.AIJ_RESULT_SEND_PORT)
		);
		if( connArenaId == NetManager.ILLEGAL_ID ) {
			System.out.println( "Cannot connect to the server!" );
			return null;
		}
		new Thread(new Runnable(){
			@Override
			public void run() {
				turn2AI(ai2, ai1, Status.DEFENSIVE, Status.OFFENSIVE, 
						aiLoader2.getAILang(), aiLoader1.getAILang(), null/*lastStep*/);
			}
		}).start();
		lastUpdate = new Date();
		while( true ){
			/*
			lastStep = ai1.itsmyturn(lastStep);
			lastStep.setStatus(Status.OFFENSIVE);
			System.out.println( "of=x:"+lastStep.getX()+",y:"+lastStep.getY() );
			if( ! this.currentInning.addStep(lastStep) ){
				System.out.println( "add failed!" );
				result.setResultCode(ResultCode.DONE);
				result.setWinner(Status.DEFENSIVE);
				break;
			}
			winner = currentInning.getWinner();
			if( winner != null ){
				System.out.println( "Winner = "+winner.getId()+"!" );
				result.setResultCode(ResultCode.DONE);
				result.setWinner(Status.OFFENSIVE);
				break;
			}
			lastStep = ai2.itsmyturn(lastStep);
			lastStep.setStatus(Status.DEFENSIVE);
			System.out.println( "de=x:"+lastStep.getX()+",y:"+lastStep.getY() );
			if( ! this.currentInning.addStep(lastStep) ){
				System.out.println( "add2 failed!" );
				result.setResultCode(ResultCode.DONE);
				result.setWinner(Status.OFFENSIVE);
				break;
			}
			winner = currentInning.getWinner();
			if( winner != null ){
				result.setResultCode(ResultCode.DONE);
				result.setWinner(Status.DEFENSIVE);
				break;
			}*/
			Date go_back = new Date();
			go_back.setTime( go_back.getTime() - AIArena.MAX_RESPONSE_TIME*1000 );
			if( lastUpdate.before(go_back) )break;
			try { Thread.sleep(1000);//sleep 1s
			} catch (InterruptedException e) { e.printStackTrace(); }
			synchronized( finalResult ){
				if( finalResult.getResultCode() != null ){
					System.out.println( "finalResult.getResultCode() != null" );
					break;
				} else {
					System.out.println( "finalResult.getResultCode() == null" );
				}
			}
		}
		System.out.println( "finalResult is not null!" );
		return finalResult;
	}
	
	private void turn2AI(final FIR_AI currentAI, final FIR_AI nextAI, final Status currentStatus, final Status nextStatus,
			Lang currentLang, Lang nextLang, Step lastStep){
		if( lastStep != null ){
			lastStep.setStatus(currentStatus);
			if( ! this.currentInning.addStep(lastStep) ){
				//error
				String stepstr = "lastStep=("+lastStep.getX()+", "+lastStep.getY()+")";
				System.out.println( stepstr );
				_win( nextAI, nextStatus, currentStatus, WIN_TYPE.POSITION_ERROR, null, stepstr );
				return;
			}
			String temp = currentStatus+".step=x:"+lastStep.getX()+",y:"+lastStep.getY();
			System.out.println( temp );
			Result result = new Result();
			result.setOffensiveAIInfo(tempResult.getOffensiveAIInfo());
			result.setDefensiveAIInfo(tempResult.getDefensiveAIInfo());
			result.setRealTimeResult(temp);
			result.setActiveStatus(currentStatus);
			NetManager.newInstance().pushMessage(connArenaId, 
					new ResultMessage( result, ResultType.REALTIME ) );
			PartInfo winner = this.currentInning.getWinner();
			if( winner != null ){
				_win( currentAI, currentStatus, nextStatus, WIN_TYPE.NORMAL, currentInning.getStepList(), null );//win
				return;
			}
		} else {
			lastStep = new Step(0, 0);
		}
		
		//time 
		Calendar c = Calendar.getInstance();
		int time = this.currentInning.getLimitedTime( nextLang );
		System.out.println( "time="+time+new Date() );
		c.add(Calendar.MILLISECOND, time );
		Timer timer = new Timer();
		timer.schedule(new TimerTask(){
			@Override
			public void run() {
				System.out.println( "timer!" );
				_win( currentAI, currentStatus, nextStatus, WIN_TYPE.TIME_OUT, null, null );
			}
		}, c.getTime() );
		Step newStep = null;
		try {
			long t = System.currentTimeMillis();
			newStep = nextAI.itsmyturn(lastStep);
			long t2 = System.currentTimeMillis();
			newStep.setUsedTime((int)(t2-t));
		} catch(Exception e){e.printStackTrace();
			_win( currentAI, currentStatus, nextStatus, WIN_TYPE.TIME_OUT, null, e.getMessage()  );//error
			return;
		}
		timer.cancel();
		turn2AI(nextAI, currentAI, nextStatus, currentStatus, 
				nextLang, currentLang, newStep );
	}
	private void _win(FIR_AI winAI, Status winStatus, Status errorStatus, 
			WIN_TYPE winType, AbstractList<Step> stepList, String cause ){
		System.out.println( "winner:"+winStatus+", winType="+winType+new Date() );
		synchronized( finalResult ){
			finalResult.setOffensiveAIInfo(tempResult.getOffensiveAIInfo());
			finalResult.setDefensiveAIInfo(tempResult.getDefensiveAIInfo());
			finalResult.setRealTimeResult(cause);
			if( winType == WIN_TYPE.NORMAL ){
				finalResult.setResultCode(ResultCode.DONE);
				finalResult.setWinner(winStatus);
			} else if ( winType == WIN_TYPE.POSITION_ERROR ){
				finalResult.setResultCode(ResultCode.ERROR);
				finalResult.setErrorStatus(errorStatus);
			} else if ( winType == WIN_TYPE.TIME_OUT ){
				finalResult.setResultCode(ResultCode.ERROR);
				finalResult.setErrorStatus(errorStatus);
			}
		}
	}

	@Override
	protected AILoader<FIR_AI> genAILoader() {
		return new FIRLoader();
	}

	
}





