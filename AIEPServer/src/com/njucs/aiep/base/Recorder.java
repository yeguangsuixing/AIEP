package com.njucs.aiep.base;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * ���л��������ļ�֮���ת��
 * @author ygsx
 * @version v1.0.0
 * @time 2012��10��14��11:35:13
 * */
public class Recorder {
	
	/**
	 * ��ֹ����Recorder����
	 * @since v1.0.0
	 * */
	private Recorder(){}
	
	/**
	 * ����һ���ļ���ȡһ�����л��Ķ���
	 * @param fileName �ļ���
	 * @return ���ļ���������л���������ļ����κδ��󣬾�����null��
	 * @since v1.0.0
	 * @see #save(Object, String)
	 * */
	public static Object open(String fileName) {
		FileInputStream fis = null;
		Object obj = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(fileName);
			ois = new ObjectInputStream(fis);
			obj = ois.readObject();
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		} finally {
			if(ois != null){
				try { ois.close(); } catch (IOException e) { e.printStackTrace(); }
			}
			if( fis != null ) {
				try { fis.close(); } catch (IOException e) { e.printStackTrace(); }
			}
		}
		return obj;
	}
	/**
	 * ��һ�����л��Ķ��󱣳ֵ�ָ���ļ���
	 * @param object ʵ�������л��Ķ���
	 * @param fileName �ļ���
	 * @return ����ɹ�����true��������󲻿����л����ļ��д�IO�������ɱ���ʧ�ܣ�����false��
	 * @since v1.0.0
	 * @see #open(String)
	 * */
	public static boolean save(Object object, String fileName){
		try {
			FileOutputStream fos = new FileOutputStream(fileName);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(object);
			oos.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}













