package com.twzcluster.webserver;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class StaticServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 422988388874878641L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		service(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		service(request, response);
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		((Response) response).send();
	}
}
