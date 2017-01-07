package com.twzcluster.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Date;

import com.twzcluster.net.socket.ListenerSocketInfo;
import com.twzcluster.net.socket.QueueSocketInfo;
import com.twzcluster.net.socket.ServerSocketInfo;
import com.twzcluster.net.socket.SocketInfo;

//////如果某消息是回复的，那么这个消息将不会放入消息队列或发生收到消息事件
public class NetManager implements INetManager {

	private static NetManager manager;

	private AbstractList<ServerSocketInfo> serverSocketInfoList = new ArrayList<ServerSocketInfo>();
	private AbstractList<SocketInfo> socketInfoList = new ArrayList<SocketInfo>();
	private AbstractList<ResponseListenerInfo> respListenerInfoList = new ArrayList<ResponseListenerInfo>();
	private int SERVICE_ID = 0;
	private int RECEIVER_ID = 0;

	private NetManager() {
	}

	public static NetManager newInstance() {
		if (manager == null)
			manager = new NetManager();
		return manager;
	}

	@Override
	public boolean init(String[] args) {
		return true;
	}

	@Override
	public boolean destroy(String[] args) {
		for (ServerSocketInfo serverInfo : serverSocketInfoList) {
			AbstractList<SocketInfo> socketInfoList = serverInfo
					.getSocketList();
			for (SocketInfo info : socketInfoList) {
				try {
					info.getObjectInputStream().close();
					info.getObjectOutputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socketInfoList.clear();
		}
		serverSocketInfoList.clear();
		for (SocketInfo socketInfo : socketInfoList) {
			try {
				socketInfo.getObjectInputStream().close();
				socketInfo.getObjectOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		socketInfoList.clear();
		respListenerInfoList.clear();
		return true;
	}

	@Override
	public int createListener(String listenHost, int listenPort) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(listenPort);
		} catch (IOException e) {
			e.printStackTrace();
			return ILLEGAL_ID;
		}
		ServerSocketInfo info = new ServerSocketInfo(++SERVICE_ID, serverSocket);
		serverSocketInfoList.add(info);
		new Thread(new ListenerRunnable(serverSocket, info)).start();
		return info.getServiceId();
	}

	public int createListener(String listenHost, int listenPort,
			NewConnListener newListener, BreakConnListener breakListener,
			ReceiveListener receiveListener) {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(listenPort);
		} catch (IOException e) {
			e.printStackTrace();
			return ILLEGAL_ID;
		}
		ServerSocketInfo info = new ServerSocketInfo(++SERVICE_ID,
				serverSocket, receiveListener, breakListener);
		serverSocketInfoList.add(info);
		Thread listenThread = new Thread(new ListenerRunnable(serverSocket,
				info, newListener, breakListener));
		listenThread
				.setName("com.twzcluster.net.NetManager.createListener().listenThread");
		listenThread.start();
		return info.getServiceId();
	}

	@Override
	public int createConnection(String remoteHost, int remotePort) {
		Socket socket = null;
		try {
			socket = new Socket(remoteHost, remotePort);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return ILLEGAL_ID;
		} catch (IOException e) {
			e.printStackTrace();
			return ILLEGAL_ID;
		}
		QueueSocketInfo info = new QueueSocketInfo(++SERVICE_ID, socket,
				++RECEIVER_ID);
		socketInfoList.add(info);
		new Thread(new SocketRunnable(info)).start();
		return info.getConn().getServiceId();
	}

	@Override
	public int createConnection(String remoteHost, int remotePort,
			BreakConnListener breakListener, ReceiveListener listener) {
		if (listener == null)
			return createConnection(remoteHost, remotePort);
		Socket socket = null;
		try {
			socket = new Socket(remoteHost, remotePort);
		} catch (UnknownHostException e) {
			// e.printStackTrace();
			return ILLEGAL_ID;
		} catch (IOException e) {
			// e.printStackTrace();
			return ILLEGAL_ID;
		}
		ListenerSocketInfo info = new ListenerSocketInfo(++SERVICE_ID, socket,
				++RECEIVER_ID, listener);
		socketInfoList.add(info);
		new Thread(new SocketRunnable(info, breakListener)).start();
		return info.getConn().getServiceId();
	}

	@Override
	public void stopService(int serviceid) {
		// boolean hasHandle = false;用removeServerInfo代替
		ServerSocketInfo removeServerInfo = null;
		for (ServerSocketInfo serverInfo : serverSocketInfoList) {
			if (serverInfo.getServiceId() != serviceid)
				continue;
			AbstractList<SocketInfo> socketInfoList = serverInfo
					.getSocketList();
			for (SocketInfo info : socketInfoList) {
				try {
					info.getObjectInputStream().close();
					info.getObjectOutputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			socketInfoList.clear();
			try {
				serverInfo.getServerSocket().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			removeServerInfo = serverInfo;
			break;
		}
		if (removeServerInfo != null) {
			serverSocketInfoList.remove(removeServerInfo);
			return;
		}
		for (SocketInfo socketInfo : socketInfoList) {
			if (socketInfo.getConn().getServiceId() != serviceid)
				continue;
			try {
				socketInfo.getObjectInputStream().close();
				socketInfo.getObjectOutputStream().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			socketInfoList.remove(socketInfo);
			break;
		}
	}

	public boolean closeConn(int connId) {
		for (ServerSocketInfo serverInfo : serverSocketInfoList) {
			AbstractList<SocketInfo> socketInfoList = serverInfo
					.getSocketList();
			for (SocketInfo info : socketInfoList) {
				if (info.getConn().getConnId() != connId)
					continue;
				try {
					info.getObjectInputStream().close();
					info.getObjectOutputStream().close();
					System.out.println("来自NetManager：#1断开connid=" + connId
							+ ",servid=" + serverInfo.getServiceId());
					socketInfoList.remove(info);
					return true;
				} catch (IOException e) {
					e.printStackTrace();
					return false;
				}
			}
		}
		for (SocketInfo socketInfo : socketInfoList) {
			if (socketInfo.getConn().getConnId() != connId)
				continue;
			try {
				socketInfo.getObjectInputStream().close();
				socketInfo.getObjectOutputStream().close();
				System.out.println("来自NetManager：#2断开connid=" + connId
						+ ",servid=" + socketInfo.getConn().getServiceId());
				socketInfoList.remove(socketInfo);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;
	}// */

	@Override
	public boolean pushMessage(int serviceid, Message message) {
		for (SocketInfo info : socketInfoList) {// 如果是客户端形式的，不需要指定接收者
			if (info.getConn().getServiceId() == serviceid) {
				return sendMessage(info.getObjectOutputStream(), message);
			}
		}
		if (message.getReceiverid() == Message.DEFAULT) {
			// 如果此消息未指定接收者者，说明是广播形式
			for (ServerSocketInfo info : serverSocketInfoList) {
				if (info.getServiceId() == serviceid) {
					for (SocketInfo socketInfo : info.getSocketList()) {
						if (!sendMessage(socketInfo.getObjectOutputStream(),
								message))
							return false;
					}
					return true;
				}
			}
			return false;
		} else {
			for (ServerSocketInfo info : serverSocketInfoList) {
				if (info.getServiceId() == serviceid) {
					for (SocketInfo socketInfo : info.getSocketList()) {
						if (socketInfo.getConn().getConnId() == message
								.getReceiverid()) {
							return sendMessage(
									socketInfo.getObjectOutputStream(), message);
						}
					}
					return false;// 该服务没有指定的接收者
				}
			}
		}
		return false;
	}

	@Override
	public boolean pushMessage(int serviceid, Message message,
			ReceiveListener listener) {
		if (!pushMessage(serviceid, message))
			return false;
		respListenerInfoList.add(new ResponseListenerInfo(serviceid, message
				.getId(), listener));
		return true;
	}

	@Override
	public AbstractList<Message> popMessage(int serviceid, Object[] args) {
		boolean hasHandle = false;
		AbstractList<Message> list = new ArrayList<Message>();
		for (SocketInfo info : socketInfoList) {
			if (info.getConn().getServiceId() == serviceid) {
				if (info instanceof QueueSocketInfo) {
					AbstractList<Message> messageList = ((QueueSocketInfo) info)
							.getMessageList();
					for (Message msg : messageList) {
						list.add(msg);
					}
					messageList.clear();
				}
				hasHandle = true;
				break;
			}
		}
		if (hasHandle)
			return list;
		for (ServerSocketInfo info : serverSocketInfoList) {
			if (info.getServiceId() == serviceid) {
				if (info.getType() == ServerSocketInfo.LISTENER) {
					return list;
				}
				AbstractList<SocketInfo> infoList = info.getSocketList();
				for (SocketInfo socketInfo : infoList) {
					AbstractList<Message> messageList = ((QueueSocketInfo) socketInfo)
							.getMessageList();
					for (Message msg : messageList) {
						list.add(msg);
					}
					messageList.clear();
				}
				break;
			}
		}
		return list;
	}

	public int getConnectionId(int serviceId) {
		for (SocketInfo info : socketInfoList) {
			if (info.getConn().getServiceId() == serviceId)
				return info.getConn().getConnId();
		}
		return Message.DEFAULT;
	}

	public AbstractList<Integer> getConnectionsId(int serviceId) {
		AbstractList<Integer> idList = new ArrayList<Integer>();
		for (ServerSocketInfo info : serverSocketInfoList) {
			if (info.getServiceId() == serviceId) {
				for (SocketInfo si : info.getSocketList()) {
					idList.add(si.getConn().getConnId());
				}
			}
		}
		return idList;
	}

	public int getServiceId(int connId) {
		for (ServerSocketInfo info : serverSocketInfoList) {
			for (SocketInfo si : info.getSocketList()) {
				if (si.getConn().getConnId() == connId)
					return si.getConn().getServiceId();
			}
		}
		for (SocketInfo info : socketInfoList) {
			if (info.getConn().getServiceId() == connId)
				return info.getConn().getServiceId();
		}
		return INetManager.ILLEGAL_ID;
	}

	private boolean sendMessage(ObjectOutputStream oos, Message message) {
		message.setSendTime(new Date());
		try {
			oos.writeObject(message);
			// oos.close();//此流不需要关闭
			return true;
		} catch (IOException e) {
			// e.printStackTrace();
			return false;
		}
	}

	class SocketRunnable implements Runnable {
		SocketInfo socketInfo;
		BreakConnListener breakListener;

		public SocketRunnable(SocketInfo socketInfo) {
			this.socketInfo = socketInfo;
		}

		public SocketRunnable(SocketInfo socketInfo,
				BreakConnListener breakListener) {
			this.socketInfo = socketInfo;
			this.breakListener = breakListener;
		}

		@Override
		public void run() {
			ObjectInputStream ois = null;
			int serviceid = socketInfo.getConn().getServiceId();
			ois = socketInfo.getObjectInputStream();
			while (true) {
				Message msg = null;
				boolean hasHandle = false;
				try {
					msg = (Message) ois.readObject(); /*
													 * System.out.println(
													 * "Rev Msg:type="
													 * +msg.type+",content="
													 * +msg.content );//
													 */
				} catch (Exception e) { // e.printStackTrace();
					NetManager.this.closeConn(socketInfo.getConn().getConnId());
					if (breakListener != null)
						breakListener.breakConn(new BreakEvent(socketInfo
								.getConn(), e.getMessage()));
					// NetManager.this.stopService(socketInfo.getConn().getServiceId());
					break;
				}
				msg.setSenderid(socketInfo.getConn().getConnId());
				msg.setReceiveTime(new Date());
				if (msg.getResponseId() != Message.DEFAULT) {
					ResponseListenerInfo respListener = null;
					for (ResponseListenerInfo responseListener : respListenerInfoList) {
						if (responseListener.getServiceid() == serviceid
								&& responseListener.getResponseid() == msg
										.getResponseId()) {
							responseListener.getListener().receivedMessage(
									new ReceiveEvent(this,
											socketInfo.getConn(), msg));
							respListener = responseListener;
							hasHandle = true;
							break;
						}
					}
					respListenerInfoList.remove(respListener);
				}
				if (hasHandle)
					continue;// 如果某消息是回复的，那么这个消息将不会放入消息队列或发生收到消息事件
				if (socketInfo instanceof QueueSocketInfo) {
					((QueueSocketInfo) socketInfo).pushMessage(msg);
				} else if (socketInfo instanceof ListenerSocketInfo) {
					ReceiveListener listener = ((ListenerSocketInfo) socketInfo)
							.getListener();
					if (listener != null) {
						listener.receivedMessage(new ReceiveEvent(this,
								socketInfo.getConn(), msg));
					}
				}
			}
		}

	}

	class ListenerRunnable implements Runnable {
		ServerSocket serverSocket;
		ServerSocketInfo serverSocketInfo;
		NewConnListener newConnectionListener;
		BreakConnListener breakListener;

		public ListenerRunnable(ServerSocket serverSocket,
				ServerSocketInfo serverSocketInfo) {
			this.serverSocket = serverSocket;
			this.serverSocketInfo = serverSocketInfo;
		}

		public ListenerRunnable(ServerSocket serverSocket,
				ServerSocketInfo serverSocketInfo,
				NewConnListener newConnectionListener,
				BreakConnListener breakListener) {
			this(serverSocket, serverSocketInfo);
			this.newConnectionListener = newConnectionListener;
			this.breakListener = breakListener;
		}

		@Override
		public void run() {
			while (true) {
				Socket socket = null;
				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
					// e.printStackTrace();
					NetManager.this
							.stopService(serverSocketInfo.getServiceId());
					return;
				}
				SocketInfo info;
				if (serverSocketInfo.getType() == ServerSocketInfo.LISTENER) {
					info = new ListenerSocketInfo(
							serverSocketInfo.getServiceId(), socket,
							++RECEIVER_ID,
							(ReceiveListener) serverSocketInfo
									.getEventListener());
				} else {
					info = new QueueSocketInfo(serverSocketInfo.getServiceId(),
							socket, ++RECEIVER_ID);
				}
				serverSocketInfo.getSocketList().add(info);
				new Thread(new SocketRunnable(info, breakListener)).start();
				if (this.newConnectionListener != null) {
					newConnectionListener.newConn(new NewConnEvent(this, info
							.getConn()));
				}
			}
		}
	}

	class ResponseListenerInfo {
		private int serviceid;
		private int responseid;
		ReceiveListener listener;

		public ResponseListenerInfo(int serviceid, int responseid,
				ReceiveListener listener) {
			this.serviceid = serviceid;
			this.responseid = responseid;
			this.listener = listener;
		}

		public int getServiceid() {
			return serviceid;
		}

		public int getResponseid() {
			return responseid;
		}

		public ReceiveListener getListener() {
			return listener;
		}

	}

}
