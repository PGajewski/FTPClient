package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPStatus;

public class RetrieveCommand implements CommandInterface {

	private String filePath;
	
	public RetrieveCommand(String file)
	{
		this.filePath = file;
	}
	
	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
		lexer.send(CommandInterface.Commands.RETRIEVE_A_COPY + ' ' + filePath);
    	String[] response = lexer.getResponse();
    	if(CommandInterface.ServerResonses.PossitivePreliminaryReply.DATA_CONNECTION_ALREADY_OPENED
    			.equals(response[0]))
    	{
		      throw new FTPException(FTPStatus.ERROR, lc.getLanguage()
		    		  .strangeResponse());
    	}
	}

}
