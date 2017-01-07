package com.twzcluster.webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

/**
 * http������
 * 
 * @ʱ�� 2012��12��4��14:05:32
 * @author tqc
 * */
public class Request extends RequestAdapter {

	/** Ĭ�ϵ�uri��������·��Ϊ��ʱʹ��Ĭ��uri */
	private final static String DEFAULT_URI = "/index.html";
	/** ������󳤶� */
	private final static int MAX_REQUEST_LEN = 1024 * 2;
	// cookieǰ׺
	private final static String COOKIE = "Cookie: ";
	// ������
	private InputStream input;
	// request String,����ͷ�ַ����������ַ���
	private String requestString, headerString;
	private StringBuilder paraStringBuilder = new StringBuilder();
	// http header
	private Hashtable<String, String> headerList = new Hashtable<String, String>();
	// �ͻ���ip��ַ
	private String remoteAddr;
	// ����·��
	private String requestURI;
	// ������չ��
	private String extension;
	// Session
	private HttpSession session = new Session();
	// Session Id
	private String requestedSessionId;
	// cookie
	private AbstractList<Cookie> cookieList = new ArrayList<Cookie>();
	// method
	private String method;

	public Request(InputStream input) {
		this.input = input;
		catchRequestString();
		parseRequestString();
	}

	@Override
	public String getRequestURI() {
		return requestURI;
	}

	public InputStream getInput() {
		return input;
	}

	/**
	 * ��ȡ������չ��������.��֮����ַ���
	 * 
	 * @return ��չ��
	 * */
	public String getExtension() {
		return extension;
	}

	@Override
	public HttpSession getSession() {
		return session;
	}

	@Override
	public String getRequestedSessionId() {
		return requestedSessionId;
	}

	@Override
	public Cookie[] getCookies() {
		return (Cookie[]) this.cookieList.toArray();
	}

	@Override
	public String getMethod() {
		return method;
	}

	@Override
	public String getHeader(String arg0) {
		return this.headerList.get(arg0);
	}

	@Override
	public String getRemoteAddr() {
		return remoteAddr;
	}

	protected void setSession(HttpSession session) {
		this.session = session;
		if (paraStringBuilder.capacity() > 0) {
			String paraString = paraStringBuilder.toString();
			String[] elemArray = paraString.split("&");
			for (String elem : elemArray) {
				int index = elem.indexOf("=");
				if (index != -1) {
					String attr = elem.substring(0, index);
					String value;
					try {
						value = URLDecoder.decode(elem.substring(index + 1),
								"gb2312");
						this.session.setAttribute(attr, value);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	protected void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	// ��ȡhttp�����ַ���
	private void catchRequestString() {
		StringBuffer requestStringBuffer = new StringBuffer(MAX_REQUEST_LEN);
		int length;
		byte[] buffer = new byte[MAX_REQUEST_LEN];
		try {
			length = input.read(buffer);
		} catch (IOException e) {
			// e.printStackTrace();
			length = -1;
		}
		for (int i = 0; i < length; i++) {
			requestStringBuffer.append((char) buffer[i]);
		}
		this.requestString = requestStringBuffer.toString();
		System.out.println(this.requestString);
	}

	// ����http�����ַ���
	private void parseRequestString() {
		int index = requestString.indexOf("\r\n\r\n");
		if (index == -1) {
			this.headerString = this.requestString;
			// this.bodyString = null;
		} else {
			this.headerString = this.requestString.substring(0, index + 2);
			this.paraStringBuilder.append(this.requestString
					.substring(index + 4));
			this.paraStringBuilder.append("&");
		}

		parseHeader(headerString);
		String[] requestStringArray = requestString.split("\r\n");
		parseMethod(requestStringArray[0]);
		for (int i = 1; i < requestStringArray.length; i++) {
			if (requestStringArray[i].startsWith(COOKIE)) {
				parseCookie(requestStringArray[i]);
			}
		}
	}

	// ������ ��GET /test.html HTTP/1.1��
	private void parseMethod(String method) {
		if (method != null) {
			int index1, index2;
			index1 = method.indexOf(' ');
			if (index1 != -1) {
				this.method = method.substring(0, index1);
				index2 = method.indexOf(' ', index1 + 1);
				if (index2 > index1) {
					this.requestURI = method.substring(index1 + 1, index2);
					int question_index = this.requestURI.indexOf("?");
					if (question_index != -1) {// ���ò����ַ���
						this.paraStringBuilder.append(this.requestURI
								.substring(question_index + 1));
						this.paraStringBuilder.append("&");
						this.requestURI = this.requestURI.substring(0,
								question_index);
					}
					try {
						this.requestURI = URLDecoder.decode(this.requestURI,
								"UTF-8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}// */
				}
			}
		}
		if (this.requestURI == null || this.requestURI.length() == 0
				|| this.requestURI.equals("/")) {
			this.requestURI = DEFAULT_URI;
		}
		int index = this.requestURI.lastIndexOf(".");
		if (index >= 0) {
			extension = this.requestURI.substring(index + 1);
		}
	}

	// ���� cookie
	private void parseCookie(String cookieString) {
		if (cookieString == null)
			return;
		cookieString = cookieString.substring(COOKIE.length());
		String[] cookieArray = cookieString.split(";");
		for (int i = 0; i < cookieArray.length; i++) {
			int equalIndex = cookieArray[i].indexOf("=");
			String attr = cookieArray[i].substring(0, equalIndex).trim();
			String value = cookieArray[i].substring(equalIndex + 1);
			Cookie cookie = new Cookie(attr, value);
			this.cookieList.add(cookie);
			if (this.requestedSessionId == null
					&& attr.equals(Session.SESSION_ID)) {
				this.requestedSessionId = cookieArray[i]
						.substring(Session.SESSION_ID.length() + 1);
			}
		}
	}

	// ����http����ͷ
	private void parseHeader(String headerString) {
		String[] headerArray = headerString.split("\r\n");
		for (String headerUnit : headerArray) {
			int index = headerUnit.indexOf(": ");
			if (index != -1) {
				String key = headerUnit.substring(0, index);
				String value = headerUnit.substring(index + 2/* ð�ſո� */);
				this.headerList.put(key, value);
			}
		}
	}

}
