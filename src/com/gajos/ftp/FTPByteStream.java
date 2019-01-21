package com.gajos.ftp;

import java.io.InputStream;
import java.util.Arrays;

public class FTPByteStream implements DataTokenizer {
	
	private CharTokenizer charTokenizer;
	
	private boolean isEnd;
		
	private byte[] bufferArray;
	
	FTPByteStream(CharTokenizer tokenizer)
	{
		this.charTokenizer = tokenizer;
		isEnd = false;
	}

	@Override
	public Object get() {
		return bufferArray;
	}

	@Override
	public void next(FTPMessageType type) throws FTPException {
		this.isEnd = false;
		final int bufferSize = type.getValue();
		byte[] tempArray = new byte[bufferSize];
		int i;
		for(i = 0; i < bufferSize; ++i)
		{
			if(charTokenizer.isEndReached())
			{
				break;
			}
			tempArray[i] = Character.getDirectionality((Character)charTokenizer.get());
			charTokenizer.next(null);
		}
		synchronized(bufferArray) {
			bufferArray = Arrays.copyOf(tempArray, i);
		}
		
	}

	@Override
	public boolean isEndReached() {
		return charTokenizer.isEndReached();
	}

	@Override
	public void send(String message) throws FTPException {
		charTokenizer.send(message);
	}

	@Override
	public void sendStream(InputStream stream) throws FTPException {
		charTokenizer.sendStream(stream);
	}

	@Override
	public void close() throws FTPException {
		charTokenizer.close();		
	}

	@Override
	public void connect() throws FTPException {
		charTokenizer.close();
	}
	
	@Override
	public void clean() throws FTPException {
		charTokenizer.clean();
	}
	
	@Override
	public void setTimeout(int time) throws FTPException {
		charTokenizer.setTimeout(time);
	}

}
