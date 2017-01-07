package com.twzcluster.webserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * http响应类
 * 
 * @时间 2012年12月4日14:34:03
 * @author tqc
 * */
public class Response extends ResponseAdapter {

	private static final int BUFFER_SIZE = 1024 * 2;// 2KB
	// 设置cookie前缀
	private final static String SET_COOKIE = "set-cookie: ";

	/** 404错误页面html字符串 */
	protected static String ERROR_404_BODY = "<html><head><title>File Not Found</title></head><h1>File Not Found</h1></html>";
	protected static String ERROR_404 = "HTTP/1.1 404 File Not Found\r\n"
			+ "Content-Type: text/html\r\n" + "Content-Length: "
			+ ERROR_404_BODY.length() + "\r\n\r\n" + ERROR_404_BODY;
	/** 500错误页面html字符串 */
	protected static String ERROR_500_BODY = "<html><head><title>Internal Server Error</title></head><h1>Internal Server Error</h1></html>";
	protected static String ERROR_500 = "HTTP/1.1 500 Internal Server Error\r\n"
			+ "Content-Type: text/html\r\n"
			+ "Content-Length: "
			+ ERROR_500_BODY.length() + "\r\n\r\n" + ERROR_500_BODY;

	String rootDir;
	Request request;
	OutputStream output;

	public Response(OutputStream output) {
		this.output = output;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	@Override
	public PrintWriter getWriter() {
		return new PrintWriter(output);
	}

	public void send() throws IOException {
		byte[] bytes = new byte[BUFFER_SIZE];
		FileInputStream fis = null;
		try {
			File file = new File(rootDir, request.getRequestURI());
			fis = new FileInputStream(file);
			send200Header();
			output.write("\r\n".getBytes());
			int ch = fis.read(bytes, 0, BUFFER_SIZE);
			while (ch != -1) {
				output.write(bytes, 0, ch);
				ch = fis.read(bytes, 0, BUFFER_SIZE);
			}
		} catch (/* FileNotFound */Exception e) {// 所有问题都归结为未找到
			send404();
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
	}

	public void send404() {
		try {
			output.write(Response.ERROR_404.getBytes());
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public void send500() {
		try {
			output.write(Response.ERROR_500.getBytes());
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

	public String getRootDir() {
		return rootDir;
	}

	public void setRootDir(String rootDir) {
		this.rootDir = rootDir;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public void send200Header() {
		try {
			output.write("HTTP/1.1 200 OK\r\n".getBytes());
			StringBuilder sb = new StringBuilder();

			sb.append(SET_COOKIE);
			sb.append(Session.SESSION_ID);
			sb.append("=");
			sb.append(request.getSession().getId());
			sb.append(";");
			sb.append("\r\n");

			Enumeration<String> iterator = request.getSession()
					.getAttributeNames();
			for (; iterator.hasMoreElements();) {
				String attr = iterator.nextElement();
				sb.append(SET_COOKIE);
				sb.append(attr);
				sb.append("=");
				sb.append(request.getSession().getValue(attr));
				sb.append(";");
				sb.append("\r\n");
			}

			output.write(sb.toString().getBytes());
		} catch (IOException e) {
			// e.printStackTrace();
		}
	}

}
