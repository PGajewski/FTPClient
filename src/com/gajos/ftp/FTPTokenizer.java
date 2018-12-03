package com.gajos.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.StringTokenizer;

public class FTPTokenizer {
	/**
	 * Language controller.
	 */
	private static LanguageController lc = LanguageController.getInstance();

	/**
	 * Random number generator.
	 */
	private final static Random generator = new Random();
	
	/**
	 * Max byte number in one saving to file.
	 */
	public static final int FILE_BUFFER_SIZE = 4096;
	
	/**
	 * Temporary file directory.
	 */
	public static final String TEMPORARY_DOWNLOAD_PATH = "downloads/";
	
	/**
	 * Tag tokenizer.
	 */
	private DataTokenizer dataTokenizer;
	
	/**
	 * Actual response.
	 */
	private FTPResponse actualResponse;
	
	/**
	 * Default constructor
	 * @param tagTokenizer
	 */
	public FTPTokenizer(DataTokenizer dataTokenizer)
	{
		this.dataTokenizer = dataTokenizer;
	}
	
	public synchronized void analize(FTPMessageType type) throws FTPException
	{
		if(dataTokenizer instanceof CharTokenizer && type == FTPMessageType.File)
		{
			//Create temporary file.
			Path tempFilePath = Paths.get(TEMPORARY_DOWNLOAD_PATH + (new Integer(generator.nextInt())).toString());
			try
			{
				dataTokenizer.connect();
				while(!dataTokenizer.isEndReached())
				{
					dataTokenizer.next(FILE_BUFFER_SIZE, FTPMessageType.File);
					Files.write(tempFilePath, ((String)dataTokenizer.get()).getBytes());
				}
				//Modify response.
				synchronized(actualResponse)
				{
					actualResponse = new FTPResponse(true, null, null, tempFilePath, null);
				}	
			}
			catch(FTPException e)
			{
				//Modify response.
				synchronized(actualResponse)
				{
					actualResponse = new FTPResponse(false, null, null, null, null);
				}
				//Throw FTPException for next level.
				throw e;
			}
			catch(IOException e)
			{
				//Modify response.
				synchronized(actualResponse)
				{
					actualResponse = new FTPResponse(false, null, null, null, null);
				}
				//Create FTPException.
				throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotCreateFile());
			}
		} else if(dataTokenizer instanceof TagTokenizer)
		{
			//Analize to get response from server.
			dataTokenizer.connect();
			try
			{
				String temp1, temp2;
				switch(type)
				{
					//Only code response.
					case CodeResponse:
						if(!dataTokenizer.isEndReached())
						{
							dataTokenizer.next(3, FTPMessageType.CodeResponse);
							
						}
						else
						{
							synchronized(actualResponse)
							{
								actualResponse =  new FTPResponse(false, null, null, null, null);
							}	
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().messageTooShort());
						}
						//Modify response.
						synchronized(actualResponse)
						{
							actualResponse =  new FTPResponse(true, (String)dataTokenizer.get(), null, null, null);
						}
						//If message is too long, return exception with information about too long message.
						if(!dataTokenizer.isEndReached())
						{
							throw new FTPException(FTPStatus.WARNING, lc.getLanguage().messageTooLong());
						}
						break;
					//Code response with file/directory path
					case FilePath:
						if(!dataTokenizer.isEndReached())
						{
							dataTokenizer.next(3, FTPMessageType.CodeResponse);
							temp1 = (String)dataTokenizer.get();
						} else {
							//Modify response.
							synchronized(actualResponse)
							{
								actualResponse =  new FTPResponse(false, null, null, null, null);
							}
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().messageTooShort());
						}
						
						if(!dataTokenizer.isEndReached())
						{
							dataTokenizer.next(3, FTPMessageType.FilePath);
							temp2 = (String)dataTokenizer.get();
						} else {
							//Modify response.
							synchronized(actualResponse)
							{
								actualResponse =  new FTPResponse(false, temp1, null, null, null);
							}
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().messageTooShort());
						}
						
						//Modify response.
						synchronized(actualResponse)
						{
							actualResponse =  new FTPResponse(true, temp1, temp2, null, null);
						}

						//Modify response.
						synchronized(actualResponse)
						{
							actualResponse =  new FTPResponse(true, (String)dataTokenizer.get(), null, null, null);
						}
						//If message is too long, return exception with information about too long message.
						if(!dataTokenizer.isEndReached())
						{
							throw new FTPException(FTPStatus.WARNING, lc.getLanguage().messageTooLong());
						}
						break;
					//Code response with IP address.
					case IPAddress:
						if(!dataTokenizer.isEndReached())
						{
							dataTokenizer.next(3, FTPMessageType.CodeResponse);
							temp1 = (String)dataTokenizer.get();
						} else {
							//Modify response.
							synchronized(actualResponse)
							{
								actualResponse =  new FTPResponse(false, null, null, null, null);
							}
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().messageTooShort());
						}
						
						if(!dataTokenizer.isEndReached())
						{
							dataTokenizer.next(30, FTPMessageType.IPAddress);
							temp2 = (String)dataTokenizer.get();
						} else {
							//Modify response.
							synchronized(actualResponse)
							{
								actualResponse =  new FTPResponse(false, temp1, null, null, null);
							}
							throw new FTPException(FTPStatus.ERROR, lc.getLanguage().messageTooShort());
						}
						//Extract and create Char Tokenizer for data connection.
					    String ipAddress = null;
					    int port = -1;
					    int opening = temp2.indexOf('(');
					    int closing = temp2.indexOf(')', opening + 1);
					    if (closing > 0) {
				    	String dataLink = temp2.substring(opening + 1, closing);
				    	StringTokenizer tokenizer = new StringTokenizer(dataLink, ",");
			    		ipAddress = tokenizer.nextToken() + "." + tokenizer.nextToken() + "." 
			    				+ tokenizer.nextToken() + "." + tokenizer.nextToken();
			    		port = Integer.parseInt(tokenizer.nextToken()) * 256
			    				+ Integer.parseInt(tokenizer.nextToken());
					    }
					    
					    CharTokenizer tempTokenizer = null;
					    try {
							tempTokenizer = new CharTokenizer(InetAddress.getByName(ipAddress), new Integer(port));
						} catch (IOException e) {
							synchronized(actualResponse)
							{
								actualResponse =  new FTPResponse(false, temp1, temp2, null, null);
							}
							if(!dataTokenizer.isEndReached())
				    		throw new FTPException(FTPStatus.ERROR, lc.getLanguage().invalidSocketFromServer() + temp2);
						}
						
						//Modify response.
						synchronized(actualResponse)
						{
							actualResponse =  new FTPResponse(true, (String)dataTokenizer.get(), null, null, null);
						}
						//If message is too long, return exception with information about too long message.
						if(!dataTokenizer.isEndReached())
						{
							throw new FTPException(FTPStatus.WARNING, lc.getLanguage().messageTooLong());
						}
						break;
					default:
						throw new FTPException(FTPStatus.ERROR, lc.getLanguage().invalidType());
				}
			}
			catch(FTPException e)
			{
				//Modify response.
				synchronized(actualResponse)
				{
					actualResponse = new FTPResponse(false, null, null, null, null);
				}
				//Throw FTPException for next level.
				throw e;
			}
		}
	}
	
	public synchronized FTPResponse getResponse()
	{
		return actualResponse;
	}
	
	public void send(String message) throws FTPException
	{
		dataTokenizer.send(message);
	}
	
	public synchronized void close() throws FTPException
	{
		dataTokenizer.close();
	}
	
	public synchronized void connect() throws FTPException
	{
		dataTokenizer.close();
	}
	
	public synchronized void sendStream(InputStream stream) throws FTPException
	{
		dataTokenizer.sendStream(stream);
	}
}
