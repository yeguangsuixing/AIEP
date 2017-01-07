package com.twzcluster.webserver.jsp;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.njucs.aiep.base.ClassOperator;
import com.twzcluster.webserver.TWZWebServer;

//import com.ygsx.frame.ClassOperator;

public class JSPServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6771011586665646810L;

	protected final static String PACKAGE_NAME = "com.twzcluster.webserver.jsp";
	protected final static String PATH_NAME = "com/twzcluster/webserver/jsp";
	protected final static String CLASS_PATH = TWZWebServer.ROOT_DIR + "\\";

	private com.sun.tools.javac.main.Main javac = new com.sun.tools.javac.main.Main(
			null);
	// 1.创建jsp引擎
	private JSPEngineer jspEngineer = new JSPEngineer();
	static {
		// 2.添加网站根目录到classpath
		try {
			ClassOperator.addURL(CLASS_PATH, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		HttpServlet servlet = null;
		String req = request.getRequestURI();
		String className = req.substring(req.lastIndexOf("/") + 1,
				req.lastIndexOf("."));
		String dst = className + ".java";
		String jspFile = TWZWebServer.ROOT_DIR + req;
		String javaFile = TWZWebServer.ROOT_DIR + "/" + dst;
		// 3.转换jsp->java
		jspEngineer.parse(jspFile, javaFile, className);
		// 4.编译，即转换java->class
		int c = javac.compile(new String[] { "-d", CLASS_PATH, javaFile });// 编译通过返回0
		if (c != 0) {
			throw new ServletException("compiling error");
		}
		try {
			// 5.加载class
			Class<?> servletClass = Class.forName(PACKAGE_NAME + "."
					+ className);
			// 6.获取class的实例
			servlet = (HttpServlet) servletClass.newInstance();
			// 7.调用service函数
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		if (servlet != null)
			servlet.service(request, response);
		else
			throw new ServletException(
					"fail to generate servlet(unknown error)");
		// new File( PATH_NAME+"/"+className+".class" ).delete();

	}

}
