package com.gajos.ftp;

import java.io.InputStream;

public interface DataTokenizer {
	public Object get();
	
	public void next() throws FTPException;
	
	public  void next(int maxCharNumber, FTPMessageType type) throws FTPException;
	
	public boolean isEndReached();
	
	public void send(String message) throws FTPException;

	public void sendStream(InputStream stream) throws FTPException;
	
	public void close() throws FTPException;
	
	public void connect() throws FTPException;
}
