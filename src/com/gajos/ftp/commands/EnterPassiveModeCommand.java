package com.gajos.ftp.commands;

import java.net.InetAddress;
import java.net.UnknownHostException;

import com.gajos.ftp.FTPClient;
import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

/**
 * Command for enter into passive mode.
 * @author P.Gajewski
 *
 */
public class EnterPassiveModeCommand implements CommandInterface {

	/**
	 * IP v4 address of FTP server
	 */
	private InetAddress dataIpAddress;
	
	/**
	 * Datastream port
	 */
	private int port;
	
	/**
	 * Method to get port number.
	 * @return Datastream port number
	 */
	public int getPort()
	{
		return port;
	}
	
	/**
	 * 
	 * @return FTP server IP address
	 */
	public InetAddress getIpAddress()
	{
		return dataIpAddress;
	}
	
	public EnterPassiveModeCommand()
	{
		
	}
	
	@Override
	public void execute(FTPLexer lexer) {
		lexer.send(CommandInterface.Commands.ENTER_PASSIVE_MODE);
    	lexer.analize(new FTPMessageType[]{		FTPMessageType.CodeResponse,
    											FTPMessageType.IPAddress,
    											FTPMessageType.PortNumber});
    	
    	String[] response = lexer.getResponse();
		
    	//Check code response.
    	if(!CommandInterface.ServerResonses.PossitiveCompletionReply.PASSIVE_MODE
				.equals(response[0]))
		{
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().strangeResponse() + response);
		}
    	
    	//Check IP address
    	try {
			dataIpAddress = InetAddress.getByName(response[1]);
		} catch (UnknownHostException e) {
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().incorrectIPAddress());
		}
    	
    	//Check port number.
    	port = Integer.parseInt(response[2]);
    	if(port < 1024 || port > 49151) {
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().incorrectPort());
    	}
	}

}
