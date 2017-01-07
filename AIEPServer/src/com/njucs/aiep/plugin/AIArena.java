package com.njucs.aiep.plugin;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComponent;
import javax.swing.JDialog;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.Language;
import com.njucs.aiep.Resource;
import com.njucs.aiep.AIEP.CmdType;
import com.njucs.aiep.AIEP.Country;
import com.njucs.aiep.AIEP.SystemPlatform;
import com.njucs.aiep.AIEPUserConf.AiaBasicConf;
import com.njucs.aiep.base.Recorder;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.frame.Competition;
import com.njucs.aiep.frame.Desk;
import com.njucs.aiep.frame.Competition.State;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.file.FileTransmit;
import com.njucs.aiep.net.file.ReceiveFileEvent;
import com.njucs.aiep.net.file.ReceiveFileEventListener;
import com.njucs.aiep.plugin.Result.ResultCode;
import com.njucs.aiep.plugin.ResultMessage.ResultType;
import com.njucs.aiep.plugin.ui.AIInfoTablePanel;
import com.njucs.aiep.pool.CompareEvent;
import com.njucs.aiep.pool.CompareEventListener;
import com.njucs.aiep.pool.CompareOverEvent;
import com.njucs.aiep.pool.ISynchronizedPool;
import com.njucs.aiep.pool.InsertEvent;
import com.njucs.aiep.pool.InsertEventListener;
import com.njucs.aiep.pool.LocateEvent;
import com.njucs.aiep.pool.LocateEventListener;
import com.njucs.aiep.pool.RoundRobinPool;
import com.njucs.aiep.pool.CompareOverEvent.CompareResult;
import com.njucs.aiep.ui.AIEPCompetitionDialog;
import com.njucs.aiep.ui.AIEPRankingListDialog;
import com.twzcluster.net.NetManager;
import com.twzcluster.net.NewConnEvent;
import com.twzcluster.net.NewConnListener;
import com.twzcluster.net.ReceiveEvent;
import com.twzcluster.net.ReceiveListener;


/**
 * 
 * @author ygsx
 * 
 * @created 2013年5月30日22:31:31
 * */
public abstract class AIArena<E_Desk extends Desk> implements Plugin<JDialog> {

	protected AIEP aiep;
	protected AIEPUserConf userConf;
//Resource.DATA_DIR+"\\
	//upload dat
	protected static String onlineAllInfoListFileName = Resource.DATA_DIR+"onlineall.dat";
	protected static String onlineCompetitionInfoListFileName = Resource.DATA_DIR+"onlinecompetition.dat";
	protected static String allInfoListFileName = Resource.DATA_DIR+"uploadall.dat";
	protected static String sortedInfoListFileName = Resource.DATA_DIR+"uploadsorted.dat";
	protected static String waitingInfoListFileName = Resource.DATA_DIR+"uploadwaiting.dat";
	protected static String competitionInfoListFileName = Resource.DATA_DIR+"uploadcompetition.dat";

	//online data
	protected ArrayList<AIInfo> onlineAllInfoList = null;
	protected ArrayList<AIInfo> onlineCompetitionInfoList = null;
	protected ArrayList<E_Desk> onlineDeskList = new ArrayList<E_Desk>();
	//upload data
	protected ArrayList<AIInfo> allAIInfoList = null;
	protected ISynchronizedPool<AIInfo> sortedAIInfoPool = null;
	protected LinkedList<AIInfo> waitingInfoList = null;
	protected List<Competition> competitionList = null;
	
	//event drive
	protected CompareAIListener compareAIListener = new CompareAIListener();
	protected AILocateListener aiLocateListener = new AILocateListener();
	
	protected FileTransmit fileServer;
	protected ReceiveFileEventListener recvListener = new AIFileReceiveEventListener();
	protected Thread pkThread;
	protected PKRunnable pkRunnable;
	protected int judgeListenerId = NetManager.ILLEGAL_ID;
	protected int siteListenerId = NetManager.ILLEGAL_ID;
	protected JudgeReceiveListener judgeReceiveListener = new JudgeReceiveListener();
	protected ArenaNewConnListener arenaNewConnListener = new ArenaNewConnListener();
	
	protected Thread saveDataThread;
	protected SaveDataRunnable saveDataRunnable;
	
	protected Thread traceCompetitionThread;
	protected TraceCompetitionRunnable traceCompetitionRunnable;
	
	public static int MAX_RESPONSE_TIME = 60;//1 min
	
	//ui
	protected AIEPRankingListDialog rankingListDialog;
	protected AIEPCompetitionDialog competitionDialog;
	protected AIInfoTablePanel allAIInfoPanel = new AIInfoTablePanel();

	public AIArena(){
		
	}
	
	public AbstractList<AIInfo> getRankingList(){
		return sortedAIInfoPool.getSorted();
	}
	public List<Competition> getCompetitionList(){
		return this.competitionList;
	}
	
	@Override
	public JDialog getExtConfDialog() {
		return null;
	}
	@Override
	public AbstractList<? extends JComponent> getSetMenu() {
		return null;
	}


	@Override
	public abstract String getAuthor();




	@Override
	public abstract String getName(Country countryno);


	@Override
	public abstract String getVersion() ;
	
	
	public abstract Class<? extends AIJudge<?>> getAIJudgeClass();

	//@Override
	//public abstract void updateUI();

	public AIEPRankingListDialog getRankingListDialog(){
		return this.rankingListDialog;
	}
	public AIEPCompetitionDialog getCompetitionDialog(){
		return this.competitionDialog;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean initialize(AIEP aiep) {
		
		ArrayList<AIInfo> t = (ArrayList<AIInfo>) Recorder.open(allInfoListFileName);
		if( t == null ) t = new ArrayList<AIInfo>();
		allAIInfoList = t;//Collections.synchronizedList(t);
		sortedAIInfoPool = (ISynchronizedPool<AIInfo>) Recorder.open(sortedInfoListFileName);
		//if( sortedAIInfoPool == null ) sortedAIInfoPool = new SynchronizedPool<AIInfo>();
		if( sortedAIInfoPool == null ) sortedAIInfoPool = new RoundRobinPool<AIInfo>();
		waitingInfoList = (LinkedList<AIInfo>) Recorder.open(waitingInfoListFileName);
		if( waitingInfoList == null ) waitingInfoList = new LinkedList<AIInfo>();
		List<Competition> s = (List<Competition>) Recorder.open(competitionInfoListFileName);
		if( s == null ) s = new ArrayList<Competition>();
		competitionList = Collections.synchronizedList(s);
		
		sortedAIInfoPool.initialize();
		
		onlineAllInfoList = (ArrayList<AIInfo>)Recorder.open(onlineAllInfoListFileName);
		if( onlineAllInfoList == null ) onlineAllInfoList = new ArrayList<AIInfo>();
		onlineCompetitionInfoList = (ArrayList<AIInfo>)
			Recorder.open(onlineCompetitionInfoListFileName);
		if( onlineCompetitionInfoList == null ) onlineCompetitionInfoList = new ArrayList<AIInfo>();

		
		
		System.out.println( "onlineAllInfoList.size="+onlineAllInfoList.size() );
		System.out.println( "onlineCompetitionInfoList.size="+onlineCompetitionInfoList.size() );
		
		System.out.println( "allAIInfoList.size="+allAIInfoList.size() );
		System.out.println( "waitingInfoList.size="+waitingInfoList.size() );
		System.out.println( "competitionList.size="+competitionList.size() );
		System.out.println( "sortedAIInfoPool.size="+sortedAIInfoPool.getSorted().size() );
		//Runtime.getRuntime().removeShutdownHook(hook);
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable(){
			@Override
			public void run() {
				System.out.println("exit!Save the data.");
				System.out.println( "onlineAllInfoList.size="+onlineAllInfoList.size() );
				System.out.println( "onlineCompInfoList.size="+onlineCompetitionInfoList.size() );
				System.out.println( "allAIInfoList.size.size="+allAIInfoList.size() );
				System.out.println( "waitingInfoList.size="+waitingInfoList.size() );
				System.out.println( "competitionList.size="+competitionList.size() );
				System.out.println( "sortedAIInfoPool.size="+sortedAIInfoPool.getSorted().size() );
				
				saveData();
			}
		}));
		
		this.aiep = aiep;
		if( this.aiep == null ) return false;
		userConf = AIEPUserConf.getInstance();
		rankingListDialog = new AIEPRankingListDialog(this.aiep.getWindow());
		competitionDialog = new AIEPCompetitionDialog(this.aiep.getWindow());
		rankingListDialog.setRankingList(sortedAIInfoPool.getSorted());
		competitionDialog.setCompetitionList(competitionList);
		allAIInfoPanel.setAIInfoList(allAIInfoList);
		this.aiep.getCavas().setLayout(new BorderLayout());
		this.aiep.getCavas().add(allAIInfoPanel, BorderLayout.CENTER);
		return true;
	}
	

	private void saveData(){
		//System.out.println("Saving Data...");System.out.flush();
		//System.out.println("Saving onlineAllInfoList...");System.out.flush();
		synchronized(onlineAllInfoList){
			Recorder.save(onlineAllInfoList, onlineAllInfoListFileName);
		}
		//System.out.println("Saving onlineCompetitionInfoList...");System.out.flush();
		synchronized(onlineCompetitionInfoList){
			Recorder.save(onlineCompetitionInfoList, onlineCompetitionInfoListFileName);			
		}
		//System.out.println("Saving competitionList...");System.out.flush();
		synchronized(competitionList){
			Recorder.save(competitionList, competitionInfoListFileName);
		}
		//System.out.println("Saving allAIInfoList...");System.out.flush();
		synchronized(allAIInfoList){
			Recorder.save(allAIInfoList, allInfoListFileName);			
		}
		//System.out.println("Saving waitingInfoList...");System.out.flush();
		synchronized(waitingInfoList){
			Recorder.save(waitingInfoList, waitingInfoListFileName);			
		}
		//System.out.println("Saving sortedAIInfoPool...");System.out.flush();
		synchronized(sortedAIInfoPool){
			Recorder.save(sortedAIInfoPool, sortedInfoListFileName);			
		}
	}
	
	protected abstract int executeOnlineMode();
	
	protected int executeUploadMode(){
		//ai file server
		fileServer = new FileTransmit( 
				userConf.getInt(AiaBasicConf.AIA_UPLOAD_PORT), 
				userConf.getString(AiaBasicConf.AIA_UPLOAD_PATH), 
				recvListener
		);
		aiep.addPrompt( "creating ai file upload listener(port="+
				userConf.getInt(AiaBasicConf.AIA_UPLOAD_PORT)+")  succeed!" );
		try { fileServer.execute(); } catch (IOException e) { e.printStackTrace(); }
		//observe the waiting list thread
		pkThread = new Thread(pkRunnable = new PKRunnable());
		pkThread.setName("com.njucs.aiep.plugin.AIArena.pkThread");
		pkThread.start();
		//receive the judge report server
		judgeListenerId = NetManager.newInstance().createListener("localhost", 
				userConf.getInt(AiaBasicConf.AIA_RECV_PORT),
				null, null, 
				judgeReceiveListener
		);
		if( judgeListenerId == NetManager.ILLEGAL_ID ){
			aiep.addPrompt( "creating judge recv listener(port="+
					userConf.getInt(AiaBasicConf.AIA_RECV_PORT)+")  failed!" );
		} else {
			aiep.addPrompt( "creating judge recv listener (port="+
					userConf.getInt(AiaBasicConf.AIA_RECV_PORT)+") succeed!" );
		}
		if( userConf.getBoolean(AiaBasicConf.AIA_RELEASE_RSL) ) {
			//receive the ai file server
			siteListenerId = NetManager.newInstance().createListener("localhost", 
					userConf.getInt(AiaBasicConf.AIA_RELEASE_PORT),
					arenaNewConnListener, null, null
			);
			if( siteListenerId == NetManager.ILLEGAL_ID ){
				aiep.addPrompt( "creating release listener(port="+
						userConf.getInt(AiaBasicConf.AIA_RELEASE_PORT)+") failed!" );
			} else {
				aiep.addPrompt( "creating release listener(port="+
						userConf.getInt(AiaBasicConf.AIA_RELEASE_PORT)+")  succeed!" );
			}
		}
		return 0;
	}
	
	protected abstract void forceExitOnlineMode();
	
	protected void forceExitUploadMode(){
		
		if( fileServer != null ){
			fileServer.stop();
		}
		
		//do not clear the list!!!
		/*
		sortedAIInfoPool.clear();
		synchronized(waitingInfoList){
			waitingInfoList.clear();
		}
		synchronized( competitionList ){			
			competitionList.clear();
		}//*/

		if( pkRunnable != null ){
			pkRunnable.stop = true;//TODO ?
		}
		if( pkThread != null ){ pkThread.interrupt(); }
		
		NetManager.newInstance().stopService(judgeListenerId);
		NetManager.newInstance().stopService(siteListenerId);
	}
	
	@Override
	public int reExecute() {
		
		forceExit();
		
		sortedAIInfoPool.setCompareEventListener(compareAIListener);
		sortedAIInfoPool.setLocateEventListener(aiLocateListener);

		saveDataThread = new Thread(saveDataRunnable = new SaveDataRunnable());
		saveDataThread.setName("com.njucs.aiep.plugin.AIArena.saveDataThread");
		saveDataThread.start();
		
		traceCompetitionThread = new Thread( traceCompetitionRunnable = new TraceCompetitionRunnable() );
		traceCompetitionThread.setName("com.njucs.aiep.plugin.AIArena.traceCompetitionThread");
		traceCompetitionThread.start();
		
		if( userConf.getBoolean(AiaBasicConf.AIA_ONLINE_OR_UPLOAD) ) {
			return executeOnlineMode();
		} else {
			return executeUploadMode();
		}
	}
	@Override
	public void forceExit(){
		saveData();
		if( this.saveDataRunnable != null ){
			this.saveDataRunnable.flag = false;
			this.saveDataRunnable = null;
		}
		if( this.saveDataThread != null ){
			saveDataThread.interrupt();
			saveDataThread = null;
		}
		if( this.traceCompetitionRunnable != null ){
			traceCompetitionRunnable.stop = false;
			traceCompetitionRunnable = null;
		}
		if( this.traceCompetitionThread != null ){
			traceCompetitionThread.interrupt();
			traceCompetitionThread = null;
		}
		if( userConf.getBoolean(AiaBasicConf.AIA_ONLINE_OR_UPLOAD) ) {
			forceExitOnlineMode();
		} else {
			forceExitUploadMode();
		}
	}

	protected int getRunningCompetitionNumber(){
		synchronized( competitionList ){
			Iterator<Competition> iter = competitionList.iterator();
			int count = 0;
			while( iter.hasNext() ){
				if( iter.next().getState() == State.RUNNING ){
					count++;
				}
			}
			return count;
		}
	}
	
	protected void startAPKProcess( String aifile1, String aifile2 ) {
		String type = CmdType.judge.toString();
		//String selfjar = System.getProperty("user.dir")+File.separator+System.getProperty("java.class.path");
		String selfjar = null;
		if( AIEP.PLATFORM == SystemPlatform.X86 ){
			selfjar = System.getProperty("user.dir")+File.separator+"aiep(32-bit).jar";
		} else if (AIEP.PLATFORM == SystemPlatform.X64){
			selfjar = System.getProperty("user.dir")+File.separator+"aiep(64-bit).jar";
		} else {
			throw new RuntimeException( "Unknown System Platform!" );
		}
		String aiJudgeClassName = getAIJudgeClass().getName();
		
		String cmd = "java -jar \"{selfjar}\" {type} {aiJudgeClassName} {port} 2 {aifile1} {aifile2}";
		cmd = cmd.replace("{port}", String.valueOf(userConf.getInt(AiaBasicConf.AIA_RECV_PORT)) );
		cmd = cmd.replace("{selfjar}", selfjar);
		cmd = cmd.replace("{type}", type);
		cmd = cmd.replace("{aiJudgeClassName}", aiJudgeClassName);
		cmd = cmd.replace("{aifile1}", aifile1);
		cmd = cmd.replace("{aifile2}", aifile2);
		System.out.println( "cmd="+cmd );
		try {
			Runtime.getRuntime().exec( cmd );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected void startAPKProcess(){
		/*if( getRunningCompetitionNumber() > 10 ){
			aiep.addPrompt( "too many process!" );
			return;
		}//*/
		AIInfo aiinfo = waitingInfoList.remove(0);
		aiep.addPrompt( "loading ai file: "+aiinfo.getJarFileName() );
		InsertEventListener<AIInfo> insertListener = sortedAIInfoPool.getInsertListener();
		InsertEvent<AIInfo> event = new InsertEvent<AIInfo>(this, aiinfo);
		insertListener.insert(event);
	}
	
	protected class TraceCompetitionRunnable implements Runnable {
		
		private  boolean stop = false;
		
		@Override
		public void run() {
			while( ! stop ){
				try { Thread.sleep(5000); } catch (InterruptedException e) { 
					aiep.addPrompt(e.getMessage()); break;//sleep 5s
				}
				Date now_minus_response_time = new Date();
				now_minus_response_time.setTime(now_minus_response_time.getTime() - MAX_RESPONSE_TIME*1000 );
				AbstractList<Competition> tempCompetitionList = new ArrayList<Competition>();
				synchronized( competitionList ){
					Iterator<Competition> iter = competitionList.iterator();
					while( iter.hasNext() ){
						Competition competition = iter.next();
						//synchronized(competition){
							if( competition.getState() == Competition.State.RUNNING
									&& competition.getLastUpdate().before(now_minus_response_time) ){// responsing time ago
								tempCompetitionList.add(competition);
							}//end if competition is dead
						//}//end synchronized competition
					}//end while
				}//end synchronized competitionList
				for(Competition competition : tempCompetitionList){
					competition.getOffensiveInfo().addTotalCount();
					competition.getDefensiveInfo().addTotalCount();
					
					Result finalResult = new Result();
					finalResult.setOffensiveAIInfo(competition.getOffensiveInfo());
					finalResult.setDefensiveAIInfo(competition.getDefensiveInfo());
					finalResult.setResultCode(ResultCode.CRASH);
					Status winStatus = Status.DEFENSIVE;
					if( competition.getReuslt() != null ){
						winStatus = competition.getReuslt().getActiveStatus();
					}
					if( winStatus == Status.OFFENSIVE ){
						competition.getOffensiveInfo().addWinCount();
					} else if ( winStatus == Status.DEFENSIVE ){
						competition.getDefensiveInfo().addWinCount();
					}
					finalResult.setWinner(winStatus);
					finalResult.setRealTimeResult(null);
					finalResult.setErrorStatus(null);
					
					competition.setReuslt(finalResult);
					competition.setState(State.FINISHED);
					competition.setEndTime(new Date());
					
					if( finalResult.getWinner() == Status.OFFENSIVE ){
						competition.setWinner(competition.getOffensiveInfo());
					} else if( finalResult.getWinner() == Status.DEFENSIVE ){
						competition.setWinner(competition.getDefensiveInfo());
					}
					CompareResult compareResult = null;
					if( finalResult.getWinner() == Status.OFFENSIVE ){//win
						compareResult = CompareResult.GREATER;
					} else {
						compareResult = CompareResult.NOT_GREATER;
					}
					sortedAIInfoPool.getCompareOverListener().compareOver(
						new CompareOverEvent<AIInfo>(this, competition.getCompareEvent(),
							compareResult	) 
					);
				}
			}//end while 
		}// end run
		
	}//end class TraceCompetitionRunnable
	
	/*
	 * 每隔1秒钟查看是否有需要PK的AI，如果有就创建一个PK进程来PK
	 * */
	protected class PKRunnable implements Runnable {
		
		private  boolean stop = false;
		private Map<String, AbstractList<String>> classmap = new TreeMap<String, AbstractList<String>>();
		
		public PKRunnable(){
			classmap.put( AI.class.getName(), new ArrayList<String>() );
		}
		
		@Override
		public void run() {
			print( "PK线程开始！" );
			while( ! stop ){
				try { Thread.sleep(1000); } catch (InterruptedException e) { 
					aiep.addPrompt(e.getMessage()); break; 
				}
				//print("请求资源:waitingInfoList");
				synchronized( waitingInfoList ){
					if( waitingInfoList.size() == 0 ) {
						//System.out.println( "waitingInfoList.size() == 0" );
						continue;
					}
					print("开始一个PK进程");
					startAPKProcess();
				}
				print("释放资源:waitingInfoList");
			}
		}
		public boolean getStop(){
			return this.stop;
		}
		public void setStop( boolean stop ){
			this.stop = stop;
		};
	}
	
	protected class AIFileReceiveEventListener implements ReceiveFileEventListener {
		@Override
		public void receiveFile(ReceiveFileEvent event) {
			print( "接收到一个AI文件" );
			//waitingInfoList
			String filename = event.getAIInfo().getJarFileName();
			aiep.addPrompt( "recv ai file from site: "+filename );
			System.out.println( "class name = "+event.getAIInfo().getClassName() );
			//aiep.addPrompt( "id="+event.getAIInfo().getId()+", ver="+event.getAIInfo().getVersion() );
			boolean exist = false, javaClassExist = false;
			String aiClassName = event.getAIInfo().getClassName();
			if( aiClassName == null ) {
				print( "请求资源：allAIInfoList" );
				synchronized ( allAIInfoList ){
					for( AIInfo aiInfo : allAIInfoList ){
						System.out.println( "check the same: id="+aiInfo.getId()+", ver="+aiInfo.getVersion() );
						if( aiInfo.equals(event.getAIInfo())  ){
							exist = true;
							break;
						}
					}
				}
				print( "释放资源：allAIInfoList" );
			} else {
				synchronized ( allAIInfoList ){
					for( AIInfo aiInfo : allAIInfoList ){
						System.out.println( "check the same: id="+aiInfo.getId()+", ver="+aiInfo.getVersion() );
						if( aiClassName.equals(aiInfo.getClassName()) ){//check the class name first
							javaClassExist = true;
							break;
						}
						if( aiInfo.equals(event.getAIInfo())  ){
							exist = true;
							break;
						}
					}
				}
			}
			if( exist ){
				aiep.addPrompt( "the same ai file!remove it!id="+
						event.getAIInfo().getId()+", ver="+event.getAIInfo().getVersion() );
				new File( filename ).delete();
				//tell the uploader
				UploadMessage um = new UploadMessage( false, Language.MSGBOX_UPLOAD_FAILED_AS_EXIST );
				NetManager.newInstance().pushMessage(siteListenerId, um);
				return;
			} else if ( javaClassExist ){
				aiep.addPrompt( "the same java class file!remove it!id="+
						event.getAIInfo().getId()+", ver="+event.getAIInfo().getVersion() );
				new File( filename ).delete();
				//tell the uploader
				UploadMessage um = new UploadMessage( false, Language.MSGBOX_UPLOAD_FAILED_AS_JAVA_CLASS_SAME );
				NetManager.newInstance().pushMessage(siteListenerId, um);
				return;
			} else {
				//tell the uploader
				UploadMessage um = new UploadMessage( true, Language.MSGBOX_UPLOAD_OK );
				NetManager.newInstance().pushMessage(siteListenerId, um);
			}
			allAIInfoList.add(event.getAIInfo());
			print( "请求资源：waitingInfoList" );
			synchronized( waitingInfoList ){
				waitingInfoList.add( event.getAIInfo() );
			}
			print( "释放资源：waitingInfoList" );
			allAIInfoPanel.updateUI();
		}
	}
	
	protected class JudgeReceiveListener implements ReceiveListener {

		@Override
		public void receivedMessage(ReceiveEvent event) {
			print("接收到Judge的信息");
			ResultMessage msg = (ResultMessage) event.getMessage();
			Result result = msg.getResult();
			if( result == null ) return;
			print( "ofid="+result.getOffensiveAIInfo().getId()+",ofver="+result.getOffensiveAIInfo().getVersion()+",ofname="+result.getOffensiveAIInfo().getName() );
			print( "deid="+result.getDefensiveAIInfo().getId()+",dever="+result.getDefensiveAIInfo().getVersion()+",dename="+ result.getDefensiveAIInfo().getName());
			
			Competition competition = null;
			print( "请求资源：competitionList" );
			synchronized( competitionList ){
				for( Competition comp : competitionList ){
					AIInfo aiInfo = comp.getOffensiveInfo();
					AIInfo aiInfo2 = comp.getDefensiveInfo();
					if( aiInfo.equals(result.getOffensiveAIInfo()) &&  aiInfo2.equals(result.getDefensiveAIInfo())  ){
						competition = comp;
						break;
					}
				}
			}
			if( competition != null ) {
				synchronized(competition){
					handle( competition, result, msg.getResultType() );
				}
			} else {
				aiep.addPrompt( "competition == null!(ofId="+result.getOffensiveAIInfo().getId()+",ofVer="+result.getOffensiveAIInfo().getVersion()+"" +
						", deId="+result.getDefensiveAIInfo().getId()+",deVer="+result.getDefensiveAIInfo().getVersion()+")" );
			}
			return;//end receivedMessage(ReceiveEvent)
		}
		private void handle( Competition competition, Result result, ResultType resultType ){
			if( competition == null ){
				aiep.addPrompt( "competition == null!" );
				return;
			}
			competition.setLastUpdate(new Date());
			if( resultType == ResultType.REALTIME ){
				System.out.println( "recv the realtime message!"+result.getRealTimeResult() );
				competition.setReuslt(result);
				return;
			}
			ResultCode code = result.getResultCode();
			competition.setState(State.FINISHED);
			competition.setEndTime(new Date());//set endtime
			competition.setReuslt(result);
			competition.getDefensiveInfo().addTotalCount();
			competition.getOffensiveInfo().addTotalCount();
			if( code == ResultCode.ERROR ) {
				aiep.addPrompt( "RslCode="+result.getResultCode()+ 
						", error id="+result.getOffensiveAIInfo().getId() +
						", error ver="+result.getOffensiveAIInfo().getVersion() );
				Status status = result.getErrorStatus();
				CompareResult compareResult = null;
				if( status == Status.OFFENSIVE ){
					competition.setWinner(competition.getDefensiveInfo());
					competition.getDefensiveInfo().addWinCount();
					compareResult = CompareResult.NOT_GREATER;
				} else if( status == Status.DEFENSIVE ){
					competition.setWinner(competition.getOffensiveInfo());
					competition.getOffensiveInfo().addWinCount();
					compareResult = CompareResult.GREATER;
				}
				sortedAIInfoPool.getCompareOverListener().compareOver(
					new CompareOverEvent<AIInfo>(this, competition.getCompareEvent(),
						compareResult	)
				);
				System.out.println( "compareOver1" );
			} else if( code == ResultCode.DONE ){
				if( result.getWinner() == Status.OFFENSIVE ){
					competition.setWinner(competition.getOffensiveInfo());
					competition.getOffensiveInfo().addWinCount();
				} else if( result.getWinner() == Status.DEFENSIVE ){
					competition.setWinner(competition.getDefensiveInfo());
					competition.getDefensiveInfo().addWinCount();
				}
				CompareResult compareResult = null;
				if( result.getWinner() == Status.OFFENSIVE ){//win
					compareResult = CompareResult.GREATER;
				} else {
					compareResult = CompareResult.NOT_GREATER;
				}
				//print( "创建一个比较完成事件，并传递给sortedAIInfoPool2" );
				sortedAIInfoPool.getCompareOverListener().compareOver(
					new CompareOverEvent<AIInfo>(this, competition.getCompareEvent(),
						compareResult	) 
				);
				System.out.println( "compareOver2" );
			}
			competitionDialog.updateUI();
			//send to site
			CompetitionMessage msg = new CompetitionMessage( competitionList );
			NetManager.newInstance().pushMessage(siteListenerId, msg);
		}
		
		
	}
	
	protected class CompareAIListener implements CompareEventListener<AIInfo>{

		@Override
		public void compare(CompareEvent<AIInfo> event) {
			AIInfo aiinfo = event.getFirstElement();
			AIInfo midInfo = event.getSecondElement();
			Competition newComp = new Competition(aiinfo, midInfo);
			newComp.setCompareEvent(event);
			aiep.addPrompt( "create a new CompareEvent:id="+event.getId()
		+ ", ofid="+aiinfo.getId()+",ofver="+aiinfo.getVersion()+",name="+aiinfo.getName()+",file="+aiinfo.getJarFileName()+
		", deid="+midInfo.getId()+",dever="+midInfo.getVersion() +",name="+midInfo.getName()+",file="+midInfo.getJarFileName());
			competitionList.add(newComp);
			competitionDialog.updateUI();
			String aifile1 = aiinfo.getJarFileName();
			String aifile2 = midInfo.getJarFileName();
			startAPKProcess(aifile1, aifile2);
		}
		
	}
	protected class AILocateListener implements LocateEventListener<AIInfo>{

		@Override
		public void locate(LocateEvent<AIInfo> locateEvent) {
			aiep.addPrompt( "insert: id="+locateEvent.getLocateElement().getId()+
					", ver="+locateEvent.getLocateElement().getVersion() );
			AbstractList<AIInfo> list = sortedAIInfoPool.getSorted();
			aiep.addPrompt( "current Sorted AI: "+list.size() );
			rankingListDialog.setRankingList(list);
			RankMessage msg = new RankMessage(list);
			NetManager.newInstance().pushMessage(siteListenerId, msg);
			//System.out.println( "b = "+b );
		}
		
	}
	
	protected class ArenaNewConnListener implements NewConnListener  {
		@Override
		public void newConn(NewConnEvent event) {
			aiep.addPrompt( "New conn!" );
			AbstractList<AIInfo> list = sortedAIInfoPool.getSorted();
			RankMessage msg = new RankMessage(list);
			NetManager.newInstance().pushMessage(siteListenerId, msg);
			CompetitionMessage msg2 = new CompetitionMessage( competitionList );
			NetManager.newInstance().pushMessage(siteListenerId, msg2);
		}
	}
	
	protected class SaveDataRunnable implements Runnable {
		public boolean flag = true;
		@Override
		public void run() {
			flag = true;
			while( flag ){
				try { Thread.sleep(1000);//10s
				} catch (InterruptedException e) {e.printStackTrace();
					System.out.println( "Save Data Thread is interruptted!" );
				}
				saveData();
			}
		}
		
	}
	
	void print(String str){
		System.out.println( str );
	}
}





