package com.njucs.aiep.plugin;

import java.util.List;

import com.njucs.aiep.frame.Competition;
import com.twzcluster.net.Message;


public class CompetitionMessage extends Message {

	/**
	 * 
	 */
	private static final long serialVersionUID = -850634115473867572L;
	
	List<Competition> competitionList;
	
	public CompetitionMessage(List<Competition> competitionList){
		this.competitionList = competitionList;
		this.receiverid = Message.DEFAULT;
	}
	
	public List<Competition> getCompetitionList(){
		return this.competitionList;
	}
	
}