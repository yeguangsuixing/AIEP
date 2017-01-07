package com.twzcluster.webserver.jsp;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.twzcluster.webserver.Request;
import com.twzcluster.webserver.Response;


public class hello/*class name*/ extends HttpServlet  {
	protected void  service( HttpServletRequest request, HttpServletResponse response )
	throws IOException, ServletException {
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		((Response)response).send200Header();
		out.print( "\r\n" );
		out.print("");
		out.print("<html>");
		out.print("<head>");
		out.print("<title>Hello");
		out.print("</title>");
		out.print("</head>");
		out.print("<body>Hello, Welcome to AIEP!");
		out.print("</body>");
		out.print("</html>");
		out.close();
	}
}
