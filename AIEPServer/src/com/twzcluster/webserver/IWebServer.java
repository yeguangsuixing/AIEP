package com.twzcluster.webserver;

/**
 * web�������ӿڡ�
 * 
 * @time 2012��12��9��13:26:08
 * @author tqc
 * */
public interface IWebServer {

	/**
	 * <p>
	 * ��ʼ��web��������
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
	 * ���ٺ�����
	 * </p>
	 * <p>
	 * ������Ҫweb������ʱ�����Ե��ô˺����ͷ�web��������Դ��
	 * </p>
	 * 
	 * @param args
	 *            ����ѡ���б�
	 * 
	 * @return true������ͷ���Դ�ɹ�������Ϊfalse��
	 * */
	public boolean destroy(String[] args);

	/**
	 * ��������
	 * */
	public void startService();

	/**
	 * ֹͣ����
	 * */
	public void stopService();

	/**
	 * ��ӡ������������Ϣ
	 * */
	public void printServerInfo();
}
