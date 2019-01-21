package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

public class MakeDirectoryCommand implements CommandInterface {

	private String path;
	
	public MakeDirectoryCommand(String path)
	{
		this.path = path;
	}
	
	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
		//Send command.
    	lexer.send(CommandInterface.Commands.MAKE_DIRECTORY + " " + path);
    	lexer.analize(new FTPMessageType[] { FTPMessageType.CodeResponse});
    	String[] response = lexer.getResponse();

	    if(!CommandInterface.ServerResonses.PossitiveCompletionReply.REQUESTED_OK.equals(response[0]))
	    	throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotCreateDirectory() + path);
	}

}
