package com.twzcluster.webserver;

/**
 * web服务器接口。
 * 
 * @time 2012年12月9日13:26:08
 * @author tqc
 * */
public interface IWebServer {

	/**
	 * <p>
	 * 初始化web服务器。
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
	 * 销毁函数。
	 * </p>
	 * <p>
	 * 当不需要web服务器时，可以调用此函数释放web服务器资源。
	 * </p>
	 * 
	 * @param args
	 *            销毁选项列表
	 * 
	 * @return true，如果释放资源成功，否则为false。
	 * */
	public boolean destroy(String[] args);

	/**
	 * 启动服务
	 * */
	public void startService();

	/**
	 * 停止服务
	 * */
	public void stopService();

	/**
	 * 打印服务器配置信息
	 * */
	public void printServerInfo();
}
