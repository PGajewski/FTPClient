package com.gajos.ftp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

public class CharTokenizer implements DataTokenizer {
	
	public final static int BUFFER_SIZE = 255;
	
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
	
	private int charCounter;
	
	public CharTokenizer(InetAddress ipAddress, int port)
	{
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public void connect() throws FTPException
	{
		try {
			socket = new Socket(ipAddress, port);
		    reader = new BufferedReader( new InputStreamReader( this.socket.getInputStream() ) );
		    writer = new BufferedWriter( new OutputStreamWriter( this.socket.getOutputStream() ) );
		    charCounter = 0;
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().portInUse());
		}
	}
	
	public void close() throws FTPException
	{
		try {
			writer.close();
			reader.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().disconnectError());
		}
	}
	
	public synchronized boolean isEndReached()
	{
		return this.isEnd;
	}
	
	public synchronized void next(int maxCharNumber, FTPMessageType type) throws FTPException
	{
		//Ignore parameters.
		this.next();
	}
	
	public synchronized void next() throws FTPException
	{
		int buffer;
		try {
			buffer = reader.read();
			if (buffer == -1)
			{
				synchronized(charBuffer)
				{
					charBuffer = null;
					isEnd = true;
					charCounter = 0;
				}
			}
			else
			{
				//Check is too many chars in package.
				if(charCounter >= BUFFER_SIZE)
				{
					this.close();
					return;
				}
				charBuffer = (char)buffer;
				isEnd = false;
				++charCounter;
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().readCharError());
		}
	}
	
	public Object get()
	{
		synchronized(charBuffer)
		{
			return this.charBuffer;
		}
	}
	
	public synchronized void send(String message) throws FTPException
	{
		try {
			synchronized(writer) {
			writer.write(message);
			writer.flush();}
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
				byte[] buffer = new byte[4096];
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
}
