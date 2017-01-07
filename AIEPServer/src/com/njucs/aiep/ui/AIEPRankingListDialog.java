package com.njucs.aiep.ui;

import java.awt.BorderLayout;
import java.util.ArrayList;
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
import com.njucs.aiep.frame.AIInfo;

public class AIEPRankingListDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2027087526476058827L;

	private Date updateDate = Calendar.getInstance().getTime();
	private RankingTableModel rankingListModel = new RankingTableModel();
	
	private JLabel updateTimeLabel = new JLabel(  );
	private JTable rankTable = new JTable(rankingListModel);
	
	public AIEPRankingListDialog(JFrame owner){
		super( owner );
		//System.out.println( owner );

		this.setTitle(Language.DLG_RANK_TITLE);
		this.setSize(400, 400);
		this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
		
		updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		updateTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		rankTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		rankTable.setRowHeight( 25 );
		rankTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		//rankTable.getColumnModel().getColumn(0).setWidth(20);
		//rankTable.set
		//rankTable.setShowGrid( false );
		
		this.setLayout(new BorderLayout());
		this.add( updateTimeLabel, BorderLayout.NORTH );
		this.add(new JScrollPane(rankTable), BorderLayout.CENTER);
	}
	
	public void setRankingList(List<AIInfo> sortedAIInfoList){
		if( this.rankingListModel.allAIInfoList == null ) {
			this.rankingListModel.setRankList(sortedAIInfoList);
		} else {
			this.rankingListModel.allAIInfoList.clear();
			this.rankingListModel.allAIInfoList.addAll(sortedAIInfoList);
		}
		this.updateDate = Calendar.getInstance().getTime();
		this.updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		this.updateTimeLabel.invalidate();
		AIEPPromptDialog.fitTableColumns( rankTable );
		this.rankTable.invalidate();
		this.invalidate();
		//this.update(getGraphics());
		this.repaint();
		System.out.println( "sortedAIInfoList.size="+sortedAIInfoList.size() );
	}
	
	public static void main(String[] args){
		AIEPRankingListDialog d = new AIEPRankingListDialog(null);
		List<AIInfo> list = new ArrayList<AIInfo>();
		list.add( new AIInfo("101220105", "tqc", "ygsx", "v0.1.1.1") );
		list.add( new AIInfo("101220107", "tms", "shenghei", "v0.1.2.1") );
		list.add( new AIInfo("101220105", "tqc", "ygsx", "v0.1.1.1") );
		list.add( new AIInfo("101220107", "tms", "shenghei", "v0.1.2.1") );
		list.add( new AIInfo("101220105", "tqc", "ygsx", "v0.1.1.1") );
		list.add( new AIInfo("101220107", "tms", "shenghei", "v0.1.2.1") );
		list.add( new AIInfo("101220105", "tqc", "ygsx", "v0.1.1.1") );
		list.add( new AIInfo("101220107", "tms", "shenghei", "v0.1.2.1") );
		d.setRankingList(list);
		d.setDefaultCloseOperation( JFrame.DISPOSE_ON_CLOSE );
		d.setVisible(true);
		List<AIInfo> list2 = new ArrayList<AIInfo>();
		list2.add( new AIInfo("101220105", "tqc", "ygsx", "v0.1.1.1") );
		list2.add( new AIInfo("101220107", "tms", "shenghei", "v0.1.2.1") );
		d.setRankingList(list2);
		//list.clear();
		//list.add( new AIInfo("101220107", "tms", "shenghei", "v0.1.2.1") );
	}
}

class RankingTableModel implements TableModel {
	
	List<AIInfo> allAIInfoList = null;
	
	private static String[] tableHeadArray = new String[]{
			"Index",
			"Nickname",
			"Version",
			"Source File",
			"Upload Time",
			"Total Count",
			"Winning Count",
			"Winning Rate"
	};
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		return;
	}
	
	public void setRankList(List<AIInfo> allAIInfoList){
		this.allAIInfoList = allAIInfoList;
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
		if( allAIInfoList == null ) return 0;
		else return allAIInfoList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if( allAIInfoList == null || allAIInfoList.size() == 0) return null;
		AIInfo aiInfo = allAIInfoList.get(rowIndex);
		switch( columnIndex ){
		case 0:  return rowIndex+1;
		case 1: return aiInfo.getNickname();
		case 2: return aiInfo.getVersion();
		case 3: return aiInfo.getSrcFileName();
		case 4: return Language.STR_TIME_FORMAT.format(aiInfo.getFileRecvTime());
		case 5: return aiInfo.getTotalCount();
		case 6: return aiInfo.getWinCount();
		case 7: return aiInfo.getWinCount()/(float)(aiInfo.getTotalCount());
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

