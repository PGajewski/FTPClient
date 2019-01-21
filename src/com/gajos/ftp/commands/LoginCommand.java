package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

public class LoginCommand implements CommandInterface {

	private String password;
	
	private String login;
	
	public LoginCommand(String login, String password)
	{
		this.login = login;
		this.password = password;
	}
	
	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
		//Transaction response.
		String[] response;
		
    	//Send username.
    	lexer.send(CommandInterface.Commands.AUTHENTICATION_USERNAME + ' ' + login);
    	lexer.analize(new FTPMessageType[] {FTPMessageType.CodeResponse});
    	response = lexer.getResponse();
	    if (!CommandInterface.ServerResonses.PositiveIntermediateReply.USERNAME_OK_PASSWORD_NEEDED.equals(response[0])) {
	    	throw new FTPException(FTPStatus.ERROR, lc.getLanguage()
	    			.strangeResponse() + response[0]);
	    }
	    
	    //Send password.
	    lexer.send(CommandInterface.Commands.AUTHENTICATION_PASSWORD + ' ' + password);
    	lexer.analize(new FTPMessageType[] {FTPMessageType.CodeResponse});
    	response = lexer.getResponse();
		if (CommandInterface.ServerResonses.PossitiveCompletionReply.USER_LOG_IN
				.equals(response[0])) {
	    	return;
	    } else if (CommandInterface.ServerResonses.TransientNegativeCompletionReply.INVALID_USERNAME_OR_PASSWORD.equals(response[0]))
	    {
	    	throw new FTPException(FTPStatus.ERROR, lc.getLanguage()
	    			.invalidLoginOrPassword());
	    }
	    else
	    {
	    	throw new FTPException(FTPStatus.ERROR, lc.getLanguage()
	    			.strangeResponse() + response);
	    }
	}

}
