

package com.njucs.aiep.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.Language;
import com.njucs.aiep.base.FrameToolKit;
import com.njucs.aiep.plugin.Plugin;
import com.njucs.aiep.ui.conf.BasicConfDialog;


/**
 * AIEP(Artificial Intelligence Experiment Platform) UI Main Class
 * 
 * @author ygsx
 * 
 * @version 0.1
 * 
 * @time 2013Äê5ÔÂ3ÈÕ20:42:45
 * */
public class AIEPWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4431942463094287161L;
	
	protected AIEP aiep;
	private boolean isStatusBarVisiable = true;
	
	//menu bar 
	protected AIEPMenuBar aiepMenuBar;
	//exhibition
	protected Container exhibition = new Container();
	//status bar
	protected JPanel statusPanel  = new JPanel();
	protected JLabel statusBar = new JLabel();//status bar label
	
	//basic conf dialog
	protected BasicConfDialog basicConfDialog;
	//configuration dialog
	protected JDialog extConfDialog;
	//data dialog
	protected AIEPPromptDialog promptDialog;
	
	
	public AIEPWindow( AIEP aiep ){
		
		this.aiep = aiep;

		FrameToolKit.frameIcon( this, "res\\img\\flag.png" );
		FrameToolKit.tray(this, "res\\img\\flag.png", Language.MAIN_TITLE, null );
		
		//*
		if( AIEP.USE_STYLE ) {
			UIManager.put("swing.boldMetal", Boolean.FALSE);
			try {
				javax.swing.UIManager.setLookAndFeel( "org.jvnet.substance.SubstanceLookAndFeel" );
			} catch (Exception e) { //e.printStackTrace();
				System.out.println( "Setting UI Manager Failed!"+e.getMessage() );
			}
			SwingUtilities.updateComponentTreeUI(this);
		}
		//*/
		
		this.setTitle( Language.MAIN_TITLE );
		this.setSize(680, 550);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);// locate the window in the center of the screen
		

		this.aiepMenuBar = new AIEPMenuBar( this );
		this.setJMenuBar(aiepMenuBar);
		
		//this.statusPanel.setBackground(Color.RED);//for test
		this.statusPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.statusPanel.setLayout(new BorderLayout());
		this.statusPanel.add(statusBar,BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(exhibition, BorderLayout.CENTER);
		this.add(statusPanel, BorderLayout.SOUTH);
		
		this.basicConfDialog = new BasicConfDialog( this, this.aiep );
		//this.confDialog = new AIEPConfDialog( aiep.getUserConf() );
		this.promptDialog = new AIEPPromptDialog( this );

	}
	
	public Container getExhibition(){
		return this.exhibition;
	}

	public void setStatusBarVisiable( boolean visible ){
		if( visible ) {
			if( ! isStatusBarVisiable ){
				isStatusBarVisiable = true;
				this.add(statusPanel, BorderLayout.SOUTH);
			}
		} else {
			if( isStatusBarVisiable ){
				isStatusBarVisiable = false;
				this.remove(statusPanel);
			}
		}
		/*
		this.invalidate();
		//this.update(getGraphics());
		if( this.aiep.getCurrentPlugin() != null ) {
			this.aiep.getCurrentPlugin().updateUI();
		}//*/
		this.updateComponentTreeUI();
	}
	protected void setPromptDialogVisible( boolean visible ){
		this.promptDialog.setVisible(visible);
	}
	
	public void setSatusBarText(String text){
		this.statusBar.setText(text);
		this.promptDialog.addPrompt(text);
	}//*/
	public void addPrompt(String prompt) {
		this.promptDialog.addPrompt(prompt);
	}
	
	public void updateByUser(){
		aiepMenuBar.updateMenuByUser();
		if( this.aiep.getCurrentPlugin() != null ) {
			String expname = this.aiep.getCurrentPlugin().getName(
					AIEPUserConf.getInstance().getCountryNo()
			);
			String title = null;
			if( expname == null ) {
				title = Language.MAIN_TITLE;
			} else {
				title = Language.MAIN_TITLE_WITH_EXP.replace("{expname}", expname);
			}
			this.setTitle( title );
			FrameToolKit.changeTrayText( title );
		} else {
			this.setTitle( Language.MAIN_TITLE );
		}
	}
	//*
	public void updateByPlugin( Plugin<?> plugin ){
		aiepMenuBar.updateMenuByPlugin( plugin );//be careful here, "plugin" may be null
		this.updateComponentTreeUI();
	}//*/
	
	
	public void updateComponentTreeUI() {
		updateComponentTreeUI(this);
    }
	
	private static void updateComponentTreeUI(Component c) {
        updateComponentTreeUI0(c);
        c.invalidate();
        c.validate();
        c.repaint();
    }
    private static void updateComponentTreeUI0(Component c) {
    	//System.out.println( "void com.njucs.aiep.ui.AIEPWindow.updateComponentTreeUI0(Component c)" );
        if (c instanceof JComponent) {
            JComponent jc = (JComponent) c;
            jc.updateUI();
            JPopupMenu jpm =jc.getComponentPopupMenu();
            if(jpm != null && jpm.isVisible() && jpm.getInvoker() == jc) {
                updateComponentTreeUI(jpm);
            }
        }
        Component[] children = null;
        if (c instanceof JMenu) {
            children = ((JMenu)c).getMenuComponents();
        }
        else if (c instanceof Container) {
            children = ((Container)c).getComponents();
        }
        if (children != null) {
            for(int i = 0; i < children.length; i++) {
                updateComponentTreeUI0(children[i]);
            }
        }
    }
    
    public void setUploadEnable(boolean uploadEnable){
    	basicConfDialog.setUpload(uploadEnable);
    }
	
}




