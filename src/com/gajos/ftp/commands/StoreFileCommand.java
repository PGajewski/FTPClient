package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

public class StoreFileCommand implements CommandInterface {

	private String filePath;
	
	public StoreFileCommand(String path)
	{
		this.filePath = path;
	}
	
	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
    	lexer.send(CommandInterface.Commands.ACCEPT_AND_STORE + ' ' + filePath);
    	lexer.analize(new FTPMessageType[] { FTPMessageType.CodeResponse});
    	
    	String[] response = lexer.getResponse();
    	if(CommandInterface.ServerResonses.PossitivePreliminaryReply.DATA_CONNECTION_ALREADY_OPENED
    			.equals(response[0]))
    	{
		      throw new FTPException(FTPStatus.ERROR, lc.getLanguage().strangeResponse());
    	}
	}

}
