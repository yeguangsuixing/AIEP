package com.njucs.aiep.net;

import java.util.Date;
import java.util.EventObject;

import com.njucs.aiep.net.DataTransmit.StudentInfo;


/**
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ5ÈÕ11:48:34
 * */
public class ReceiveEvent<E> extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3455373978521187310L;
	
	public static enum RecvEventType { COMMAND, DATA };
	public static enum RecvCmdType { RESTART };
	
	private StudentInfo studentInfo;
	private Date sendTime;
	
	private RecvEventType eventType;
	private E data;
	private RecvCmdType cmdType;

	public ReceiveEvent(Object source, Date sendTime, StudentInfo studentInfo, E data) {
		super(source);
		this.sendTime = sendTime;
		this.studentInfo = studentInfo;
		this.data = data;
		this.eventType = RecvEventType.DATA;
	}
	public ReceiveEvent(Object source, Date sendTime, StudentInfo studentInfo, RecvCmdType cmdType){
		super(source);
		this.sendTime = sendTime;
		this.studentInfo = studentInfo;
		this.eventType = RecvEventType.COMMAND;
		this.cmdType = cmdType;
	}

	public E getData() {
		return data;
	}


	/**
	 * @return the studentInfo
	 */
	public StudentInfo getStudentInfo() {
		return studentInfo;
	}


	/**
	 * @return the sendTime
	 */
	public Date getSendTime() {
		return sendTime;
	}
	/**
	 * @return the type
	 */
	public RecvEventType getEventType() {
		return eventType;
	}
	/**
	 * @return the cmdType
	 */
	public RecvCmdType getCmdType() {
		return cmdType;
	}

}
