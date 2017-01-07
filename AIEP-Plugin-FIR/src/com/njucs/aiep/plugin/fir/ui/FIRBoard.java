package com.njucs.aiep.plugin.fir.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import com.njucs.aiep.game.GameTreePanel;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.plugin.fir.AIEPP_FIR;
import com.njucs.aiep.plugin.fir.FIRJudge;
import com.njucs.aiep.plugin.fir.frame.Inning;
import com.njucs.aiep.plugin.fir.frame.Mode;
import com.njucs.aiep.plugin.fir.frame.Step;
import com.njucs.aiep.plugin.fir.frame.Inning.PartInfo;
import com.njucs.aiep.plugin.ui.DoubleBufferedPanel;


/**
 * Five In Row Board
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ6ÈÕ0:13:15
 * */
public class FIRBoard extends DoubleBufferedPanel {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4772445162598681470L;
	
	public final static int RADIUS = 18;
	
	public final static int START_X = 120, START_Y = 20;

	private final static int OFFENSIVE_INFO_START_X = 10;
	private final static int DEFENSIVE_INFO_START_X = 
		START_X+RADIUS*2*(AIEPP_FIR.DIMENSION-1)+OFFENSIVE_INFO_START_X;
	
	private final static int INFO_ID_START_Y = 100;
	private final static int INFO_NAME_START_Y = 130;
	private final static int INFO_NICK_START_Y = 160;

	private FIRJudge server;
	
	private Inning inning;
	
	private Status currentStatus = Status.EMPTY, winStatus = Status.EMPTY;
	
	public final static  Color BG_COLOR = GameTreePanel.BG_COLOR;
	
	public FIRBoard(FIRJudge server){
		this.server = server;
		this.setBackground( BG_COLOR );
		this.setDoubleBuffered(true);
		
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent e) {
				if( inning == null || winStatus != Status.EMPTY) return;
				if( inning.getMode() == Mode.AI_VS_HUMAN  && inning.getNext() == Status.DEFENSIVE 
						|| inning.getMode() == Mode.HUMAN_VS_AI && inning.getNext() ==Status.OFFENSIVE ){
					clickHandler( e.getX(), e.getY() );
				} else {
					//JOptionPane.showMessageDialog( null, "mode="+innings.getMode()+", next="+innings.getNext() );
				}
				//JOptionPane.showMessageDialog(null, e.getX()+","+e.getY());
			}
		});
	}
	public FIRBoard(FIRJudge server, Inning inning){
		this(server);
		this.setInning(inning);
	}
	public void setInning(Inning inning){
		this.inning = inning;
	}
	
	public void turn(Status status){
		this.currentStatus = status;
	}
//*
	public void win(PartInfo winnerInfo){
		this.winStatus = winnerInfo.getStatus();
	}//*/
	
/*
	protected void paintComponent(Graphics g){
		super.paintComponents(g);

		g.drawImage(offImage, 0, 0, this);
		//paintComponent0(g);
	}//*/

	protected void paint0(Graphics g){
		if( g == null ) return;
		for( int i = 0; i < AIEPP_FIR.DIMENSION; i ++ ){
			g.drawLine(START_X, i*RADIUS*2+START_Y, START_X+(AIEPP_FIR.DIMENSION-1)*RADIUS*2, i*RADIUS*2+START_Y);//row
			g.drawLine(i*RADIUS*2+START_X, START_Y, START_X+i*RADIUS*2, START_Y+(AIEPP_FIR.DIMENSION-1)*RADIUS*2  );//col
		}
		if( this.inning != null ){
			for( int i = 0; i < AIEPP_FIR.DIMENSION; i ++ ){
				for( int j = 0; j < AIEPP_FIR.DIMENSION; j ++ ){
					Status st = this.inning.getBoardStatus(i, j);
					if( st == Status.OFFENSIVE ) {
						drawPiece( g, i, j, Color.WHITE );
					} else if( st == Status.DEFENSIVE ) {
						drawPiece( g, i, j, Color.BLACK );
					}
				}
			}
			Step lastStep = this.inning.getLastStep();
			if( lastStep != null ) {
				drawLastPieceSurrinding( g, lastStep.getX(), lastStep.getY() );
			}
			g.setColor(Color.BLACK);
			PartInfo info = this.inning.getOffensiveInfo();
			if( info != null ){
				g.drawString(  String.valueOf(info.getId()), OFFENSIVE_INFO_START_X, INFO_ID_START_Y);
				g.drawString( String.valueOf( info.getName()), OFFENSIVE_INFO_START_X, INFO_NAME_START_Y);
				g.drawString(String.valueOf(  info.getNickname()), OFFENSIVE_INFO_START_X, INFO_NICK_START_Y);
			}
			info = this.inning.getDefensiveInfo();
			if( info != null ){
				g.drawString( String.valueOf( info.getId()), DEFENSIVE_INFO_START_X, INFO_ID_START_Y);
				g.drawString( String.valueOf( info.getName()), DEFENSIVE_INFO_START_X, INFO_NAME_START_Y);
				g.drawString( String.valueOf( info.getNickname()), DEFENSIVE_INFO_START_X, INFO_NICK_START_Y);
			}
		}
		
	}
	
	private void drawPiece( Graphics g, int x, int y, Color color ){
		int sx = START_X + x*RADIUS*2 - RADIUS;
		int sy = START_Y + y*RADIUS*2 - RADIUS;
		g.setColor(color);
		g.fillOval(sx, sy, RADIUS*2, RADIUS*2);
	}
	private void drawLastPieceSurrinding( Graphics g, int x, int y ){
		int sx = START_X + x*RADIUS*2 - RADIUS;
		int sy = START_Y + y*RADIUS*2 - RADIUS;
		g.setColor(Color.BLUE);
		Graphics2D g2d = (Graphics2D)g;
		Stroke stroke = g2d.getStroke();
		g2d.setStroke(new BasicStroke( 2.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		drawPieceSurrinding( g2d, sx, sy );
		g2d.setStroke(stroke);//restore
	}
	private void drawPieceSurrinding( Graphics2D g2d, int sx, int sy ){
		final int len = 5;
		g2d.drawLine( sx, sy, sx + len, sy );
		g2d.drawLine( sx, sy, sx, sy + len );
		
		g2d.drawLine( sx + RADIUS*2, sy, sx + RADIUS*2-len, sy );
		g2d.drawLine( sx + RADIUS*2, sy, sx + RADIUS*2, sy + len);
		

		g2d.drawLine( sx, sy + RADIUS*2, sx, sy + RADIUS*2-len );
		g2d.drawLine( sx, sy + RADIUS*2, sx+len, sy + RADIUS*2 );
		
		g2d.drawLine( sx + RADIUS*2, sy + RADIUS*2, sx + RADIUS*2-len, sy + RADIUS*2 );
		g2d.drawLine( sx + RADIUS*2, sy + RADIUS*2, sx + RADIUS*2, sy + RADIUS*2-len );
	}
	
	private void clickHandler( int x, int y ){
		if( x + RADIUS - START_X < 0 ) return;
		if( y + RADIUS - START_Y < 0 ) return;
		int ix = (x + RADIUS - START_X)/(RADIUS*2);
		int iy = (y + RADIUS - START_Y)/(RADIUS*2);
		if( server != null ){
			server.turnToAI( new Step( this.currentStatus, ix, iy ) );
		}
	}
	public void clear() {
		winStatus = Status.EMPTY;
	}
	
}




