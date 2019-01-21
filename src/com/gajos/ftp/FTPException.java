package com.gajos.ftp;

public class FTPException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Status of response.
	 */
	private FTPStatus status;
	
	/**
	 * 
	 * @param status Status of response
	 * @param message Additional message of response.
	 */
	public FTPException(FTPStatus status, String message)
	{
		super(message);
		this.status = status;
	}
	
	public FTPStatus getStatus()
	{
		return this.status;
	}
	
}
