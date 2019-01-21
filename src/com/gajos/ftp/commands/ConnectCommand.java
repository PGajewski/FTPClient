package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

/**
 * Command for init connection with server.
 * @author P.Gajewski
 *
 */
public class ConnectCommand implements CommandInterface {

	@Override
	public void execute(FTPLexer lexer) {

		//Transaction response.
		String[] response;
		
		//Connect
		lexer.connect();
    	
    	//Analyze starting connections.
    	lexer.analize(new FTPMessageType[] {FTPMessageType.CodeResponse});
    	response = lexer.getResponse();
    	if (!CommandInterface.ServerResonses.PossitiveCompletionReply.READY_FOR_NEW_USER.equals(response[0])) {
	    	throw new FTPException(FTPStatus.ERROR, lc.getLanguage()
	    			.strangeResponse() + response[0]);
    	}
	}
}
