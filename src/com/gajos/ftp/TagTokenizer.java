package com.gajos.ftp;

import java.io.InputStream;
import java.util.Arrays;

public class TagTokenizer implements DataTokenizer{
	
	/**
	 * Language controller.
	 */
	private static LanguageController lc = LanguageController.getInstance();
	
	public static final Character[] INVALID_PATH_CHARS = {'?', '*', '\"', '|', '>', '<'};
	
	public static final Character[] VALID_IP_CHARS = {'0','1','2','3','4','5','6','7','8','9','.'};
	
	static final int DEFAULT_MAX_CHAR_NUMBER = 4096;
	
	public static FTPMessageType DEFAULT_MESSAGE_TYPE = FTPMessageType.CodeResponse;
	
	private CharTokenizer charTokenizer;
	
	private String tagBuffer;
	
	private int charCounter;
	
	private StringBuffer buffer;
	
	private boolean isEnd;
	
	public TagTokenizer(CharTokenizer tokenizer)
	{
		this.charTokenizer = tokenizer;
	}
	
	public Object get()
	{
		return this.tagBuffer;
	}
	
	private void clearBuffer()
	{
		buffer.delete(0, tagBuffer.length());
	}
	
	public synchronized void next() throws FTPException
	{
		this.next(DEFAULT_MAX_CHAR_NUMBER,DEFAULT_MESSAGE_TYPE);
	}
	
	public synchronized void next(int maxCharNumber, FTPMessageType type) throws FTPException
	{
		this.charCounter = 0;
		isEnd = false;
		try {
			while(charCounter < maxCharNumber)
			{
				final char temp = (Character)(charTokenizer.get());

				if(charTokenizer.isEndReached() || temp == ' ')
				{
					synchronized(tagBuffer)
					{
						tagBuffer = buffer.toString();
						isEnd = true;
						clearBuffer();
					}
					return;
				}
				
				//Analize message depends of type.
				switch(type)
				{
					case CodeResponse: 
						//Break if response is longer than expected code.
						if(charCounter > 3)
						{
							tagBuffer = null;
							isEnd = true;
							clearBuffer();
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().notCodeType());
						}
						break;
						
					case FilePath:
						//Check first char is root sign.
						if(charCounter == 0 && (buffer.charAt(0) != '/'))
						{
							tagBuffer = null;
							isEnd = true;
							clearBuffer();
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().notFilePath());
						}
						//Check any of invalid chars.
						if(Arrays.stream(INVALID_PATH_CHARS)
								.anyMatch(c -> {
									return temp == c;
								}))
						{
							tagBuffer = null;
							isEnd = true;
							clearBuffer();
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().notFilePath());
	
						}
						break;
					case IPAddress:
						//Check character pass to IP address.
						if(!Arrays.stream(INVALID_PATH_CHARS)
								.anyMatch(c -> {
									return temp == c;
								}))
						{
							tagBuffer = null;
							isEnd = true;
							clearBuffer();
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().notIPAddress());
						}
					default:
						throw new FTPException(FTPStatus.ERROR, lc.getLanguage().invalidType());
				}
				//All correct, add char.
				++charCounter;
				buffer.append(temp);
				charTokenizer.next();
			}
			//Check 
		}catch(FTPException e)
		{
			tagBuffer = null;
			isEnd = true;
			clearBuffer();
			charTokenizer.close();
			throw e;
		}
	}
	
	public boolean isEndReached()
	{
		return this.isEnd;
	}
	
	public void send(String message) throws FTPException
	{
		charTokenizer.send(message);
	}
	
	public synchronized void close() throws FTPException
	{
		charTokenizer.close();
	}
	
	public synchronized void connect() throws FTPException
	{
		charTokenizer.connect();
	}
	
	public synchronized void sendStream(InputStream stream) throws FTPException
	{
		charTokenizer.sendStream(stream);
	}
}
