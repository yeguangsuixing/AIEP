package com.twzcluster.net;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息类。用于在集群中传输数据。所有消息应该继承自此类。
 * 
 * @time 2012年12月5日20:12:12
 * @author tqc
 * */
public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6975921972183050054L;

	public static final int ORIGIN = 0;
	/**
	 * 默认发送者id
	 * */
	public static final int DEFAULT = -1;

	// ////////////////////////////////////////////////

	/**
	 * 消息id
	 * */
	protected int id = DEFAULT;
	/**
	 * 回应的消息的id
	 * */
	protected int responseId = DEFAULT;
	/**
	 * <p>
	 * 消息发送者id，用于通知消息使用者
	 * </p>
	 * <p>
	 * 注意：此属性不被序列化
	 * </p>
	 * */
	protected transient int senderid = DEFAULT;
	/**
	 * 消息接收者id，用于通知网络发送管理员
	 * <p>
	 * 注意：此属性不被序列化
	 * <li>此属性不会被序列化</li>
	 * <li>此属性可继承但不应被覆盖</li>
	 * </p>
	 * */
	protected transient int receiverid = DEFAULT;
	/**
	 * 消息发送、接收时间(在接收到此包的线程负责赋值)
	 * **/
	protected Date sendTime, receiveTime;
	// protected Date createTime = new Date();
	/**
	 * 消息类型
	 * */
	protected int type = Message.ORIGIN;
	/**
	 * 消息内容。此消息类为原始类，故消息内容只有一个字符串
	 * */
	protected String content;

	protected Message() {
	}

	protected Message(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getResponseId() {
		return responseId;
	}

	public int getSenderid() {
		return senderid;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public Date getReceiveTime() {
		return receiveTime;
	}

	public int getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public int getReceiverid() {
		return receiverid;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setResponseId(int responseId) {
		this.responseId = responseId;
	}

	protected void setSenderid(int senderid) {
		this.senderid = senderid;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public void setReceiveTime(Date receiveTime) {
		this.receiveTime = receiveTime;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setReceiverId(int receiverid) {
		this.receiverid = receiverid;
	}
	/*
	 * public Date getCreateTime() { return this.createTime; }
	 */
}
