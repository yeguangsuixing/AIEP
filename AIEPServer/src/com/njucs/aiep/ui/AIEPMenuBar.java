package com.njucs.aiep.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.AbstractList;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;

import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.Language;
import com.njucs.aiep.AIEP.AiepRole;
import com.njucs.aiep.AIEP.Country;
import com.njucs.aiep.plugin.Plugin;

/**
 * MenuBar
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ4ÈÕ14:01:46
 * */
public class AIEPMenuBar extends JMenuBar {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2570244741238567559L;
	
	private AIEPWindow aiepWindow;
	private AIEPUserConf userConf;
	private AbstractList<JComponent> pluginItemList = new ArrayList<JComponent>();

	protected JMenu systemMenu = new JMenu(Language.MENU_SYS);
	protected JMenu setMenu = new JMenu(Language.MENU_SET);
	protected JMenu viewMenu = new JMenu( Language.MENU_VIEW );
	protected JMenu helpMenu = new JMenu( Language.MENU_HELP );
	//system menu
	protected JMenu roleExpMenu = new JMenu( Language.MENU_SYS_ROLE );
	protected ButtonGroup roleButtonGroup = new ButtonGroup();
	protected JRadioButtonMenuItem aiArenaRoleItem = 
		new JRadioButtonMenuItem( Language.MENU_SYS_ROLE_AIA );
	protected JRadioButtonMenuItem aiJudgeRoleItem = 
		new JRadioButtonMenuItem( Language.MENU_SYS_ROLE_AIJ );
	protected JRadioButtonMenuItem aiSiteRoleItem = 
		new JRadioButtonMenuItem( Language.MENU_SYS_ROLE_AIS );
	protected JMenu chooseExpMenu = new JMenu( Language.MENU_SYS_CHOOSE );
	protected JRadioButtonMenuItem[] expPluginItems;
	protected ButtonGroup expPluginButtonGroup = new ButtonGroup();
	protected JMenuItem exitMenuItem = new JMenuItem( Language.MENU_SYS_EXIT );
	//set menu
	protected JMenuItem basicConfMenuItem = new JMenuItem( Language.MENU_SET_BASIC );
	protected JMenuItem extConfMenuItem = new JMenuItem( Language.MENU_SET_EXT );
	protected JMenuItem runMenuItem = new JMenuItem( Language.MENU_SET_START );
	protected JMenuItem stopMenuItem = new JMenuItem( Language.MENU_SET_STOP );
	//view menu
	protected JMenuItem aiaRankingListMenuItem = 
		new JMenuItem(Language.MENU_VIEW_ARENA_RANKING_LIST);
	protected JMenuItem aiaStateListMenuItem = 
		new JMenuItem(Language.MENU_VIEW_ARENA_STATE_LIST);
	protected JMenu viewStyleMenu = new JMenu(Language.MENU_VIEW_STYLE);
	protected JMenu windowMenu = new JMenu(Language.MENU_VIEW_WIND);
	protected JCheckBoxMenuItem statusBarMenuItem = 
		new JCheckBoxMenuItem( Language.MENU_VIEW_WIND_STATUSBAR );
	protected JMenuItem promptDialogMenuItem = 
		new JMenuItem( Language.MENU_VIEW_WIND_PROMPT_DLG );
	//help menu
	protected JMenuItem helpMenuItem = new JMenuItem(Language.MENU_HELP_HELP);
	protected JMenuItem aboutMenuItem = new JMenuItem(Language.MENU_HELP_ABOUT);
	
	public AIEPMenuBar( AIEPWindow window){
		aiepWindow = window;
		userConf = AIEPUserConf.getInstance();
		this.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		
		this.add(systemMenu);
		this.add(setMenu);
		this.add(viewMenu);
		this.add(helpMenu);
		
		
		addAllMenuItems();
		addMenuItemListener();
	}
	
	private void addAllMenuItems(){
		systemMenu.add(roleExpMenu);
		roleExpMenu.add(aiArenaRoleItem);
		roleExpMenu.add(aiJudgeRoleItem);
		roleExpMenu.add(aiSiteRoleItem);
		roleButtonGroup.add(aiArenaRoleItem);
		roleButtonGroup.add(aiJudgeRoleItem);
		roleButtonGroup.add(aiSiteRoleItem);
		
		aiSiteRoleItem.setSelected(true);
		//roleItems[0].setSelected(true);
		//roleItems[1].setEnabled(false);//TODO the subsequential some version

		
		systemMenu.add(chooseExpMenu);
		systemMenu.add(exitMenuItem);

		//setMenu.add(startMenuItem);
		basicConfMenuItem.setEnabled(false);
		extConfMenuItem.setEnabled(false);
		runMenuItem.setEnabled(false);
		stopMenuItem.setEnabled(false);

		viewMenu.add(viewStyleMenu);
		viewMenu.add(windowMenu);
		windowMenu.add(aiaRankingListMenuItem);
		windowMenu.add(aiaStateListMenuItem);
		windowMenu.add(promptDialogMenuItem);
		windowMenu.add(statusBarMenuItem);
		statusBarMenuItem.setSelected(true);
		//Prompt Dialog is disable
		//promptDialogMenuItem.setEnabled(false);
		
		
		helpMenu.add(helpMenuItem);
		helpMenu.add(aboutMenuItem);
		
		updateMenuByUser();
	}
	private void addMenuItemListener(){
		exitMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				aiepWindow.aiep.exit();
			}
		});
		statusBarMenuItem.addItemListener( new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				aiepWindow.setStatusBarVisiable(statusBarMenuItem.isSelected());
				aiepWindow.invalidate();
				aiepWindow.update(aiepWindow.getGraphics());
			}
		} );
		promptDialogMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				aiepWindow.setPromptDialogVisible( true );
			}
		});
		aiArenaRoleItem.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				aiepWindow.aiep.changeExpRole(AiepRole.AIArena);
				//runMenuItem.setText(Language.MENU_SET_START);
			}
		} );
		aiJudgeRoleItem.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				aiepWindow.aiep.changeExpRole(AiepRole.AIJudge);
				//runMenuItem.setText(Language.MENU_SET_START);
			}
		} );
		aiSiteRoleItem.addActionListener( new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent event) {
				aiepWindow.aiep.changeExpRole(AiepRole.AISite);
			}
		} );
		runMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				aiepWindow.aiep.pluginExecute();
				stopMenuItem.setEnabled(true);
			}
		});
		basicConfMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if( aiepWindow.basicConfDialog == null ){
					return;
				}
				aiepWindow.basicConfDialog.setRole(userConf.getRole());
				aiepWindow.basicConfDialog.setVisible(true);
			}
		});
		extConfMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if( aiepWindow.aiep.getCurrentPlugin() == null ) return;
				JDialog dlg = aiepWindow.aiep.getCurrentPlugin().getExtConfDialog();
				if( dlg != null ){
					dlg.setVisible(true);
				} else {
					JOptionPane.showMessageDialog(null, 
							Language.MSGBOX_NO_OFFER);
				}
			}
		});
		stopMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				aiepWindow.aiep.pluginExit();
			}
		});

		aiaRankingListMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				//JOptionPane.showMessageDialog(null, "Nothing!");
				Plugin<?> plugin = aiepWindow.aiep.getCurrentPlugin();
				if( plugin != null ){
					if( plugin.getRankingListDialog() != null ) {
						plugin.getRankingListDialog().setVisible(true);
					} else {
						JOptionPane.showMessageDialog(null, Language.MSGBOX_NO_OFFER);
					}
				} else {
					JOptionPane.showMessageDialog(null, Language.MSGBOX_NO_OFFER);
				}
			}
		});
		aiaStateListMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Plugin<?> plugin = aiepWindow.aiep.getCurrentPlugin();
				if( plugin != null ){
					if( plugin.getCompetitionDialog() != null ){
						plugin.getCompetitionDialog().setVisible(true);
					} else {
						JOptionPane.showMessageDialog(null, Language.MSGBOX_NO_OFFER);
					}
				} else {
					JOptionPane.showMessageDialog(null, Language.MSGBOX_NO_OFFER);
				}
			}
		});
		
		helpMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, Language.MSG_HELP_CONTENT, 
						Language.MSG_HELP_TITLE, JOptionPane.INFORMATION_MESSAGE);
			}
		});
		aboutMenuItem.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				JOptionPane.showMessageDialog(null, 
						aiepWindow.aiep.getAbout(),
						Language.MSG_ABOUT_TITLE, 
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}
	
	
	//called by aiep window
	public void updateMenuByUser(){
		if( userConf.getRole() == AiepRole.AIArena ){
			aiArenaRoleItem.setSelected( true );
			aiJudgeRoleItem.setSelected( false );
			aiSiteRoleItem.setSelected( false );
		} else if( userConf.getRole() == AiepRole.AIJudge ){
			aiArenaRoleItem.setSelected( false );
			aiJudgeRoleItem.setSelected( true );
			aiSiteRoleItem.setSelected( false );
		} else {
			aiArenaRoleItem.setSelected( false );
			aiJudgeRoleItem.setSelected( false );
			aiSiteRoleItem.setSelected( true );
		}
		AbstractList<? extends Plugin<?>> pluginList = aiepWindow.aiep.getPluginList();
		//remove
		if( expPluginItems != null ){
			for( int i = 0; i < expPluginItems.length; i ++ ){
				expPluginButtonGroup.remove( expPluginItems[i] );
			}
			expPluginItems = null;
		}
		chooseExpMenu.removeAll();
		//add
		if( pluginList != null && pluginList.size() > 0 ) {
			expPluginItems = new JRadioButtonMenuItem[pluginList.size()];
			int index = 0;
			String pn = userConf.getCurrentPluginName();
			Country countryno = userConf.getCountryNo();
			for( Plugin<?> plugin: pluginList ){ 
				expPluginItems[index] = new JRadioButtonMenuItem( plugin.getName( countryno ) );
				expPluginItems[index].setSelected( plugin.getClass().getName().equals( pn ) );
				expPluginItems[index].addActionListener( new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						for( int i = 0; i < expPluginItems.length; i ++ ) {
							if( e.getSource() == expPluginItems[i] ){
								aiepWindow.aiep.changeExpPlugin( i );
								basicConfMenuItem.setEnabled(true);
								extConfMenuItem.setEnabled(true);
								runMenuItem.setEnabled(true);
								//stopMenuItem.setEnabled(true);
								break;
							}
						}
					}
				});
				chooseExpMenu.add( expPluginItems[index] );
				expPluginButtonGroup.add(expPluginItems[index]);//add again
				index++;
			}
		}

		
		this.setMenu.removeAll();
		this.setMenu.add( basicConfMenuItem );
		this.setMenu.add( extConfMenuItem );
		this.setMenu.add( runMenuItem );
		this.setMenu.add( stopMenuItem );
		runMenuItem.setEnabled(false);
		stopMenuItem.setEnabled(false);
		extConfMenuItem.setEnabled(false);
		basicConfMenuItem.setEnabled(false);
		
		/*
		Plugin<?> plugin = this.aiepWindow.aiep.getCurrentPlugin();
		if( plugin != null ){
			AbstractList<? extends JComponent> menuList = plugin.getSetMenu();
			if( menuList == null ) return;
			for( JComponent comp : menuList ){
				this.setMenu.add( comp );
			}
		}//*/
		this.updateUI();
	}
	
	//called by aiep window
	public void updateMenuByPlugin(Plugin<?> plugin){
		for( JComponent comp : pluginItemList ){
			setMenu.remove( comp );
		}
		pluginItemList.clear();
		if( plugin == null ) return;
		//System.out.println( "T3: "+plugin.toString() );
		AbstractList<? extends JComponent> compList = plugin.getSetMenu();
		if( compList != null ) {
			for( JComponent comp : compList ){
				setMenu.add( comp );
				pluginItemList.add( comp );
			}
		}

		
	}
	
}




