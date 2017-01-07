package com.njucs.aiep.plugin.fir.frame;

import java.io.BufferedReader;
import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;

import com.njucs.aiep.base.FileIO;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.net.DataTransmit.Lang;
import com.njucs.aiep.plugin.fir.AIEPP_FIR;


/**
 * 
 * @author ygsx
 * 
 * @time 2013年5月5日23:12:51
 * */
public class Inning implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7941956279604173452L;

	public static enum WIN_TYPE { NORMAL, POSITION_ERROR, TIME_OUT };
	
	public static enum WIN_LINE_DIR{ HORIZONAL, VERTICAL, LT2RB, LB2RT };//LeftTop2RightBottom,LeftBottom2RightTop

	private Mode mode;
	
	private PartInfo part1, part2, winner;
	
	private Step lastStep;
	
	private Status[][]  chessboard = new Status[AIEPP_FIR.DIMENSION][];
	
	private Status next = Status.OFFENSIVE;

	private AbstractList<Step> winStepList = new  ArrayList<Step>();
	
	private AbstractList<Step> stepList = new ArrayList<Step>();

	private WIN_TYPE winType;
	
	private int cppTime, javaTime;
	
	private String chessFile;
	
	public Inning(){}
	
	public Inning(Mode mode, PartInfo offensivePartInfo, PartInfo defensivePartInfo){
		this.mode = mode;
		this.part1 = offensivePartInfo;
		this.part2 = defensivePartInfo;
		
		this.part1.setStatus(Status.OFFENSIVE);
		this.part2.setStatus(Status.DEFENSIVE);
		
		this.clearChessBoard();
		
	}
	
	
	public boolean addStep(Step step){
		if( step == null ) return false;
		if( step.getStatus() != this.next ) return false;
		int ix = step.getX();
		int iy = step.getY();
		if( ix >= 0 && ix < AIEPP_FIR.DIMENSION && iy >= 0 && iy < AIEPP_FIR.DIMENSION ){
			if( this.chessboard[ix][iy] == Status.EMPTY ){
				//this.chessboard[ix][iy] = step.getStatus();
				//use the next line to replace the last line
				this.chessboard[ix][iy] =this.next;
				stepList.add(step);
				lastStep = step;
				
				if( this.next == Status.OFFENSIVE ) this.next = Status.DEFENSIVE;
				else if( this.next == Status.DEFENSIVE ) this.next = Status.OFFENSIVE;
				Status s = win();
				if( s == Status.OFFENSIVE ){
					winner = part1;
				} else if( s == Status.DEFENSIVE ) {
					winner = part2;
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * check whether the Innings finished
	 * <p><strong>copy by Internet</strong></p>
	 * <p><strong>change again at 2013年6月21日21:18:51</strong></p>
	 * @return the winner {{@link Status.OFFENSIVE},{@link Status.DEFENSIVE}, {@link Status.EMPTY} }
	 * */
	private Status  win(){
		Status tmp;
		int k;
		/* 判断所有横行 */
		for (int i = 0; i < AIEPP_FIR.DIMENSION; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION - 4; j++) {
				tmp = chessboard[i][j];
				if (tmp != Status.EMPTY) {
					for (k = 1; k < 5; k++){
						if (chessboard[i][j + k] != tmp) break;
					}
					if (k == 5) {
						setWinStep( i, j, WIN_LINE_DIR.HORIZONAL );
						return tmp;
					}
				}
			}
		}
		/* 判断所有纵行 */
		for (int i = 0; i < AIEPP_FIR.DIMENSION - 4; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION; j++) {
				tmp = chessboard[i][j];
				if (tmp != Status.EMPTY) {
					for (k = 1; k < 5; k++){
						if (chessboard[i + k][j] != tmp) break;
					}
					if (k == 5){
						setWinStep( i, j, WIN_LINE_DIR.VERTICAL );
						return tmp;
					}
				}
			}
		}
		/* 判断所有斜行（撇） */
		for (int i = 4; i < AIEPP_FIR.DIMENSION; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION - 4; j++) {
				tmp = chessboard[i][j];
				if (tmp != Status.EMPTY) {
					for (k = 1; k < 5; k++)
						if (chessboard[i - k][j + k] != tmp)
							break;
					if (k == 5) {
						setWinStep( i, j, WIN_LINE_DIR.LB2RT );
						return tmp;
					}
				}
			}
		}
		/* 判断所有斜行（捺） */
		for (int i = 0; i < AIEPP_FIR.DIMENSION - 4; i++) {
			for (int j = 0; j < AIEPP_FIR.DIMENSION - 4; j++) {
				tmp = chessboard[i][j];
				if (tmp == Status.EMPTY) continue;
				for (k = 1; k < 5; k++){
					if (chessboard[i + k][j + k] != tmp)
						break;
				}
				if (k == 5){
					setWinStep( i, j, WIN_LINE_DIR.LT2RB );
					return tmp;
				}
			}
		}
		
		return Status.EMPTY;
	}
	private void setWinStep( int startX, int startY, WIN_LINE_DIR dir ){
		if( winStepList == null ) winStepList = new ArrayList<Step>();
		winStepList.clear();
		if( dir == WIN_LINE_DIR.HORIZONAL ){
			for( int i = 0; i < 5; i ++ ){
				winStepList.add( findStep( startX, startY + i ) );
			}
		} else  if( dir == WIN_LINE_DIR.VERTICAL ){
			for( int i = 0; i < 5; i ++ ){
				winStepList.add( findStep( startX + i, startY ) );
			}
		} else  if( dir == WIN_LINE_DIR.LB2RT ){
			for( int i = 0; i < 5; i ++ ){
				winStepList.add( findStep( startX - i, startY + i ) );
			}
		} else {//WIN_LINE_DIR.LT2RB
			for( int i = 0; i < 5; i ++ ){
				winStepList.add( findStep( startX + i, startY + i ) );
			}
		}
	}
	private Step findStep( int sx, int sy ){
		for(Step step : stepList){
			if( step.getX() == sx && step.getY() == sy ){
				//System.out.println( "found the step at ("+sx+", "+sy+")" );
				return step;
			}
		}
		System.out.println( "cannot find the step at ("+sx+", "+sy+")" );
		return null;
	}

	/**
	 * @return the stepList
	 */
	public AbstractList<Step> getStepList() {
		return stepList;
	}

	/**
	 * @return the winType
	 */
	public WIN_TYPE getWinType() {
		return winType;
	}
	
	public PartInfo getWinner(){
		return winner;
	}

	/**
	 * @return the chessboard status
	 */
	public Status getBoardStatus( int x, int y ) {
		if( x >= 0 && x < AIEPP_FIR.DIMENSION && y >= 0 && y < AIEPP_FIR.DIMENSION)
			return chessboard[x][y];
		else return Status.EMPTY;
	}

	/**
	 * @return the mode
	 */
	public Mode getMode() {
		return mode;
	}


	/**
	 * @return the lastStep
	 */
	public Step getLastStep() {
		return lastStep;
	}
	/**
	 * @return the iterator of the winStepList
	 */
	public AbstractList<Step> getWinStepList() {
		return winStepList;
	}
	
	public PartInfo getOffensiveInfo(){
		return this.part1;
	}
	public PartInfo getDefensiveInfo(){
		return this.part2;
	}



	/**
	 * @return the cppTime
	 */
	public int getCppTime() {
		return cppTime;
	}
	
	public int getLimitedTime(Lang lang){
		if( lang == Lang.CPP ) {
			return cppTime;
		} else {
			return javaTime;
		}
	}


	/**
	 * @return the javaTime
	 */
	public int getJavaTime() {
		return javaTime;
	}


	/**
	 * @param cppTime the cppTime to set
	 */
	public void setCppTime(int cppTime) {
		this.cppTime = cppTime;
	}


	/**
	 * @param javaTime the javaTime to set
	 */
	public void setJavaTime(int javaTime) {
		this.javaTime = javaTime;
	}

	/**
	 * Get the next part
	 * */
	public Status getNext(){
		return this.next;
	}
	
	/**
	 * @param chessFile the chessFile to set
	 */
	public void setChessFile(String chessFile) throws Exception {
		this.chessFile = chessFile;
		if( this.chessFile == null || this.chessFile.length() == 0) return;
		//System.out.println( "this.chessfile(length="+this.chessFile.length()+")="+this.chessFile );
		int ofcount = 0, dfcount = 0;
		BufferedReader br = FileIO.getBufferedReader(this.chessFile);
		for( int i = 0; i < AIEPP_FIR.DIMENSION; i ++ ) {
			String str = br.readLine();
			String[] posList = str.split(",");
			if( posList.length < AIEPP_FIR.DIMENSION ) return;//error
			for( int j = 0; j < AIEPP_FIR.DIMENSION; j ++ ){
				if( posList[j].equals("1") ){
					chessboard[i][j] = Status.OFFENSIVE;
					this.stepList.add(new Step( Status.OFFENSIVE, i, j ));
					ofcount++;
				} else if( posList[j].equals("-1") ){
					chessboard[i][j] = Status.DEFENSIVE;
					this.stepList.add(new Step( Status.DEFENSIVE, i, j ));
					dfcount++;
				} else if( posList[j].equals("0") ){
					chessboard[i][j] = Status.EMPTY;
				} else {
					clearChessBoard();
					throw new Exception( "chess file error!" );
				}
			}
		}
		FileIO.close(br);
		if( ofcount == dfcount + 1 ){
			this.next = Status.DEFENSIVE;
		} else if( ofcount == dfcount ){
			this.next = Status.OFFENSIVE;
		} else {
			clearChessBoard();
			throw new Exception( "chess file error!" );
		}
	}
	public void clearChessBoard(){
		for( int i = 0; i < AIEPP_FIR.DIMENSION; i ++ ){
			chessboard[i] = new Status[AIEPP_FIR.DIMENSION];
			for( int j = 0; j < AIEPP_FIR.DIMENSION; j ++ ){
				chessboard[i][j] = Status.EMPTY;
			}
		}
	}

	/**
	 * @return the chessFile
	 */
	public String getChessFile() {
		return chessFile;
	}


	public static class PartInfo implements Serializable {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4855620079963925682L;

		private Status status;
		
		private String id, name, nickname;
		private AIInfo aiInfo;
		
		private int usedTime;
		
		private boolean isThinking = false;
		
		private int heuristic;
		
		private int connId;
		
		private Lang language;

		public PartInfo(){}
		
		public PartInfo(AIInfo aiInfo){
			this.aiInfo = aiInfo;
			if( aiInfo != null ){
				this.id = aiInfo.getId();
				this.name = aiInfo.getName();
				this.nickname = aiInfo.getNickname();
				this.language = aiInfo.getLanguage();
			}
		}
		//*		
		public PartInfo(String id, String name, String nickname){
			set(id, name, nickname);
		} 

		public void set(String id, String name, String nickname){
			this.id = id;
			this.name = name;
			this.nickname = nickname;
		}
		
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * @return the nickname
		 */
		public String getNickname() {
			return nickname;
		}

		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * @param nickname the nickname to set
		 */
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
//*/
		/**
		 * @param status the status to set
		 */
		protected void setStatus(Status status) {
			this.status = status;
		}

		/**
		 * @return the status
		 */
		public Status getStatus() {
			return status;
		}

		/**
		 * @param usedTime the usedTime to set
		 */
		public void setUsedTime(int usedTime) {
			this.usedTime = usedTime;
		}

		/**
		 * @return the usedTime
		 */
		public int getUsedTime() {
			return usedTime;
		}
		
		/**
		 * add an addtion to the used time, and return the new time
		 * 
		 * @param addtion the time used newly
		 * 
		 * @return the added newly used time
		 * */
		public int addUsedTime( int addtion ){
			this.usedTime += addtion;
			return this.usedTime;
		}

		/**
		 * @param isThinking the isThinking to set
		 */
		public void setThinking(boolean isThinking) {
			this.isThinking = isThinking;
		}

		/**
		 * @return the isThinking
		 */
		public boolean isThinking() {
			return isThinking;
		}

		/**
		 * @param connId the connId to set
		 */
		public void setConnId(int connId) {
			this.connId = connId;
		}

		/**
		 * @return the connId
		 */
		public int getConnId() {
			return connId;
		}

		/**
		 * @param heuristic the heuristic to set
		 */
		public void setHeuristic(int heuristic) {
			this.heuristic = heuristic;
		}

		/**
		 * @return the heuristic
		 */
		public int getHeuristic() {
			return heuristic;
		}

		/**
		 * @param language the language to set
		 */
		public void setLanguage(Lang language) {
			this.language = language;
		}

		/**
		 * @return the language
		 */
		public Lang getLanguage() {
			return language;
		}

		/**
		 * @return the aiInfo
		 */
		public AIInfo getAiInfo() {
			return aiInfo;
		}
		
	}
	
	public String toChessString(){
		StringBuffer sb = new StringBuffer();
		for( int i = 0; i < AIEPP_FIR.DIMENSION; i ++ ){
			for( int j = 0; j <AIEPP_FIR.DIMENSION; j ++ ){
				if( this.chessboard[i][j] == Status.EMPTY){
					sb.append("0");
				} else if( this.chessboard[i][j] == Status.OFFENSIVE ){
					sb.append("1");
				} else {
					sb.append("-1");
				}
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static class Five {

		@SuppressWarnings("unused")
		private int x0, x1, x2, x3, x4;
		@SuppressWarnings("unused")
		private int y0, y1, y2, y3, y4;
		
	}
	
}







