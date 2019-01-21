package com.gajos.ftp;

import java.util.List;

public class FTPDirectory extends FTPFile {

	List<FTPFile> directoryContent;
	
	FTPDirectory(FTPClient client, String name, String serverPath) {
		super(client, name, serverPath);
	}
	
	public void setContent(List<FTPFile> directoryContent)
	{
		this.directoryContent = directoryContent;
	}

	
	public List<FTPFile> getContent()
	{
		return this.directoryContent;
	}
}
