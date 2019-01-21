package com.gajos.ftp;

public class FTPFile {
	
	private FTPClient client;
	
	private String serverPath;
	
	private String name;
	
	private long size;
	
	FTPFile(FTPClient client, String name, String serverPath)
	{
		this.client = client;
		this.name = name;
		this.serverPath = serverPath;
		this.size = -1;
	}
	
	public long getSize()
	{
		if(size == -1)
			size = client.getSize(serverPath);
		
		return size;
	}
	
	public String getServerPath()
	{
		return serverPath;
	}
	
	public String getName()
	{
		return name;
	}
}
