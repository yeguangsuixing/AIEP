package com.njucs.aiep.base;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * 类操作，包括给指定的类/对象设置属性值、添加类路径(ClassPath)等
 * @author ygsx
 * @version v1.0.0
 * @time 2012年10月14日11:46:36
 * */
public class ClassOperator {

	/**
	 * 是否已经初始化
	 * @since v1.0.0
	 * */
	protected static boolean hasInitialized = false;
	/**
	 * 添加URL的方法对象
	 * @since v1.0.0
	 * */
	protected static Method addURLMethod;
	/**
	 * URL类加载器
	 * @since v1.0.0
	 * */
	protected static URLClassLoader urlClassLoader = null;
	
	
	/**
	 * 通过文件给对象加载非静态属性，其中文件为文本类型，每行格式如下(“\n”为换行符)：<br />
	 * 	#这是注释行<br />
	 * 	FIELD_NAME=FIELD_VALUE_LINE_1\nFIELD_VALUE_LINE_2
	 * 
	 * @param object 指定的对象
	 * @param fieldFileName 属性文件
	 * @return void
	 * @throws Exception 文件读取失败或文件格式不正确。
	 * 			如果访问权限不够、参数无效、以及设置了一个不存在的类属性不会抛出异常
	 * @since v1.0.1
	 * @see #loadField(Class, String)
	 * */
	public static void loadField( Object object, String fieldFileName ) throws Exception {
		if( object == null )
			throw new IllegalArgumentException( "object");
		if( fieldFileName == null )
			throw new IllegalArgumentException( "fieldFileName"  );
		
		ArrayList<String[]> fieldList = new ArrayList<String[]>();
		
		FileIO.load2List(fieldList, fieldFileName);
		
		String name, value;
		for( int i = 0; i < fieldList.size(); i ++ ){
			String[] temp = fieldList.get(i);
			name = temp[0];
			value = temp[1];
			setFieldValue( object, name, value );
		}
	}
	/**
	 * 通过文件给类T加载静态属性，其中文件为文本类型，每行格式如下：<br />
	 * 	#这是注释行<br />
	 * 	FILED_NAME=FILED_VALUE_LINE_1\nFIELD_VALUE_LINE_2
	 * 
	 * @param T 指定的类型T
	 * @param fieldFileName 属性文件
	 * @return void
	 * @throws Exception 文件读取失败或文件格式不正确。
	 * 如果访问权限不够、参数无效、以及设置了一个不存在的类属性不会抛出异常
	 * @since v1.0.0
	 * @see #loadField(Object, String)
	 * */
	public static void loadField( Class<?> T, String fieldFileName ) throws Exception {
		if( T == null )
			throw new IllegalArgumentException( "T" );
		if( fieldFileName == null )
			throw new IllegalArgumentException( "fieldFileName" );
		
		ArrayList<String[]> fieldList = new ArrayList<String[]>();
		
		FileIO.load2List(fieldList, fieldFileName);
		
		String name, value;
		for( int i = 0; i < fieldList.size(); i ++ ){
			String[] temp = fieldList.get(i);
			name = temp[0];
			value = temp[1];
			setFieldValue( T, name, value );
		}
	}
	
	/** 
	 * 初始化，用于添加URL
	 * @return void
	 * @exception NoSuchMethodException if a matching method is not found.
	 * @exception SecurityException
     *             If a security manager, <i>s</i>, is present and any of the
     *             following conditions is met:
     *
     *             <ul>
     *
     *             <li> invocation of 
     *             <tt>{@link SecurityManager#checkMemberAccess
     *             s.checkMemberAccess(this, Member.DECLARED)}</tt> denies
     *             access to the declared method
     *
     *             <li> the caller's class loader is not the same as or an
     *             ancestor of the class loader for the current class and
     *             invocation of <tt>{@link SecurityManager#checkPackageAccess
     *             s.checkPackageAccess()}</tt> denies access to the package
     *             of this class
     *
     *             </ul>
     *
     * @since v1.0.0
	 *  */
	private synchronized static void initialize() throws SecurityException, NoSuchMethodException {
		if( hasInitialized ) return;
		addURLMethod = URLClassLoader.class.getDeclaredMethod(
				"addURL", new Class[] { URL.class });
		addURLMethod.setAccessible(true);
		hasInitialized = true;
	}
	
	/** 
	 * 添加jar
	 * @param jarAbsolutePath 要添加的包的绝对路径
	 * @param childClassmap 子类Map
	 * @return void
	 * @exception IllegalArgumentException if the method is an instance method and the specified object argument is not an instance of the class or interface declaring the underlying method (or of a subclass or implementor thereof); if the number of actual and formal parameters differ; if an unwrapping conversion for primitive arguments fails; or if, after possible unwrapping, a parameter value cannot be converted to the corresponding formal parameter type by a method invocation conversion.
	 * @exception IllegalAccessException if this Method object enforces Java language access control and the underlying method is inaccessible.
	 * @exception InvocationTargetException if the underlying method throws an exception.
	 * @exception NoSuchMethodException if a matching method is not found.
	 * @exception SecurityException 
     *             If a security manager, <i>s</i>, is present and any of the
     *             following conditions is met:
     *
     *             <ul>
     *
     *             <li> invocation of 
     *             <tt>{@link SecurityManager#checkMemberAccess
     *             s.checkMemberAccess(this, Member.DECLARED)}</tt> denies
     *             access to the declared method
     *
     *             <li> the caller's class loader is not the same as or an
     *             ancestor of the class loader for the current class and
     *             invocation of <tt>{@link SecurityManager#checkPackageAccess
     *             s.checkPackageAccess()}</tt> denies access to the package
     *             of this class
     *
     *             </ul>
	 * @throws IOException if an I/O error has occurred
	 * @throws ClassNotFoundException 
     * @since v1.0.1
     * @see #addCustomJAR(String, Map)
	 *  */
	public static void addURL( String jarAbsolutePath, Map<String, AbstractList<String>> childClassmap ) 
	throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, 
	SecurityException, NoSuchMethodException, IOException, ClassNotFoundException {
		if( ! hasInitialized ) initialize();
		urlClassLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		URL classUrl = new URL( "file:"+ jarAbsolutePath);
		addURLMethod.invoke(urlClassLoader, new Object[] { classUrl });
		
		if( childClassmap != null && childClassmap.size() > 0 ) {
			Set<Entry<String, AbstractList<String>>> set = childClassmap.entrySet();
			JarFile jarFile = new JarFile(jarAbsolutePath);
			Enumeration<JarEntry> entrys = jarFile.entries();
			while (entrys.hasMoreElements()) {
				JarEntry jarEntry = entrys.nextElement();
				String name = jarEntry.getName();
				if( ! name.endsWith(".class") ||  name.indexOf("$") != -1 )  continue;
				name = name.substring(0, name.indexOf("."));
				String className = name.replace("/", ".");
				Class<?> c = Class.forName(className);
				if( c.isInterface() || (c.getModifiers() & Modifier.ABSTRACT) != 0) continue;
				for( Entry<String, AbstractList<String>> entry : set ){
					Class<?> aip = Class.forName( entry.getKey() );
					if( aip.isAssignableFrom( c ) ){ entry.getValue().add(className);/*not break;*/ }
				}
			}
		}
	}
	
	
	
	/** 
	 * 给指定的对象加载名为name的属性的值value
	 *  */
	private static void setFieldValue( Object object, String name, String value ) {
		if( name == null || value == null || name.length() == 0) return;
		
		Field field = null;
		
		try{ 
			field = object.getClass().getField(name);
			Class<?> fieldType = field.getType();
			if( fieldType.isArray()  ){
				field.set(object, value.split(","));
			} else {
				field.set(object, value);
			}
		} catch( Exception e ){ e.printStackTrace();
		}
	}
	/** 
	 * 给指定的类加载名为name的属性的值value
	 *  */
	private static void setFieldValue( Class<?> T, String name, String value ) {
		if( name == null || value == null || name.length() == 0) return;
		
		Field field = null;
		
		try{ 
			field = T.getField(name);
			Class<?> fieldType = field.getType();
			if( fieldType.isArray()  ){
				field.set(T, value.split(","));
			} else {
				field.set(T, value);
			}
		} catch( Exception e ){ e.printStackTrace();
		}
	}
	
	/** 
	 * 添加第三方查询jar，不添加子文件夹的jar
	 * @author tqc
	 * @param userdir 要添加的jar的目录
	 * @return void
	 * @since v1.0.0
	 * @see #addCustomJAR(String, Map)
	 *  */
	public static void addCustomJAR( String userdir ) {
		addCustomJAR( userdir, null );
	}
	
	/** 
	 * 添加第三方查询jar，不添加子文件夹的jar
	 * @author tqc
	 * @param userdir 要添加的jar的目录
	 * @param childClassmap 子类Map，关键字为父类类名，值为子类链表
	 * @return void
	 * @since v1.0.1
	 * @see #addCustomJAR(String)
	 *  */
	public static void addCustomJAR( String userdir, Map<String, AbstractList<String>> childClassmap ) {
		File[] userFiles = new File( userdir ).listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String filename = pathname.getName();
				if( pathname.isFile() && filename.length() > 4 ) {
					if( filename.substring(filename.length() - 4).equalsIgnoreCase(".jar") ) 
						return true;
				}
				return false;
			}
		});
		if( userFiles == null ) return;
		for( File file : userFiles ){
			try { ClassOperator.addURL( file.getAbsolutePath(), childClassmap );
			} catch (Exception e) {   e.printStackTrace();
			}
		}
	}
}







