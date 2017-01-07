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
 * web��������
 * 
 * @time 2012��12��3��23:33:34
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

	/** �����׽��� */
	protected ServerSocket serverSocket;
	/** �Ƿ����ڷ��� */
	protected volatile boolean isServicing = false;
	/** �����߳� */
	protected Thread serviceThread;

	/**
	 * <p>
	 * �����ļ�������������ַ������˿ڡ���վ��Ŀ¼�ȵ�������Ϣ��
	 * </p>
	 * 
	 * <p>
	 * ���û�������Ĭ������
	 * </p>
	 * */
	protected String disposeFile;
	/** Ĭ�ϵķ�������ַ */
	protected static String LOCALHOST = "127.0.0.1";
	/** Ĭ�ϵĶ˿ں� */
	protected static int PORT = 8080;
	/** ��վ��Ŀ¼��Ĭ��Ϊ��ǰĿ¼ */
	public static String ROOT_DIR = System.getProperty("user.dir");
	/** ��׺�����ֵ�� */
	protected static Map<String, String> SUFFIX_ENGINEER_MAP = new HashMap<String, String>();

	/**
	 * ����Ĭ�����ô���һ����������������ַΪ{@link #LOCALHOST ����}���˿ں�Ϊ{@link #PORT 8080}
	 * */
	public TWZWebServer() {
	}

	/**
	 * ����ָ����������ַ���˿ڴ���һ��������
	 * 
	 * @param host
	 *            ������ַ
	 * @param port
	 *            ����˿�
	 * */
	public TWZWebServer(String host, int port) {
		TWZWebServer.LOCALHOST = host;
		TWZWebServer.PORT = port;
	}

	/** �������� */
	public void startService() {
		if (isServicing)
			return;
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			System.out.println("�޷�����web����(port=" + PORT + ")��ԭ��"
					+ e.getMessage());
			return;
		}
		isServicing = true;
		serviceThread = new Thread(new ServiceRunnable(serverSocket));
		serviceThread.start();
	}

	/** ֹͣ���� */
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
	 * ��������������
	 * </p>
	 * <p>
	 * ע�⣺��������д��С��������������������ֱ���˳�����
	 * </p>
	 * 
	 * @param args
	 *            ������������
	 * @return void
	 * */
	public boolean init(String[] args) {
		if (args == null || args.length == 0)
			return true;// û�в��������Ĭ������
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
	 * ��ӡ������������Ϣ
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
	 * ����������
	 * 
	 * @time 2012��12��3��23:33:34
	 * @author tqc
	 * */
	private class ServiceRunnable implements Runnable {

		private final static String METHOD_SERVICE = "service";

		protected ServerSocket serverSocket;
		/** ��̬��Դ������ */
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
				} finally {// �ر�socket
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

			// �����������
			Request request = new Request(input);
			String addr = socket.getInetAddress().getHostAddress();
			request.setRemoteAddr(addr);
			sessionManager.setSession(request);
			// ������Ӧ����
			Response response = new Response(output);
			response.setRequest(request);
			response.setRootDir(ROOT_DIR);

			String className = TWZWebServer.SUFFIX_ENGINEER_MAP.get(request
					.getExtension());// ���û����չ����û�ж�Ӧ�Ĵ������򷵻�null
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
				} else {// ����δ֪���쳣
					response.send500();
				}
			}
			return;
		}

	}

	/**
	 * ������������
	 * 
	 * @time 2012��12��3��23:33:34
	 * @author tqc
	 * */
	private static class Dispose {
		private final static String LOCALHOST = "localhost";
		private final static String PORT = "port";
		private final static String ROOT_DIR = "root";
		private final static String SUFFIX = "suffix";
		private final static String ERROR_404 = "error404";

		// ���������ļ����÷�������������������Ч��
		// ע�⣺�˺�������������������
		private static void dispose(String file) throws Exception {
			AbstractList<String[]> list = new ArrayList<String[]>();
			// ��ȡ�ļ�����
			FileIO.load2List(list, file, true);
			// ���ԭ�����ݣ������
			TWZWebServer.SUFFIX_ENGINEER_MAP.clear();
			// ����������
			for (String[] stringArray : list) {
				if (stringArray[0].equals(LOCALHOST)) {
					TWZWebServer.LOCALHOST = stringArray[1];
				} else if (stringArray[0].equals(PORT)) {
					TWZWebServer.PORT = Integer.parseInt(stringArray[1]);
				} else if (stringArray[0].equals(ROOT_DIR)) {
					TWZWebServer.ROOT_DIR = stringArray[1];
				} else if (stringArray[0].equals(SUFFIX)) {// ��չ��
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
	 * �������
	 * 
	 * @param args
	 *            �������
	 * */
	public static void main(String[] args) {
		TWZWebServer server = new TWZWebServer();
		server.init(args);
		server.startService();
	}

}
