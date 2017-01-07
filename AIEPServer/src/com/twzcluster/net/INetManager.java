package com.twzcluster.net;

import java.util.AbstractList;

/**
 * �������ӿڡ��ṩ�������
 * 
 * @time 2012��12��5��20:27:43
 * @author tqc
 * */
public interface INetManager {

	/**
	 * �Ƿ����ӷ���id
	 * */
	public static final int ILLEGAL_ID = -1;

	/**
	 * <p>
	 * ��ʼ���������
	 * </p>
	 * <p>
	 * ���ĳЩ�����ȽϺ�ʱ�������������С�
	 * </p>
	 * 
	 * @param args
	 *            ��ʼ�������б�
	 * 
	 * @return true�������ʼ���ɹ�������Ϊfalse��
	 * */
	public boolean init(String[] args);

	/**
	 * <p>
	 * ������Դ������
	 * </p>
	 * <p>
	 * ������Ҫ���紫��ʱ�����Ե��ô˺����ͷ�������Դ��
	 * </p>
	 * 
	 * @param args
	 *            ����ѡ���б�
	 * 
	 * @return true������ͷ���Դ�ɹ�������Ϊfalse��
	 * */
	public boolean destroy(String[] args);

	/**
	 * <p>
	 * ����ָ����listenHost�Լ�listenPort����һ�������������
	 * </p>
	 * 
	 * @param listenHost
	 *            ������
	 * @param listenPort
	 *            �˿ں�
	 * 
	 * @return ����id�������뷢�͡�������Ϣʱ��Ҫ��id����{@link #ILLEGAL_ID}�����<code>host</code>
	 *         ���Ϸ����޷���<code>port</code>�ȡ�
	 * 
	 * @see #createListener(String, int, NewConnListener, ReceiveListener)
	 * */
	public int createListener(String listenHost, int listenPort);

	/**
	 * <p>
	 * ����ָ����listenHost��listenPort�������Ӽ����������ռ���������һ�������������
	 * </p>
	 * <p>
	 * �������<code>newConListener</code>��<code>receiveListener</code>��Ϊ�գ���˺���ͬ
	 * {@link #createListener(String, int)}
	 * </p>
	 * 
	 * @param listenHost
	 *            ������
	 * @param listenPort
	 *            �˿ں�
	 * @param newConnListener
	 *            �����������¼��ļ�����
	 * @param receiveListener
	 *            ����������Ϣ�¼��ļ�����
	 * 
	 * @return ����id�������뷢�͡�������Ϣʱ��Ҫ��id����{@link #ILLEGAL_ID}�����<code>host</code>
	 *         ���Ϸ����޷���<code>port</code>�ȡ�
	 * 
	 * @see #createConnection(String, int)
	 * */
	public int createListener(String listenHost, int listenPort,
			NewConnListener newConnListener,
			BreakConnListener breakConnListener, ReceiveListener receiveListener);

	/**
	 * <p>
	 * ����ָ��remoteHost�Լ�remotePort����һ���ͻ������ӷ���
	 * </p>
	 * 
	 * @param remoteHost
	 *            ������
	 * @param remotePort
	 *            �˿ں�
	 * 
	 * @return ����id�������뷢�͡�������Ϣʱ��Ҫ��id����{@link #ILLEGAL_ID}�� ���<code>host</code>
	 *         ���Ϸ���<code>host</code>δ�ڴ�<code>port</code>�����ȡ�
	 * 
	 * @see #createService(String, int)
	 * */
	public int createConnection(String remoteHost, int remotePort);

	/**
	 * <p>
	 * ����ָ����remoteHost�Լ�remotePort����һ���ͻ������ӷ���
	 * </p>
	 * 
	 * @param remoteHost
	 *            ������
	 * @param remotePort
	 *            �˿ں�
	 * @param listener
	 *            ��������������Ϣ�����¼�
	 * 
	 * @return ����id�������뷢�͡�������Ϣʱ��Ҫ��id����{@link #ILLEGAL_ID}�����<code>host</code>
	 *         ���Ϸ����޷���<code>port</code>�ȡ�
	 * */
	public int createConnection(String remoteHost, int remotePort,
			BreakConnListener breakListener, ReceiveListener listener);

	/**
	 * ֹͣ��ָ��serviceid�ķ���
	 * 
	 * @param serviceid
	 *            Ҫֹͣ�ķ���id
	 * @return void
	 * */
	public void stopService(int serviceid);

	/**
	 * �ر�ָ��������
	 * 
	 * @param connId
	 *            Ҫ�رյ����ӵ�id
	 * @return true������رճɹ�������Ϊfalse
	 * */
	public boolean closeConn(int connId);

	/**
	 * <p>
	 * ������Ϣ������
	 * </p>
	 * <p>
	 * ��ָ���ķ���id�����ӵ�����������Ϣ��(�������Ѿ�������<code>message</code>�����У�
	 * ���������Ϊnull���ʾ�㲥�������serviceid���Ǽ������������)
	 * </p>
	 * <p>
	 * ע�⣺�˺����������첽������Ϣ���뿴����ʵ�֡�
	 * </p>
	 * 
	 * @param serviceid
	 *            �����ʶ
	 * @param message
	 *            Ҫ���͵���Ϣ
	 * 
	 * @return true��������ͳɹ�����false�����<code>serviceid</code>Ϊ{@link #ILLEGAL_ID}��
	 *         <code>message</code>Ϊnull��������δ�뵱ǰ�������ӵȡ�
	 * 
	 * @see #pushMessage(int, Message, ReceiveListener)
	 * */
	public boolean pushMessage(int serviceid, Message message);

	/**
	 * <p>
	 * ������Ϣ������
	 * </p>
	 * <p>
	 * ����ָ����serviceid��message�Լ������¼�������������Ϣ�����������Ϊnull�� Ч����ͬ��
	 * {@link #pushMessage(int, Message)}
	 * </p>
	 * <p>
	 * ע�⣺
	 * <li>�˺������ܻ��첽������Ϣ���뿴����ʵ�֡�</li>
	 * </p>
	 * 
	 * @param serviceid
	 *            �����ʶ
	 * @param message
	 *            Ҫ���͵���Ϣ
	 * @param listener
	 *            ���¼�<���յ���Ϣ�ظ�>�ļ�����
	 * 
	 * @return true��������ͳɹ�����false�����<code>serviceid</code>Ϊ��������id��
	 *         <code>message</code>Ϊnull��������Ϊnull��δ���������ӵȡ�
	 * 
	 * @see #pushMessage(int, Message)
	 * */
	public boolean pushMessage(int serviceid, Message message,
			ReceiveListener listener);

	/**
	 * ��ȡ������һ�λ�ȡ֮�����ڵģ�����������id���յ��ģ���Ϣ���б�
	 * 
	 * @param serviceid
	 *            �����ʶ
	 * @param args
	 *            �����ַ�(��ֻ��ȡĳ��type����Ϣ)
	 * 
	 * @return ��Ϣ�б����ָ��serviceid���ڼ�������Ϊ{@link #ILLEGAL_ID}�����������ԶΪ�ա�
	 * */
	public AbstractList<Message> popMessage(int serviceid, Object[] args);

	public int getConnectionId(int serviceId);

	public AbstractList<Integer> getConnectionsId(int serviceId);

	public int getServiceId(int connId);

}
