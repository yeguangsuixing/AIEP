package com.njucs.aiep.ui;


import java.awt.BorderLayout;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.njucs.aiep.Language;

/**
 * Data Check Dialog
 * 
 * @author ygsx
 * 
 * @version 0.1
 * 
 * @time 2013年5月4日22:16:41
 * 
 * */
public class AIEPPromptDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3648549396432258426L;

	private AbstractList<Prompt>promptList = new ArrayList<Prompt>();
	

	private Date updateDate = Calendar.getInstance().getTime();
	private PromptTableModel promptModel = new PromptTableModel();
	
	private JLabel updateTimeLabel = new JLabel(  );
	private JTable promptTable = new JTable(promptModel);
	
	
	public AIEPPromptDialog(JFrame source){
		super(source);
		//System.out.println("#source: "+source);
		if( source == null )
		throw new RuntimeException("source == null");

		this.setTitle(Language.DLG_PROMPT_TITLE);
		this.setSize(600, 400);
		this.setLocationRelativeTo(null);// locate the dialog in the center of the screen
		
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); 
		
		updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		updateTimeLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		//promptTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		//promptTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		fitTableColumns( promptTable );

		promptTable.setRowHeight( 25 );
		TableColumnModel tcm = promptTable.getColumnModel();
		tcm.getColumn(0).setPreferredWidth (50);
		tcm.getColumn(1).setPreferredWidth (140);
		tcm.getColumn(2).setPreferredWidth (400);
		promptTable.setColumnModel(tcm);
		
		this.setLayout(new BorderLayout());
		this.add( updateTimeLabel, BorderLayout.NORTH );
		this.add(new JScrollPane(promptTable), BorderLayout.CENTER);
		
		promptModel.setPromptList(promptList);
	}
	
	public void addPrompt(String prompt){
		promptList.add( new Prompt(new Date(), prompt ) );
		updateUI();
		System.out.println( "prompt:"+prompt );
	}
	
	
	public void updateUI(){
		fitTableColumns( promptTable );
		this.updateDate = new Date();
		updateTimeLabel.setText( 
			String.format(Language.STR_UPDATE_TIME, 
					Language.STR_TIME_FORMAT.format(updateDate)
			)
		);
		if( this.promptTable != null ){
			this.promptTable.invalidate();
		}
		this.invalidate();
		this.update(getGraphics());
	}
	
	/**
	 * copy by Internet
	 * */
	public static void fitTableColumns(JTable myTable) {
        myTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        JTableHeader header = myTable.getTableHeader();
        int rowCount = myTable.getRowCount();
        Enumeration<?> columns = myTable.getColumnModel().getColumns();
        while(columns.hasMoreElements()) {
            TableColumn column = (TableColumn)columns.nextElement();
            int col = header.getColumnModel().getColumnIndex(column.getIdentifier());
            int width = (int)header.getDefaultRenderer().getTableCellRendererComponent
            (myTable, column.getIdentifier(), false, false, -1, col).getPreferredSize().getWidth();
            for(int row = 0; row < rowCount; row++) {
                int preferedWidth = (int)myTable.getCellRenderer(row, col).getTableCellRendererComponent
                (myTable, myTable.getValueAt(row, col), false, false, row, col).getPreferredSize().getWidth();
                width = Math.max(width, preferedWidth);
            }
            header.setResizingColumn(column); // 此行很重要
            column.setWidth(width+myTable.getIntercellSpacing().width);
        }
   }

	
}


class Prompt {
	Date date;
	String prompt;
	
	public Prompt(Date date,String prompt ){
		this.date = date;
		this.prompt = prompt;
	}
}
class PromptTableModel extends DefaultTableModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1217995357777685483L;

	AbstractList<Prompt>promptList = null;
	
	private static String[] tableHeadArray = new String[]{
			"Index",
			"Time",
			"Prompt",
	};
	
	@Override
	public void addTableModelListener(TableModelListener l) {
		return;
	}
	
	public void setPromptList(AbstractList<Prompt>promptList){
		this.promptList = promptList;
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
		if( promptList == null ) return 0;
		else return promptList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if( promptList == null || promptList.size() == 0) return null;
		Prompt prompt = promptList.get(rowIndex);
		switch( columnIndex ){
		case 0:  return rowIndex+1;
		case 1: return Language.STR_TIME_FORMAT.format(prompt.date);
		case 2: return prompt.prompt;
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




