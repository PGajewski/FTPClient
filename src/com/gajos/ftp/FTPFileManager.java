package com.gajos.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class FTPFileManager {

	/**
	 * Maximal time for doesn't receive data from server.
	 */
	public static final long DOWNLOAD_TIMEOUT = 3000;
	
	private class DisconnectTask extends TimerTask
	{

		@Override
		public void run() {
			FTPFileManager.this.byteStream.close();
		}
		
	}
	
	private Timer timer;
	
	/**
	 * Language controller.
	 */
	private static LanguageController lc = LanguageController.getInstance();

	/**
	 * Random number generator.
	 */
	private final static Random generator = new Random();
	
	private FTPByteStream byteStream;
	
	FTPFileManager(FTPByteStream byteStream)
	{
		this.byteStream = byteStream;
	}
	
	public void downloadFile(File endFile) throws FTPException
	{
		//Temporary file
		File tempFile = new File(endFile.getParent() + (new Integer(generator.nextInt())).toString());
		
		//Open file output stream.
		byteStream.connect();
		try {
			while(byteStream.isEndReached())
			{
				reinitTimer();
				Files.write(tempFile.toPath(), (byte[]) byteStream.get());
			}
		} catch(IOException e)
		{
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotCreateFile());
		} finally {
			byteStream.close();
		}
		
		//Move file.
		try {
			Files.move(tempFile.toPath(), endFile.toPath(), REPLACE_EXISTING);
		} catch (IOException e) {
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotCreateFile());
		}
	}
	
	public void uploadFile(File uploadFile) throws FTPException
	{
		//Check file exist.
		if(!uploadFile.exists()) {
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().fileNotFound());
		}
		
		//Create stream for data from file.
		InputStream input = null;
		try
		{
			input = new FileInputStream(uploadFile);
			byteStream.connect();
			byteStream.sendStream(input);
		} catch(FileNotFoundException e)
		{
		      throw new FTPException(FTPStatus.ERROR, lc.getLanguage().fileNotFound() + uploadFile.getAbsolutePath());
		} finally {
			byteStream.close();
		}
		
		//Redirect input stream.
	}
	
	private void reinitTimer()
	{
		timer.cancel();
		timer.schedule(new DisconnectTask(), DOWNLOAD_TIMEOUT);
	}
	
	public void close()
	{
		byteStream.close();
	}
}
