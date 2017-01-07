package com.twzcluster.webserver.jsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.njucs.aiep.base.FileIO;

//import com.ygsx.file.FileIO;

public class JSPEngineer {

	public void parse(String jspFile, String javaFile, String className)
			throws IOException {
		// System.out.println(
		// "jspFile="+jspFile+",javaFile"+javaFile+",class="+className );
		// 1.检查源文件是否存在，不存在抛出异常
		File src = new File(jspFile);
		if (!src.exists()) {
			throw new FileNotFoundException("the file(\""
					+ src.getCanonicalPath() + "\") does not exist.");
		}
		// 2.检查目标目录是否存在，不存在则创建
		File dst = new File(javaFile.substring(0, javaFile.lastIndexOf("/")));
		if (!dst.exists()) {
			dst.mkdirs();
		}
		// 3.解析源文件
		ClassInfo ci = new ClassInfo();
		ci.parse(jspFile, javaFile, className);
	}

	private static class Node {
		public static final int TYPE_TEXT = 1, TYPE_CODE = 2, TYPE_PRINT = 3;
		public int type;
		public String text;
	}

	private static class ClassInfo {

		private final static int STATE_CODE = 1;// <%
		private final static int STATE_PRINT = 2;// <%=
		private final static int STATE_DECLEAR = 3;// <%!
		private final static int STATE_REFER = 4;// <%@

		private final static String PACKAGE = "package %s;\r\n\r\n";
		private final static String[] IMPORT = { "java.io.IOException",
				"java.io.PrintWriter", "javax.servlet.ServletException",
				"javax.servlet.http.HttpServlet",
				"javax.servlet.http.HttpSession",
				"javax.servlet.http.HttpServletRequest",
				"javax.servlet.http.HttpServletResponse",
				"com.twzcluster.webserver.Request",
				"com.twzcluster.webserver.Response" };
		private final static String CLASS_HEADER = "public class %s/*class name*/ extends HttpServlet  {\r\n";
		private static String SERVICE_FUNCTION_HEADER = "	protected void  service( HttpServletRequest request, HttpServletResponse response )\r\n"
				+ "	throws IOException, ServletException {\r\n"
				+ "		PrintWriter out = response.getWriter();\r\n"
				+ "		HttpSession session = request.getSession();\r\n"
				+ "		((Response)response).send200Header();\r\n"
				+ "		out.print( \"\\r\\n\" );\r\n";
		private static String SERVICE_FUNCTION_TAIL = "		out.close();\r\n"
				+ "	}\r\n";
		private static String CLASS_TAIL = "}\r\n";

		ArrayList<String> referList = new ArrayList<String>();
		ArrayList<String> declearList = new ArrayList<String>();
		ArrayList<Node> contextList = new ArrayList<Node>();

		public void parse(String jspFile, String javaFile, String className)
				throws IOException {
			referList.clear();
			declearList.clear();
			contextList.clear();
			parseElement(jspFile);
			generateFile(className, javaFile);
		}

		// 解析jsp文件内容
		public void parseElement(String jspFile) throws IOException {
			// 1.获取输入流
			FileReader reader = new FileReader(jspFile);
			char[] buffer = new char[1];

			Node node = new Node();
			StringBuilder builder = new StringBuilder();
			while (reader.read(buffer) > 0) {
				if (buffer[0] == '<') {
					node.type = Node.TYPE_TEXT;
					node.text = builder.toString();
					contextList.add(node);
					node = new Node();
					builder = new StringBuilder();
					// 设置新状态
					if (reader.read(buffer) > 0) {
						if (buffer[0] == '%') {
							parseJSPElement(reader);// 遇到了jsp
						} else {
							builder.append('<');
							builder.append(buffer[0]);
						}
					} else {
						throw new IOException("the src file has error(s).");
					}
				} else {
					builder.append(buffer[0]);
				}
			}
			node.type = Node.TYPE_TEXT;
			node.text = builder.toString();
			contextList.add(node);
			// System.out.println( "over." );
		}

		// 生成java文件
		public void generateFile(String className, String dstFile)
				throws IOException {
			// 获取输出流
			// System.out.println( "dstFile="+dstFile );
			PrintWriter writer = FileIO.getWriter(dstFile);
			// 写入包声明
			writer.append(String.format(PACKAGE, JSPServlet.PACKAGE_NAME));
			// 写入导入类
			writer.append(getImportString());
			// 写入类定义头
			writer.append(String.format(CLASS_HEADER, className));
			// 写入其他声明
			writer.append(getDeclearListString());
			// 写入service函数头
			writer.append(SERVICE_FUNCTION_HEADER);
			// 添加函数体
			writer.append(getContextListString());
			// 写入service函数尾
			writer.append(SERVICE_FUNCTION_TAIL);
			// 写入类定义尾
			writer.append(CLASS_TAIL);
			FileIO.close(writer);
		}

		private String getDeclearListString() {
			StringBuilder sb = new StringBuilder();

			for (String string : declearList) {
				sb.append(string);
				sb.append("\r\n");
			}

			return sb.toString();
		}

		private String getContextListString() {
			StringBuilder sb = new StringBuilder();
			for (Node node : this.contextList) {
				if (node.type == Node.TYPE_TEXT) {
					sb.append("\t\tout.print(\""
							+ node.text.replace("\r", "").replace("\n", "")
									.replace("\\", "\\\\")
									.replace("\"", "\\\"") + "\");\r\n");
				} else if (node.type == Node.TYPE_PRINT) {
					sb.append("\t\tout.print(" + node.text + ");\r\n");// 不带引号
				} else if (node.type == Node.TYPE_CODE) {
					sb.append("\t\t" + node.text + "\r\n");
				}
			}
			return sb.toString();
		}

		private String getImportString() {
			StringBuilder sb = new StringBuilder();
			for (String importClass : IMPORT) {
				sb.append("import ");
				sb.append(importClass);
				sb.append(";\r\n");
			}
			for (String string : referList) {
				if (string.startsWith("page")) {
					string = string.substring(string.indexOf(" "),
							string.length()).trim();
					String[] pageArray = string.split(" ");
					for (String pageNode : pageArray) {
						if (pageNode.startsWith("import")) {
							pageNode = pageNode.substring(
									pageNode.indexOf("\"") + 1,
									pageNode.lastIndexOf("\""));
							String[] importArray = pageNode.split(";");
							for (String importClass : importArray) {
								sb.append("import ");
								sb.append(importClass);
								sb.append(";\r\n");
							}
						}
					}
				}
			}
			sb.append("\r\n\r\n");
			return sb.toString();
		}

		private void parseJSPElement(FileReader reader) throws IOException {
			int state;
			char[] buffer = new char[1];
			StringBuilder builder = new StringBuilder();
			// 读取一个字符
			if (reader.read(buffer) <= 0) {
				throw new IOException("the src file has error(s).");
			}
			// 根据第一个字符判断是哪一种状态
			if (buffer[0] == '=') {
				state = ClassInfo.STATE_PRINT;
			} else if (buffer[0] == '!') {
				state = ClassInfo.STATE_DECLEAR;
			} else if (buffer[0] == '@') {
				state = ClassInfo.STATE_REFER;
			} else {
				state = ClassInfo.STATE_CODE;
				builder.append(buffer[0]);
			}

			while (reader.read(buffer) > 0) {// while循环直到遇到“%>”
				if (buffer[0] == '%') {
					if (reader.read(buffer) > 0) {
						if (buffer[0] == '>') {
							if (state == STATE_CODE) {
								Node node = new Node();
								node.type = Node.TYPE_CODE;
								node.text = builder.substring(0,
										builder.length());
								node.text = node.text.replace("%\\>", "%>");
								contextList.add(node);
								return;
							} else if (state == STATE_PRINT) {
								Node node = new Node();
								node.type = Node.TYPE_PRINT;
								node.text = builder.substring(0,
										builder.length());
								node.text = node.text.replace("%\\>", "%>");
								contextList.add(node);
								return;
							} else if (state == STATE_DECLEAR) {
								declearList.add(builder.toString().replace(
										"%\\>", "%>"));
								return;
							} else if (state == STATE_REFER) {
								referList.add(builder.toString().trim());
								return;
							} else {// 不是jsp但是却遇到了“%>”说明语法有问题
								break;
							}
						} else {// 只是单个%，不是分界符
							builder.append('%');
							builder.append(buffer[0]);
						}
					} else {// 只有开始符“<%”，没有结束符
						break;
					}
				} else {// 只是不同字符，连%都不是
					builder.append(buffer[0]);
				}
			}
			// 能够到达这里说明文件有问题
			throw new IOException("the src file has error(s).");
		}
	}

}
