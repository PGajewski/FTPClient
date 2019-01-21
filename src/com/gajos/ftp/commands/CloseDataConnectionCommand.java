package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

/**
 * Command for closing data connection.
 * @author P.Gajewski
 *
 */
public class CloseDataConnectionCommand implements CommandInterface {

	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
    	lexer.analize(new FTPMessageType[] { FTPMessageType.CodeResponse });
    	String[] response = lexer.getResponse();
	    if (!CommandInterface.ServerResonses.PossitiveCompletionReply.CLOSING_DATA_CONNECTION.equals(response[0])) {
		      throw new FTPException(FTPStatus.ERROR, lc.getLanguage()
		    		  .cannotCreateFile());
	    }
	}

}
