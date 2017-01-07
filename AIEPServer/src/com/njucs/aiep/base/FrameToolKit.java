package com.njucs.aiep.base;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowStateListener;

import javax.swing.JFrame;


/**
 * UI框架工具包，包括系统托盘、程序图标
 * @author ygsx
 * @version v1.0.0
 * @time 2012年10月14日13:06:41
 * */
public class FrameToolKit {
	private static TrayIcon trayicon;
	/**
	 * 系统托盘
	 * @param frame 要添加托盘的窗体
	 * @param picPath 托盘图标路径
	 * @param title 托盘显示的文字
	 * @param popupMenu 右键点击托盘时弹出的菜单
	 * @return void
	 * @since v1.0.0
	 * */
	public static void tray( final JFrame frame,  String picPath, String title, PopupMenu popupMenu ){
		if (!SystemTray.isSupported()) // 判断当前系统是否支持系统栏
			return;
		SystemTray sysTray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage(picPath);
		
		trayicon = new TrayIcon(image, title,  popupMenu);// 创建托盘图标：由图标、文本、右击菜单组成
		trayicon.setImageAutoSize(true);// 设置是否自动调整图标的大小
		trayicon.addActionListener(new ActionListener()// 双击图标时显示窗体
				{
					public void actionPerformed(ActionEvent e) {
						boolean b = frame.isVisible();
						frame.setVisible( ! b );
						if( ! b ) frame.setState(JFrame.NORMAL );
						frame.toFront();//双击显示/隐藏
					}
				});
		frame.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				//System.out.println("state changed:"+e.getNewState());
				if( e.getNewState() == JFrame.ICONIFIED ){//最小化
					frame.setVisible(false);
				} 
			}
		});
		try { sysTray.add(trayicon);
		} catch (AWTException e1) { e1.printStackTrace();
		}
	}
	
	/**
	 * @author ygsx
	 * @created 2013年6月8日10:34:51
	 * */
	public static boolean changeTrayText(String text){
		if( trayicon == null ) return false;
		trayicon.setToolTip(text);
		return true;
	}
	
	/**
	 * 设置程序图标
	 * @param frame 要设置图标的窗体
	 * @param iconPath 程序图标路径
	 * @return void
	 * @since v1.0.0
	 * */
	public static void frameIcon( JFrame frame, String iconPath ){
		Image image = Toolkit.getDefaultToolkit().getImage(iconPath);
		frame.setIconImage(image);
		
	}
}



