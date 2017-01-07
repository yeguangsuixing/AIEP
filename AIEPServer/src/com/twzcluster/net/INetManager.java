package com.twzcluster.net;

import java.util.AbstractList;

/**
 * 网络管理接口。提供网络服务。
 * 
 * @time 2012年12月5日20:27:43
 * @author tqc
 * */
public interface INetManager {

	/**
	 * 非法连接服务id
	 * */
	public static final int ILLEGAL_ID = -1;

	/**
	 * <p>
	 * 初始化网络管理。
	 * </p>
	 * <p>
	 * 如果某些操作比较耗时，请在这里运行。
	 * </p>
	 * 
	 * @param args
	 *            初始化参数列表
	 * 
	 * @return true，如果初始化成功，否则为false。
	 * */
	public boolean init(String[] args);

	/**
	 * <p>
	 * 销毁资源函数。
	 * </p>
	 * <p>
	 * 当不需要网络传输时，可以调用此函数释放网络资源。
	 * </p>
	 * 
	 * @param args
	 *            销毁选项列表
	 * 
	 * @return true，如果释放资源成功，否则为false。
	 * */
	public boolean destroy(String[] args);

	/**
	 * <p>
	 * 根据指定的listenHost以及listenPort创建一个网络监听服务。
	 * </p>
	 * 
	 * @param listenHost
	 *            主机名
	 * @param listenPort
	 *            端口号
	 * 
	 * @return 服务id，当申请发送、接收消息时需要此id；或{@link #ILLEGAL_ID}，如果<code>host</code>
	 *         不合法、无法绑定<code>port</code>等。
	 * 
	 * @see #createListener(String, int, NewConnListener, ReceiveListener)
	 * */
	public int createListener(String listenHost, int listenPort);

	/**
	 * <p>
	 * 根据指定的listenHost、listenPort、新连接监听器、接收监听器创建一个网络监听服务。
	 * </p>
	 * <p>
	 * 如果参数<code>newConListener</code>和<code>receiveListener</code>均为空，则此函数同
	 * {@link #createListener(String, int)}
	 * </p>
	 * 
	 * @param listenHost
	 *            主机名
	 * @param listenPort
	 *            端口号
	 * @param newConnListener
	 *            监听新连接事件的监听器
	 * @param receiveListener
	 *            监听接收消息事件的监听器
	 * 
	 * @return 服务id，当申请发送、接收消息时需要此id；或{@link #ILLEGAL_ID}，如果<code>host</code>
	 *         不合法、无法绑定<code>port</code>等。
	 * 
	 * @see #createConnection(String, int)
	 * */
	public int createListener(String listenHost, int listenPort,
			NewConnListener newConnListener,
			BreakConnListener breakConnListener, ReceiveListener receiveListener);

	/**
	 * <p>
	 * 根据指定remoteHost以及remotePort创建一个客户端连接服务。
	 * </p>
	 * 
	 * @param remoteHost
	 *            主机名
	 * @param remotePort
	 *            端口号
	 * 
	 * @return 服务id，当申请发送、接收消息时需要此id；或{@link #ILLEGAL_ID}， 如果<code>host</code>
	 *         不合法、<code>host</code>未在此<code>port</code>监听等。
	 * 
	 * @see #createService(String, int)
	 * */
	public int createConnection(String remoteHost, int remotePort);

	/**
	 * <p>
	 * 根据指定的remoteHost以及remotePort创建一个客户端连接服务。
	 * </p>
	 * 
	 * @param remoteHost
	 *            主机名
	 * @param remotePort
	 *            端口号
	 * @param listener
	 *            监听器，监听消息接收事件
	 * 
	 * @return 服务id，当申请发送、接收消息时需要此id；或{@link #ILLEGAL_ID}，如果<code>host</code>
	 *         不合法、无法绑定<code>port</code>等。
	 * */
	public int createConnection(String remoteHost, int remotePort,
			BreakConnListener breakListener, ReceiveListener listener);

	/**
	 * 停止对指定serviceid的服务
	 * 
	 * @param serviceid
	 *            要停止的服务id
	 * @return void
	 * */
	public void stopService(int serviceid);

	/**
	 * 关闭指定的连接
	 * 
	 * @param connId
	 *            要关闭的连接的id
	 * @return true，如果关闭成功；否则为false
	 * */
	public boolean closeConn(int connId);

	/**
	 * <p>
	 * 发送消息函数。
	 * </p>
	 * <p>
	 * 向指定的服务id所连接的主机发送消息。(接收者已经包含在<code>message</code>对象中，
	 * 如果接收者为null则表示广播，但如果serviceid不是监听服务则出错。)
	 * </p>
	 * <p>
	 * 注意：此函数可能是异步发送消息，请看具体实现。
	 * </p>
	 * 
	 * @param serviceid
	 *            服务标识
	 * @param message
	 *            要发送的消息
	 * 
	 * @return true，如果发送成功；或false，如果<code>serviceid</code>为{@link #ILLEGAL_ID}、
	 *         <code>message</code>为null、接收者未与当前主机连接等。
	 * 
	 * @see #pushMessage(int, Message, ReceiveListener)
	 * */
	public boolean pushMessage(int serviceid, Message message);

	/**
	 * <p>
	 * 发送消息函数。
	 * </p>
	 * <p>
	 * 根据指定的serviceid，message以及接收事件监听器发送消息。如果监听器为null， 效果等同于
	 * {@link #pushMessage(int, Message)}
	 * </p>
	 * <p>
	 * 注意：
	 * <li>此函数可能会异步发送消息，请看具体实现。</li>
	 * </p>
	 * 
	 * @param serviceid
	 *            服务标识
	 * @param message
	 *            要发送的消息
	 * @param listener
	 *            对事件<接收到消息回复>的监听器
	 * 
	 * @return true，如果发送成功；或false，如果<code>serviceid</code>为监听服务id、
	 *         <code>message</code>为null、接收者为null或未与主机连接等。
	 * 
	 * @see #pushMessage(int, Message)
	 * */
	public boolean pushMessage(int serviceid, Message message,
			ReceiveListener listener);

	/**
	 * 获取（从上一次获取之后到现在的）（给定服务id接收到的）消息的列表。
	 * 
	 * @param serviceid
	 *            服务标识
	 * @param args
	 *            控制字符(如只获取某种type的消息)
	 * 
	 * @return 消息列表。如果指定serviceid存在监听器或为{@link #ILLEGAL_ID}，则此序列永远为空。
	 * */
	public AbstractList<Message> popMessage(int serviceid, Object[] args);

	public int getConnectionId(int serviceId);

	public AbstractList<Integer> getConnectionsId(int serviceId);

	public int getServiceId(int connId);

}
