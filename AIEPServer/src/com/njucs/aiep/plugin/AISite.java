package com.njucs.aiep.plugin;

import java.util.AbstractList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.AIEPUserConf.AisBasicConf;
import com.njucs.aiep.plugin.AILoader.AilMode;
import com.njucs.aiep.ui.AIEPCompetitionDialog;
import com.njucs.aiep.ui.AIEPRankingListDialog;
import com.twzcluster.net.Message;
import com.twzcluster.net.NetManager;
import com.twzcluster.net.ReceiveEvent;
import com.twzcluster.net.ReceiveListener;


/**
 * AI Site<br />
 * the user can upload the AI to the AI arena
 * @author ygsx
 * 
 * @created 2013Äê6ÔÂ1ÈÕ20:55:47
 * 
 * */
public abstract class AISite<E_AI extends AI > implements Plugin<JDialog> {
	public static enum AisRole { CONN_AIA, CONN_AIJ };
	
	protected AIEP aiep;
	protected AILoader<E_AI> aiLoader = null;
	//protected AisRole aisRole = AisRole.CONN_AIJ;
	
	//protected AbstractList<AIInfo> arenaInfoList;
	//public abstract boolean checkAIFile( String aiFileName );

	protected int connArenaId = NetManager.ILLEGAL_ID;
	protected int connOnlineArenaId = NetManager.ILLEGAL_ID;
	protected RecvAIInfoListener recvAIInfoListener = new RecvAIInfoListener();
	
	protected AIEPRankingListDialog rankingListDialog;
	protected AIEPCompetitionDialog competitionDialog;
	
	public abstract boolean checkAIFile(String aiFileName) throws Exception;
	
	public AILoader<E_AI> getAILoader(){
		return this.aiLoader;
	}
	
	public AIEPRankingListDialog getRankingListDialog(){
		return this.rankingListDialog;
	}
	public AIEPCompetitionDialog getCompetitionDialog(){
		return this.competitionDialog;
	}
	
	public boolean initialize( AIEP aiep ){
		this.aiep = aiep;
		if( AIEPUserConf.getInstance().getAisRole() == AisRole.CONN_AIJ){
			if( aiLoader == null ) {
				System.out.println( "aiLoader == null" );
				return false;
			}
			aiLoader.setAilMode(AilMode.NET);
			aiLoader.setAISite(this);
		} else {//to arena
			rankingListDialog = new AIEPRankingListDialog(this.aiep.getWindow());
			competitionDialog = new AIEPCompetitionDialog(this.aiep.getWindow());
		}
		return true;
	}
	
	@Override
	public int reExecute(){
		forceExit();
		if( AIEPUserConf.getInstance().getAisRole() == AisRole.CONN_AIJ){
			try { aiLoader.setAI(AIEPUserConf.getInstance().getString(AisBasicConf.AI_FILE_NAME));
			} catch (Exception e) {
				e.printStackTrace(); return 1;
			}
			if( aiLoader == null ) return 1;
			if( aiLoader.initialize() && aiLoader.execute()) return 0;
			else return 1;
		} else {
			connArenaId = NetManager.newInstance().createConnection(
					AIEPUserConf.getInstance().getString(AisBasicConf.AIA_IP),//remoteHost, 
					AIEPUserConf.getInstance().getInt(AisBasicConf.AIA_RELEASE_PORT),//remotePort, 
					null, //breakListener, 
					recvAIInfoListener);
			if( connArenaId != NetManager.ILLEGAL_ID ){
				aiep.setStatusBarText( "creating connection with Arena's Release Port succeed!" );
				return 0;
			}
			aiep.setStatusBarText( "creating connection with Arena's Release Port failed!\n" +
					"Try to conn with Arena's Online Port!" );
			connOnlineArenaId = NetManager.newInstance().createConnection(
					AIEPUserConf.getInstance().getString(AisBasicConf.AIA_IP),//remoteHost, 
					AIEPUserConf.getInstance().getInt(AisBasicConf.AIA_ONLINE_PORT),//remotePort, 
					null, //breakListener,
					recvAIInfoListener);
			if( connOnlineArenaId != NetManager.ILLEGAL_ID ){
				aiep.setStatusBarText( "creating connection with Arena's Online Port succeed!" );
				return 0;
			} else {
				aiep.setStatusBarText( "creating connection with Arena's Online Port failed!" );
				return 1;
			}
		}
	}
	
	@Override
	public void forceExit(){
		if( AIEPUserConf.getInstance().getAisRole() == AisRole.CONN_AIJ){
			if( aiLoader == null ){
				System.out.println( "aiLoader == null" ); return;
			}
			aiLoader.forceExit();
		} else {//to ai arena
			if( connArenaId != NetManager.ILLEGAL_ID ){
				NetManager.newInstance().closeConn(connArenaId);
				connArenaId = NetManager.ILLEGAL_ID;
			}
			if( connOnlineArenaId != NetManager.ILLEGAL_ID ){
				NetManager.newInstance().closeConn(connOnlineArenaId);
				connOnlineArenaId = NetManager.ILLEGAL_ID;
			}
		}
	}

	
	@Override
	public AbstractList<? extends JComponent> getSetMenu() {
		return null;
	}
	
	@Override
	public JDialog getExtConfDialog() {
		return null;
	}
	
	protected abstract void handleRecvMessage(ReceiveEvent event);
	
	protected class RecvAIInfoListener implements ReceiveListener {

		@Override
		public void receivedMessage(ReceiveEvent event) {
			System.out.println( "recv from arena!!!" );
			Message msg = event.getMessage();
			if( msg instanceof RankMessage ) {
				if( rankingListDialog != null ) {
					RankMessage rankmsg = (RankMessage)msg;
					rankingListDialog.setRankingList( rankmsg.getRankList() );
				}
			} else if ( msg instanceof  CompetitionMessage ){
				if( competitionDialog != null ){
					CompetitionMessage compmsg = (CompetitionMessage)msg;
					competitionDialog.setCompetitionList(compmsg.getCompetitionList());
				}
			} else if ( msg instanceof UploadMessage ) {
				JOptionPane.showMessageDialog(null,  msg.getContent() );
			} else {
				handleRecvMessage(event);
			}
		}
	}


	
}
