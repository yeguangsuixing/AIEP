package com.njucs.aiep.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.njucs.aiep.flow.FlowRunnable;
import com.njucs.aiep.net.ReceiveEvent.RecvCmdType;


/**
 * Data Transmit Class<br />
 * This class will handle all net missions.
 * 
 * @author ygsx
 * 
 * @version 0.1
 * 
 * @time 2013Äê5ÔÂ4ÈÕ11:28:23
 * 
 * */
public class DataTransmit {
	
	public enum Lang { CPP, JAVA };
	private enum Conn { CONN_REJECT, CONN_SUCCESS, CONN_REQ_RESTART, CONN_READY };
	@SuppressWarnings("unused")
	private enum Cmd { CMD_OK, CMD_ERROR };
	
	public final static int ILLEGAL_ID = 0, SERV_REJECT = -1, CONN_TIMEOUT = -2;
	
	public final static String CODESET = "gb2312";


	private final static String TAG_TYPE = "__type";
	private final static String TAG_TIME_SEND = "__sendtime";
	private final static String TAG_DATA = "__data";
	private final static String TAG_ID = "__id";
	private final static String TAG_NAME = "__name";
	private final static String TAG_NICKNAME = "__nickname";
	private final static String TAG_LANG = "__lang";//language
	private final static String TAG_CONN = "__conn";
	@SuppressWarnings("unused")
	private final static String TAG_CMD = "__cmd";//ÃüÁî
	@SuppressWarnings("unused")
	private final static int TYPE_INFO = 0, TYPE_DATA = 1, TYPE_CONN = 2, TYPE_CMD = 3;
	
	private static DataTransmit dataTransmit;
	private static int LISTEN_ID = 0, CONN_ID = 0;
	
	/**
	 * the millsecond of timeout to connect with the server
	 * */
	public final static int DEFAULT_TIMEOUT = 5000;

	private AbstractList<ServerDataChannel> serverList = new ArrayList<ServerDataChannel>();
	private AbstractList<ClientDataChannel<?>> connectionList = new ArrayList<ClientDataChannel<?>>();
	
	private DataTransmit(){ }
	
	public synchronized static DataTransmit getInstance(){
		if( dataTransmit == null ){
			dataTransmit = new DataTransmit();
		}
		return dataTransmit;
	}

	public void sendData( JSONObject jsonObject, int listenId ) throws IOException{
		byte[] sendBuf = packData(jsonObject).toString().getBytes();
		for( ServerDataChannel serverChannel : serverList ){
			if( serverChannel.getListenId() == listenId ){
				for( ClientDataChannel<?> clientChannel : serverChannel.getClientList() ){
					DatagramPacket sendPacket = new DatagramPacket(
							sendBuf ,sendBuf.length , 
							clientChannel.getServerAddress() , 
							clientChannel.getServerPort() );
					clientChannel.getClientSocket().send( sendPacket );
				}
				break;
			}
		}
	}
	public void sendData( JSONObject jsonObject, int listenId, int connectionId ) throws IOException{
		byte[] sendBuf = packData(jsonObject).toString().getBytes();
		for( ServerDataChannel serverChannel : serverList ){
			if( serverChannel.getListenId() == listenId ){
				for( ClientDataChannel<?> clientChannel : serverChannel.getClientList() ){
					if( clientChannel.studentInfo.getConnectionId() == connectionId ){
						DatagramPacket sendPacket = new DatagramPacket(
								sendBuf ,sendBuf.length,
								clientChannel.getServerAddress(),
								clientChannel.getServerPort() );
						try{
							clientChannel.getClientSocket().send( sendPacket );
						} catch( IOException e){
							System.out.println( "Sending data failed!MSG:"+e.getMessage() );
						}
						return;
					}
				}
				System.out.println( "cannot find the recevier!" );
				return;
			}
		}
		System.out.println( "cannot find the sender!" );
	}
	public void sendRestartRequest( int listenId ){
		for( ServerDataChannel serverChannel : serverList ){
			if( serverChannel.getListenId() != listenId ) continue;
			JSONObject restartObject = new JSONObject();
			try {
				restartObject.put( TAG_TYPE, TYPE_CONN );
				restartObject.put( TAG_TIME_SEND, new Date() );
				restartObject.put( TAG_CONN, Conn.CONN_REQ_RESTART.ordinal() );
				byte[] sendBuf = restartObject.toString().getBytes();
				for( ClientDataChannel<?> clientChannel : serverChannel.getClientList() ){
					DatagramPacket sendPacket = new DatagramPacket(
							sendBuf ,sendBuf.length,
							clientChannel.getServerAddress(),
							clientChannel.getServerPort() );
						clientChannel.getClientSocket().send( sendPacket );
				}
			} catch (JSONException e) {
				e.printStackTrace(); return;
			} catch (IOException e) {
				e.printStackTrace(); return;
			}
			break;
		}//end for
		
		
	}
	public void pushData( JSONObject jsonObject, int connectionId ) throws IOException{
		byte[] sendBuf = packData(jsonObject).toString().getBytes();
		for( ClientDataChannel<?> clientChannel : connectionList ){
			if( clientChannel.studentInfo.getConnectionId() == connectionId ){
				DatagramPacket sendPacket = new DatagramPacket(
						sendBuf ,sendBuf.length , 
						clientChannel.getServerAddress() , 
						clientChannel.getServerPort() );
				clientChannel.getClientSocket().send( sendPacket );
				break;
			}
		}
	}
	
	public synchronized int createListener( int port, NewConnListener newConnListener,
			ReceiveEventListener<JSONObject> recvListener){
		System.out.println( "listen at port "+port );
		DatagramSocket  server = null;
		try { server = new DatagramSocket( port );
		} catch (SocketException e) {
			//e.printStackTrace();
			System.out.println( e.getMessage() );
		}
		if( server != null ){
			ServerDataChannel serverChannel = new ServerDataChannel();
			serverChannel.setListenId(++LISTEN_ID);
			serverChannel.setServerSocket(server);
			serverChannel.setNewConnListener(newConnListener);
			serverChannel.setReceiveEventListener(recvListener);
			Thread recvThread = new Thread( new ServerRecvRunnable( serverChannel ) );
			serverChannel.setRecvThread( recvThread );
			recvThread.start();
			
			serverList.add(serverChannel);
			
			return serverChannel.getListenId();
		} else {
			return DataTransmit.ILLEGAL_ID;
		}
	}
	/**
	 * create a connection to the given host
	 * 
	 * @param hostIp the ip of the host to conn
	 * @param hostPort the port of the host to conn
	 * @param studentInfo the info of the student of the conn, which can not be <code>null</code>
	 * @param recvListener the recv listener of the conn, which will be called 
	 * once the conn recv a msg
	 * @return 
	 * <ul>
	 * 		<li>{@link #SERV_REJECT} - if the host reject the conn request</li>
	 * 		<li>{@link #CONN_TIMEOUT} - if there are not any answer</li>
	 * 		<li>{@link #ILLEGAL_ID} - other unknown error</li>
	 * 		<li>an int greater than zero - a legal conn id</li>
	 * </ul>
	 * @see #createConnection(String, int, StudentInfo, ReceiveEventListener, int, UncaughtExceptionHandler)
	 * */
	public int createConnection( String hostIp, int hostPort, StudentInfo studentInfo,
			ReceiveEventListener<JSONObject> recvListener){
		return createConnection(hostIp, hostPort, studentInfo, recvListener, DEFAULT_TIMEOUT, null );
	}
	/**
	 * @see #createConnection(String, int, StudentInfo, ReceiveEventListener, int, UncaughtExceptionHandler)
	 * */
	public int createConnection( String hostIp, int hostPort, StudentInfo studentInfo,
			ReceiveEventListener<JSONObject> recvListener, int timeout){
		return createConnection(hostIp, hostPort, studentInfo, recvListener, timeout, null );
	}
	/**
	 * @see #createConnection(String, int, StudentInfo, ReceiveEventListener, int, UncaughtExceptionHandler)
	 * */
	public int createConnection( String hostIp, int hostPort, StudentInfo studentInfo,
			ReceiveEventListener<JSONObject> recvListener, UncaughtExceptionHandler eh){
		
		return createConnection(hostIp, hostPort, studentInfo, recvListener, DEFAULT_TIMEOUT, eh );
	}
	
	/**
	 * create a connection to the given host
	 * 
	 * @param hostIp the ip of the host to conn
	 * @param hostPort the port of the host to conn
	 * @param studentInfo the info of the student of the conn, which can not be <code>null</code>
	 * @param recvListener the recv listener of the conn, which will be called 
	 * once the conn recv a msg
	 * @param timeout the method will return {@link #CONN_TIMEOUT} over the time
	 * @return 
	 * <ul>
	 * 		<li>{@link #SERV_REJECT} - if the host reject the conn request</li>
	 * 		<li>{@link #CONN_TIMEOUT} - if there are not any answer</li>
	 * 		<li>{@link #ILLEGAL_ID} - other unknown error</li>
	 * 		<li>an int greater than zero - a legal conn id</li>
	 * </ul>
	 * @see #createConnection(String, int, StudentInfo, ReceiveEventListener)
	 * */
	public int createConnection( String hostIp, int hostPort, StudentInfo studentInfo,
			ReceiveEventListener<JSONObject> recvListener, int timeout, UncaughtExceptionHandler eh ){
		if( studentInfo.getId() == null || studentInfo.getId().length() == 0
			|| studentInfo.getName() == null || studentInfo.getName().length() == 0){
			throw new IllegalArgumentException( "studentInfo" );
		}
		DatagramSocket client = null;
		try { client = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
			return DataTransmit.ILLEGAL_ID;
		}
		JSONObject jsonObject = packInfoData( studentInfo );
		 try {
			 byte[] sendBuf = jsonObject.toString().getBytes();
			 InetAddress addr = InetAddress.getByName( hostIp );
			 DatagramPacket sendPacket 
			 	= new DatagramPacket(sendBuf ,sendBuf.length , addr , hostPort);
			 client.setSoTimeout( timeout );
			 int count = 1;//only once. please call the method again if necessary
			 int rsl = sendConnPakcet( client, count, sendPacket );
			 if( rsl > 0 ){
				 client.setSoTimeout( 0 );
				 return createConn( addr , hostPort, studentInfo, recvListener, client, eh );
			 } else {
				 return rsl;
			 }
			 //return clientChannel.studentInfo.getConnectionId();
		} catch (UnknownHostException e) {
			e.printStackTrace(); 
			return DataTransmit.ILLEGAL_ID;
		} catch (IOException e) {
			e.printStackTrace();
			return DataTransmit.ILLEGAL_ID;
		}
	}
	private int sendConnPakcet(DatagramSocket socket, 
			int count, DatagramPacket sendPacket ) {
		byte[] recvBuf = new byte[1024];
		DatagramPacket recvPacket  = new DatagramPacket(recvBuf , recvBuf.length);
		if( count <= 0 ) return CONN_TIMEOUT;
		//wait the server
		String s = null;
		try {
			socket.send(sendPacket);
			socket.receive(recvPacket);
			s = new String(recvBuf, CODESET);
			//System.out.println( "#recv#s = " + s );
			JSONObject jsonObject = new JSONObject( s );
			int type = jsonObject.getInt(TAG_TYPE);
			//Date sendTime = new Date( jsonObject.getString(TAG_TIME_SEND) );
			if ( type == TYPE_CONN ){
				int conn = jsonObject.getInt(TAG_CONN);
				if( conn == Conn.CONN_REJECT.ordinal() ){
					return SERV_REJECT;
				} else if( conn == Conn.CONN_SUCCESS.ordinal() ){
					return 1;
				}
			} else { //ignore
			}
			return sendConnPakcet(socket, count - 1 , sendPacket  );
		} catch (JSONException e) {
			e.printStackTrace();
			return ILLEGAL_ID;
		} catch (SocketTimeoutException e) {
			//System.out.println( e.getMessage() ); 
			return CONN_TIMEOUT;
		} catch (IOException e) {
			e.printStackTrace();
			return ILLEGAL_ID;
		}
	}
	private synchronized int createConn(InetAddress addr, int hostPort, StudentInfo studentInfo,
			ReceiveEventListener<JSONObject> recvListener , DatagramSocket socket,
			UncaughtExceptionHandler eh) throws IOException{
		ClientDataChannel<JSONObject> clientChannel = new ClientDataChannel<JSONObject>();
		clientChannel.setServerAddress( addr );
		clientChannel.setClientSocket(socket);
		//clientChannel.setConnectionId(++CONN_ID);
		studentInfo.setConnectionId(++CONN_ID);
		clientChannel.setServerPort(hostPort);
		clientChannel.setStudentInfo(studentInfo);
		clientChannel.setReceiveEventListener(recvListener);
		
		connectionList.add(clientChannel);
		
		//Thread recvThread = new Thread( new ClientRecvRunnable( clientChannel ) );
		Thread recvThread = new Thread( new ClientRecvRunnable( clientChannel ) );
		if( eh != null ) {
			recvThread.setUncaughtExceptionHandler(eh);
		}
		clientChannel.setRecvThread( recvThread );
		recvThread.start(); 
		
		//sendCreateConnSuccessPacket(socket, addr, hostPort  );
		
		return studentInfo.getConnectionId();
	}
	@SuppressWarnings("unused")
	private boolean sendCreateConnSuccessPacket( DatagramSocket socket,
			InetAddress addr, int hostPort ){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put( TAG_TYPE, TYPE_CONN );
			jsonObject.put( TAG_TIME_SEND, new Date() );
			jsonObject.put(TAG_CONN, Conn.CONN_SUCCESS.ordinal());
			byte[] sendBuf = jsonObject.toString().getBytes();
			DatagramPacket sendPacket 
				= new DatagramPacket(sendBuf ,sendBuf.length , addr , hostPort);
			socket.send(sendPacket);
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
 	public AbstractList<StudentInfo> getConnectionList ( int listenId ){
		AbstractList<StudentInfo> t = new ArrayList<StudentInfo>();
		for( ServerDataChannel serverChannel: serverList ){
			if( serverChannel.getListenId() == listenId ){
				for( ClientDataChannel<?> clientChannel : serverChannel.getClientList()){
					t.add(clientChannel.getStudentInfo());
				}
				break;
			}
		}
		return t;
	}
	
	/**
	 * set the max conn number of the listener 
	 * 
	 * @param listenId the id of  the listener to set
	 * @param maxConn the max conn to set
	 * */
	public void setMaxCount( int listenId,  int maxConn ){
		for( ServerDataChannel serverChannel: serverList ){
			if( serverChannel.getListenId() == listenId ){
				serverChannel.setMaxConn(maxConn);
				break;
			}
		}
	}
	/**
	 * get the max conn of the given listener
	 * 
	 * @param listenId the id of the given listener
	 * */
	public int getMaxCount( int listenId ){
		for( ServerDataChannel serverChannel: serverList ){
			if( serverChannel.getListenId() == listenId ){
				return serverChannel.getMaxConn();
			}
		}
		return -1;
	}
	
	/**
	 * close the listener
	 * 
	 * @param listenId the listen Id to close
	 * */
	public void closeListener( int listenId ){
		for( ServerDataChannel sdc :  serverList ){
			if( sdc.getListenId() == listenId ){
				for(ClientDataChannel<?> cdc : sdc.getClientList() ){
					cdc.getClientSocket().close();
					//sdc.getClientList().remove(cdc);
				}
				sdc.getClientList().clear();
				sdc.getServerSocket().close();
				serverList.remove(sdc);
				break;
			}
		}
	}
	
	/**
	 * close the connection to release the resource
	 * 
	 * @param connectionId the id of the connection to close
	 * */
	public void closeConnection( int connectionId ){
		System.out.println( "close conn(id="+connectionId+")." );
		for(ClientDataChannel<?> cdc : connectionList ){
			if( cdc.getStudentInfo().getConnectionId() == connectionId ){
				cdc.getClientSocket().close();
				connectionList.remove(cdc);
				break;
			}
		}
	}
	
	/**
	 * close all listeners and conns
	 * */
	public synchronized void closeAll(){
		//System.out.println( "closeAll." );
		for(ClientDataChannel<?> cdc : connectionList ){
			cdc.getClientSocket().close();
		}
		connectionList.clear();
		for( ServerDataChannel sdc :  serverList ){
			for(ClientDataChannel<?> cdc : sdc.getClientList() ){
				cdc.getClientSocket().close();
			}
			sdc.getClientList().clear();
			sdc.getServerSocket().close();
		}
		serverList.clear();
	}
	
	/**
	 * bind or unbind the FlowEventListener(recvListener) from/to some connection
	 * 
	 * @param listener the bound listener, if listener is null, it will be unbound from conn,
	 * @param listenId the id of the listen of the connection
	 * @param connId the id of the connection
	 * */
	public   void bindFlowRunnable( FlowRunnable<JSONObject> runnable, int listenId, int connId ){
		for( ServerDataChannel sdc : serverList ){
			if( sdc.getListenId() == listenId ){
				for( ClientDataChannel<JSONObject> cdc : sdc.getClientList() ){
					if( cdc.studentInfo.getConnectionId() == connId ){
						cdc.setFlowRunnable(runnable);
						break;
					}
				}
				break;
			}
		}
	}
	
	
	private JSONObject packData(JSONObject data){
		JSONObject finalObject = new JSONObject();
		try {
			finalObject.put( TAG_TYPE, TYPE_DATA );
			finalObject.put( TAG_TIME_SEND, new Date() );
			finalObject.put( TAG_DATA, data);
		} catch (JSONException e1) {
			e1.printStackTrace(); return finalObject;
		}
		return finalObject;
	}
	private JSONObject packInfoData(StudentInfo studentInfo){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put( TAG_TYPE, TYPE_INFO );
			jsonObject.put( TAG_TIME_SEND, new Date() );
			jsonObject.put(TAG_ID, studentInfo.getId() );
			jsonObject.put(TAG_NAME, studentInfo.getName() );
			jsonObject.put(TAG_NICKNAME, studentInfo.getNickname() );
			jsonObject.put( TAG_LANG, Lang.JAVA.ordinal() );
			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return jsonObject;
		}
	}
	
	
	public static class StudentInfo {
		private String nickname;
		private String name;
		private String id;
		private int connectionId;
		
		public StudentInfo(String id, String name, String nickname){
			this.id = id;
			this.name = name;
			this.nickname = nickname;
		}
		
		/**
		 * @return the nickname
		 */
		public String getNickname() {
			return nickname;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}
		/**
		 * @param nickname the nickname to set
		 */
		public void setNickname(String nickname) {
			this.nickname = nickname;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/**
		 * @param id the id to set
		 */
		public void setId(String id) {
			this.id = id;
		}

		/**
		 * @param connectionId the connectionId to set
		 */
		protected void setConnectionId(int connectionId) {
			this.connectionId = connectionId;
		}

		/**
		 * @return the connectionId
		 */
		public int getConnectionId() {
			return connectionId;
		}
	}
	
	private class ServerDataChannel {
		
		private int maxConn = 1;
		
		private int listenId;
		private DatagramSocket serverSocket;
		private AbstractList<ClientDataChannel<JSONObject>> clientList = new ArrayList<ClientDataChannel<JSONObject>>();
		
		@SuppressWarnings("unused")
		private Thread recvThread;
		
		private NewConnListener newConnListener;
		private ReceiveEventListener<JSONObject> receiveEventListener;
		
		/**
		 * @return the listenId
		 */
		public int getListenId() {
			return listenId;
		}
		/**
		 * @return the clientList
		 */
		public AbstractList<ClientDataChannel<JSONObject>> getClientList() {
			return clientList;
		}
		/**
		 * @param listenId the listenId to set
		 */
		public void setListenId(int listenId) {
			this.listenId = listenId;
		}

		/**
		 * @param serverSocket the serverSocket to set
		 */
		public void setServerSocket(DatagramSocket serverSocket) {
			this.serverSocket = serverSocket;
		}
		/**
		 * @return the serverSocket
		 */
		public DatagramSocket getServerSocket() {
			return serverSocket;
		}
		/**
		 * @param recvThread the recvThread to set
		 */
		public void setRecvThread(Thread recvThread) {
			this.recvThread = recvThread;
		}

		public void setNewConnListener(NewConnListener newConnListener) {
			this.newConnListener = newConnListener;
		}
		public NewConnListener getNewConnListener() {
			return newConnListener;
		}
		public void setReceiveEventListener(ReceiveEventListener<JSONObject> receiveEventListener) {
			this.receiveEventListener = receiveEventListener;
		}
		public ReceiveEventListener<JSONObject> getReceiveEventListener() {
			return receiveEventListener;
		}
		/**
		 * @param maxConn the maxConn to set
		 */
		public void setMaxConn(int maxConn) {
			this.maxConn = maxConn;
		}
		/**
		 * @return the maxConn
		 */
		public int getMaxConn() {
			return maxConn;
		}
		
	}
	
	private class ClientDataChannel<E> {

		private InetAddress serverAddress;
		private int serverPort;
		private DatagramSocket clientSocket;
		
		@SuppressWarnings("unused")
		private Thread recvThread;
		
		private FlowRunnable<E> flowRunnable;
		private ReceiveEventListener<E> receiveEventListener;
		
		private StudentInfo studentInfo;


		/**
		 * @return the studentInfo
		 */
		public StudentInfo getStudentInfo() {
			return studentInfo;
		}

		/**
		 * @return the serverPort
		 */
		public int getServerPort() {
			return serverPort;
		}

		/**
		 * @param serverPort the serverPort to set
		 */
		public void setServerPort(int serverPort) {
			this.serverPort = serverPort;
		}
		/**
		 * @param clientSocket the clientSocket to set
		 */
		public void setClientSocket(DatagramSocket clientSocket) {
			this.clientSocket = clientSocket;
		}
		/**
		 * @return the clientSocket
		 */
		public DatagramSocket getClientSocket() {
			return clientSocket;
		}
		/**
		 * @param studentInfo the studentInfo to set
		 */
		public void setStudentInfo(StudentInfo studentInfo) {
			this.studentInfo = studentInfo;
		}

		public void setFlowRunnable(FlowRunnable<E> flowRunnable) {
			this.flowRunnable = flowRunnable;
		}
		public FlowRunnable<E> getFlowRunnable() {
			return flowRunnable;
		}
		public void setReceiveEventListener(ReceiveEventListener<E> receiveEventListener) {
			this.receiveEventListener = receiveEventListener;
		}
		public ReceiveEventListener<E> getReceiveEventListener() {
			return receiveEventListener;
		}
		public void setRecvThread(Thread recvThread) {
			this.recvThread = recvThread;
		}
		/**
		 * @param serverAddress the serverAddress to set
		 */
		public void setServerAddress(InetAddress serverAddress) {
			this.serverAddress = serverAddress;
		}
		/**
		 * @return the serverAddress
		 */
		public InetAddress getServerAddress() {
			return serverAddress;
		}


	}
	
	
	private class ServerRecvRunnable implements Runnable {
		
		private ServerDataChannel serverChannel;
		
		public ServerRecvRunnable( ServerDataChannel serverChannel ){
			this.serverChannel = serverChannel;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			if( this.serverChannel == null ) return;
			byte[] recvBuf = new byte[1024];
			DatagramPacket recvPacket  = new DatagramPacket(recvBuf, recvBuf.length);

			while ( true ) {
				for( int i = 0; i < recvBuf.length; i ++ ) recvBuf[i] = 0;
				//System.arraycopy(src, srcPos, recvBuf, 0, 1024);
				try {
					serverChannel.getServerSocket().receive(recvPacket);
				} catch (IOException e) {
					//e.printStackTrace();//not print
					return;//break the current thread and return
				}/*
				System.out.println( "recvBuf.length="+recvBuf.length );
				for(int i = 0; i < recvBuf.length; i ++){
					System.out.print( (char)recvBuf[i] );
				}
				System.out.println();//*/
				String s = null;
				try {
					s = new String(recvBuf,  CODESET );//
					//System.out.println( "s = "+ s );
				} catch (UnsupportedEncodingException e2) {
					e2.printStackTrace();
					continue;
				}
				try {
					JSONObject jsonObject = new JSONObject( s );
					int type = jsonObject.getInt(TAG_TYPE);
					Date sendTime = new Date( jsonObject.getString(TAG_TIME_SEND) );
					if( type == TYPE_INFO ){
						newConn( jsonObject, recvPacket.getAddress(), recvPacket.getPort() );
					} else if ( type == TYPE_DATA ){
						JSONObject obj = new JSONObject( jsonObject.getString(TAG_DATA) );
						handleData( sendTime, obj, recvPacket.getAddress(), recvPacket.getPort() );
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					continue;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void newConn( JSONObject obj, InetAddress addr, int port ) throws IOException, JSONException{
			if( this.serverChannel.getClientList().size() >= this.serverChannel.getMaxConn()  ){
				//System.out.println( "[DataTrasnmit]too many connection!" );
				this.sendRejectPackage( this.serverChannel.serverSocket, addr, port );
				return;
			} else {
				this.sendServerInfoPackage( this.serverChannel.serverSocket, addr, port );
				/*byte[] recvBuf = new byte[1024];
				DatagramPacket recvPacket  = new DatagramPacket(recvBuf , recvBuf.length);
				this.serverChannel.getServerSocket().receive(recvPacket);
				String s = new String(recvBuf, CODESET);
				System.out.println( "#Server#s="+s );
				JSONObject jsonObject = new JSONObject( s );
				int type = jsonObject.getInt(TAG_TYPE);
				if( type == TYPE_CONN ){
					if( jsonObject.getInt(TAG_CONN) == Conn.CONN_SUCCESS.ordinal() ){
						//do nothing
					} else {
						return ;//error
					}
				} else {
					return;//error
				}//*/
			}
			
			String nickname = obj.getString(TAG_NICKNAME);
			String name = obj.getString(TAG_NAME);
			String id = obj.getString(TAG_ID);
			Lang lang;
			if( obj.getInt(TAG_LANG) == Lang.CPP.ordinal() ) {
				lang = Lang.CPP;
			} else {
				lang = Lang.JAVA;
			}
				
			StudentInfo info = new StudentInfo( id, name, nickname );
			info.setConnectionId(++CONN_ID);
			
			ClientDataChannel<JSONObject> newChannel = new ClientDataChannel<JSONObject>();
			newChannel.setClientSocket(serverChannel.serverSocket);
			//newChannel.setConnectionId(++CONN_ID);//the connId is saved in the studentInfo obj
			newChannel.setServerAddress( addr );
			newChannel.setServerPort( port);
			newChannel.setStudentInfo(info);
			serverChannel.getClientList().add(newChannel);
			
			if( serverChannel.getNewConnListener() != null ){
				NewConnEvent e = new NewConnEvent( this, serverChannel.getListenId(),
						newChannel.studentInfo.getConnectionId(), info, lang );
				serverChannel.getNewConnListener().newConn(e);
			}
			//System.out.println( "A new Conn!" );
		}
		private void handleData(  Date sendTime, JSONObject obj, InetAddress addr, int port  ){
			FlowRunnable<JSONObject> runnable = null;
			ClientDataChannel<JSONObject> clientChannel = null;
			//System.out.println( "addr="+addr.toString()+", port="+port );
			for( ClientDataChannel<JSONObject> cdc : serverChannel.getClientList() ){
				//System.out.println( "cmp: addr="+cdc.getServerAddress()+", port="+cdc.getServerPort() );
				if( cdc.getServerAddress().toString().equals(addr.toString()) 
						&& cdc.getServerPort() == port ){
					clientChannel = cdc;
					runnable = cdc.getFlowRunnable();
					break;
				}
			}//System.out.println("#ddd1");
			if( runnable != null ){
				runnable.run(sendTime, clientChannel.getStudentInfo(),obj);
			} else if( clientChannel != null && serverChannel.getReceiveEventListener() != null ){
				//System.out.println("#ddd3");
				serverChannel.getReceiveEventListener().receive( 
						new ReceiveEvent<JSONObject>(this, sendTime, clientChannel.getStudentInfo(), obj)
				);
			}/*
			if( clientChannel == null ){
				System.out.println( "#ddd clientChannel == null" );
			} 
			System.out.println("#ddd2");//*/
		}
		
		private void sendRejectPackage( DatagramSocket socket, InetAddress address, int port ){
			JSONObject dataObject = new JSONObject();
			try {
				dataObject.put( TAG_TYPE, TYPE_CONN );
				dataObject.put( TAG_TIME_SEND, new Date() );
				dataObject.put( TAG_CONN, Conn.CONN_REJECT.ordinal() );
				byte[] sendBuf = dataObject.toString().getBytes();
				DatagramPacket sendPacket = new DatagramPacket(
						sendBuf ,sendBuf.length, address, port );
				socket.send( sendPacket );
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		private void sendServerInfoPackage( DatagramSocket socket, InetAddress address, int port ){
			JSONObject dataObject = new JSONObject();
			try {
				dataObject.put( TAG_TYPE, TYPE_CONN );
				dataObject.put( TAG_TIME_SEND, new Date() );
				dataObject.put( TAG_CONN, Conn.CONN_SUCCESS.ordinal() );
				byte[] sendBuf = dataObject.toString().getBytes();
				DatagramPacket sendPacket = new DatagramPacket(
						sendBuf ,sendBuf.length, address, port );
				socket.send( sendPacket );
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private class ClientRecvRunnable implements Runnable {
		private ClientDataChannel<JSONObject> clientChannel;
		
		public ClientRecvRunnable( ClientDataChannel<JSONObject> clientChannel ){
			this.clientChannel = clientChannel;
		}
		@SuppressWarnings("deprecation")
		@Override
		public void run() {
			byte[] recvBuf = new byte[1024];
			DatagramPacket recvPacket  = new DatagramPacket(recvBuf , recvBuf.length);
			
			
			while ( true ) {
				try {
					clientChannel.getClientSocket().receive(recvPacket);
				} catch (IOException e) {
					//e.printStackTrace();
					return;//break the current thread and return
				}
				String s = null;
				try {
					s = new String(recvBuf, CODESET);
				} catch (UnsupportedEncodingException e2) {
					e2.printStackTrace();
					continue;
				}
				try {
					JSONObject jsonObject = new JSONObject( s );
					int type = jsonObject.getInt(TAG_TYPE);
					Date sendTime = new Date( jsonObject.getString(TAG_TIME_SEND) );
					if ( type == TYPE_DATA ){
						JSONObject obj = new JSONObject( jsonObject.getString(TAG_DATA) );
						handleData( sendTime, obj, recvPacket.getAddress(), recvPacket.getPort() );
					} else if( type == TYPE_CONN ) {
						int conn = jsonObject.getInt(TAG_CONN);
						handleConn( sendTime, conn, recvPacket.getAddress(), recvPacket.getPort() );
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					continue;
				}
			}
		}
		private void handleData(Date sendTime, JSONObject obj, InetAddress address,
				int port) {
			FlowRunnable<JSONObject> runnable = clientChannel.getFlowRunnable();
			if( runnable != null ){
				runnable.run(sendTime, clientChannel.getStudentInfo(), obj);
			} else if( clientChannel.getReceiveEventListener() != null ){
				clientChannel.getReceiveEventListener().receive( 
						new ReceiveEvent<JSONObject>(this, sendTime, clientChannel.getStudentInfo(), obj)
				);
			}
		}
		private void handleConn(Date sendTime, int conn, InetAddress address,
				int port){
			//in the current version(0.1) only one command via TYPE_CONN
			if( conn == Conn.CONN_REQ_RESTART.ordinal() ){
				if( clientChannel.getReceiveEventListener() != null ){
					clientChannel.getReceiveEventListener().receive( 
							new ReceiveEvent<JSONObject>(this, sendTime, clientChannel.getStudentInfo(), RecvCmdType.RESTART)
					);
				}
			}
		}

		
	}
	
	public static void main(String[] args){
		DataTransmit dt = DataTransmit.getInstance();
		//*
		//@SuppressWarnings("unused")
		final int lid = dt.createListener( 8890, new NewConnListener(){
			@Override
			public void newConn(NewConnEvent e) {
				System.out.println( "new conn:"+e.getConnId()+
						", No="+e.getStudentInfo().getId()+
						",name="+e.getStudentInfo().getName() +
						",nickname="+e.getStudentInfo().getNickname());

				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("welcom", "Welcome to us!And please send me your info in 3s");
					DataTransmit.getInstance().sendData(jsonObject, e.getListenId(), e.getConnId());
				} catch (JSONException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}, new ReceiveEventListener<JSONObject>(){
			@SuppressWarnings("deprecation")
			@Override
			public void receive(ReceiveEvent<JSONObject> event) {
				System.out.println( "server recv:"+event.getData() +", at "+event.getSendTime().getHours() );
			}
		});
		dt.setMaxCount(lid, 1);

	}
	
}







