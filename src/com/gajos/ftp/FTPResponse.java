package com.gajos.ftp;

import java.net.InetAddress;
import java.nio.file.Path;

public class FTPResponse {

	private boolean isCorrect;
	private String response;
	private String argument;
	private Path filePath;
	private CharTokenizer tokenizer;
	
	FTPResponse(boolean isCorrect, String response, String argument, Path filePath, CharTokenizer tempTokenizer)
	{
		this.isCorrect = isCorrect;
		this.response = response;
		this.argument = argument;
		this.filePath = filePath;
		this.tokenizer = tempTokenizer;
	}
	
	//*****************************Getters****************************
	public boolean check()
	{
		return isCorrect;
	}
	
	public String getResponse()
	{
		return response;
	}
	
	public String getArgument()
	{
		return argument;
	}
	
	public Path getFilePath()
	{
		return filePath;
	}
	
	public CharTokenizer getTokenizer()
	{
		return tokenizer;
	}
}
