package com.njucs.aiep.ui.conf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.njucs.aiep.AIEP;
import com.njucs.aiep.AIEPUserConf;
import com.njucs.aiep.Language;
import com.njucs.aiep.AIEP.AiepRole;
import com.njucs.aiep.AIEPUserConf.AiaBasicConf;
import com.njucs.aiep.AIEPUserConf.AijBasicConf;
import com.njucs.aiep.AIEPUserConf.AisBasicConf;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.net.DataTransmit.Lang;
import com.njucs.aiep.net.file.FileTransmit;
import com.njucs.aiep.plugin.AI;
import com.njucs.aiep.plugin.AILoader;
import com.njucs.aiep.plugin.AISite;
import com.njucs.aiep.plugin.Plugin;
import com.njucs.aiep.plugin.AISite.AisRole;
/**
 * AI Basic(ip, port) conf dialog
 * 
 * @author ygsx
 * @created 2013Äê6ÔÂ2ÈÕ23:56:19
 * */
public class BasicConfDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3640399492131925730L;
	private AIEP aiep;

	
	
	private AIABasicConfPanel aiaPanel = new AIABasicConfPanel();
	private AIJBasicConfPanel aijPanel = new AIJBasicConfPanel();
	private AISBasicConfPanel aisPanel = new AISBasicConfPanel();

	public BasicConfDialog(  JFrame source, AIEP aiep ){ 
		super(source);
		this.aiep = aiep;
		
		this.setSize(400, 400);
		//this.setResizable( false );
		this.setModal(true);
		this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
	}
	

	
	/**
	 * @param role the role to set
	 */
	public void setRole(AiepRole role) {

		this.getContentPane().removeAll();
		if( role == AiepRole.AIArena ){
			this.setTitle(Language.DLG_BASIC_AIA_TITLE);
			this.getContentPane().add( aiaPanel );
			this.setSize(400, 200);
			this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		} else if( role == AiepRole.AISite  ){
			this.setTitle(Language.DLG_BASIC_AIS_TITLE);
			this.getContentPane().add( aisPanel );
			this.setSize(400, 400);
			this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		} else if( role == AiepRole.AIJudge ){
			this.setTitle(Language.DLG_BASIC_AIJ_TITLE);
			this.getContentPane().add( aijPanel );
			this.setSize(400, 100);
			this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		}
		//this.pack();
	}

	public void setUpload(boolean uploadEnable){
		aisPanel.uploadButton.setEnabled(uploadEnable);
	}
	
	private class AIABasicConfPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3831814881993158261L;

		protected JLabel aiaReleasePortLabel = new JLabel( Language.DLG_BC_AIA_RELEASE_PORT_LABEL );
		protected JTextField aiaReleasePortField = new JTextField( 20 );
		protected JLabel aiaUploadPortLabel = new JLabel( Language.DLG_BC_AIA_UPLOAD_PORT_LABEL );
		protected JTextField aiaUploadPortField = new JTextField( 20 );
		//recv the judge report
		protected JLabel aiaRecvPortLabel = new JLabel( Language.DLG_BC_AIA_RECEIVE_PORT_LABEL );
		protected JTextField aiaRecvPortField = new JTextField( 20 );
		
		protected JLabel aiaUploadPathLabel = new JLabel( Language.DLG_BC_AIA_UPLOAD_PATH_LABEL );
		protected JTextField aiaUploadPathField = new JTextField( 20 );
		
		protected JRadioButton uploadRadioButton = new JRadioButton( Language.DLG_BC_UPLOAD );
		protected JRadioButton onlineRadioButton = new  JRadioButton( Language.DLG_BC_ONLINE );
		protected ButtonGroup modeGroup = new ButtonGroup();
		protected JCheckBox releaseCompetitionCheckBox = new JCheckBox( Language.DLG_BC_RELEASE );
		

		protected JButton startButton = new JButton(Language.DLG_BC_BTN_START);
		protected JButton applyButton = new JButton(Language.DLG_BC_BTN_APPLY);
		protected JButton cancelButton = new JButton(Language.DLG_BC_BTN_CANCEL);
		
		public AIABasicConfPanel(){
			AIEPUserConf conf = AIEPUserConf.getInstance();
			addComponent();
			addListener();
			setComponent( conf );
		}


		private void addComponent() {
			this.add( aiaReleasePortLabel );
			this.add( aiaReleasePortField );
			this.add( aiaUploadPortLabel );
			this.add( aiaUploadPortField );
			this.add( aiaRecvPortLabel );
			this.add( aiaRecvPortField );

			this.add(onlineRadioButton);modeGroup.add(onlineRadioButton);
			this.add(uploadRadioButton);modeGroup.add(uploadRadioButton);
			this.add(releaseCompetitionCheckBox);
			
			this.add( aiaUploadPathLabel );
			this.add( aiaUploadPathField );
			
			this.add(startButton);
			this.add(applyButton);
			this.add(cancelButton);
		}
		private void addListener() {
			startButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) { 
					if( setByComponent( AIEPUserConf.getInstance() ) ){
						BasicConfDialog.this.setVisible(false);
						BasicConfDialog.this.aiep.pluginExecute();
					} else {
						JOptionPane.showMessageDialog(null, Language.MSGBOX_SET_FAILED);
					}
				}

			});
			applyButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if( setByComponent( AIEPUserConf.getInstance() ) ){
						BasicConfDialog.this.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, Language.MSGBOX_SET_FAILED);
					}
				}
			});
			cancelButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					BasicConfDialog.this.setVisible(false);
				}
			});
		}

		private void setComponent(AIEPUserConf conf) {
			aiaReleasePortField.setText( conf.getString(AiaBasicConf.AIA_RELEASE_PORT) );
			aiaUploadPortField.setText( conf.getString(AiaBasicConf.AIA_UPLOAD_PORT) );
			aiaRecvPortField.setText( conf.getString(AiaBasicConf.AIA_RECV_PORT) );

			//uploadCheckBox.setEnabled(false);
			if( conf.getBoolean(AiaBasicConf.AIA_ONLINE_OR_UPLOAD) ){
				onlineRadioButton.setSelected(true);
				uploadRadioButton.setSelected(false);
			} else {
				onlineRadioButton.setSelected(false);
				uploadRadioButton.setSelected(true);
			}
			
			//uploadCheckBox.setSelected(false);
			releaseCompetitionCheckBox.setSelected(conf.getBoolean(AiaBasicConf.AIA_RELEASE_RSL));
			
			aiaUploadPathField.setText( conf.getString(AiaBasicConf.AIA_UPLOAD_PATH) );
		}
		private boolean setByComponent(AIEPUserConf conf) {
			conf.setString(AiaBasicConf.AIA_RELEASE_PORT, aiaReleasePortField.getText());
			conf.setString(AiaBasicConf.AIA_UPLOAD_PORT, aiaUploadPortField.getText());
			conf.setString(AiaBasicConf.AIA_RECV_PORT, aiaRecvPortField.getText());
			conf.setBoolean(AiaBasicConf.AIA_ONLINE_OR_UPLOAD, onlineRadioButton.isSelected());
			conf.setBoolean(AiaBasicConf.AIA_RELEASE_RSL, releaseCompetitionCheckBox.isSelected());
			conf.setString(AiaBasicConf.AIA_UPLOAD_PATH, aiaUploadPathField.getText());
			return true;
		}
		
	}
	private class AIJBasicConfPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4167250847762651230L;
		
		protected JLabel aijPortLabel = new JLabel( Language.DLG_BC_AIJ_PORT_LABEL );
		protected JTextField aijPortField = new JTextField( 20 );

		protected JButton startButton = new JButton(Language.DLG_BC_BTN_START);
		protected JButton applyButton = new JButton(Language.DLG_BC_BTN_APPLY);
		protected JButton cancelButton = new JButton(Language.DLG_BC_BTN_CANCEL);
		
		public AIJBasicConfPanel(){

			
			AIEPUserConf conf = AIEPUserConf.getInstance();
			
			this.add( aijPortLabel );
			this.add( aijPortField );

			this.add(startButton);
			this.add(applyButton);
			this.add(cancelButton);
			
			setComponent( conf );
			addListener();
		}

		private void setComponent(AIEPUserConf conf) {
			aijPortField.setText( conf.getString(AijBasicConf.AIJ_PORT) );
		}

		private void addListener() {

			startButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) { 
					if( setByComponent( AIEPUserConf.getInstance() ) ){
						BasicConfDialog.this.setVisible(false);
						BasicConfDialog.this.aiep.pluginExecute();
					} else {
						JOptionPane.showMessageDialog(null, "Set Failed!");
					}
				}
			});
			applyButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if( setByComponent( AIEPUserConf.getInstance() ) ){
						BasicConfDialog.this.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, "Set Failed!");
					}
				}
			});
			cancelButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					BasicConfDialog.this.setVisible(false);
				}
			});
		}
		private boolean setByComponent(AIEPUserConf conf){
			if( conf == null ) return true;
			conf.setString(AijBasicConf.AIJ_PORT,  aijPortField.getText() );
			return true;
		}
		
	}
	private class AISBasicConfPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 2582715813737845998L;
		
		//private AISandBox sandbox = new AISandBox();

		protected JRadioButton aijRoleButton = new JRadioButton(Language.DLG_BC_CONN_AIJ);
		protected JRadioButton aiaRoleButton = new JRadioButton(Language.DLG_BC_CONN_AIA);
		protected ButtonGroup roleMode = new ButtonGroup();
		
		protected JLabel aijIpLabel = new JLabel( Language.DLG_BC_AIJ_IP_LABEL );
		protected JTextField aijIpField = new JTextField( 20 );
		protected JLabel aijPortLabel = new JLabel( Language.DLG_BC_AIJ_PORT_LABEL );
		protected JTextField aijPortField = new JTextField( 20 );
		

		protected JLabel aiaIpLabel = new JLabel( Language.DLG_BC_AIA_IP_LABEL );
		protected JTextField aiaIpField = new JTextField( 20 );
		protected JLabel aiaOnlinePortLabel = new JLabel( Language.DLG_BC_AIA_ONLINE_PORT_LABEL );
		protected JTextField aiaOnlinePortField = new JTextField( 20 );
		protected JLabel aiaReleasePortLabel = new JLabel( Language.DLG_BC_AIA_RELEASE_PORT_LABEL );
		protected JTextField aiaReleasePortField = new JTextField( 20 );
		
		protected JCheckBox runInSanboxCheckBox = new JCheckBox( Language.DLG_BC_RUN_IN_SANDBOX );
		
		protected JLabel aiFileLabel = new JLabel(Language.DLG_BC_AI_FILE);
		protected JTextField aiFileField = new JTextField( 20 );
		protected JButton aiScanButton = new JButton(Language.DLG_BC_SCAN_AI_FILE);
		protected JFileChooser fileChooser = new JFileChooser( new File( "." ) );
		
		protected JButton startButton = new JButton(Language.DLG_BC_BTN_START);
		protected JButton applyButton = new JButton(Language.DLG_BC_BTN_APPLY);
		protected JButton uploadButton = new JButton(Language.DLG_BC_BTN_UPLOAD);
		protected JButton cancelButton = new JButton(Language.DLG_BC_BTN_CANCEL);
		
		public AISBasicConfPanel(){ 
			
			AIEPUserConf conf = AIEPUserConf.getInstance();
			addComponent();
			addListener();
			setComponent( conf );
		}
		
		private void addComponent(){
			
			roleMode.add(aijRoleButton);
			roleMode.add(aiaRoleButton);
			this.add(aijRoleButton);
			this.add(aiaRoleButton);
			
			this.add( aijIpLabel );
			this.add( aijIpField );
			this.add( aijPortLabel );
			this.add( aijPortField );
			
			this.add( aiaIpLabel );
			this.add( aiaIpField );
			this.add( aiaOnlinePortLabel );
			this.add( aiaOnlinePortField );
			this.add( aiaReleasePortLabel );
			this.add( aiaReleasePortField );
			
			this.add(runInSanboxCheckBox);
			

			this.add(aiFileLabel);
			this.add(aiFileField);
			this.add(aiScanButton);
			
			this.add(startButton);
			this.add(applyButton);
			this.add(uploadButton);
			this.add(cancelButton);

			//upload button is enable after the plugin executes
			uploadButton.setEnabled(false);
			
			if( aiep != null && aiep.isloaded() ){
				aiFileField.setEditable(false);
				aiScanButton.setEnabled(false);
			}
			
		}
		
		public void setVisible( boolean visible ){
			if( aiep != null && aiep.isloaded() ){
				aiFileField.setEditable(false);
				aiScanButton.setEnabled(false);
			}
			super.setVisible(visible);
		}
		
		private void setComponent( AIEPUserConf conf ){
			if( conf == null ) return;
			
			aijRoleButton.setSelected( conf.getAisRole() == AisRole.CONN_AIJ );
			aiaRoleButton.setSelected( conf.getAisRole() == AisRole.CONN_AIA );
			
			aijIpField.setText( conf.getString(AisBasicConf.AIJ_IP) );
			aijPortField.setText( conf.getString(AisBasicConf.AIJ_PORT) );
			aiaIpField.setText( conf.getString(AisBasicConf.AIA_IP) );
			aiaOnlinePortField.setText( conf.getString(AisBasicConf.AIA_ONLINE_PORT) );
			aiaReleasePortField.setText( conf.getString(AisBasicConf.AIA_RELEASE_PORT) );
			
			runInSanboxCheckBox.setEnabled(false);
			runInSanboxCheckBox.setSelected( conf.getBoolean(AisBasicConf.AIJ_RUN_IN_SANDBOX) );
			
			aiFileField.setText( conf.getString(AisBasicConf.AI_FILE_NAME) );
		}
		
		private void addListener(){

			aiScanButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					if(fileChooser.showOpenDialog(BasicConfDialog.this)
							== JFileChooser.APPROVE_OPTION){
						aiFileField.setText( fileChooser.getSelectedFile().getAbsolutePath() );
					}
				}
			});
			

			startButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) { 
					try {
						if( setByComponent( AIEPUserConf.getInstance() ) ){
							BasicConfDialog.this.setVisible(false);
							BasicConfDialog.this.aiep.pluginExecute();
							aiep.setloaded(true);
							aiFileField.setEditable(false);
							aiScanButton.setEnabled(false);
						} else {
							JOptionPane.showMessageDialog(null, Language.MSGBOX_SET_FAILED);
						}
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, Language.MSGBOX_SET_FAILED+e1.getMessage());
					}
				}
			});
			applyButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						if( setByComponent( AIEPUserConf.getInstance() ) ){
							BasicConfDialog.this.setVisible(false);
							aiep.setloaded(true);
							aiFileField.setEditable(false);
							aiScanButton.setEnabled(false);
						} else {
							JOptionPane.showMessageDialog(null, Language.MSGBOX_SET_FAILED);
						}
					}  catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, Language.MSGBOX_SET_FAILED+e1.getMessage());
					}
				}
			});
			uploadButton.addActionListener(new ActionListener(){
				@SuppressWarnings("unchecked")
				@Override
				public void actionPerformed(ActionEvent e) {
				//	if(aiep.isloaded()){
				//		JOptionPane.showMessageDialog(null, Language.MSGBOX_LOAD_FAILED_BY_TWICE);
				//	}
					
					try {
						if( ! setByComponent( AIEPUserConf.getInstance() ) ) {
							JOptionPane.showMessageDialog(null, Language.MSGBOX_SET_FAILED);
							return;
						}
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, Language.MSGBOX_SET_FAILED+e1.getMessage());
					}
					//String file = AIEPUserConf.getInstance().getString(AisBasicConf.AI_FILE_NAME);
					FileTransmit client = new FileTransmit(
							AIEPUserConf.getInstance().getString(AisBasicConf.AIA_IP),
							AIEPUserConf.getInstance().getInt(AisBasicConf.AIA_UPLOAD_PORT)		
					);
					try {
						Plugin<? extends JDialog> p =aiep.getCurrentPlugin();
						if( p instanceof AISite<?> ){
							AILoader<AI> aiLoader =  ((AISite<AI>)(p)).getAILoader();
							AI ai = aiLoader.getAI();
							if( ai == null ){
								throw new RuntimeException( "AI is null!" );
							}
							AIInfo aiInfo = new AIInfo( ai.getId(), ai.getName(), ai.getNickname(), ai.getVersion() );
							System.out.println( "AI Lang = "+aiLoader.getAILang() );
							if( aiLoader.getAILang() == Lang.JAVA ){
								System.out.println( "ai.getClass().getName()="+ai.getClass().getName() );
								aiInfo.setClassName( ai.getClass().getName() );
							} else {
								aiInfo.setClassName(null);
							}
							aiInfo.setSrcFileName(
									AIEPUserConf.getInstance().getString(AisBasicConf.AI_FILE_NAME)
							);/*
							if( aiInfo == null ){
								JOptionPane.showMessageDialog(null, Language.MSGBOX_UPLOAD_FAILED_AS_AI);
								return;
							}//*/
							client.sendAI( aiInfo );
							client.stop();
							//JOptionPane.showMessageDialog(null, Language.MSGBOX_UPLOAD_OK);
							BasicConfDialog.this.setVisible(false);
							uploadButton.setEnabled(false);
							aiep.setloaded(true);
							aiFileField.setEditable(false);
							aiScanButton.setEnabled(false);
						} else {
							throw new RuntimeException("The current plugin is not AISite!");
						}
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null, Language.MSGBOX_UPLOAD_FAILED+e1.getMessage());
					}
				}
			});
			
			cancelButton.addActionListener(new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) {
					BasicConfDialog.this.setVisible(false);
				}
			});
		}
		
		private boolean setByComponent(AIEPUserConf conf) throws Exception{
			if( conf == null ) return true;
			conf.setAisRole( aijRoleButton.isSelected()?AisRole.CONN_AIJ:AisRole.CONN_AIA );
			conf.setString(AisBasicConf.AIJ_IP,  aijIpField.getText() );
			conf.setString(AisBasicConf.AIJ_PORT,  aijPortField.getText() );
			conf.setString(AisBasicConf.AIA_IP,  aiaIpField.getText() );
			conf.setString(AisBasicConf.AIA_ONLINE_PORT,  aiaOnlinePortField.getText() );
			conf.setString(AisBasicConf.AIA_RELEASE_PORT,  aiaReleasePortField.getText() );
			
			conf.setBoolean(AisBasicConf.AIJ_RUN_IN_SANDBOX, runInSanboxCheckBox.isSelected());
			
			conf.setString(AisBasicConf.AI_FILE_NAME,  aiFileField.getText() );
			
			Plugin<? extends JDialog> plugin = aiep.getCurrentPlugin();
			if( plugin instanceof AISite<?> ){
 				return ((AISite<?>)plugin).checkAIFile(aiFileField.getText());
			}
			return true;
		}
		
	}
	
	
	
}







