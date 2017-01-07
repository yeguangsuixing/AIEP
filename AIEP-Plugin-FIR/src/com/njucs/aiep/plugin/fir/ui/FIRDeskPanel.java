package com.njucs.aiep.plugin.fir.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import com.njucs.aiep.Language;
import com.njucs.aiep.plugin.fir.FIRSite;
import com.njucs.aiep.plugin.fir.frame.AIWrapper;
import com.njucs.aiep.plugin.fir.frame.FIRDesk;

public class FIRDeskPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6852072960281451172L;

	private FIRSite firSite = null;

	private Date updateDate = Calendar.getInstance().getTime();
	private FIRDeskTableModel firDeskTableModel = new FIRDeskTableModel();
	
	private JLabel updateTimeLabel = new JLabel(  );
	private JTable firDeskTable = new JTable(firDeskTableModel);
	
	private JPopupMenu popupMenu = null;
	
	private List<FIRDesk> deskList = null;
	
	
	public FIRDeskPanel( FIRSite firSite){
		this.firSite = firSite;

		updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		updateTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		firDeskTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		firDeskTable.setRowHeight( 25 );
		
		this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		this.setLayout(new BorderLayout());
		this.add( updateTimeLabel, BorderLayout.NORTH );
		this.add(new JScrollPane(firDeskTable), BorderLayout.CENTER);
		
		//firDeskTable.setRowSelectionAllowed( true );
		firDeskTable.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				//popupMenu.setVisible(false);
				if( e.getButton() == MouseEvent.BUTTON1 ){
					if( e.getClickCount() != 2 || deskList == null ){ return; }
					FIRDesk desk = deskList.get( firDeskTable.getSelectedRow() );
					FIRDeskPanel.this.firSite.enterDeskRequest( desk.getDeskId() );
				} else if ( e.getButton() == MouseEvent.BUTTON3 ) {//right mouse button
					if( popupMenu != null ){
					//	popupMenu.setLocation( e.getXOnScreen(), e.getYOnScreen() );
						popupMenu.requestFocus();
					//	popupMenu.setVisible(true);
						popupMenu.show(firDeskTable, e.getX(), e.getY());
					}
				}
			}
		});
	}
	
	public void setEnable(boolean enable){
		firDeskTable.setEnabled(enable);
	}
	
	public void setPopupMenu( JPopupMenu popupmenu ){
		this.popupMenu = popupmenu;
	}
	
	public int getTableSeelctedRow() {
		return firDeskTable.getSelectedRow();
	}
	
	/**
	 * @param deskList the deskList to set
	 */
	public void setDeskList(List<FIRDesk> deskList) {
		this.deskList = deskList;
		this.firDeskTableModel.setDeskList(deskList);
		this.updateDate = Calendar.getInstance().getTime();
		this.updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		updateUI();
		//System.out.println( "setCompetitionList" );
	}

	

	public void updateUI(){
		super.updateUI();
		if( this.firDeskTable != null ) {			
			this.firDeskTable.invalidate();
		}
		this.invalidate();
	//	this.update(getGraphics());
	}
}

class FIRDeskTableModel  implements TableModel {
	
	List<FIRDesk> deskList = null;
	
	private static String[] tableHeadArray = new String[]{
			"Desk Index",
			"Offensive",
			"Defensive"
	};
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		return;
	}
	
	public void setDeskList(List<FIRDesk> deskList) {
		this.deskList = deskList;
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
		if( deskList == null ) return 0;
		else return deskList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if( deskList == null || deskList.size() == 0) return null;
		FIRDesk desk = deskList.get(rowIndex);
		switch( columnIndex ){
		case 0:  return desk.getDeskId();
		case 1: 
			if( desk.getWrapper1() != null ){
				AIWrapper wrapper = desk.getWrapper1();
				if( wrapper.getAiInfo() != null ){
					return wrapper.getAiInfo().getNickname();
				}
			}
			return null;
		case 2: 
			if( desk.getWrapper2() != null ){
				AIWrapper wrapper = desk.getWrapper2();
				if( wrapper.getAiInfo() != null ){
					return wrapper.getAiInfo().getNickname();
				}
			}
			return null;
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


