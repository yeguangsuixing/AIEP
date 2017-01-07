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
 * UI��ܹ��߰�������ϵͳ���̡�����ͼ��
 * @author ygsx
 * @version v1.0.0
 * @time 2012��10��14��13:06:41
 * */
public class FrameToolKit {
	private static TrayIcon trayicon;
	/**
	 * ϵͳ����
	 * @param frame Ҫ������̵Ĵ���
	 * @param picPath ����ͼ��·��
	 * @param title ������ʾ������
	 * @param popupMenu �Ҽ��������ʱ�����Ĳ˵�
	 * @return void
	 * @since v1.0.0
	 * */
	public static void tray( final JFrame frame,  String picPath, String title, PopupMenu popupMenu ){
		if (!SystemTray.isSupported()) // �жϵ�ǰϵͳ�Ƿ�֧��ϵͳ��
			return;
		SystemTray sysTray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().getImage(picPath);
		
		trayicon = new TrayIcon(image, title,  popupMenu);// ��������ͼ�꣺��ͼ�ꡢ�ı����һ��˵����
		trayicon.setImageAutoSize(true);// �����Ƿ��Զ�����ͼ��Ĵ�С
		trayicon.addActionListener(new ActionListener()// ˫��ͼ��ʱ��ʾ����
				{
					public void actionPerformed(ActionEvent e) {
						boolean b = frame.isVisible();
						frame.setVisible( ! b );
						if( ! b ) frame.setState(JFrame.NORMAL );
						frame.toFront();//˫����ʾ/����
					}
				});
		frame.addWindowStateListener(new WindowStateListener() {
			public void windowStateChanged(WindowEvent e) {
				//System.out.println("state changed:"+e.getNewState());
				if( e.getNewState() == JFrame.ICONIFIED ){//��С��
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
	 * @created 2013��6��8��10:34:51
	 * */
	public static boolean changeTrayText(String text){
		if( trayicon == null ) return false;
		trayicon.setToolTip(text);
		return true;
	}
	
	/**
	 * ���ó���ͼ��
	 * @param frame Ҫ����ͼ��Ĵ���
	 * @param iconPath ����ͼ��·��
	 * @return void
	 * @since v1.0.0
	 * */
	public static void frameIcon( JFrame frame, String iconPath ){
		Image image = Toolkit.getDefaultToolkit().getImage(iconPath);
		frame.setIconImage(image);
		
	}
}



