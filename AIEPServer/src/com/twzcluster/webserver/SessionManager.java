package com.twzcluster.webserver;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;

public class SessionManager {

	private static int MAX_INACTIVE_INTERVAL = 60 * 20;// 20����

	private AbstractList<Session> sessionList = new ArrayList<Session>();
	private boolean isServicing = true;

	public void init() {
		sessionList.clear();
	}

	/**
	 * Ϊָ����http�������session
	 * 
	 * @param request
	 *            http����
	 * */
	public void setSession(Request request) {
		String jsessionid = request.getRequestedSessionId();
		if (jsessionid == null) {
			request.setSession(this.createSession(request));
		} else {
			Session session = this.catchSession(jsessionid);
			if (session == null) {
				request.setSession(this.createSession(request));
			} else {
				request.setSession(session);
			}
		}
		// System.out.println(
		// "SessionManager:��ǰSession������"+this.sessionList.size() );
	};

	// �������󴴽�һ��session����
	private Session createSession(Request request) {
		Session session = new Session();
		long time = new Date().getTime();
		session.setValidate(true);
		session.setId(createId(request));
		session.setMaxInactiveInterval(MAX_INACTIVE_INTERVAL);
		session.setCreationTime(time);
		session.setLastAccessedTime(time);
		synchronized (sessionList) {
			sessionList.add(session);
		}
		return session;
	}

	// ����������б��л�ȡһ��session����
	private Session catchSession(String jsessionid) {
		synchronized (sessionList) {
			for (Session session : sessionList) {
				if (session.isValidate() && session.getId().equals(jsessionid)) {
					session.setLastAccessedTime(new Date().getTime());
					return session;
				}
			}
		}
		return null;
	}

	private String createId(Request request) {
		return request.getRemoteAddr().toString() + new Date().getTime();
	}

	class SessionRunnable implements Runnable {

		@Override
		public void run() {
			while (isServicing) {
				try {
					Thread.sleep(1000);// ˯��1��
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				synchronized (sessionList) {
					for (Session session : sessionList) {
						if (session.getLastAccessedTime()
								- session.getCreationTime() < session
									.getMaxInactiveInterval()) {
							session.setValidate(false);
						}
					}
				}
			}
		}

	}

}
