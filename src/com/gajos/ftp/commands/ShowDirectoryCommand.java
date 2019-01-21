package com.gajos.ftp.commands;

import com.gajos.ftp.FTPClient;
import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

public class ShowDirectoryCommand implements CommandInterface {

	private String name;
	
	public String getName()
	{
		return name;
	}
	
	public ShowDirectoryCommand()
	{
		
	}
	
	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
		//Send command.
    	lexer.send(CommandInterface.Commands.PRINT_WORKING_DIRECTORY);
    	lexer.analize(new FTPMessageType[] {FTPMessageType.CodeResponse, 
    										FTPMessageType.FilePath} );
    	String[] response = lexer.getResponse();
			    
		if(CommandInterface.ServerResonses.PossitiveCompletionReply.PATHNAME_CREATED.equals(response[0]))
		{
			//Return path of folder.
			name = response[1];
		}
		else
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotPrintDirectoryPath());
	}

}
