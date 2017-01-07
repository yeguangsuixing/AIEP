package com.njucs.aiep.sandbox;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

/**
 * @author ygsx
 * @created 2013Äê6ÔÂ2ÈÕ11:15:17
 * */
public class FileReaderFactory {

	public static enum Mode { GENERAL, SAFETY };
	
	private static Mode mode = Mode.SAFETY;
	
	private FileReaderFactory(){}
	
	//seen in the same package
	static void setMode( Mode mode ){
		FileReaderFactory.mode = mode;
	}
	
	public static Reader getReader( String fileName ) throws FileNotFoundException{
		if( Mode.GENERAL == mode ) {			
			return new FileReader( fileName );
		} else if ( Mode.SAFETY == mode ) {
			return new NetFileReader( fileName );			
		}
		return null;
	}
}
