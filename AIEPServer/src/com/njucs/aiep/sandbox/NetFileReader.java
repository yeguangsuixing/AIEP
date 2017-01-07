package com.njucs.aiep.sandbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;

public class NetFileReader extends Reader {

	@SuppressWarnings("unused")
	private String fileName;
	
	public NetFileReader(String fileName) throws FileNotFoundException {
		this.fileName = fileName;
	}
	
	@Override
	public void close() throws IOException {
		//do nothing
	}

	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
