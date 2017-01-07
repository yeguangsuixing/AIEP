package com.twzcluster.webserver;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

@SuppressWarnings("deprecation")
public class Session implements HttpSession {

	public final static String SESSION_ID = "jsessionid";

	private boolean validate = true, newCreate = true;
	private String id;
	private long creationTime = new Date().getTime(),
			lastAccessedTime = creationTime;
	private int maxInactiveInterval;
	private Hashtable<String, Object> session = new Hashtable<String, Object>();

	@Override
	public Object getAttribute(String arg0) {
		return null;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		if (!validate)
			return null;
		return session.keys();
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	@Override
	public int getMaxInactiveInterval() {
		return maxInactiveInterval;
	}

	@Override
	public ServletContext getServletContext() {
		return null;
	}

	@Override
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	public Object getValue(String arg0) {
		if (!validate)
			return null;
		return session.get(arg0);
	}

	@Override
	public String[] getValueNames() {
		if (!validate)
			return null;
		Object[] objArray = session.values().toArray();
		String[] strArray = new String[objArray.length];
		for (int i = 0; i < objArray.length; i++)
			strArray[i] = objArray[i].toString();
		return strArray;
	}

	@Override
	public void invalidate() {
		this.validate = false;
		this.session.clear();
	}

	@Override
	public boolean isNew() {
		return newCreate;
	}

	@Override
	public void putValue(String arg0, Object arg1) {
		if (!validate)
			return;
		this.session.put(arg0, arg1);
	}

	@Override
	public void removeAttribute(String arg0) {
		if (!validate)
			return;
		this.session.remove(arg0);
	}

	@Override
	public void removeValue(String arg0) {
		// TODO Auto-generated method stub
		// ÕâÊÇÊ²Ã´
	}

	@Override
	public void setAttribute(String arg0, Object arg1) {
		if (!validate)
			return;
		this.session.remove(arg0);
		this.session.put(arg0, arg1);
	}

	@Override
	public void setMaxInactiveInterval(int arg0) {
		this.maxInactiveInterval = arg0;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	/**
	 * @return the validate
	 */
	public boolean isValidate() {
		return validate;
	}

	public void setValidate(boolean validate) {
		this.validate = validate;
	}

	public void setNewCreate(boolean newCreate) {
		this.newCreate = newCreate;
	}

}
