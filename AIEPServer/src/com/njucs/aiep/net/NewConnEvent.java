package com.njucs.aiep.net;

import java.util.EventObject;

import com.njucs.aiep.net.DataTransmit.Lang;
import com.njucs.aiep.net.DataTransmit.StudentInfo;

public class NewConnEvent extends EventObject {
	
	/**
	 * 
	 * @author ygsx
	 * 
	 * @time 2013Äê5ÔÂ5ÈÕ11:37:58
	 */
	private static final long serialVersionUID = 3067004521324432118L;
	
	private int listenId;
	private int connId;
	private StudentInfo studentInfo;
	private Lang lang;
	
	public NewConnEvent( Object source, int listenId, int connId, StudentInfo studentInfo, Lang lang ){
		super(source);
		this.listenId = listenId;
		this.connId = connId;
		this.studentInfo = studentInfo;
		this.lang = lang;
	}
	
	
	/**
	 * @return the connId
	 */
	public int getConnId() {
		return connId;
	}
	/**
	 * @return the studentInfo
	 */
	public StudentInfo getStudentInfo() {
		return studentInfo;
	}
	/**
	 * @return the listenId
	 */
	public int getListenId() {
		return listenId;
	}




	/**
	 * @return the lang
	 */
	public Lang getLang() {
		return lang;
	}
	
}
