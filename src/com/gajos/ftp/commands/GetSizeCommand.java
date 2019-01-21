package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

/**
 * Command for getting file size.
 * @author P.Gajewski
 *
 */
public class GetSizeCommand implements CommandInterface {

	private String path;
	private long size;
	
	public GetSizeCommand(String path)
	{
		this.path = path;
	}
	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
		//Send command.
    	lexer.send(CommandInterface.Commands.SIZE_OF_FILE + path);
    	lexer.analize(new FTPMessageType[] {FTPMessageType.CodeResponse, 
    										FTPMessageType.Number} );
    	String[] response = lexer.getResponse();
			    
		if(CommandInterface.ServerResonses.PossitiveCompletionReply.PATHNAME_CREATED.equals(response[0]))
		{
			//Return path of folder.
			size =  Long.parseLong(response[1]);
		}
		else
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotPrintDirectoryPath());
	}
	
	public long getSize()
	{
		return size;
	}

}
