

package com.njucs.aiep;

import java.awt.Container;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.njucs.aiep.base.ClassOperator;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.frame.Competition;
import com.njucs.aiep.net.DataTransmit;
import com.njucs.aiep.plugin.AIArena;
import com.njucs.aiep.plugin.AIJudge;
import com.njucs.aiep.plugin.AISite;
import com.njucs.aiep.plugin.Plugin;
import com.njucs.aiep.plugin.AIJudge.AijRole;
import com.njucs.aiep.ui.AIEPWindow;
import com.twzcluster.webserver.TWZWebServer;

/**
 * AIEP(Artificial Intelligence Experiment Platform) Entry Class
 * 
 * @author ygsx
 * 
 * @version 0.1
 * 
 * @time 2013年5月3日20:42:16
 * 
 * */
public class AIEP {
	
	public static enum SystemPlatform { X86, X64 };
	public final static SystemPlatform PLATFORM = SystemPlatform.X86;
	public final static boolean USE_STYLE = true;
	
	public static enum AiepRole  { AIArena, AIJudge, AISite };
	public static enum Country { ENG, CHN };
	
	//user conf
	private AIEPUserConf userConf = null;
	
	private Map<String, AbstractList<String>> pluginMap = new TreeMap<String, AbstractList<String>>();
	
	private AbstractList<AIArena<?>> aiaPluginList = new ArrayList<AIArena<?>>();
	private AbstractList<AIJudge<?>> aijPluginList = 
		new ArrayList<AIJudge<?>>();
	private AbstractList<AISite<?>> aisPluginList = new ArrayList<AISite<?>>();
	//current experiment
	private Plugin<? extends JDialog> currentExp = null;

	/**
	 * Upload once allowed
	 * @see #setUploaded()
	 * @see #isUploaded()
	*/
	private boolean  isloaded = false;
	
	//ui
	private AIEPWindow aiepWindow = null;
	
	private TWZWebServer webServer;
	
	public AIEP(){
		this.userConf = AIEPUserConf.getInstance();
		this.pluginMap.put( AIArena.class.getName() , new ArrayList<String>());
		this.pluginMap.put( AIJudge.class.getName() , new ArrayList<String>());
		this.pluginMap.put( AISite.class.getName() , new ArrayList<String>());
		//System.setProperty("user.country", Locale.US.getCountry());
	//	System.setProperty("user.language", Locale.US.getLanguage());
		webServer = new TWZWebServer( "localhost", 8899 );
		webServer.init(new String[]{ "-d", "res/web.cfg", "-p" });
		//webServer.printServerInfo();
		webServer.startService();
	}
	
	public void initialize(){
		ClassOperator.addCustomJAR( System.getProperty("user.dir")+Resource.LIBRARY_DIR);
		ClassOperator.addCustomJAR( System.getProperty("user.dir")+Resource.LOOK_AND_FEEL_DIR);
		ClassOperator.addCustomJAR( System.getProperty("user.dir")+Resource.PLUGIN_DIR , pluginMap);
		try {
			AbstractList<String> pl = pluginMap.get( AIArena.class.getName() );
			for( String str : pl ){
				Class<?> T = Class.forName( str );
				aiaPluginList.add( (AIArena<?>)T.newInstance() );
			}
			pl = pluginMap.get( AIJudge.class.getName() );
			for( String str : pl ){
				Class<?> T = Class.forName( str );
				aijPluginList.add( (AIJudge<?>) T.newInstance() );
			}
			pl = pluginMap.get( AISite.class.getName()  );
			for( String str : pl ){
				Class<?> T = Class.forName( str );
				aisPluginList.add( (AISite<?>)T.newInstance() );
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		changeExpRole( this.userConf.getRole() );
	}
	
	public void showUI(){
		aiepWindow = new AIEPWindow( this );
		aiepWindow.setVisible(true);
	}

	public void setWindowSize( int width, int height ){
		aiepWindow.setSize(width, height);
	}
	public  void setResizable(boolean resizable){
		aiepWindow.setResizable(resizable);
	}
	/**
	 * Set the Status Bar Text
	 * 
	 * @param text the status bar text
	 * */
	
	public void setStatusBarText( String text ){
		if( aiepWindow == null ) return;
		aiepWindow.setSatusBarText(text);		
	}
	
	public void addPrompt( String prompt ){
		if( aiepWindow == null ) return;
		this.aiepWindow.addPrompt(prompt);
	}


	public Plugin<? extends JDialog> getCurrentPlugin(){
		return this.currentExp;
	}
	
	public Container getCavas(){
		if( aiepWindow == null ) return null;
		return aiepWindow.getExhibition();
	}
	public AIEPWindow getWindow(){
		return aiepWindow;
	}
	

	public AbstractList<? extends Plugin<?>> getPluginList(){
		if( this.userConf.getRole() == AiepRole.AIArena ){
			return aiaPluginList;
		} else if( this.userConf.getRole() == AiepRole.AIJudge ){
			return aijPluginList;
		} else if( this.userConf.getRole() == AiepRole.AISite ){
			return aisPluginList;
		} else {
			throw new RuntimeException("An Unknown fatal error occurs!");
		}
	}
	//*//called by menubar 
	public AbstractList<AIArena<?>> getAIAPluginList(){
		return aiaPluginList;
	}
	public AbstractList<AIJudge<?>> getAIJPluginList(){
		return aijPluginList;
	}
	public AbstractList<AISite<?>> getAISPluginList(){
		return aisPluginList;
	}
	/**
	 * @return the userConf
	 */
	/*public AIEPUserConf getUserConf() {
		return userConf;
	}//*/
	
	
	
	/**
	 * exchange ranging Server & Client
	 * 
	 * @param role the dest role
	 * */
	public void changeExpRole( AiepRole role ){
		if( role == null ) return;
		pluginDispose();
		userConf.setRole(role);
		this.setStatusBarText( String.format(Language.STATUS_CHOOSE, role ) );
		this.addPrompt("change role to "+role );
		if( aiepWindow != null ) {
			aiepWindow.updateByUser();
		}
	}
	
	/**
	 * change the experiment plugin by index in the <code>ExpPluginList</code><br />
	 * This method should be called by menubar
	 * 
	 * @param the new exp-plugin index
	 * */
	public void changeExpPlugin( int index ){
		AbstractList<?> curPluginList = getPluginList();
		if( index < 0 || index >= curPluginList.size() ) return;
		this.pluginDispose();
		this.pluginInitialize(index);
	};


	
	
	
	private void pluginDispose(){
		pluginExit();
		userConf.setCurrentPluginName(null);
		DataTransmit.getInstance().closeAll();
		if( this.aiepWindow != null ) {
			this.aiepWindow.updateByPlugin(null);
		}
		if( this.getCavas() != null ) {
			this.getCavas().removeAll();
		}
	}
	//*
	private boolean pluginInitialize(int index){
		currentExp = (Plugin<?>) this.getPluginList().get(index);
		userConf.setCurrentPluginName(currentExp.getClass().getName());
		if( aiepWindow != null ) {
			aiepWindow.updateByUser();
		}
		this.aiepWindow.updateByPlugin(currentExp);
		setStatusBarText( String.format(Language.STATUS_CHOOSE, 
				currentExp.getName( this.userConf.getCountryNo() )) );
		this.addPrompt( "change to "+currentExp.getClass().getName() );
		return currentExp.initialize( this );
	}//*/
	
	public void pluginExit(){
		if(currentExp != null){
			currentExp.forceExit();
			currentExp = null;
		}
	}
	public void pluginExecute(){
		if( currentExp == null ) {
			this.addPrompt( "currentExp == null!" );
		} else {
			try{
				if( currentExp.reExecute() == 0 ) {
					this.setStatusBarText(Language.EXECUTE_SUCCEED);
					if( this.aiepWindow != null ){
						this.aiepWindow.setUploadEnable(true);
					}
				} else {
					throw new Exception("");
				}
				//this.addPrompt(Language.EXECUTE_SUCCEED);
			} catch( Exception e ){
				String str = Language.EXECUTE_FAILED+e.getMessage();
				JOptionPane.showMessageDialog(null, str);
				this.setStatusBarText(str);
			}
		}
	}
	
	/**
	 * exit the program, called by menu "exit"
	 * */
	public void exit(){
		pluginDispose();
		System.exit(0);
	}
	
	public void runJudge( String aiJudgeClassName,  int port, String[] aiFileArray ){
		//userConf.setString(AijBasicConf.AIJ_AI_JAR_FILE1, aifile1);
		//userConf.setString(AijBasicConf.AIJ_AI_JAR_FILE2, aifile2);
		try {
			Class<?> aijudgeClass = Class.forName(aiJudgeClassName);
			AIJudge<?> aijudge = (AIJudge<?>)aijudgeClass.newInstance();
			aijudge.setAijRole(AijRole.CONN_ARENA);
			aijudge.initialize(this);
			aijudge.setAIFileArray(aiFileArray);
			aijudge.reExecute();
			//System.exit(0);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	
	public static enum CmdType  { judge };
	//entry
	public static AIEP aiep;
	
	public String getAbout(){
		StringBuffer sb = new StringBuffer();
		Country countryno = userConf.getCountryNo();
		AbstractList<AIArena<?>> aialist = getAIAPluginList();
		for( AIArena<?> plugin : aialist ){
			sb.append(plugin.getName( countryno ));
			sb.append(": ");
			sb.append(plugin.getVersion());
			sb.append("\n");
		}
		AbstractList<AIJudge<?>> aijlist = getAIJPluginList();
		for( AIJudge<?> plugin : aijlist ){
			sb.append(plugin.getName( countryno ));
			sb.append(": ");
			sb.append(plugin.getVersion());
			sb.append("\n");
		}
		AbstractList<AISite<?>> aislist = getAISPluginList();
		for( AISite<?> plugin : aislist ){
			sb.append(plugin.getName( countryno ));
			sb.append(": ");
			sb.append(plugin.getVersion());
			sb.append("\n");
		}
		return String.format(Language.MSG_ABOUT_CONTENT, sb.toString());
	}
	
	public AbstractList<AIInfo> getRankingList(){
		if( currentExp instanceof AIArena<?> ){
			return ((AIArena<?>)currentExp).getRankingList();
		} else {
			return null;
		}
	}
	public List<Competition> getCompetitionList(){
		if( currentExp instanceof AIArena<?> ){
			return ((AIArena<?>)currentExp).getCompetitionList();
		} else {
			return null;
		}
	}
	
	public static void main(String[] args){
		//System.out.println(System.getProperty("java.class.path"));
		Thread.currentThread().setName("com.njucs.aiep.AIEP.main");
		aiep = new AIEP();
		//initialize
		aiep.initialize();
		/*
		aiep.runJudge(  "com.njucs.aiep.plugin.fir.FIRJudge", 
				8894,  new String[]{ "upload/2013_07_11_17_12_43_70723484_101220151.jar",
				"upload/2013_07_11_17_14_31_-2051584568_MyFirAI.jar" } );
		return;//*/ 
		 
		//*
		if( args != null && args.length > 0 ){
			String type = args[0];
			if( CmdType.judge.toString().equals(type) ){//judge
				//"judge" judge_class report_port ai_count ai_file_name...
				if( args.length >= 4 ){
					String aiJudgeClassName = args[1];
					int arenaport = Integer.parseInt(args[2]);
					int aicount = Integer.parseInt(args[3]);
					if( args.length >= 4 + aicount ){
						String[] aiFileArray = new String[aicount];
						for( int i = 0; i < aicount; i ++ ){
							aiFileArray[i] = args[4+i];
						}
						aiep.runJudge(  aiJudgeClassName, arenaport, aiFileArray);
					}
					return;
				}
			}
		}
		
		aiep.showUI();
		AIEPUserConf conf = AIEPUserConf.getInstance();
		aiep.addPrompt( "main: role="+conf.getRole()+", plugin="+conf.getCurrentPluginName() );
		//*/
		
		
		/****************************************************
					below for testing
		*****************************************************/
		/*
		//System.out.println( new Date() );
		DataTransmit dt = DataTransmit.getInstance();
		//*
		//@SuppressWarnings("unused")
		final int lid = dt.createListener( 8890, new NewConnListener(){
			@Override
			public void newConn(NewConnEvent e) {
				System.out.println( "new conn:"+e.getConnId()+
						", No="+e.getStudentInfo().getId()+
						",name="+e.getStudentInfo().getName() +
						",nickname="+e.getStudentInfo().getNickname());

				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("welcom", "Welcome to us!And please send me your info in 3s");
					DataTransmit.getInstance().sendData(jsonObject, e.getListenId(), e.getConnId());
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}, new ReceiveEventListener(){
			@SuppressWarnings("deprecation")
			@Override
			public void receive(ReceiveEvent event) {
				System.out.println( "server recv:"+event.getData() +", at "+event.getSendTime().getHours() );
			}
		});
		dt.setMaxCount(lid, 1);

	//*	
		StudentInfo studentInfo = new StudentInfo( "b101220105", "夜光随行", "YGSX");
		int cid = dt.createConnection("127.0.0.1", 8890, studentInfo,new ReceiveEventListener(){
			@Override
			public void receive(ReceiveEvent event) {
				System.out.println( "client recv:"+event.getData()+", at "+event.getSendTime() );
			}
		});
		if( cid > 0 ){
			System.out.println( "accessed!" );
		} else if( cid == DataTransmit.CONN_TIMEOUT ){
			System.out.println( "Failed!timeout" );
		} else if( cid == DataTransmit.SERV_REJECT ){
			System.out.println( "Failed!server reject" );
		}
		int cid2 = dt.createConnection("127.0.0.1", 8890, studentInfo,new ReceiveEventListener(){
			@Override
			public void receive(ReceiveEvent event) {
				System.out.println( "client recv:"+event.getData()+", at "+event.getSendTime() );
			}
		});
		if( cid2 > 0 ){
			System.out.println( "accessed!" );
		} else if( cid2 == DataTransmit.CONN_TIMEOUT ){
			System.out.println( "Failed!timeout" );
		} else if( cid2 == DataTransmit.SERV_REJECT ){
			System.out.println( "Failed!server reject" );
		}
		dt.closeListener(lid);
		dt.closeConnection(cid);

		
		
		/*
		try {
			Thread.sleep(1000);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("grade", "3");
			dt.pushData(jsonObject, cid);
			
			
			Calendar c = Calendar.getInstance();
			c.add(Calendar.SECOND, 3);
			Flow flow = new Flow(c.getTime(), new FlowEventListener(){
				@Override
				public void flow(FlowEvent event) {
					if( event.getType() == FlowEvent.TIME_OUT ){
						System.out.println( "TIME_OUT" );
					} else if( event.getType() == FlowEvent.RECEIVE ){
						System.out.println( "RECEIVE:"+event.getData() );
						try {
							JSONObject obj = new JSONObject();
							obj.put("hello", "Hello!");
							DataTransmit.getInstance().sendData(obj, lid);
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}, lid,  dt.getConnectionList(lid).get(0).getConnectionId() );
			flow.run();
			
			
			
			Thread.sleep(999);
			dt.pushData(jsonObject, cid);
			dt.pushData(jsonObject, cid);
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//*/
	}

	/**
	 * @return the isloaded
	 */
	public boolean isloaded() {
		return isloaded;
	}

	/**
	 * @param isloaded the isloaded to set
	 */
	public void setloaded(boolean isloaded) {
		this.isloaded = isloaded;
	}

	
}









