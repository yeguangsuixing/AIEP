package com.njucs.aiep.base;

import java.io.*;
import java.util.AbstractList;
import java.util.regex.Matcher;

/**
 * �����ļ���������Ĳ����࣬������ȡ{@link Writer}����ȡ{@link Reader}����ȡ��ͨ�����ļ�
 * @author ygsx
 * 
 * @version v1.0.0
 * 
 * @time 2012��10��14��11:28:52
 * */
public class FileIO {

	/**
	 * �����ļ�����ȡ��Ӧ��<code>{@link BufferedReader}</code>
	 * @param fileName �ļ���
	 * @return ��Ӧ��<code>BufferedReader</code>
	 * @exception FileNotFoundException �ļ�������ʱ�׳�
	 * @since v1.0.0
	 * @see #getBufferedReader(String, String)
	 * */
	public static BufferedReader getBufferedReader( String fileName ) 
			throws FileNotFoundException {
		File file = new File(fileName);
		return new BufferedReader(new FileReader(file));
	}
	/**
	 * �����ļ����Լ��ַ�����ȡ��Ӧ��<code>{@link BufferedReader}</code>
	 * @param fileName �ļ���
	 * @param charsetName �ַ�����Ӧ���ַ���
	 * @return ��Ӧ��<code>BufferedReader</code>
	 * @exception FileNotFoundException �ļ�������ʱ�׳�
	 * @exception UnsupportedEncodingException ָ���ַ�����֧��ʱ�׳�
	 * @since v1.0.0
	 * @see #getBufferedReader(String)
	 * */
	public static BufferedReader getBufferedReader( String fileName, String charsetName ) 
			throws UnsupportedEncodingException, FileNotFoundException {
		  InputStreamReader isr =
	            new InputStreamReader(new FileInputStream(fileName), charsetName  );
		  return new BufferedReader(isr);
	}
	
	/**
	 * �����ļ����Լ��ַ�����ȡ��Ӧ��<code>{@link OutputStreamWriter}</code>
	 * @param fileName �ļ���
	 * @param charsetName �ַ�����Ӧ���ַ���
	 * @return ��Ӧ��<code>OutputStreamWriter</code>
	 * @exception FileNotFoundException �ļ�������ʱ�׳�
	 * @exception UnsupportedEncodingException ָ���ַ�����֧��ʱ�׳�
	 * @since v1.0.0
	 * @see #getWriter(String)
	 * @see #getWriter(File, boolean)
	 * @see #getWriter(String, boolean)
	 * */
	public static OutputStreamWriter getWriter( String fileName, String charsetName ) 
			throws UnsupportedEncodingException, FileNotFoundException{
		return  new OutputStreamWriter( new FileOutputStream( fileName ), charsetName );
	}
	/**
	 * �����ļ�����ȡ��Ӧ��<code>{@link PrintWriter}</code>
	 * @param fileName �ļ���
	 * @return ��Ӧ��<code>PrintWriter</code>
	 * @throws FileNotFoundException �ļ�������ʱ�׳�
	 * @exception FileNotFoundException 
	 * @since v1.0.0
	 * @see #getWriter(String, String)
	 * @see #getWriter(File, boolean)
	 * @see #getWriter(String, boolean)
	 * */
	public static PrintWriter getWriter( String fileName ) 
		throws FileNotFoundException  {
		return  new PrintWriter( new File( fileName ) );
	}
	/**
	 * �����ļ�����ȡ��Ӧ��<code>{@link FileWriter}</code>
	 * @param fileName �ļ���
	 * @param append �Ƿ���׷�ӵķ�ʽд���ļ���
	 * @return ��Ӧ��<code>FileWriter</code>
	 * @throws IOException  if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
	 * @since v1.0.0
	 * @see #getWriter(String, String)
	 * @see #getWriter(File, boolean)
	 * @see #getWriter(String)
	 * */
	public static FileWriter getWriter( String fileName, boolean append ) throws IOException 
		 {
		return new FileWriter( new File( fileName ), append );
	}
	/**
	 * �����ļ�����ȡ��Ӧ��<code>{@link FileWriter}</code>
	 * @param fileName �ļ���
	 * @param append �Ƿ���׷�ӵķ�ʽд���ļ���
	 * @return ��Ӧ��<code>FileWriter</code>
	 * @throws IOException  if the file exists but is a directory rather than a regular file, does not exist but cannot be created, or cannot be opened for any other reason
	 * @since v1.0.0
	 * @see #getWriter(String, String)
	 * @see #getWriter(String, boolean)
	 * @see #getWriter(String)
	 * */
	public static FileWriter getWriter( File file, boolean append ) throws IOException   {
		return new FileWriter( file, append );
	}
	

	
	/**
	 * �ر�ʵ���˽ӿ�<code>{@link Closeable}</code>�Ķ���
	 * @param closeable Ҫ�رյĶ���
	 * @return void
	 * @exception IOException if an I/O error occurs
	 * @see v1.0.0
	 * */
	public static void close( Closeable closeable ) throws IOException {
		if( closeable != null ){
			closeable.close();
		}
	}
	
	/**
	 * ����һ���ļ����ݵ�һ�������У��ԡ�=����Ϊ�ַֽ����
	 * �����Լ���#����ͷ���н��ᱻ���ԡ����з���ʹ�á�\n����
	 * ����֮�⣬���ĳ��û�С�=���������׳��쳣��
	 * @param list Ҫ��ӵ�������
	 * @param fileName �ļ���
	 * @return void
	 * @throws Exception �������Ϸ����ļ������ڡ�IO�����ļ������Ƿ�Ϸ�
	 * @since v1.0.0
	 * @see #load2List(AbstractList, String, boolean)
	 * */
	public static void load2List( AbstractList<String[]> list,  String fileName ) throws Exception {
		load2List(list, fileName, true);
	}
	/**
	 * ����һ���ļ����ݵ�һ�������У���"="��Ϊ�ֽ����
	 * �����Լ�"#"��ͷ���н��ᱻ���ԡ����з���ʹ�á�\n����
	 * @param list Ҫ��ӵ�������
	 * @param fileName �ļ���
	 * @param popupErrorLine ��ĳ�в�����������û�С�=����ʱ�Ƿ��׳��쳣
	 * @return void
	 * @throws Exception �������Ϸ����ļ������ڡ�IO����[���ļ������Ƿ�Ϸ�]
	 * @since v1.0.0
	 * @see #load2List(AbstractList, String)
	 * */
	public static void load2List( AbstractList<String[]> list,  String fileName, boolean popupErrorLine ) throws Exception {
		if( list == null )
			throw new IllegalArgumentException( "The argument \"list\" is illegal." );
		if( fileName == null )
			throw new IllegalArgumentException( "The argument \"fileName\" is illegal." );
		
		BufferedReader reader = FileIO.getBufferedReader( fileName );
		
		String tempString, name, value;
		int line = 0;
		while( ( tempString = reader.readLine() ) != null ){
			line ++;
			if( tempString.length() == 0 || //��������
					tempString.startsWith("#") )//ע��Ϊ#��ͷ
				continue;
			int index = tempString.indexOf("=");
			if( index < 0 ){
				if( popupErrorLine ){
					//System.out.println( tempString );
					FileIO.close(reader);
					throw new Exception(  "error at line "+ line );
				} else continue;
			}
			name = tempString.substring(0, index);
			value = tempString.substring( index+1 );
			value = value.replaceAll(Matcher.quoteReplacement("\\n"), "\n");//\nתΪ���з�
			list.add( new String[]{ name, value } );
		}
		FileIO.close(reader);
	}
	
	
}




