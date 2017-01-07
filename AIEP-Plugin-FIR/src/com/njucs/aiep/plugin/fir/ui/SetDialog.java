package com.njucs.aiep.plugin.fir.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.njucs.aiep.plugin.fir.FIRArena;
import com.njucs.aiep.plugin.fir.FIRJudge;
import com.njucs.aiep.plugin.fir.Language;
import com.njucs.aiep.plugin.fir.frame.Mode;

/**
 * FIR Set Dialog
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ6ÈÕ0:09:56
 * */
public class SetDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3147070664854848853L;
	
	private static enum SetMode { ARENA, JUDGE };

	private FIRJudge firServer;
	private FIRArena firArena;
	private SetMode setMode;

	protected JPanel jpRoot = new JPanel();
	protected ButtonGroup bgMode = new ButtonGroup();
	protected JRadioButton jrbHumanAIMode = new JRadioButton( Language.BTN_MODE_HUMAN_AI );
	protected JRadioButton jrbAIAIMode = new JRadioButton( Language.BTN_MODE_AI_AI );
	//protected JRadioButton jrbServerMode = new JRadioButton( Language.BTN_MODE_SERVER );
	protected JButton jbStart = new JButton( Language.BTN_START );
	protected JButton jbApply = new JButton( Language.BTN_APPLY );
	protected JButton jbCancel = new JButton( Language.BTN_CANCEL );

	protected JPanel jpHumanAIMode = new JPanel();
	protected JPanel jpAIAIMode = new JPanel();
	//protected JPanel jpServerMode = new JPanel();

	//who is first(offensive) in the HUMEN vs AI mode?
	protected ButtonGroup bgFirst = new ButtonGroup();
	protected JRadioButton jrbHumenFirst = new JRadioButton( Language.BTN_FIRST_HUMEN );
	protected JRadioButton jrbAIFirst = new JRadioButton( Language.BTN_FIRST_AI );
	
	protected JLabel jlId = new JLabel( Language.LABEL_DLG_SET_ID );
	protected JLabel jlName = new JLabel( Language.LABEL_DLG_SET_NAME );
	protected JLabel jlNick = new JLabel( Language.LABEL_DLG_SET_NICK );
	protected JTextField jtfId = new JTextField( 20 );
	protected JTextField jtfName = new JTextField( 20 );
	protected JTextField jtfNick = new JTextField( 20 );
	
	//AI vs AI
	protected JLabel chooseOfHeLabel = new JLabel( Language.LABEL_OF_CHOOSE_HE );
	protected JLabel chooseDeHeLabel = new JLabel( Language.LABEL_DE_CHOOSE_HE );
	protected ButtonGroup ofHeuristicGroup = new ButtonGroup();
	protected ButtonGroup deHeuristicGroup = new ButtonGroup();
	protected JRadioButton ofHeuristic1 = new JRadioButton( Language.LABEL_HE1 );
	protected JRadioButton ofHeuristic2 = new JRadioButton( Language.LABEL_HE2 );
	protected JRadioButton deHeuristic1 = new JRadioButton( Language.LABEL_HE1 );
	protected JRadioButton deHeuristic2 = new JRadioButton( Language.LABEL_HE2 );
	
	protected JLabel levelLabel = new JLabel( Language.LABEL_LEVEL );
	protected ButtonGroup levelGroup = new ButtonGroup();
	protected JRadioButton level1RB = new JRadioButton( Language.LABEL_LEVEL1 );
	protected JRadioButton level2RB = new JRadioButton( Language.LABEL_LEVEL2 );
	protected JRadioButton level3RB = new JRadioButton( Language.LABEL_LEVEL3 );
	
	protected JLabel chessFileLabel = new JLabel( Language.LABEL_CHESS_FILE );
	protected JTextField chessFileField = new JTextField();
	protected JButton chooseFileButton = new JButton( Language.LABEL_CHOOSE );
	protected JFileChooser fileChooser = new JFileChooser( new File( "." ) );
	
	public SetDialog(Dialog owner, FIRJudge firServer) {
		super( owner, true );
		this.firServer = firServer;
		this.setMode = SetMode.JUDGE;
		
		this.setTitle(Language.TIP_TITLE_DLG_SET);
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
		
		this.initialize();
		//pack();
	}
	public SetDialog(Dialog owner, FIRArena firArena) {
		super( owner );
		this.firArena = firArena;
		this.setMode = SetMode.ARENA;
		
		this.setTitle(Language.TIP_TITLE_DLG_SET);
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
		
		this.initialize();
		
		jrbHumanAIMode.setEnabled(false);
		jrbAIAIMode.setSelected(true);
		setHeuristicButtonEnable();
		
	}

	
	private void initialize(){
		
		jpRoot.setLayout(new BorderLayout());
		//north
		JPanel jpChooseMode = new JPanel();
		bgMode.add(jrbHumanAIMode);
		bgMode.add(jrbAIAIMode);
		//bgMode.add(jrbServerMode);
		jpChooseMode.add(jrbHumanAIMode);
		jpChooseMode.add(jrbAIAIMode);
		//jpChooseMode.add(jrbServerMode);
		jpRoot.add(jpChooseMode, BorderLayout.NORTH);
		//center
		ofHeuristicGroup.add(ofHeuristic1);
		ofHeuristicGroup.add(ofHeuristic2);
		deHeuristicGroup.add(deHeuristic1);
		deHeuristicGroup.add(deHeuristic2);
		levelGroup.add(level1RB);
		levelGroup.add(level2RB);
		levelGroup.add(level3RB);
		ofHeuristic1.setSelected(true);
		deHeuristic1.setSelected(true);
		level1RB.setSelected(true);
		initializeHumanAI();
		initializeAIAI();
		jpRoot.add(jpHumanAIMode, BorderLayout.CENTER);
		jpHumanAIMode.setBorder(new LineBorder(Color.GRAY));
		jpAIAIMode.setBorder(new LineBorder(Color.GRAY));
	//	jpServerMode.setBorder(new LineBorder(Color.GRAY));
		//south
		JPanel jpControlButton = new JPanel();
		jpControlButton.add(jbStart);
		jpControlButton.add(jbApply);
		jpControlButton.add(jbCancel);
		jpRoot.add(jpControlButton, BorderLayout.SOUTH);
		
		this.add(jpRoot);
		addListener();
		jrbHumanAIMode.setSelected(true);
		//Server Mode is disable
		//jrbServerMode.setEnabled(false);
		/*
		try {
			this.firServer.setMode( Mode.AI_VS_HUMAN, 1,1,1,null);
		} catch (Exception e) {
			e.printStackTrace();
		}//*/
	}
	private void addListener(){
		jrbHumanAIMode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				jpRoot.remove(jpHumanAIMode);
				jpRoot.remove(jpAIAIMode);
				//jpRoot.remove(jpServerMode);
				jpRoot.add(jpHumanAIMode, BorderLayout.CENTER);
				jpRoot.updateUI();
				addSameComponent( 1 );
				setHeuristicButtonEnable();
				//JOptionPane.showMessageDialog(null, "Human VS AI");
			}
		});
		jrbAIAIMode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				jpRoot.remove(jpHumanAIMode);
				jpRoot.remove(jpAIAIMode);
			//	jpRoot.remove(jpServerMode);
				jpRoot.updateUI();
				jpRoot.add(jpAIAIMode, BorderLayout.CENTER);
				addSameComponent( 2 );
				setHeuristicButtonEnable();
			}
		});
		/*
		jrbServerMode.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				jpRoot.remove(jpHumanAIMode);
				jpRoot.remove(jpAIAIMode);
			//	jpRoot.remove(jpServerMode);
				jpRoot.updateUI();
			//	jpRoot.add(jpServerMode, BorderLayout.CENTER);
				setHeuristicButtonEnable();
			}
		});//*/
		jbCancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				SetDialog.this.setVisible(false);
			}
		});
		jbApply.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SetDialog.this.handleApplyButton();
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, Language.MSG_ERROR_FAILED_SET );
				}
			}
		});
		jbStart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SetDialog.this.handleStartButton();
				} catch (Exception e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(null, Language.MSG_ERROR_FAILED_SET );
					if( setMode == SetMode.JUDGE ){
						firServer.setStatusBarText(Language.MSG_ERROR_FAILED_SET);
					} else if( setMode == SetMode.ARENA ){
						firArena.setStatusBarText(Language.MSG_ERROR_FAILED_SET);
					}
				}
			}
		});
		jrbHumenFirst.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeuristicButtonEnable();
			}
		});
		jrbAIFirst.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setHeuristicButtonEnable();
			}
		});
		chooseFileButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(fileChooser.showOpenDialog(SetDialog.this)
						== JFileChooser.APPROVE_OPTION){
					chessFileField.setText( fileChooser.getSelectedFile().getAbsolutePath() );
				}
			}
		});
	}
	
	private void initializeHumanAI(){
		
		
		jpHumanAIMode.setLayout(null);
		
		jrbAIFirst.setBounds(70, 10, 120, 30);
		jrbHumenFirst.setBounds( 200, 10, 120, 30);

		jlId.setBounds(35, 50, 100, 30);
		jlName.setBounds(35, 80, 100, 30);
		jlNick.setBounds(35, 110, 100, 30);
		jtfId.setBounds(135, 52, 150, 24);
		jtfName.setBounds(135, 82, 150, 24);
		jtfNick.setBounds(135, 112, 150, 24);
		
		

		chooseOfHeLabel.setBounds(35, 140, 100, 30);
		ofHeuristic1.setBounds(135, 140, 100, 30);
		ofHeuristic2.setBounds(235, 140, 100, 30);
		
		chooseDeHeLabel.setBounds(35, 170, 100, 30);
		deHeuristic1.setBounds(135, 170, 100, 30);
		deHeuristic2.setBounds(235, 170, 100, 30);
		
		levelLabel.setBounds(35, 200, 100, 30);
		level1RB.setBounds(35, 220, 100, 30);
		level2RB.setBounds(135, 220, 100, 30);
		level3RB.setBounds(235, 220, 100, 30);
		
		chessFileLabel.setBounds(35, 250, 100, 30);
		chessFileField.setBounds(135, 252, 150, 26);
		chooseFileButton.setBounds(295, 255, 70, 20);
		addSameComponent( 1 );
		setHeuristicButtonEnable();
		
		this.bgFirst.add(jrbHumenFirst);
		this.bgFirst.add(jrbAIFirst);
		jrbAIFirst.setSelected(true);
		

	}
	private void initializeAIAI(){
		jpAIAIMode.setLayout(null);
		//addSameComponent( 2 );
		
	}
	
	private void addSameComponent( int mode ){

		JPanel jp = jpHumanAIMode;
		if( mode == 2  ) jp = jpAIAIMode;
		

		jp.add(jrbAIFirst);
		jp.add(jrbHumenFirst);
		
		jp.add(jlId);
		jp.add(jtfId);
		jp.add(jlName);
		jp.add(jtfName);
		jp.add(jlNick);
		jp.add(jtfNick);
		
		
		jp.add( chooseOfHeLabel );
		jp.add( ofHeuristic1 );
		jp.add( ofHeuristic2 );
		jp.add( chooseDeHeLabel );
		jp.add( deHeuristic1 );
		jp.add( deHeuristic2 );
		jp.add( levelLabel );
		jp.add( level1RB );
		jp.add( level2RB );
		jp.add( level3RB );
		jp.add( chessFileLabel );
		jp.add( chessFileField );
		jp.add( chooseFileButton );
		
	}
	
	private void setHeuristicButtonEnable(){
		if( jrbHumanAIMode.isSelected() ){
			jrbHumenFirst.setEnabled(true);
			jrbAIFirst.setEnabled(true);
			jtfId.setEnabled(true);
			jtfName.setEnabled(true);
			jtfNick.setEnabled(true);
			
			
			if( jrbHumenFirst.isSelected() ){
				ofHeuristic1.setEnabled(false);
				ofHeuristic2.setEnabled(false);
				deHeuristic1.setEnabled(true);
				deHeuristic2.setEnabled(true);
			} else if( jrbAIFirst.isSelected() ){
				ofHeuristic1.setEnabled(true);
				ofHeuristic2.setEnabled(true);
				deHeuristic1.setEnabled(false);
				deHeuristic2.setEnabled(false);
			}
		} else if( jrbAIAIMode.isSelected() ){
			

			jrbHumenFirst.setEnabled(false);
			jrbAIFirst.setEnabled(false);
			jtfId.setEnabled(false);
			jtfName.setEnabled(false);
			jtfNick.setEnabled(false);
			
			ofHeuristic1.setEnabled(true);
			ofHeuristic2.setEnabled(true);
			deHeuristic1.setEnabled(true);
			deHeuristic2.setEnabled(true);
		}
	}
	
	private void handleApplyButton() throws Exception{
		
		int level = 1;
		if( level2RB.isSelected() ){
			level = 2;
		} else if(level3RB.isSelected()){
			level = 3;
		}
		int ofhu = 1, dehu = 1;
		if( ofHeuristic2.isSelected() ) ofhu = 2;
		if( deHeuristic2.isSelected() ) dehu = 2;
		String chess = chessFileField.getText();
		
		if( jrbHumanAIMode.isSelected() ){
			if( setMode == SetMode.JUDGE ) {
				if(jrbHumenFirst.isSelected()){
					SetDialog.this.firServer.setMode(Mode.HUMAN_VS_AI, ofhu, dehu, level,chess );
				} else {
					SetDialog.this.firServer.setMode(Mode.AI_VS_HUMAN, ofhu, dehu, level,chess);
				}
				SetDialog.this.firServer.setHumanInfo(jtfId.getText(), jtfName.getText(), jtfNick.getText());
			}
		} else if( jrbAIAIMode.isSelected() ){
			if( setMode == SetMode.JUDGE ) {
				SetDialog.this.firServer.setMode(Mode.AI_VS_AI, ofhu, dehu, level,chess);
			} else if( setMode == SetMode.ARENA ){
				SetDialog.this.firArena.setInningInfo(ofhu, dehu, level,chess);
			}
		//	JOptionPane.showMessageDialog(null, "Mode: AI VS AI");
		} /*else if( jrbServerMode.isSelected() ){
		//	SetDialog.this.firServer.setMode(Mode.SERVER);
		//	JOptionPane.showMessageDialog(null, "Mode: Server");
		}//*/
		SetDialog.this.setVisible(false);
	}
	private void handleStartButton() throws Exception{
		handleApplyButton();
		if( setMode == SetMode.JUDGE ) {
			SetDialog.this.firServer.executeByOK();			
		} else if ( setMode == SetMode.ARENA ){
			SetDialog.this.firArena.executeByOK();
		}
	}
}









