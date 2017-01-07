package com.njucs.aiep.plugin;

import java.util.AbstractList;

import javax.swing.JComponent;
import javax.swing.JDialog;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.AIEP.Country;
import com.njucs.aiep.AIEPUserConf.AijBasicConf;
import com.njucs.aiep.plugin.AILoader.AilMode;
import com.njucs.aiep.plugin.ResultMessage.ResultType;
import com.njucs.aiep.ui.AIEPCompetitionDialog;
import com.njucs.aiep.ui.AIEPRankingListDialog;
import com.twzcluster.net.INetManager;
import com.twzcluster.net.NetManager;



/**
 * ExperimentPlugin is the interface of the plugin of the AI experiment.
 * If you want to add an experiment to the AIEPS, please implement the Class.
 * 
 * @author ygsx
 * 
 * @version 0.1
 * 
 * @time 2013Äê5ÔÂ3ÈÕ21:33:39
 * 
 * */
public abstract class AIJudge<E_AI extends AI> implements Plugin<JDialog> {
	
	public static enum AijRole { CONN_ARENA, CONN_SITE };
	
	protected AIEP aiep;
	protected AIEPUserConf userConf;
	protected AijRole aijRole = AijRole.CONN_SITE;

	protected String[] aiFileArray;

	@Override
	public abstract AbstractList<? extends JComponent> getSetMenu();

	@Override
	public abstract JDialog getExtConfDialog();
	
	protected abstract int executeSiteRole();
	
	@Override
	public abstract void forceExit();

	@Override
	public abstract String getAuthor();

	@Override
	public abstract String getName(Country countryno);

	@Override
	public abstract String getVersion();


	/**
	 * get the result between the given 2 AIs
	 * */
	protected abstract Result getResult( AILoader<E_AI> aiLoader1, AILoader<E_AI> aiLoader2 );

	
	protected abstract AILoader<E_AI> genAILoader();


	public AIEPRankingListDialog getRankingListDialog(){
		return null;
	}
	public AIEPCompetitionDialog getCompetitionDialog(){
		return null;
	}
	
	public void setAijRole(AijRole role){
		this.aijRole = role;
	}
	public AijRole getAijRole(){
		return this.aijRole;
	}
	
	@Override
	public final int reExecute() {//forbid overrided
		if( this.aijRole == AijRole.CONN_ARENA ){
			return exceuteArenaRole();
		} else if( this.aijRole == AijRole.CONN_SITE ){
			return executeSiteRole();
		} else {
			throw new RuntimeException( "AijRole is unknown!" );
		}
	}

	public void setAIFileArray(String[] aiFileArray){
		this.aiFileArray = aiFileArray;
	}
	
	@Override
	public boolean initialize(AIEP aiep) {
		this.aiep = aiep;
		if( this.aiep == null ) return false;
		userConf = AIEPUserConf.getInstance();
		return true;
	}

	protected final int exceuteArenaRole() {
		if( aiFileArray == null || aiFileArray.length < 2 ) return 1;
		AILoader<E_AI> loader1 = genAILoader();
		AILoader<E_AI> loader2 = genAILoader();
		if( loader1 == null || loader2 == null){
			System.out.println( "Loader is null!" ); return -1;
		}
		String aifile1 = aiFileArray[0];//userConf.getString(AijBasicConf.AIJ_AI_JAR_FILE1);d
		String aifile2 = aiFileArray[1];//userConf.getString(AijBasicConf.AIJ_AI_JAR_FILE2);
		if( aifile1 == null ){
			System.out.println( "ai file 1 is null" ); return -1;
		} else if ( aifile2 == null ){
			System.out.println( "ai file 2 is null" ); return -1;
		}
		System.out.println( "aifile1="+aifile1 );
		System.out.println( "aifile2="+aifile2 );
		try {
			loader1.setAI( aifile1 );
			loader2.setAI( aifile2 );
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		loader1.setAilMode(AilMode.LOCAL);
		loader2.setAilMode(AilMode.LOCAL);
		E_AI ai1 = loader1.getAI();
		E_AI ai2 = loader2.getAI();
		System.out.println( "ai1="+ai1.getId() );
		System.out.println( "ai2="+ai2.getId() );
		Result result = getResult(loader1, loader2);//a block function
		
		INetManager manager = NetManager.newInstance();
		manager.init(null);
		int t = manager.createConnection(
				userConf.getString(AijBasicConf.AIJ_RESULT_SEND_IP), 
				userConf.getInt(AijBasicConf.AIJ_RESULT_SEND_PORT)
		);
		if( t != NetManager.ILLEGAL_ID ) {
			//System.out.println( "ofid="+result.getOffensiveAIInfo().getId() );
			manager.pushMessage(t, new ResultMessage( result, ResultType.FINAL ) );
			manager.closeConn(t);
		}
		//manager.destroy(null);
		manager = null;
		return 0;
	}
	
}




