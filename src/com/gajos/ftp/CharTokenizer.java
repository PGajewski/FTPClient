package com.gajos.ftp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class CharTokenizer implements DataTokenizer {
		
	public final static int BUFFER_SIZE = 4096;
		
	/**
	 * Language controller.
	 */
	private static LanguageController lc = LanguageController.getInstance();
	
	/**
	 * Socket for sending data
	 */
	private Socket socket = null;
	
	/**
	 * IP address of FTP server.
	 */
	private InetAddress ipAddress = null;
	
	/**
	 * IP protocol Port.
	 */
	private int port;
	
	/**
	 * Buffered reader for data transmission.
	 */
	private BufferedReader reader = null;

	/**
	 * Buffered writer for data transmission.
	 */
	private BufferedWriter writer = null;
	
	/**
	 * Actual char buffer.
	 */
	private Character charBuffer;
	
	/**
	 * Boolean inform about end of transmission.
	 */
	private boolean isEnd;
		
	public CharTokenizer(InetAddress ipAddress, int port)
	{
		this.charBuffer = new Character('\0');
		this.ipAddress = ipAddress;
		this.port = port;
	}
		
	public void connect() throws FTPException
	{
		try {
			socket = new Socket(ipAddress, port);
			socket.setReceiveBufferSize(BUFFER_SIZE);
			socket.setTcpNoDelay(true);
		    reader = new BufferedReader( new InputStreamReader( this.socket.getInputStream()), BUFFER_SIZE );
		    writer = new BufferedWriter( new OutputStreamWriter( this.socket.getOutputStream() ) );
		} catch (ConnectException e)
		{
			close();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().connectError());
		} catch (IOException e) {
			close();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().connectError());
		} 
	}
	
	public void close() throws FTPException
	{
		try {
			if(writer != null)
			{
				writer.flush();
				writer.close();
				writer = null;
			}
			if(reader != null)
			{
				reader.close();
				reader = null;
			}
			if(socket != null)
			{
				socket.close();
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().disconnectError());
		}
	}
	
	public synchronized boolean isEndReached()
	{
		return this.isEnd;
	}
	
	public synchronized void next(FTPMessageType type) throws FTPException
	{
		int buffer;
		try {
			buffer = reader.read();
			if (buffer == -1)
			{

				isEnd = true;
			}
			else
			{
				//Check is too many chars in package.

				charBuffer = (char)buffer;
				isEnd = false;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().readCharError());
		}
	}
	
	public void clean()
	{
		//Start new reader and writer.
		try {
			//Catch all from socket.
			for(int i = 0; i < BUFFER_SIZE; ++i)
			{
				if(reader.read() == 10)
				{
					return;
				}
			}
			close();
		} catch (IOException e ) {

		} 
	}
	
	public Object get()
	{
		return this.charBuffer;
	}
	
	public synchronized void send(String message) throws FTPException
	{
		try {
			writer.write(message + "\r\n");
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().sendingError());
		}
	}
	
	public synchronized void sendStream(InputStream stream) throws FTPException
	{
		try {
			synchronized(writer) {
				BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream());
				byte[] buffer = new byte[BUFFER_SIZE];
				int bytesRead = 0;
				while ((bytesRead = stream.read(buffer)) != -1) {
					output.write(buffer, 0, bytesRead);
				}
				output.flush();
				output.close();
				stream.close(); }
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().sendingError());
		}
	}

	@Override
	public void setTimeout(int time) {
		try {
			if(socket != null)
			{
				socket.setSoTimeout(time);
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}
}
