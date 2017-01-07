package com.njucs.aiep.game;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.util.AbstractList;

import javax.swing.JPanel;
import javax.swing.JScrollBar;

public class GameTreePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3456646431221114254L;

	private static final int RADIUS = 18, UINT_HEIGHT = 100, UINT_WIDTH = 100, V_SPAN = 50;//, H_SPAN = 50;
	private GameTree gameTree;
	private int gameTreeHeight = 0, gameTreeWidth = 0;
	private int indexX = 0, indexY = 0;
	private InnerPanel innerPanel = new InnerPanel();
	private JScrollBar vScrollBar = new JScrollBar( JScrollBar.VERTICAL );
	private JScrollBar hScrollBar = new JScrollBar( JScrollBar.HORIZONTAL );
	
	
	public final static Color BG_COLOR = new Color( 185, 122, 87 );
	
	public final static Color DE_COLOR = Color.GRAY, OF_COLOR = Color.WHITE, VALUE_COLOR = Color.CYAN;
	
	private final static int SCROLL_MIN = 0, SCROLL_MAX = 100;
	
	public GameTreePanel(){
		
		this.setLayout(new BorderLayout());
		this.add( innerPanel, BorderLayout.CENTER );
		this.add( vScrollBar, BorderLayout.EAST );
		this.add( hScrollBar, BorderLayout.SOUTH );
		this.vScrollBar.addAdjustmentListener(new AdjustmentListener(){
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				innerPanel.updateUI();
			}
		});
		this.hScrollBar.addAdjustmentListener(new AdjustmentListener(){
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				innerPanel.updateUI();
			}
		});
	}
	
	public void updateByGameTree(GameTree gameTree){
		this.gameTree = gameTree;
		if( gameTree == null ){
			this.gameTreeHeight = 0;
			this.gameTreeWidth = 0;
		} else {
			this.gameTreeHeight = gameTree.getHeight();
			this.gameTreeWidth = gameTree.getWidth();
		}
		this.invalidate();
		this.updateUI();
	}
	
	public void updateUI(){
		super.updateUI();
		if( this.innerPanel != null ){
			this.innerPanel.updateUI();
		}
		if( this.hScrollBar != null ){
			this.hScrollBar.updateUI();
		}
		if( this.vScrollBar != null ){
			this.vScrollBar.updateUI();
		}
	}
	
	
	private class InnerPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1603723782092698729L;

		public InnerPanel(){
			this.setBackground( BG_COLOR );
		}
		
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			//System.out.println( "paint" );
			
			int totalheight = V_SPAN*2 + UINT_HEIGHT*gameTreeHeight;
			int totalWidth = UINT_WIDTH*gameTreeWidth;
			int verticalDelta = totalheight - getHeight();
			int horizontalDelta = totalWidth - getWidth();
			int newVerticalValue = 0, newVerticalExtent = 10;
			int newHorizontalValue = 0, newHorizontalExtent = 10;
			//vertical
			if( vScrollBar != null ) {
				newVerticalValue = vScrollBar.getValue();//*
				if( totalheight > getHeight() ){
					//newVerticalExtent =getHeight()*SCROLL_MAX/(totalheight+getHeight());
					newVerticalExtent = getHeight()*SCROLL_MAX/totalheight;
					if( newVerticalValue > SCROLL_MAX - newVerticalExtent ){
						newVerticalValue = SCROLL_MAX - newVerticalExtent;//newValue = newExtent - newExtent;
					}
					if( newVerticalValue < 0 ) newVerticalValue = 0;
				} else {
					newVerticalExtent = SCROLL_MAX;
					newVerticalValue = 0;
				}
				vScrollBar.setValues(newVerticalValue, newVerticalExtent, SCROLL_MIN, SCROLL_MAX);//*/
				/*System.out.println( "newVerticalValue = "+newVerticalValue +
						", newVerticalExtent = "+newVerticalExtent+
						", totalheight= "+totalheight +
						", getHeight() = "+ getHeight() );//*/
			}
			if( verticalDelta > 0 && SCROLL_MAX > newVerticalExtent){
				indexY = - verticalDelta*newVerticalValue/(SCROLL_MAX-newVerticalExtent);
			} else {
				indexY = 0;
			}
			//horizontal
			if( hScrollBar != null ) {
				newHorizontalValue = hScrollBar.getValue();
				if( totalWidth > getWidth() ){
					//newHorizontalExtent = getWidth()*SCROLL_MAX/( totalWidth + getWidth() );
					newHorizontalExtent = getWidth()*SCROLL_MAX/( totalWidth );
					if( newHorizontalValue > SCROLL_MAX - newHorizontalExtent ){
						newHorizontalValue = SCROLL_MAX - newHorizontalExtent;
					}
				} else {
					newHorizontalExtent = SCROLL_MAX;
					newHorizontalValue = 0;
				}
				hScrollBar.setValues(newHorizontalValue, newHorizontalExtent, SCROLL_MIN, SCROLL_MAX);//*/
			}
			if( horizontalDelta > 0 && SCROLL_MAX > newHorizontalExtent){
				indexX = - horizontalDelta*newHorizontalValue/(SCROLL_MAX-newHorizontalExtent);
			} else {
				indexX = 0;
			}
			
			
			if( gameTree != null ) {
				Color c = OF_COLOR;
				if( gameTree.getStatus() == Status.DEFENSIVE ) c = DE_COLOR;
				//indexX = 0; paintNode( g, gameTree, 0, getWidth() - H_SPAN*2, 0, 0, 0, c );
				paintNode( g, gameTree, indexX, gameTreeWidth*UINT_WIDTH, 0, 0, 0, c );
			}
		}
		
		private void paintNode( Graphics g, GameTree node,int xindex, int width, int height, int parentX, int parentY, Color color ){
			if( node == null ) {
				return;
			}
			int newx = xindex+width/2, newy = indexY + V_SPAN+height;
			if( node.getName() != null && node.getName().length() > 0 ){
				
				
				AbstractList<GameTree> children = node.getTreeChildren();
				int size = children.size();
				if( size > 0 ) {
					int newwidth = width / size;
					int newheight = height + UINT_HEIGHT;
					int count = 0;
					Color newc = color==DE_COLOR?OF_COLOR:DE_COLOR;
					for( GameTree child : children  ){
						int nx = xindex+newwidth*count+newwidth/2, ny = indexY + V_SPAN+newheight;
						g.drawLine( nx, ny, newx, newy );
						paintNode( g, child, xindex+newwidth*count, newwidth, newheight, newx, newy, newc );
						count++;
					}
				}
				drawPiece( g, newx, newy, color );
				
				int strw = xindex+width/2+RADIUS+5, strh = indexY + V_SPAN+height+10;
				Color tempc = g.getColor();
				g.setColor(VALUE_COLOR);
				if( node.getName() != null ){
					g.drawString(node.getName()+"("+node.getValue()+")", strw, strh);
				} else {
					g.drawString(node.getName(), strw, strh);
				}
				g.setColor(tempc);
				
			} else {
				drawPiece( g, newx, newy, Color.GREEN );
			}
		}
		
		private void drawPiece( Graphics g, int x, int y, Color color ){
			int sx = x - RADIUS;
			int sy = y - RADIUS;
			Color c = g.getColor();
			g.setColor(color);
			g.fillOval(sx, sy, RADIUS*2, RADIUS*2);
			g.setColor(c);
		}
		
	}
	
}
