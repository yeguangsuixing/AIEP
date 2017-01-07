package com.njucs.aiep.plugin;

import java.util.AbstractList;

import javax.swing.JComponent;
import javax.swing.JDialog;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEP.Country;
import com.njucs.aiep.ui.AIEPCompetitionDialog;
import com.njucs.aiep.ui.AIEPRankingListDialog;

/**
 * @author ygsx
 * 
 * @created 2013Äê5ÔÂ31ÈÕ15:30:55
 * */
public interface Plugin<E extends JDialog> {
	
	

	/**
	 * Please return your plugin version by string.
	 * 
	 * @return the plugin version
	 * */
	public String getVersion();
	
	/**
	 * Please return the author's name of the plugin. 
	 * 
	 * @return the author's name
	 * */
	public String getAuthor();

	/**
	 * return the name of the plugin, 
	 * and the name returned will be showed on the window's title
	 * 
	 * @param country_no the country No as below:
	 * <ul>
	 * 	<li>0 ENG(default)</li>
	 * 	<li>1 CHN</li>
	 * </ul>
	 * 
	 * @return the name of the plugin
	 * */
	public String getName( Country countryno );


	/**
	 * if your plugin want to set something, please tell the main class.
	 * And the menu items returned will be showed in the "Set" menu.
	 * 
	 * Of course, the menu items should have been bound by some event(s).
	 * 
	 * @return the list of the menu item
	 * */
	public AbstractList<? extends JComponent> getSetMenu();

	/**
	 * get the plugin extending conf dialog
	 * */
	public  E getExtConfDialog();

	
	/**
	 * when the user click the menu "start" (again), the main program will call the function to run
	 * 
	 * @return 0-exit succeedly, 1- exit by error(e.g. sending msg falsely )
	 * 
	 * @see #execute()
	 * */
	public int reExecute();
	
	/**
	 * if the (server) user chooses another plugin or closes the window, the main program
	 *  will call the method. you should dispose all resources in this method.Not need you 
	 *  send a close-msg to the client.
	 * */
	public void forceExit();

	
	/**
	 * if you want to call some functions, please remember to implement
	 *  this method.
	 *  
	 *  @param aiep the main program--AIEP
	 *  @return <code>true</code>-if initializing succeed, <code>false</code> otherwize.
	 * */
	public boolean initialize( AIEP aiep );

	
	public AIEPRankingListDialog getRankingListDialog();
	
	public AIEPCompetitionDialog getCompetitionDialog();
	
}


