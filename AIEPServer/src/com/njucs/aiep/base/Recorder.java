package com.njucs.aiep.base;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化对象与文件之间的转换
 * @author ygsx
 * @version v1.0.0
 * @time 2012年10月14日11:35:13
 * */
public class Recorder {
	
	/**
	 * 禁止创建Recorder对象
	 * @since v1.0.0
	 * */
	private Recorder(){}
	
	/**
	 * 根据一个文件获取一个序列化的对象
	 * @param fileName 文件名
	 * @return 该文件代表的序列化对象，如果文件有任何错误，均返回null。
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
	 * 将一个序列化的对象保持到指定文件中
	 * @param object 实现了序列化的对象
	 * @param fileName 文件名
	 * @return 保存成功返回true。如果对象不可序列化、文件有错，IO出错等造成保存失败，返回false。
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













