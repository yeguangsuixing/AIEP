package com.twzcluster.net;

import java.io.Serializable;
import java.util.Date;

/**
 * ��Ϣ�ࡣ�����ڼ�Ⱥ�д������ݡ�������ϢӦ�ü̳��Դ��ࡣ
 * 
 * @time 2012��12��5��20:12:12
 * @author tqc
 * */
public class Message implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6975921972183050054L;

	public static final int ORIGIN = 0;
	/**
	 * Ĭ�Ϸ�����id
	 * */
	public static final int DEFAULT = -1;

	// ////////////////////////////////////////////////

	/**
	 * ��Ϣid
	 * */
	protected int id = DEFAULT;
	/**
	 * ��Ӧ����Ϣ��id
	 * */
	protected int responseId = DEFAULT;
	/**
	 * <p>
	 * ��Ϣ������id������֪ͨ��Ϣʹ����
	 * </p>
	 * <p>
	 * ע�⣺�����Բ������л�
	 * </p>
	 * */
	protected transient int senderid = DEFAULT;
	/**
	 * ��Ϣ������id������֪ͨ���緢�͹���Ա
	 * <p>
	 * ע�⣺�����Բ������л�
	 * <li>�����Բ��ᱻ���л�</li>
	 * <li>�����Կɼ̳е���Ӧ������</li>
	 * </p>
	 * */
	protected transient int receiverid = DEFAULT;
	/**
	 * ��Ϣ���͡�����ʱ��(�ڽ��յ��˰����̸߳���ֵ)
	 * **/
	protected Date sendTime, receiveTime;
	// protected Date createTime = new Date();
	/**
	 * ��Ϣ����
	 * */
	protected int type = Message.ORIGIN;
	/**
	 * ��Ϣ���ݡ�����Ϣ��Ϊԭʼ�࣬����Ϣ����ֻ��һ���ַ���
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
