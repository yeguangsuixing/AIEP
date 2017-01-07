package com.njucs.aiep.plugin.fir;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.AIEP.Country;
import com.njucs.aiep.AIEPUserConf.AisBasicConf;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.game.GameTree;
import com.njucs.aiep.game.GameTreePanel;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.plugin.AISite;
import com.njucs.aiep.plugin.fir.ai.FIR_AI;
import com.njucs.aiep.plugin.fir.frame.FIRDesk;
import com.njucs.aiep.plugin.fir.frame.Inning;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage;
import com.njucs.aiep.plugin.fir.frame.Step;
import com.njucs.aiep.plugin.fir.frame.Inning.WIN_TYPE;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage.Cmd;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage.Data;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage.DeskReply;
import com.njucs.aiep.plugin.fir.frame.OnlineMessage.MsgType;
import com.njucs.aiep.plugin.fir.ui.FIRBoard;
import com.njucs.aiep.plugin.fir.ui.FIRDeskPanel;
import com.twzcluster.net.NetManager;
import com.twzcluster.net.ReceiveEvent;

public class FIRSite extends AISite<FIR_AI> {

	private static enum UIState { DESK, BOARD };
	
	private GameTreePanel gameTreePanel = new GameTreePanel();
	private FIRDeskPanel firDeskPanel = null;
	private List<FIRDesk> firDeskList = null;
	private boolean isDeskEnable = false;
	
	private Inning currentInning = null;
	private int deskIdBuffer = FIRDesk.ILLEGAL_ID;
	private boolean isRunning = false;
	private AIInfo ofBufferInfo = null, deBufferInfo = null;
	private FIRBoard firBoardPanel = null;
	//private PartInfo offensivePartInfo, defensivePartInfo, thisPartInfo;
	private Status myStatus, opponentStatus;
	
	private final static int WIDTH = FIRJudge.WIDTH, HEIGHT = FIRJudge.HEIGHT;
	
	private AIInfo currentAIInfo = null;
	private FIR_AI currentAI = null;
	private UIState uiState = UIState.DESK;
	
	
	private JPopupMenu popupMenu = new JPopupMenu();
	JMenuItem enterMenuItem = new JMenuItem( Language.MENU_POPUP_ENTER );
	JMenuItem exitMenuItem = new JMenuItem( Language.MENU_POPUP_EXIT );
	JMenuItem lookInfoMenuItem = new JMenuItem( Language.MENU_POPUP_LOOK_INFO );
	JMenuItem switchMenuItem = new JMenuItem( Language.MENU_POPUP_SWITCH );
	
	public FIRSite(){
		super();
		aiLoader =  new FIRLoader();
		aiLoader.setAISite( this );
		firDeskPanel = new FIRDeskPanel(this);
		firBoardPanel = new FIRBoard( null );
		

		
		firDeskPanel.setPopupMenu(popupMenu);
		firBoardPanel.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				if ( e.getButton() == MouseEvent.BUTTON3 ) {//right mouse button
					popupMenu.show(firBoardPanel, e.getX(), e.getY());
				}
			}
		});
		
		popupMenu.add(enterMenuItem);
		popupMenu.add(lookInfoMenuItem);
		popupMenu.add(switchMenuItem);
//		popupMenu.addSeparator();
//		popupMenu.add(exitMenuItem); 
		
		addMenuItemListener();
		
	}
	
	private void addMenuItemListener(){
		exitMenuItem.setEnabled(false);
		switchMenuItem.setEnabled(false);
		enterMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( ! isDeskEnable ){
					JOptionPane.showMessageDialog(null, "Please (Re)start!"); return;
				}
				
				int row = firDeskPanel.getTableSeelctedRow();
				if( firDeskList == null || firDeskList.size() <= row ) return;
				enterDeskRequest( firDeskList.get(row).getDeskId() );
			}
		});
		exitMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if( ! isDeskEnable ){
					JOptionPane.showMessageDialog(null, "Please (Re)start!"); return;
				}
				//int row = firDeskPanel.getTableSeelctedRow();
				//JOptionPane.showMessageDialog(null, "exit!row="+row);
				OnlineMessage om = new OnlineMessage(MsgType.CMD, Cmd.EXIT_DESK);
				NetManager.newInstance().pushMessage(connArenaId, om);
			}
		});
		lookInfoMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if( ! isDeskEnable ){
					JOptionPane.showMessageDialog(null, "Please (Re)start!"); return;
				}
				if( uiState == UIState.DESK ) {
					int row = firDeskPanel.getTableSeelctedRow();
					if( firDeskList == null || firDeskList.size() <= row ) return;
					FIRDesk desk = firDeskList.get(row);
					AIInfo ofinfo = desk.getWrapper1() == null ? null : desk.getWrapper1().getAiInfo();
					AIInfo deinfo = desk.getWrapper2() == null ? null : desk.getWrapper2().getAiInfo();
					showDeskInfo( ofinfo, deinfo );
				} else {
					showInningInfo( currentInning );
				}
			}
		});
		switchMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if( ! isDeskEnable ){
					JOptionPane.showMessageDialog(null, "Please (Re)start!"); return;
				}
				switchJPanel();
			}
		});
	}

	@Override
	public boolean checkAIFile(String aiFileName) throws Exception {
		return getFirAiByFile(aiFileName) != null;
	}

	/*default*/ void addPrompt(String prompt){
		if( this.aiep != null){
			this.aiep.addPrompt(prompt);
		}
	}
	
	/**
	 * @param aiFileName jar file or dll file
	 * @throws Exception 
	 * */
	private FIR_AI getFirAiByFile( String aiFileName ) throws Exception{
		boolean b = aiLoader.setAI(aiFileName);
		aiep.setloaded(true);
		if( ! b ) return null;
		return aiLoader.getAI();
	} 
	
	@Override
	public String getAuthor() {
		return "ygsx";
	}

	@Override
	public String getName(Country countryNo) {
		return "Five In Row[Site]";
	}


	@Override
	public String getVersion() {
		return "v0.1.2";
	}


	//called by AILoader
	public void showGameTree(GameTree treeRoot){
		this.gameTreePanel.updateByGameTree(treeRoot);
	}


	@Override
	public boolean initialize(AIEP aiep) {
		if(! super.initialize(aiep)){
			return false;
		}
		return true;
	}
	
	
	@Override
	public int reExecute(){
		if(super.reExecute() != 0) return 1;
		

		if( AIEPUserConf.getInstance().getAisRole() == AisRole.CONN_AIJ){
			if( this.aiep != null && this.aiep.getCavas() != null ){
				this.aiep.getCavas().setLayout(new BorderLayout());
				this.aiep.getCavas().add( gameTreePanel, BorderLayout.CENTER );
				this.aiep.getCavas().update(aiep.getCavas().getGraphics());
			}
		} else {//to arena, show the arena room
		//	System.out.println( "conn to arena, show the arena room" );
			if( this.aiep != null && this.aiep.getCavas() != null ){
				this.aiep.setWindowSize(WIDTH, HEIGHT);
				this.aiep.setResizable(false);
				
				this.aiep.getCavas().setLayout(new BorderLayout());
				this.aiep.getCavas().add( firDeskPanel, BorderLayout.CENTER );
				this.aiep.getCavas().update(aiep.getCavas().getGraphics());

				this.uiState = UIState.DESK;
				
				this.firDeskPanel.setEnable( this.isDeskEnable = false );
				SwingUtilities.updateComponentTreeUI(firDeskPanel);
				SwingUtilities.updateComponentTreeUI(popupMenu);
			}
		}
		
		//send current ai info to arena
		if( connOnlineArenaId != NetManager.ILLEGAL_ID ){
			try {
				currentAI = getFirAiByFile( AIEPUserConf.getInstance().getString(AisBasicConf.AI_FILE_NAME) );
			} catch (Exception e) {
				e.printStackTrace();
				aiep.setStatusBarText( Language.MSG_ERROR_FAILED_SET + e.getMessage() );
			}
			if( currentAI == null ){
				return 1;
			}
			currentAIInfo = new AIInfo(currentAI.getId(), currentAI.getName(), currentAI.getNickname(), currentAI.getVersion());
			currentAIInfo.setLanguage( aiLoader.getAILang() );
			//thisPartInfo = new PartInfo( currentAIInfo );
			OnlineMessage  msg = new OnlineMessage(currentAIInfo);
			NetManager.newInstance().pushMessage(this.connOnlineArenaId, msg);

		}
		return 0;
	}

	@Override
	protected void handleRecvMessage(ReceiveEvent event) {
		if( ! ((event.getMessage()) instanceof OnlineMessage) ){
			return;
		}
		OnlineMessage message = (OnlineMessage)(event.getMessage());
		if( message.getMsgType() == MsgType.CMD ){
			if( message.getCmd() == Cmd.DESK_REPLY ){
				if( message.getDeskReply() == DeskReply.OK ){
					enterDeskOK(message  );
				} else {
					JOptionPane.showMessageDialog(null,  message.getContent() );
				}
			} else {
				JOptionPane.showMessageDialog(null,  message.getContent() );
			}
		} else if( message.getMsgType() == MsgType.DATA ){
			if( message.getDataType() == Data.DESK_INFO ){
				firDeskList = message.getDeskList();
				firDeskPanel.setDeskList(firDeskList);
				firDeskPanel.setEnable( isDeskEnable = true );
			} else if( message.getDataType() == Data.INNING_INFO ) {
				System.out.println( "recv from arena: ining info!"+new Date() );
				recvInningInfo( message );
			} else if( message.getDataType() == Data.GAME_OVER_INFO ) {
				System.out.println( "recv from arena: game over info!"+new Date() );
				System.out.println( "winner = "+message.getWinner() );
				System.out.println( "wintype="+message.getWinType() );
				gameOver( message );
			} else if( message.getDataType() == Data.STEP_INFO ) {//its my turn
				System.out.println( "recv from arena: step info! it's my turn!"+new Date() );
				itsmyturn( message.getStep() );
			}
		}
	}
	
	public void enterDeskRequest( int deskId ){
		//deskIdBuffer = deskId;
		OnlineMessage msg = new OnlineMessage(Cmd.ENTER_DESK);
		msg.setDeskId( deskId );
		NetManager.newInstance().pushMessage(this.connOnlineArenaId, msg);
	}

	public void exitDesk( int deskId ){
		OnlineMessage msg = new OnlineMessage(Cmd.EXIT_DESK);
		msg.setDeskId(deskId);
		NetManager.newInstance().pushMessage(this.connOnlineArenaId, msg);
	}
	public void itsmyturn(Step lastStep){
		if( currentAI == null ){
			throw new RuntimeException("currentAI == null");
		}
		if( lastStep != null ) {
			lastStep.setStatus(opponentStatus );
			if( ! currentInning.addStep(lastStep) ){
				throw new RuntimeException("Adding to the board failed!");
			}
			this.firBoardPanel.invalidate();
			this.firBoardPanel.repaint();
		} else {
			lastStep = new Step(0, 0);
		}
		
		
		Step myStep = null;
		try{
			myStep = this.currentAI.itsmyturn(lastStep);
		} catch(Exception e){
			System.out.println( "Exception("+e.getClass().getName()+"): "+e.getMessage() );
			StackTraceElement[]  stackArray = e.getStackTrace();
			StringBuilder sb = new StringBuilder();
			sb.append( e.getClass().getName() );
			sb.append("\n");
			for( StackTraceElement elem : stackArray ){
				sb.append("    ");
				sb.append( elem.getFileName() );
				sb.append(": ");
				sb.append( elem.getLineNumber() );
				sb.append("\n");
			}
			JOptionPane.showMessageDialog(null, sb.toString());
			System.out.println( sb.toString() );
		}
		if( myStep != null ) {
			System.out.println( "myStep=("+myStep.getX()+", "+myStep.getY()+"), send to arena" );
			myStep.setStatus(myStatus );
			if( ! currentInning.addStep(myStep) ){
				//throw new RuntimeException("Adding to the board failed!");
				System.out.println( "Adding to the board failed!" );
			}
			this.firBoardPanel.invalidate();
			this.firBoardPanel.repaint();
			
			OnlineMessage msg = new OnlineMessage(myStep);
			NetManager.newInstance().pushMessage(this.connOnlineArenaId, msg);
		} else {
			System.out.println("You get me a NULL value!");
			//not send the step info
		}
	}
	
	public void enterDeskOK(OnlineMessage message){
		//this.aiep.getCavas().setLayout(new BorderLayout());
		this.currentInning = null;
		this.firBoardPanel.setInning(currentInning);
		if( uiState != UIState.BOARD ){
			switchJPanel();
			System.out.println( "enterDeskOK::switchJPanel" );
		}
		isRunning = true;
		deskIdBuffer = message.getDeskId();
		
		enterMenuItem.setEnabled(false);
		exitMenuItem.setEnabled(true);
		switchMenuItem.setEnabled(true);
		//myStatus = message.getStatus();
	}
	
	public void recvInningInfo( OnlineMessage message ){
		currentInning = message.getInning();
		myStatus = message.getStatus();
		opponentStatus = myStatus==Status.OFFENSIVE?Status.DEFENSIVE:Status.OFFENSIVE;
		this.firBoardPanel.setInning(currentInning);
		this.firBoardPanel.invalidate();
		this.firBoardPanel.repaint();
	}
	public void gameOver(OnlineMessage message){
		String winner = null, winType = null;
		if( message.getWinner() == myStatus ){
			winner = "You are the winner! ";
		} else {
			winner = "You lost! ";
		}
		if( message.getWinType() == WIN_TYPE.NORMAL ){
			winType = "Five in Row! ";
		} else if ( message.getWinType() == WIN_TYPE.POSITION_ERROR ){
			winType = "Position is error! ";
		} else if ( message.getWinType() == WIN_TYPE.TIME_OUT ){
			winType = "Time is running out! ";
		}
		deskIdBuffer = FIRDesk.ILLEGAL_ID;
		isRunning = false;
		System.out.println( winner+winType );
		JOptionPane.showMessageDialog(null, winner+winType);
		
		enterMenuItem.setEnabled(true);
		exitMenuItem.setEnabled(false);
	}
	private void switchJPanel(  ){
		if( uiState == UIState.BOARD ){
			this.aiep.getCavas().remove(firBoardPanel);
			this.aiep.getCavas().add( firDeskPanel, BorderLayout.CENTER );
			this.uiState = UIState.DESK;
			//firDeskPanel.invalidate();
			firDeskPanel.updateUI();//.repaint();
//			this.enterMenuItem.setEnabled(false);
//			this.exitMenuItem.setEnabled(false);
		} else if( uiState == UIState.DESK ){
			this.aiep.getCavas().remove(firDeskPanel);
			this.aiep.getCavas().add( firBoardPanel, BorderLayout.CENTER );
			this.uiState = UIState.BOARD;
			firBoardPanel.invalidate();
			firBoardPanel.updateUI();
//			this.enterMenuItem.setEnabled(false);
//			this.exitMenuItem.setEnabled(false);
		}
		this.aiep.getCavas().invalidate();
		this.aiep.getCavas().update(aiep.getCavas().getGraphics());
		//this.aiep.getCavas().requestFocus();
	}
	
	private void showDeskInfo( AIInfo ofInfo, AIInfo deInfo ){

		String deskInfoStringformat = "Offensive:\n" +
				"id=%s\n" +
				"name=%s\n" +
				"nickname=%s\n" +
				"TN=%d\n" +
				"WN=%d\n" +
				"WR=%.2f\n" +
				"\n" +
				"Defensive:\n" +
				"id=%s\n" +
				"name=%s\n" +
				"nickname=%s\n" +
				"TN=%d\n" +
				"WN=%d\n" +
				"WR=%.2f\n" +
				"\n";
		String[] id = new String[2], name = new String[2], nickname = new String[2];
		int[] totalNumber = new int[2], winNumber = new int[2];
		float[] winRato = new float[2];
		id[0] = id[1] = name[0] = name[1] = nickname[0] = nickname[1] = null;
		totalNumber[0] = totalNumber[1] = winNumber[0] = winNumber[1] = 0;
		winRato[0] = winRato[1] = 0.00f;

		if( ofInfo != null ){
			id[0] = ofInfo.getId();
			name[0] = ofInfo.getName();
			nickname[0] = ofInfo.getNickname();
			totalNumber[0] = ofInfo.getTotalCount();
			winNumber[0] = ofInfo.getWinCount();
			if( ofInfo.getTotalCount() > 0 ) {
				winRato[0] = ((float)(ofInfo.getWinCount()))/(ofInfo.getTotalCount());
			} else {
				winRato[0] = 0.00f;
			}
		}
		if( deInfo != null ){
			id[1] = deInfo.getId();
			name[1] = deInfo.getName();
			nickname[1] = deInfo.getNickname();
			totalNumber[1] = deInfo.getTotalCount();
			winNumber[1] = deInfo.getWinCount();
			if( deInfo.getTotalCount() > 0 ) {
				winRato[1] = ((float)(deInfo.getWinCount()))/(deInfo.getTotalCount());
			} else {
				winRato[1] = 0.00f;
			}
		}
		String deskInfoString = String.format(deskInfoStringformat, 
				id[0],name[0],nickname[0],totalNumber[0],winNumber[0],winRato[0],
				id[1],name[1],nickname[1],totalNumber[1],winNumber[1],winRato[1]
		);
		JOptionPane.showMessageDialog(null, deskInfoString, "", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void showInningInfo( Inning inning ){
		if( isRunning ) {
			FIRDesk firdesk = null;
			synchronized( firDeskList ) {
				Iterator<FIRDesk> iter = firDeskList.iterator();
				while( iter.hasNext() ){
					FIRDesk desk = iter.next();
					if( desk.getDeskId() == deskIdBuffer ){
						firdesk = desk; break;
					}
				}
			}
			if( firdesk != null ){
				ofBufferInfo = firdesk.getWrapper1() == null ? null : firdesk.getWrapper1().getAiInfo();
				deBufferInfo = firdesk.getWrapper2() == null ? null : firdesk.getWrapper2().getAiInfo();
			}
			//firdesk = null;
		}
		showDeskInfo( ofBufferInfo, deBufferInfo );
	}
	
}




