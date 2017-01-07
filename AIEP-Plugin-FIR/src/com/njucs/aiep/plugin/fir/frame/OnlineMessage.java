package com.njucs.aiep.plugin.fir.frame;

import java.util.AbstractList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.njucs.aiep.frame.AIInfo;
import com.njucs.aiep.game.Status;
import com.njucs.aiep.plugin.fir.frame.Inning.WIN_TYPE;
import com.twzcluster.net.Message;

public class OnlineMessage extends Message {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2087692576521843967L;

	public static enum MsgType { CMD, DATA };
	public static enum Cmd { ENTER_DESK, EXIT_DESK, DESK_REPLY, AI_REPLY };
	public static enum Data { DESK_INFO, AI_INFO, INNING_INFO, STEP_INFO, GAME_OVER_INFO };
	public static enum DeskReply { OK , ERROR };
	
	private MsgType msgType;
	
	private Cmd cmd;
	private Data dataType;
	
	//if cmd == ENTER_DESK/EXIT_DESK, id means deskid
	//if cmd == DESK_REPLY, id == -1 means OK, -2 means ERROR
	private int deskId;
	private DeskReply deskReply;
	
	// 
	private Step step;
	private AIInfo aiInfo;
	private String deskStringList;
	
	//inning info
	private Inning inning;
	private Status status;
	/*
	private boolean isOffensive;
	private int limitedTime;
	private int heuristic;
	private PartInfo opponentInfo;
	private Status[] chessStatus;//*/
	
	//game over
	private Status winner;
	private WIN_TYPE winType;
	private AbstractList<Step> firStep;
	
	
	public OnlineMessage( Status winner,  WIN_TYPE winType, AbstractList<Step> firStep ){
		this.msgType = MsgType.DATA;
		this.dataType = Data.GAME_OVER_INFO;
		this.winner = winner;
		this.winType = winType;
		this.firStep = firStep;
	}
	
	public OnlineMessage(Cmd cmd){
		this.msgType = MsgType.CMD;
		this.cmd =cmd;
	}
	public OnlineMessage( Data datatype ){
		this.msgType = MsgType.DATA;
		this.dataType = datatype;
	}
	public OnlineMessage( DeskReply reply ){
		this.msgType = MsgType.CMD;
		this.cmd = Cmd.DESK_REPLY;
		this.deskReply = reply;
	}
	public OnlineMessage(Step step){
		this.msgType = MsgType.DATA;
		this.dataType = Data.STEP_INFO;
		this.step = step;
	}
	public OnlineMessage(AIInfo aiInfo){
		this.msgType = MsgType.DATA;
		this.dataType = Data.AI_INFO;
		this.aiInfo = aiInfo;
	}
	public OnlineMessage(AbstractList<FIRDesk> deskList) {
		this.msgType = MsgType.DATA;
		this.dataType = Data.DESK_INFO;
		this.deskStringList = JSON.toJSONString( deskList );
	}
	public OnlineMessage( MsgType msgType, Cmd cmd){
		this.msgType = msgType;
		this.cmd = cmd;
	}
	public OnlineMessage( MsgType msgType, Cmd cmd, String content ){
		this.msgType = msgType;
		this.cmd = cmd;
		this.content = content;
	}
	
	public DeskReply getDeskReply(){
		return this.deskReply;
	}
	public int getDeskId(){
		return this.deskId;
	}
	public void setDeskId( int deskId ){
		this.deskId = deskId;
	}

	/**
	 * @param inning the inning to set
	 */
	public void setInningInfo(Status status, Inning inning) {
		this.status = status;
		this.inning = inning;
	}
	
	/**
	 * @param isOffensive the isOffensive to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the isOffensive
	 */
	public Status getStatus() {
		return status;
	}


	/**
	 * @return the inning
	 */
	public Inning getInning() {
		return inning;
	}
	
	
	
/*
	public void setInningInfo( boolean isOffensive, int limitedTime, int heuristic, PartInfo opponentInfo,
			Status[] chessStatus){
		this.isOffensive = isOffensive;
		this.limitedTime = limitedTime;
		this.heuristic = heuristic;
		this.opponentInfo = opponentInfo;
		this.chessStatus = chessStatus;
	}
	public boolean isOffensive(){
		return this.isOffensive;
	}
	public int getLimitedTime(){
		return this.limitedTime;
	}
	public int getHeuritic(){
		return this.heuristic;
	}
	public PartInfo getOpponentInfo(){
		return this.opponentInfo;
	}
	public Status[] getChessString(){
		return this.chessString;
	}//*/
	/**
	 * @return the msgType
	 */
	public MsgType getMsgType() {
		return msgType;
	}

	/**
	 * @return the step
	 */
	public Step getStep() {
		return step;
	}

	/**
	 * @param msgType the msgType to set
	 */
	public void setMsgType(MsgType msgType) {
		this.msgType = msgType;
	}
	/**
	 * @param cmd the cmd to set
	 */
	public void setCmd(Cmd cmd) {
		this.cmd = cmd;
	}

	/**
	 * @return the cmd
	 */
	public Cmd getCmd() {
		return cmd;
	}

	/**
	 * @return the dataType
	 */
	public Data getDataType() {
		return dataType;
	}
	/**
	 * @return the aiInfo
	 */
	public AIInfo getAiInfo() {
		return aiInfo;
	}
	
	/**
	 * @return the deskList
	 */
	public List<FIRDesk> getDeskList() {
		try {
			return JSON.parseArray(deskStringList, FIRDesk.class);
		} catch ( Exception e ){
			e.printStackTrace();
			System.out.println( "OnlineMessage.getDeskList() error! deskStringList = "+deskStringList );
			return null;
		}
	}

	/**
	 * @return the winner
	 */
	public Status getWinner() {
		return winner;
	}

	/**
	 * @return the winType
	 */
	public WIN_TYPE getWinType() {
		return winType;
	}

	/**
	 * @return the firStep
	 */
	public AbstractList<Step> getFirStep() {
		return firStep;
	}

}
