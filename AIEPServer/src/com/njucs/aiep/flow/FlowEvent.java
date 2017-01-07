package com.njucs.aiep.flow;


import java.util.Date;
import java.util.EventObject;

import com.njucs.aiep.net.DataTransmit.StudentInfo;


/**
 * 
 * 
 * @author ygsx
 * 
 * @time 2013Äê5ÔÂ4ÈÕ12:45:19
 * */
public class FlowEvent<E> extends EventObject {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4061495004935845089L;

	public final static Integer TIME_OUT = 1, RECEIVE = 2;
	
	private int type;
	private Date sendTime;
	private StudentInfo studentInfo;
	private E data;
	
	public FlowEvent(Object source, int type, Date sendTime, StudentInfo info, E data){
		super(source);
		this.type = type;
		this.studentInfo = info;
		this.sendTime = sendTime;
		this.data = data;
	}

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return the data
	 */
	public E getData() {
		return data;
	}

	/**
	 * @return the sendTime
	 */
	public Date getSendTime() {
		return sendTime;
	}

	/**
	 * @return the studentInfo
	 */
	public StudentInfo getStudentInfo() {
		return studentInfo;
	}
	
	
	
}
