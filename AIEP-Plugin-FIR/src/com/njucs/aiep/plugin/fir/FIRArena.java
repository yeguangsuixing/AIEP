package com.njucs.aiep.plugin.fir;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.AbstractList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEP.Country;
import com.njucs.aiep.AIEPUserConf.AiaBasicConf;
import com.njucs.aiep.flow.Flow;
import com.njucs.aiep.flow.FlowEvent;
import com.njucs.aiep.flow.FlowEventListener;
import com.njucs.aiep.flow.FlowTrigger;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.frame.Desk;
import com.njucs.aiep.frame.AIInfo.AIState;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.plugin.AIArena;
import com.njucs.aiep.plugin.AIJudge;
import com.njucs.aiep.plugin.fir.frame.AIWrapper;
import com.njucs.aiep.plugin.fir.frame.FIRDesk;
import com.njucs.aiep.plugin.fir.frame.Inning;
import com.njucs.aiep.plugin.fir.frame.InningInfo;
import com.njucs.aiep.plugin.fir.frame.Mode;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage;
import com.njucs.aiep.plugin.fir.frame.Step;
import com.njucs.aiep.plugin.fir.frame.Inning.PartInfo;
import com.njucs.aiep.plugin.fir.frame.Inning.WIN_TYPE;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage.Cmd;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage.Data;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage.DeskReply;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage.MsgType;
import com.njucs.aiep.plugin.fir.ui.SetDialog;
import com.twzcluster.net.BreakConnListener;
import com.twzcluster.net.BreakEvent;
import com.twzcluster.net.Message;
import com.twzcluster.net.NetManager;
import com.twzcluster.net.NewConnEvent;
import com.twzcluster.net.NewConnListener;
import com.twzcluster.net.ReceiveEvent;
import com.twzcluster.net.ReceiveListener;


public class FIRArena extends AIArena<FIRDesk> implements Remote {

	protected int onlineListenerId = NetManager.ILLEGAL_ID;

	protected OnlineNewConnListener onlineNewConnListener = new OnlineNewConnListener();
	protected OnlineBreakConnListener onlineBreakConnListener = new OnlineBreakConnListener();
	protected OnlineRecvListener onlineRecvListener = new OnlineRecvListener();

	//connid--map--ai
	protected Map<Integer, AIWrapper> aiWrapperMap = Collections.synchronizedMap(new TreeMap<Integer, AIWrapper>());
	
	//private Inning currentInning;
	private SetDialog setDialog = null;
	private int[] cppLevelTime = { 2, 8, 15 }, javaLevelTime = { 3, 10, 20 };
	private Thread releaseDeskInfoThread;
	private ReleaseDeskInfoRunnable releaseDeskInfoRunnable = null;

	//private int offensiveHeuristic = 1, defensiveHeuristic = 1, level = 1;
	//private String chessFileName;
	private InningInfo inningInfo = new InningInfo();
	

	
	@Override
	public String getAuthor() {
		return "ygsx";
	}

	@Override
	public String getName(Country countryno) {
		return "Five In Row[Arena]";
	}
	@Override
	public String getVersion() {
		return "v0.1.2.1";
	}

	@Override
	public Class<? extends AIJudge<?>> getAIJudgeClass() {
		return FIRJudge.class;
	}
	
	public boolean initialize(AIEP aiep) {
		boolean b = super.initialize(aiep);
		if( ! b ) return false;
		for( int i = 0; i < 10; i ++ ){//TODO 10!
			FIRDesk desk = FIRDesk.newInstance();
			onlineDeskList.add( desk );
		}
		String errmsg = null;
		if( (errmsg = FIRJudge.readConfFile( cppLevelTime, javaLevelTime )) != null ){
			JOptionPane.showMessageDialog(null, errmsg);
			aiep.setStatusBarText(errmsg);
		}
		this.setDialog = new SetDialog( null, this );
		return true;
	}

	@Override
	protected int executeOnlineMode() {
		onlineListenerId  = NetManager.newInstance().createListener("localhost", 
				userConf.getInt(AiaBasicConf.AIA_ONLINE_PORT),
				onlineNewConnListener, onlineBreakConnListener, onlineRecvListener
		);
		releaseDeskInfoRunnable = new ReleaseDeskInfoRunnable();
		releaseDeskInfoThread = new Thread( releaseDeskInfoRunnable );
		releaseDeskInfoThread.setName("com.njucs.aiep.plugin.fir.FIRArena.releaseDeskInfoThread");
		releaseDeskInfoThread.start();
		if( onlineListenerId == NetManager.ILLEGAL_ID ){
			this.aiep.addPrompt("creating the online listener failed! ");
			return 1;
		} else {
			this.aiep.addPrompt("creating the online listener succeed! ");
			return 0;
		}
	}
	
	@Override
	protected void forceExitUploadMode(){
		super.forceExitUploadMode();
		try {
			Naming.unbind("rmi://localhost:"+AIEPP_FIR.REMOTE_PORT+"/InningInfo");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected int executeUploadMode(){
		int a = super.executeUploadMode();
		if( a != 0 ) return a;
		try {
			LocateRegistry.createRegistry(AIEPP_FIR.REMOTE_PORT);
			Naming.bind( "rmi://localhost:"+AIEPP_FIR.REMOTE_PORT+"/InningInfo", this.inningInfo ); 
		} catch (RemoteException e) {
			e.printStackTrace(); return 1;
		} catch (MalformedURLException e) {
			e.printStackTrace();return 1;
		} catch (AlreadyBoundException e) {
			e.printStackTrace();return 1;
		}
		return 0;
	}

	@Override
	protected void forceExitOnlineMode() {
		if( releaseDeskInfoRunnable != null ) {
			releaseDeskInfoRunnable.stop = true;
		}
		if( releaseDeskInfoThread != null ) {
			releaseDeskInfoThread.interrupt();
		}
		releaseDeskInfoRunnable =null;
		releaseDeskInfoThread = null;
		NetManager.newInstance().closeConn(onlineListenerId);
	}
	
	@Override
	public JDialog getExtConfDialog() {
		return this.setDialog;
	}

	
	
	public class OnlineNewConnListener implements NewConnListener {
		@Override
		public void newConn(NewConnEvent event) {
			synchronized(aiWrapperMap){
				AIWrapper wrapper = new AIWrapper();
				wrapper.setConnId(event.getConn().getConnId());
				aiWrapperMap.put(event.getConn().getConnId(), wrapper);
			}
			//send the room info message
			synchronized(onlineDeskList){
				OnlineMessage om = new OnlineMessage(onlineDeskList);
				om.setReceiverId(event.getConn().getConnId());
				NetManager.newInstance().pushMessage(onlineListenerId, om);
			}
		}
		
	} 
	public class  OnlineBreakConnListener implements BreakConnListener {

		@Override
		public void breakConn(BreakEvent event) {
			int  connid = event.getConn().getConnId();
			AIWrapper wrapper = aiWrapperMap.get(connid);
			synchronized(aiWrapperMap){
				aiWrapperMap.remove(connid);
			}
			FIRDesk desk = wrapper.getDesk();
			if( desk != null ){
				synchronized( desk ){
					if( desk.getWrapper2() == null ){
						desk.setWrapper1(null);
						System.out.println( "set the desk.wrapper1 == null" );
						//desk.setWrapper2(wrapper2)
					}
				}
			}
			if( wrapper != null ){
				synchronized( wrapper ) {
					wrapper.setDesk(null);
					if( wrapper.getAiInfo() != null ){
						wrapper.getAiInfo().setAiState(AIState.OFFLINE);
						aiep.addPrompt( "The  competitor(id="+wrapper.getAiInfo().getId()+") dropped!" );
					}
				}
			}
		}
		
	}
	public  class OnlineRecvListener implements ReceiveListener {

		@Override
		public void receivedMessage(ReceiveEvent event) {
			int connid = event.getConn().getConnId();
			AIWrapper  aiWrapper = aiWrapperMap.get( connid );
			if( aiWrapper == null ) return;
			int enterDeskId = Desk.ILLEGAL_ID, exitDeskId = Desk.ILLEGAL_ID;
			synchronized(aiWrapper) {
				OnlineMessage msg = (OnlineMessage)(event.getMessage());
				if( msg.getMsgType() == MsgType.CMD ){
					if( msg.getCmd() == Cmd.ENTER_DESK ){ //int deskid = msg.getDeskId();
						OnlineMessage backmsg = null;
						if( aiWrapper.getAiInfo() == null ){
							backmsg = new OnlineMessage(DeskReply.ERROR  );
							backmsg.setContent( "you info is null!" );
						} else {
							if( aiWrapper.getAiInfo().getDesk() != null ){
								//error
								backmsg = new OnlineMessage(DeskReply.ERROR);
								backmsg.setContent( "you have been into a desk!" );
							} else {
								enterDeskId = msg.getDeskId();
							}
						}
						if( backmsg != null ) {
							backmsg.setReceiverId( event.getConn().getConnId() );
							NetManager.newInstance().pushMessage(onlineListenerId, backmsg);
							return;
						}
					} else if( msg.getCmd() == Cmd.EXIT_DESK ){ 
						exitDeskId = msg.getDeskId();
					}
				} else if (msg.getMsgType() == MsgType.DATA){
					if( msg.getDataType() == Data.AI_INFO ){
						AIInfo servInfo = null;
						AIInfo aiInfo = msg.getAiInfo();
						if( aiInfo != null ){
							System.out.println( "AI Language: "+aiInfo.getLanguage() );
							synchronized( onlineAllInfoList ){
								Iterator<AIInfo>  iter = onlineAllInfoList.iterator();
								while( iter.hasNext() ){
									AIInfo ai = iter.next();
									if( ai.equals(aiInfo) ){
										servInfo = ai; break;
									}
								}
								if( servInfo == null ){
									System.out.println( "servInfo == null" );
									onlineAllInfoList.add(aiInfo);
									servInfo = aiInfo;
								} else {
									System.out.println( "servInfo != null. id = "+servInfo.getId() );
									if( servInfo.getAiState() != AIState.OFFLINE ){
										OnlineMessage om = new OnlineMessage( MsgType.CMD, 
												Cmd.AI_REPLY, "There is an AI online with the same info." );
										om.setReceiverId(connid);
										NetManager.newInstance().pushMessage(  FIRArena.this.onlineListenerId, om);
										return;
									}
								}
								servInfo.setAiState(AIState.WATCHING);
								aiWrapper.setAiInfo( servInfo );	
							}
						}
					} else if( msg.getDataType() == Data.STEP_INFO ){
						if( aiWrapper.getFlowRunnable() != null ) {
							aiWrapper.getFlowRunnable().run(null, null, msg.getStep());
						}
					}
				}
			}
			if( enterDeskId != Desk.ILLEGAL_ID ) {
				handleEnterDesk(aiWrapper, enterDeskId, event.getConn().getConnId());
			} else  if( exitDeskId != Desk.ILLEGAL_ID ) {
				//synchronized(onlineDeskList){ }
			} else {
				
			}
		}
		
		private void handleEnterDesk(AIWrapper  aiWrapper, int enterDeskId, int connId){
			OnlineMessage backmsg = null;/*
			if( aiWrapper.getAiInfo() == null ) {
				backmsg = new OnlineMessage(DeskReply.ERROR  );
				backmsg.setContent( "you info is null!" );
				backmsg.setReceiverId( connId );
				NetManager.newInstance().pushMessage(onlineListenerId, backmsg);
				return;
			}//*/
			FIRDesk enterDesk = null;
			FIRDesk startDesk = null;//if start, then startDesk != null
			if( aiWrapper.getDesk() != null ){
				backmsg = new OnlineMessage(DeskReply.ERROR  );
				backmsg.setContent( "you have been into a desk!" );
			} else {
				synchronized(onlineDeskList){
					Iterator<FIRDesk> iter = onlineDeskList.iterator();
					while(iter.hasNext()){
						enterDesk = iter.next();
						if( enterDesk.getDeskId() == enterDeskId ){ break; }
						enterDesk = null;
					}
				}
				if( enterDesk != null ){
					synchronized( enterDesk ){
						if( enterDesk.getWrapper1() == null ){
							aiWrapper.setDesk(enterDesk);
							aiWrapper.setStatus(Status.OFFENSIVE);
							enterDesk.setWrapper1(aiWrapper);
							//aiWrapper.getAiInfo().setConnId(connid);
							aiWrapper.getAiInfo().setAiState(AIState.WAITING);
							backmsg = new OnlineMessage(DeskReply.OK  );
							backmsg.setDeskId(enterDeskId);
							backmsg.setStatus(Status.OFFENSIVE);
							aiep.setStatusBarText( "ai(id="+aiWrapper.getAiInfo().getId()+
									", name="+aiWrapper.getAiInfo().getName()+
									", nickname="+aiWrapper.getAiInfo().getNickname()+
									",ver="+aiWrapper.getAiInfo().getVersion()+
									") enter the desk, id = "+enterDesk.getDeskId() );
							//send the desk info
							OnlineMessage om = new OnlineMessage(onlineDeskList);
							om.setReceiverId( Message.DEFAULT );
							NetManager.newInstance().pushMessage(onlineListenerId, om);
						} else if( enterDesk.getWrapper2() == null ){
							aiWrapper.setDesk(enterDesk);
							aiWrapper.setStatus(Status.DEFENSIVE);
							enterDesk.setWrapper2(aiWrapper);
							//aiWrapper.getAiInfo().setConnId(connid);
							enterDesk.getWrapper1().getAiInfo().setAiState(AIState.RUNNING);
							aiWrapper.getAiInfo().setAiState(AIState.RUNNING);
							
							aiep.setStatusBarText( "ai(id="+aiWrapper.getAiInfo().getId()+
									", name="+aiWrapper.getAiInfo().getName()+
									", nickname="+aiWrapper.getAiInfo().getNickname()+
									",ver="+aiWrapper.getAiInfo().getVersion()+
									") enter the desk, id = "+enterDesk.getDeskId() );
							
							//send the desk info
							OnlineMessage om = new OnlineMessage(onlineDeskList);
							om.setReceiverId( Message.DEFAULT );
							NetManager.newInstance().pushMessage(onlineListenerId, om);
							
							backmsg = new OnlineMessage(DeskReply.OK  );
							backmsg.setDeskId(enterDeskId);
							backmsg.setStatus(Status.DEFENSIVE);
							startDesk = enterDesk;
						} else {
							backmsg = new OnlineMessage(DeskReply.ERROR  );
							backmsg.setDeskId(enterDeskId);
							backmsg.setContent( "the desk is busy!" );
						}
					}
				}  else {// enterDesk == null
					backmsg = new OnlineMessage(DeskReply.ERROR  );
					backmsg.setDeskId(enterDeskId);
					backmsg.setContent( "desk == null" );
				}
			}
			
			//send enter info
			backmsg.setReceiverId( connId );
			NetManager.newInstance().pushMessage(onlineListenerId, backmsg);
			if( startDesk != null ){
				startAGame( startDesk );
			}
		}//enterDeskId == Desk.ILLEGAL_ID
		
		private void startAGame(FIRDesk desk){

			AIInfo ofinfo = desk.getWrapper1().getAiInfo();
			AIInfo deinfo = desk.getWrapper2().getAiInfo();
			
			Mode mode = Mode.AI_VS_AI;
			PartInfo offensivePartInfo = new PartInfo( ofinfo );
			PartInfo defensivePartInfo = new PartInfo( deinfo );
			Inning inning = new Inning(mode, offensivePartInfo, defensivePartInfo);
			
			//set begin
			inning.setCppTime(cppLevelTime[inningInfo.level-1]);
			inning.setJavaTime(javaLevelTime[inningInfo.level-1]);
			try {
				inning.setChessFile(inningInfo.chessFileName);
			} catch (Exception e1) {
				e1.printStackTrace();
				if( desk.getWrapper1() != null ) {
					desk.getWrapper1().setDesk(null);
					if( desk.getWrapper1().getAiInfo()!= null ){
						desk.getWrapper1().getAiInfo().setAiState(AIState.WATCHING);
					}
				}
				if( desk.getWrapper2() != null ) {
					desk.getWrapper2().setDesk(null);
					if( desk.getWrapper2().getAiInfo()!= null ){
						desk.getWrapper2().getAiInfo().setAiState(AIState.WATCHING);
					}
				}
				desk.setWrapper1(null);
				desk.setWrapper2(null);
				aiep.addPrompt( "Setting Chess File Failed!" + e1.getMessage());
				return;
			}
			offensivePartInfo.setHeuristic(inningInfo.offensiveHeuristic);
			defensivePartInfo.setHeuristic(inningInfo.defensiveHeuristic);
			
			//set end
			
			desk.setInning(inning);
			
			offensivePartInfo.setLanguage(ofinfo.getLanguage());
			defensivePartInfo.setLanguage(deinfo.getLanguage());
			offensivePartInfo.setHeuristic(0);//0-defualt heuristic
			defensivePartInfo.setHeuristic(0);
			offensivePartInfo.setConnId(desk.getWrapper1().getConnId());
			defensivePartInfo.setConnId(desk.getWrapper2().getConnId());
			//offensivePartInfo.set
			//inning
		//	inning.setCppTime(cppLevelTime[0]);
		//	inning.setJavaTime(javaLevelTime[0]);
		//	try { inning.setChessFile(null);
		//	} catch (Exception e) { e.printStackTrace(); }
			desk.setInning(inning);
			//wrapper
			desk.getWrapper1().setDesk(desk);
			desk.getWrapper2().setDesk(desk);
			//send inning info
			System.out.println( "start a game!send the inning info th the two competitors!" );
			packetInningsInfoAndSend( Status.OFFENSIVE, 
					inning.getLimitedTime(offensivePartInfo.getLanguage()), 
					offensivePartInfo.getHeuristic(), inning,
					defensivePartInfo, 
					offensivePartInfo.getConnId() );
			packetInningsInfoAndSend( Status.DEFENSIVE, 
					inning.getLimitedTime(defensivePartInfo.getLanguage()), 
					defensivePartInfo.getHeuristic(), inning,
					offensivePartInfo, 
					defensivePartInfo.getConnId() );
			
			turnToAI(desk, desk.getWrapper2(), desk.getWrapper1(), defensivePartInfo, offensivePartInfo, null  );
		}
		

		
		private void turnToAI( final FIRDesk desk, final AIWrapper curAIWrapper, final AIWrapper nextAIWrapper,
				final PartInfo curPartInfo, final PartInfo nextPartInfo, Step lastStep ){
			if( lastStep != null){
				System.out.println( "lastStep="+lastStep.getX()+", "+lastStep.getY() );
				lastStep.setStatus( curAIWrapper.getStatus() );
				boolean b = desk.getInning().addStep(lastStep);
				if( ! b ) {
					System.out.println( "position is error!"+lastStep.getX()+", "+lastStep.getY() );
					synchronized( curAIWrapper ){
						if( curAIWrapper.getAiInfo() != null ){
							curAIWrapper.getAiInfo().setTotalCount( curAIWrapper.getAiInfo().getTotalCount() +1);
						}
					}
					synchronized( nextAIWrapper ){
						if( nextAIWrapper.getAiInfo() != null ){
							nextAIWrapper.getAiInfo().setTotalCount( nextAIWrapper.getAiInfo().getTotalCount() +1);
							nextAIWrapper.getAiInfo().setWinCount(nextAIWrapper.getAiInfo().getWinCount()+1);
						}
					}
					error( desk, curPartInfo, WIN_TYPE.POSITION_ERROR );
					return;
				};
				PartInfo winner = desk.getInning().getWinner();
				if( winner != null ){
					synchronized( curAIWrapper ){
						if( curAIWrapper.getAiInfo() != null ){
							curAIWrapper.getAiInfo().setTotalCount( curAIWrapper.getAiInfo().getTotalCount() +1);
							curAIWrapper.getAiInfo().setWinCount(curAIWrapper.getAiInfo().getWinCount()+1);
						}
					}
					synchronized( nextAIWrapper ){
						if( nextAIWrapper.getAiInfo() != null ){
							nextAIWrapper.getAiInfo().setTotalCount( nextAIWrapper.getAiInfo().getTotalCount() +1);
						}
					}
					win( desk, winner, WIN_TYPE.NORMAL, desk.getInning().getWinStepList() );
					System.out.println( "winner : "+winner.getNickname() );
					return;
				}
			} //else {
			//	lastStep = new Step(0, 0);
			//}
			if( nextAIWrapper.getDesk() == null || nextAIWrapper.getAiInfo() == null
					|| nextAIWrapper.getAiInfo().getAiState() == AIState.OFFLINE ){
				System.out.println( "The next competitor dropped!"+lastStep.getX()+", "+lastStep.getY() );
				synchronized( curAIWrapper ){
					if( curAIWrapper.getAiInfo() != null ){
						curAIWrapper.getAiInfo().setTotalCount( curAIWrapper.getAiInfo().getTotalCount() +1);
					}
				}
				synchronized( nextAIWrapper ){
					if( nextAIWrapper.getAiInfo() != null ){
						nextAIWrapper.getAiInfo().setTotalCount( nextAIWrapper.getAiInfo().getTotalCount() +1);
						nextAIWrapper.getAiInfo().setWinCount(nextAIWrapper.getAiInfo().getWinCount()+1);
					}
				}
				error( desk, nextPartInfo, WIN_TYPE.TIME_OUT );
				return;
			}
			
			//
			int time = desk.getInning().getLimitedTime(nextPartInfo.getLanguage());
			Calendar c = Calendar.getInstance();
			c.add(Calendar.MILLISECOND, time );
			final Flow<Step> flow = new Flow<Step>(c.getTime(), 
					new FlowEventListener<Step>(){
				@Override
				public void flow(FlowEvent<Step> event) {
					if( event.getType() == FlowEvent.TIME_OUT ){
						System.out.println( "#1-TIME_OUT"+new Date() );
						System.out.println( "timeout id: "+nextPartInfo.getId() );
						error( desk, nextPartInfo, WIN_TYPE.TIME_OUT );
						//K.O.
					} else if( event.getType() == FlowEvent.RECEIVE ){
						System.out.println( "Server recv:"+event.getData().getX()+", "+event.getData().getY()+", "+new Date() );
						turnToAI( desk, nextAIWrapper, curAIWrapper, nextPartInfo, curPartInfo, event.getData() );
					}
				}
			}, onlineListenerId,  nextPartInfo.getConnId() );
			flow.setFlowTrigger(new FlowTrigger(){
				@Override
				public void trigger() {
					System.out.println( "trigger"+new Date() );
					nextAIWrapper.setFlowRunnable(flow.getFlowRunnable());
				}
				@Override
				public void untrigger() {
					System.out.println( "untrigger"+new Date() );
					nextAIWrapper.setFlowRunnable(null);
				}
			});
			
			flow.run();
			System.out.println("it's turn, time="+time+", "+new Date() );
			OnlineMessage msg = new OnlineMessage(lastStep);
			msg.setReceiverId(nextPartInfo.getConnId());
			NetManager.newInstance().pushMessage(onlineListenerId, msg);
		}
		
		private void error( FIRDesk desk, PartInfo partInfo, WIN_TYPE winType ){
			if( partInfo == desk.getInning().getDefensiveInfo() ) {
				win( desk, desk.getInning().getOffensiveInfo(), winType, null );
			} else {
				win( desk, desk.getInning().getDefensiveInfo(), winType, null );
			}
		}
		private void win( FIRDesk desk, PartInfo winnerInfo, WIN_TYPE winType, AbstractList<Step> stepList ){
			synchronized( desk ){
				AIWrapper wrapper1 =  desk.getWrapper1();
				AIWrapper wrapper2 =  desk.getWrapper2();
				desk.setWrapper1(null);
				desk.setWrapper2(null);
				if( wrapper1 != null ) wrapper1.setDesk(null);
				if( wrapper2 != null ) wrapper2.setDesk(null);
			}
			packetGameOverAndSend( winnerInfo.getStatus(), winType, stepList, 
					desk.getInning().getOffensiveInfo().getConnId(), 
					desk.getInning().getDefensiveInfo().getConnId());
		//	this.firBoard.win(winnerInfo);
		//	String winText = "Player " + winnerInfo.getNickname() + " is Winner!";
		//	this.setStatusBarText( winText );
		//	JOptionPane.showMessageDialog(null, winText);
			
		}

		private void packetInningsInfoAndSend(Status status, int limitedTime, int heuristic, 
				Inning inning, PartInfo opponentInfo, int connId ){
			OnlineMessage msg = new OnlineMessage(Data.INNING_INFO);
			msg.setReceiverId(connId);
			////msg.setInningInfo(offensive, limitedTime, heuristic, opponentInfo, inning.toChessString());
			msg.setInningInfo(status, inning);
			NetManager.newInstance().pushMessage(onlineListenerId, msg);
		}
		private void packetGameOverAndSend(Status status, WIN_TYPE winType,
				AbstractList<Step> stepList, int connId, int connId2) {
			OnlineMessage msg = new OnlineMessage(status, winType, stepList);
			msg.setReceiverId(connId);
			NetManager.newInstance().pushMessage(onlineListenerId, msg);
			msg.setReceiverId(connId2);
			NetManager.newInstance().pushMessage(onlineListenerId, msg);
		}
	}

	private class ReleaseDeskInfoRunnable implements Runnable {
		
		boolean stop = false;
		
		@Override
		public void run() {
			while( ! stop ){
				synchronized(FIRArena.this.onlineDeskList){/*
					System.out.println( "send to site: desklist.size="+onlineDeskList.size() );
					FIRDesk desk = onlineDeskList.get(0);
					AIWrapper wrapper = desk.getWrapper1();
					if( wrapper == null ){
						System.out.println( "wrapper1 == null" );
					} else {
						AIInfo info = wrapper.getAiInfo();
						if( info == null ){
							System.out.println( "wrapper1.info == null" );
						} else {
							System.out.println( "wrapper1.info = "+info.getId()+", "+info.getName()+", "+info.getNickname() );
						}
					}//*/
					OnlineMessage om = new OnlineMessage(onlineDeskList);
					om.setReceiverId( Message.DEFAULT );//send to all
					boolean b = NetManager.newInstance().pushMessage(onlineListenerId, om);
					if( !b ){
						System.out.println( "Sending the desk info failed!" );
					}
				}
				try { Thread.sleep(5000); } catch (InterruptedException e) {
					e.printStackTrace(); break;//exit the thread
				}
			}
		}
	}

	public void setInningInfo(int ofhu, int dehu, int level, String chessFileName) {
		inningInfo.offensiveHeuristic = ofhu;
		inningInfo.defensiveHeuristic = dehu;
		inningInfo.level = level;
		inningInfo.chessFileName = chessFileName;
	}

	public void setStatusBarText(String msgErrorFailedSet) {
		aiep.setStatusBarText(msgErrorFailedSet);
	}

	public void executeByOK() {
		aiep.pluginExecute();
	}
	
}



