package com.njucs.aiep.base;

import java.io.*;
import java.util.AbstractList;
import java.util.regex.Matcher;

/**
 * 关于文件输入输出的操作类，包括获取{@link Writer}，获取{@link Reader}，读取普通配置文件
 * @author ygsx
 * 
 * @version v1.0.0
 * 
 * @time 2012年10月14日11:28:52
 * */
public class FileIO {

	/**
	 * 根据文件名获取对应的<code>{@link BufferedReader}</code>
	 * @param fileName 文件名
	 * @return 对应的<code>BufferedReader</code>
	 * @exception FileNotFoundException 文件不存在时抛出
	 * @since v1.0.0
	 * @see #getBufferedReader(String, String)
	 * */
	public static BufferedReader getBufferedReader( String fileName ) 
			throws FileNotFoundException {
		File file = new File(fileName);
		return new BufferedReader(new FileReader(file));
	}
	/**
	 * 根据文件名以及字符集获取对应的<code>{@link BufferedReader}</code>
	 * @param fileName 文件名
	 * @param charsetName 字符集对应的字符串
	 * @return 对应的<code>BufferedReader</code>
	 * @exception FileNotFoundException 文件不存在时抛出
	 * @exception UnsupportedEncodingException 指定字符集不支持时抛出
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
	 * 根据文件名以及字符集获取对应的<code>{@link OutputStreamWriter}</code>
	 * @param fileName 文件名
	 * @param charsetName 字符集对应的字符串
	 * @return 对应的<code>OutputStreamWriter</code>
	 * @exception FileNotFoundException 文件不存在时抛出
	 * @exception UnsupportedEncodingException 指定字符集不支持时抛出
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
	 * 根据文件名获取对应的<code>{@link PrintWriter}</code>
	 * @param fileName 文件名
	 * @return 对应的<code>PrintWriter</code>
	 * @throws FileNotFoundException 文件不存在时抛出
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
	 * 根据文件名获取对应的<code>{@link FileWriter}</code>
	 * @param fileName 文件名
	 * @param append 是否以追加的方式写到文件中
	 * @return 对应的<code>FileWriter</code>
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
	 * 根据文件名获取对应的<code>{@link FileWriter}</code>
	 * @param fileName 文件名
	 * @param append 是否以追加的方式写到文件中
	 * @return 对应的<code>FileWriter</code>
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
	 * 关闭实现了接口<code>{@link Closeable}</code>的对象
	 * @param closeable 要关闭的对象
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
	 * 加载一个文件内容到一个链表中，以“=”作为分分界符。
	 * 空行以及“#”开头的行将会被忽略。换行符请使用“\n”。
	 * 除此之外，如果某行没有“=”，将会抛出异常。
	 * @param list 要添加到的链表
	 * @param fileName 文件名
	 * @return void
	 * @throws Exception 参数不合法、文件不存在、IO错误、文件中行是否合法
	 * @since v1.0.0
	 * @see #load2List(AbstractList, String, boolean)
	 * */
	public static void load2List( AbstractList<String[]> list,  String fileName ) throws Exception {
		load2List(list, fileName, true);
	}
	/**
	 * 加载一个文件内容到一个链表中，以"="作为分界符。
	 * 空行以及"#"开头的行将会被忽略。换行符请使用“\n”。
	 * @param list 要添加到的链表
	 * @param fileName 文件名
	 * @param popupErrorLine 当某行不满足条件（没有“=”）时是否抛出异常
	 * @return void
	 * @throws Exception 参数不合法、文件不存在、IO错误[、文件中行是否合法]
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
			if( tempString.length() == 0 || //空行跳过
					tempString.startsWith("#") )//注释为#开头
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
			value = value.replaceAll(Matcher.quoteReplacement("\\n"), "\n");//\n转为换行符
			list.add( new String[]{ name, value } );
		}
		FileIO.close(reader);
	}
	
	
}




