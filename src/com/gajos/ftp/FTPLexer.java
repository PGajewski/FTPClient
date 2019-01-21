package com.gajos.ftp;

import java.io.InputStream;
import java.util.Arrays;

public class FTPLexer {
	/**
	 * Language controller.
	 */
	private static LanguageController lc = LanguageController.getInstance();
	
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
	private FTPScanner scanner;
	
	/**
	 * Actual response.
	 */
	private String[] actualResponse;
	
	/**
	 * Default constructor
	 * @param tagTokenizer
	 */
	public FTPLexer(FTPScanner scanner)
	{
		this.scanner = scanner;
	}
	
	public void analize(FTPMessageType[] types) throws FTPException
	{
		//Analyze to get response from server.

		String[] tagArray = Arrays.stream(types)
				.map(t -> {
						scanner.next(t);
						return (String)scanner.get();
				})
				.toArray(String[]::new);
		
		//If not exception raised, copy result array.
		actualResponse = Arrays.copyOf(tagArray, types.length);
		
		//Clean stream.
		scanner.clean();
	}
	
	public String[] getResponse()
	{
		return actualResponse;
	}
	
	public void send(String message) throws FTPException
	{
		scanner.send(message);
	}
	
	public synchronized void close() throws FTPException
	{
		scanner.close();
	}
	
	public synchronized void connect() throws FTPException
	{
		scanner.connect();
	}
	
	public synchronized void sendStream(InputStream stream) throws FTPException
	{
		scanner.sendStream(stream);
	}

	public boolean isEndReached() {
		return this.isEndReached();
	}
	
	public void setTimeout(int time)
	{
		scanner.setTimeout(time);
	}
}
