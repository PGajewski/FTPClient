package com.gajos.ftp.commands;

import com.gajos.ftp.FTPLexer;

/**
 * Command for disconnection with server.
 * @author P.Gajewski
 *
 */
public class DisconnectCommand implements CommandInterface {

	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
		//Disconnect
		//Send exit command to server.
		lexer.send(CommandInterface.Commands.DISCONNECT);
		
		//Disconnect on client side - delete tokenizer.
		lexer.close();
	}
}
