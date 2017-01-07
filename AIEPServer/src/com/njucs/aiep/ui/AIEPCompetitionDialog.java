package com.njucs.aiep.ui;

import java.awt.BorderLayout;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.njucs.aiep.Language;
import com.njucs.aiep.frame.Competition;

public class AIEPCompetitionDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2027087526476058827L;

	private Date updateDate = Calendar.getInstance().getTime();
	private CompetitionTableModel competitionListModel = new CompetitionTableModel();
	
	private JLabel updateTimeLabel = new JLabel(  );
	private JTable competitionTable = new JTable(competitionListModel);
	
	public AIEPCompetitionDialog(JFrame owner){
		super( owner );
		
		this.setTitle(Language.DLG_COMPETITION_TITLE);
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
		
		updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		updateTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		competitionTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		competitionTable.setRowHeight( 25 );
		
		this.setLayout(new BorderLayout());
		this.add( updateTimeLabel, BorderLayout.NORTH );
		this.add(new JScrollPane(competitionTable), BorderLayout.CENTER);
	}
	
	public void setCompetitionList(List<Competition> competitionList){
		if( this.competitionListModel.competitionList == null ) {
			this.competitionListModel.setCompetitionList(competitionList);
		} else {
			this.competitionListModel.competitionList.clear();
			this.competitionListModel.competitionList.addAll(competitionList);
		}
		this.updateDate = Calendar.getInstance().getTime();
		this.updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		updateUI();
		System.out.println( "setCompetitionList" );
	}
	
	public void updateUI(){
		System.out.println( "AIEPCompetitionDialog::updateUI(), size="+
				this.competitionListModel.competitionList.size() );
		this.updateDate = Calendar.getInstance().getTime();
		this.updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		this.updateTimeLabel.invalidate();
		AIEPPromptDialog.fitTableColumns( competitionTable );
		this.invalidate();
		this.update(getGraphics());
		
	}
	
}

class CompetitionTableModel implements TableModel {
	
	List<Competition> competitionList = null;
	
	private static String[] tableHeadArray = new String[]{
			"Index",
			"Offensive",
			"Defensive",
			"Winner",
			"Time",
			"Result",
			"State",
	};
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		return;
	}
	
	public void setCompetitionList(List<Competition> competitionList) {
		this.competitionList = competitionList;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		Object o = getValueAt(0, columnIndex);
		if( o == null ) return Object.class;
		else return o.getClass();
	}

	@Override
	public int getColumnCount() {
		return tableHeadArray.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return tableHeadArray[columnIndex];
	}

	@Override
	public int getRowCount() {
		if( competitionList == null ) return 0;
		else return competitionList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if( competitionList == null || competitionList.size() == 0) return null;
		Competition competition = competitionList.get(rowIndex);
		StringBuilder sb = new StringBuilder();
		switch( columnIndex ){
		case 0:  return rowIndex+1;
		case 1: return competition.getOffensiveInfo().getNickname()+"("+competition.getOffensiveInfo().getVersion()+")";
		case 2: return competition.getDefensiveInfo().getNickname()+"("+competition.getDefensiveInfo().getVersion()+")";
		case 3: 
			if( competition.getWinner() != null ) {
				return competition.getWinner().getNickname()+"("+competition.getWinner().getVersion()+")";
			} else {
				return null;
			}
		case 4: 
			if( competition.getStartTime() != null ){
				sb.append(Language.STR_TIME_FORMAT.format(competition.getStartTime()));
			} else {
				sb.append(Language.TABLE_UNIT_HIATUS);
			}
			sb.append("¡ª");
			if( competition.getEndTime() != null ){
				sb.append(Language.STR_TIME_FORMAT.format(competition.getEndTime()));
			} else {
				sb.append(Language.TABLE_UNIT_HIATUS);
			}
			return sb;
		case 5:
			if( competition.getReuslt() != null && competition.getReuslt().getResultCode() != null ){
				sb.append( competition.getReuslt().getResultCode() );
			} else {
				sb.append( Language.TABLE_UNIT_HIATUS );
			}
			if( competition.getReuslt() != null && competition.getReuslt().getRealTimeResult() !=null ){
				sb.append("(");
				sb.append(competition.getReuslt().getRealTimeResult());
				sb.append(")");
			}
			return sb;
		case 6: return competition.getState();
		}
		return null;
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener l) {
	}

	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		return;
	}
	
}

