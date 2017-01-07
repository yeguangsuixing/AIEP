package com.twzcluster.webserver;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.njucs.aiep.base.FileIO;

//import com.ygsx.file.FileIO;

/**
 * web服务器类
 * 
 * @time 2012年12月3日23:33:34
 * @author tqc
 * */
public class TWZWebServer implements IWebServer {

	private final static String CMD_HELP = "-help";
	private final static String CMD_ROOT = "-d";
	private final static String CMD_PRINT = "-p";

	private final static String USAGE = "Usage: twzwebserver <option>\r\n"
			+ "where option is include:\r\n"
			+ "-help \t\tshow the user manual\r\n"
			+ "-d <file> \tset the dispose file\r\n"
			+ "-p \t\tprint the server dispose\r\n";

	/** 服务套接字 */
	protected ServerSocket serverSocket;
	/** 是否正在服务 */
	protected volatile boolean isServicing = false;
	/** 服务线程 */
	protected Thread serviceThread;

	/**
	 * <p>
	 * 配置文件，包括主机地址、服务端口、网站根目录等的配置信息。
	 * </p>
	 * 
	 * <p>
	 * 如果没有则采用默认配置
	 * </p>
	 * */
	protected String disposeFile;
	/** 默认的服务器地址 */
	protected static String LOCALHOST = "127.0.0.1";
	/** 默认的端口号 */
	protected static int PORT = 8080;
	/** 网站根目录，默认为当前目录 */
	public static String ROOT_DIR = System.getProperty("user.dir");
	/** 后缀引擎键值对 */
	protected static Map<String, String> SUFFIX_ENGINEER_MAP = new HashMap<String, String>();

	/**
	 * 根据默认设置创建一个服务器，主机地址为{@link #LOCALHOST 本机}，端口号为{@link #PORT 8080}
	 * */
	public TWZWebServer() {
	}

	/**
	 * 根据指定的主机地址及端口创建一个服务器
	 * 
	 * @param host
	 *            主机地址
	 * @param port
	 *            服务端口
	 * */
	public TWZWebServer(String host, int port) {
		TWZWebServer.LOCALHOST = host;
		TWZWebServer.PORT = port;
	}

	/** 启动服务 */
	public void startService() {
		if (isServicing)
			return;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("无法创建web服务(port=" + PORT + ")，原因："
					+ e.getMessage());
			return;
		}
		isServicing = true;
		serviceThread = new Thread(new ServiceRunnable(serverSocket));
		serviceThread.start();
	}

	/** 停止服务 */
	public void stopService() {
		if (!isServicing)
			return;
		isServicing = false;
		if (serverSocket != null) {
			try {
				serverSocket.close();
				serverSocket = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (serviceThread != null) {
			serviceThread.interrupt();
			serviceThread = null;
		}
	}

	/**
	 * <p>
	 * 服务器启动配置
	 * </p>
	 * <p>
	 * 注意：如果参数中带有“帮助”命令或参数错误会直接退出程序。
	 * </p>
	 * 
	 * @param args
	 *            程序启动参数
	 * @return void
	 * */
	public boolean init(String[] args) {
		if (args == null || args.length == 0)
			return true;// 没有参数则采用默认配置
		boolean help = false, print = false;
		// /parse
		for (int index = 0; index < args.length; index++) {
			if (args[index].equals(CMD_HELP)) {
				help = true;
			} else if (args[index].equals(CMD_ROOT)) {
				if (args.length < 2) {
					help = true;
					break;
				} else {
					disposeFile = args[++index];
				}
			} else if (args[index].equals(CMD_PRINT)) {
				print = true;
			} else {
				help = true;
				break;
			}
		}
		// ///handle
		if (help) {
			System.out.print(USAGE);
			// System.exit(0);
			return false;
		}
		if (disposeFile != null) {
			try {
				Dispose.dispose(disposeFile);
			} catch (Exception e) {
				e.printStackTrace();
				// System.exit(1);
				return false;
			}
		}
		if (print) {
			printServerInfo();
		}
		return true;
	}

	public boolean destroy(String[] args) {
		return true;
	}

	public String getHost() {
		return LOCALHOST;
	}

	public int getPort() {
		return PORT;
	}

	public void setHost(String host) {
		TWZWebServer.LOCALHOST = host;
	}

	public void setPort(int port) {
		TWZWebServer.PORT = port;
	}

	/**
	 * 打印服务器配置信息
	 * */
	public void printServerInfo() {
		System.out.printf("HOST: %s\r\n" + "PORT: %d\r\n" + "ROOT: %s\r\n",
				TWZWebServer.LOCALHOST, TWZWebServer.PORT,
				TWZWebServer.ROOT_DIR);
		Set<Entry<String, String>> set = TWZWebServer.SUFFIX_ENGINEER_MAP
				.entrySet();
		for (Entry<String, String> entry : set) {
			System.out.printf("%s: %s\r\n", entry.getKey(), entry.getValue());
		}
	}

	/**
	 * 服务任务类
	 * 
	 * @time 2012年12月3日23:33:34
	 * @author tqc
	 * */
	private class ServiceRunnable implements Runnable {

		private final static String METHOD_SERVICE = "service";

		protected ServerSocket serverSocket;
		/** 静态资源处理类 */
		protected StaticServlet staticServlet = new StaticServlet();
		protected HashMap<Class<?>, HttpServlet> servletList = new HashMap<Class<?>, HttpServlet>();
		protected SessionManager sessionManager = new SessionManager();

		public ServiceRunnable(ServerSocket serverSocket) {
			this.serverSocket = serverSocket;
		}

		@Override
		public void run() {
			Socket socket = null;
			while (isServicing) {
				try {
					socket = serverSocket.accept();
					System.out.printf("new conn:%s\n", socket.getInetAddress());
					handle(socket);
				} catch (Exception e) {
					e.printStackTrace(); // System.exit(1);
				} finally {// 关闭socket
					if (socket != null) {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}// end while
		}// end run

		public void handle(Socket socket) throws IOException,
				ClassNotFoundException, InstantiationException,
				IllegalAccessException, SecurityException,
				NoSuchMethodException, IllegalArgumentException,
				InvocationTargetException, ServletException {
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();

			// 创建请求对象
			Request request = new Request(input);
			String addr = socket.getInetAddress().getHostAddress();
			request.setRemoteAddr(addr);
			sessionManager.setSession(request);
			// 创建响应对象
			Response response = new Response(output);
			response.setRequest(request);
			response.setRootDir(ROOT_DIR);

			String className = TWZWebServer.SUFFIX_ENGINEER_MAP.get(request
					.getExtension());// 如果没有扩展名或没有对应的处理类则返回null
			if (className == null) {
				staticServlet.service(request, response);
				return;
			}
			Class<?> servletClass = Class.forName(className);
			HttpServlet servlet = servletList.get(servletClass);
			if (servlet == null) {
				servlet = (HttpServlet) servletClass.newInstance();
				servletList.put(servletClass, servlet);
			}
			Method method = servletClass.getDeclaredMethod(METHOD_SERVICE,
					new Class<?>[] { HttpServletRequest.class,
							HttpServletResponse.class });
			method.setAccessible(true);
			try {
				method.invoke(servlet, new Object[] { request, response });
			} catch (InvocationTargetException e) {
				Throwable throwable = e.getTargetException();
				System.out.println("throwable = " + throwable.getClass() + ":"
						+ throwable.getMessage());
				if (throwable instanceof FileNotFoundException) {
					response.send404();
				} else if (throwable instanceof ServletException) {
					response.send500();
				} else {// 其他未知的异常
					response.send500();
				}
			}
			return;
		}

	}

	/**
	 * 服务器配置类
	 * 
	 * @time 2012年12月3日23:33:34
	 * @author tqc
	 * */
	private static class Dispose {
		private final static String LOCALHOST = "localhost";
		private final static String PORT = "port";
		private final static String ROOT_DIR = "root";
		private final static String SUFFIX = "suffix";
		private final static String ERROR_404 = "error404";

		// 根据配置文件配置服务器。服务器重启生效。
		// 注意：此函数不会重启服务器。
		private static void dispose(String file) throws Exception {
			AbstractList<String[]> list = new ArrayList<String[]>();
			// 读取文件内容
			FileIO.load2List(list, file, true);
			// 清除原有数据，如果有
			TWZWebServer.SUFFIX_ENGINEER_MAP.clear();
			// 设置新数据
			for (String[] stringArray : list) {
				if (stringArray[0].equals(LOCALHOST)) {
					TWZWebServer.LOCALHOST = stringArray[1];
				} else if (stringArray[0].equals(PORT)) {
					TWZWebServer.PORT = Integer.parseInt(stringArray[1]);
				} else if (stringArray[0].equals(ROOT_DIR)) {
					TWZWebServer.ROOT_DIR = stringArray[1];
				} else if (stringArray[0].equals(SUFFIX)) {// 扩展名
					String[] values = stringArray[1].split("[ |\t]");
					if (values.length < 2) {
						throw new Exception("suffix \"" + values[0]
								+ "\" occurs error(s).");
					} else {
						TWZWebServer.SUFFIX_ENGINEER_MAP.put(values[0],
								values[1]);
					}
				} else if (stringArray[0].equals(ERROR_404)) {
					BufferedReader reader = FileIO
							.getBufferedReader(stringArray[1]);
					StringBuffer sb = new StringBuffer();
					String t;
					while ((t = reader.readLine()) != null) {
						sb.append(t);
						sb.append("\r\n");
					}
					FileIO.close(reader);
					Response.ERROR_404 = sb.toString();
				}
			}
		}
	}

	/**
	 * 程序入口
	 * 
	 * @param args
	 *            程序参数
	 * */
	public static void main(String[] args) {
		TWZWebServer server = new TWZWebServer();
		server.init(args);
		server.startService();
	}

}
