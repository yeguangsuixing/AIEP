package com.njucs.aiep.plugin.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.njucs.aiep.Language;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.ui.AIEPPromptDialog;


public class AIInfoTablePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6003889051490950471L;
	
	private Date updateDate = Calendar.getInstance().getTime();
	private AIInfoTableModel allInfoTableModel = new AIInfoTableModel();
	
	private JLabel updateTimeLabel = new JLabel(  );
	private JTable infoTable = new JTable(allInfoTableModel);
	
	private boolean isload = false;
	
	public AIInfoTablePanel(){

		this.setLayout(new BorderLayout());
		this.add( updateTimeLabel, BorderLayout.NORTH );
		this.add(new JScrollPane(infoTable), BorderLayout.CENTER);
		updateTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.setBackground(Color.GRAY);

		infoTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		infoTable.setRowHeight( 25 );
		
		//this.setUI(javax.swing.UIManager.getUI(this));
	}
	

	public void setAIInfoList(List<AIInfo> aiInfoList){
		if( allInfoTableModel.allAIInfoList == null ) {
			this.allInfoTableModel.setAIInfoList(aiInfoList);
		} else {
			this.allInfoTableModel.allAIInfoList.clear();
			this.allInfoTableModel.allAIInfoList.addAll( aiInfoList );
		}
		if( ! isload ){
			isload = true;
			SwingUtilities.updateComponentTreeUI(this);
		}
		updateUI();
		System.out.println( "[com.njucs.aiep.plugin.ui.AIInfoTablePanel]allAIIInfoList.size="+aiInfoList.size() );
	}
	
	public void updateUI(){
		super.updateUI();
		this.updateDate = new Date();//Calendar.getInstance().getTime();
		if( this.updateTimeLabel != null ) {
			this.updateTimeLabel.setText( 
				String.format(Language.STR_UPDATE_TIME, 
						Language.STR_TIME_FORMAT.format(updateDate)
				)
			);
			this.updateTimeLabel.invalidate();
		}
		if( this.infoTable != null ) {
			AIEPPromptDialog.fitTableColumns( infoTable );//
			this.infoTable.invalidate();
		}
		this.invalidate();
		this.update(getGraphics());
	}
	
}


class AIInfoTableModel implements TableModel {
	
	List<AIInfo> allAIInfoList = null;
	
	private static String[] tableHeadArray = new String[]{
			"Index",
			"Identity",
			"Name",
			"Nickname",
			"Version",
			"Source File",
			"Destination File",
			"Upload Time"
	};

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if( allAIInfoList == null || allAIInfoList.size() == 0) return null;
		AIInfo aiInfo = allAIInfoList.get(rowIndex);
		switch( columnIndex ){
		case 0:  return rowIndex+1;
		case 1: return aiInfo.getId();
		case 2: return aiInfo.getName();
		case 3: return aiInfo.getNickname();
		case 4: return aiInfo.getVersion();
		case 5: return aiInfo.getSrcFileName();
		case 6: return aiInfo.getJarFileName();
		case 7: return Language.STR_TIME_FORMAT.format(aiInfo.getFileRecvTime());
		}
		return null;
	}
	@Override
	public void addTableModelListener(TableModelListener l) {
		return;
	}
	
	public void setAIInfoList(List<AIInfo> allAIInfoList){
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


