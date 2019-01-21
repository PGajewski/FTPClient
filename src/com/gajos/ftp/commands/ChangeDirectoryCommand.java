package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

/**
 * Command class for change directory.
 * @author P.Gajewski
 *
 */
public class ChangeDirectoryCommand implements CommandInterface {

	/**
	 * Path of directory
	 */
	private String path;
	
	/**
	 * Public constructor
	 * @param path Directory path
	 */
	public ChangeDirectoryCommand(String path)
	{
		this.path = path;
	}
	
	@Override
	public void execute(FTPLexer lexer) {
		//Send command.
    	lexer.send(CommandInterface.Commands.CHANGE_WORKING_DIRECTORY + " " + path);
    	lexer.analize(new FTPMessageType[] {FTPMessageType.CodeResponse} );
    	String[] response = lexer.getResponse();
	    
	    if(!CommandInterface.ServerResonses.PossitiveCompletionReply.REQUESTED_OK.equals(response[0]))
	    	throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotChangeDirectory());

	}

}
