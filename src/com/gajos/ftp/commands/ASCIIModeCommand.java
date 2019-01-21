package com.gajos.ftp.commands;

import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPStatus;

/**
 * Command to enter into ASCII mode.
 * @author P.Gajewski
 *
 */
public class ASCIIModeCommand implements CommandInterface {

	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
		//Send command.
    	lexer.send(CommandInterface.Commands.SET_TRANSFER_TYPE + ' ' + CommandInterface.TransmissionTypes.ASCII);
    	lexer.analize(new FTPMessageType[]{FTPMessageType.CodeResponse});
    	String[] response = lexer.getResponse();

    	//Check code response.
		if(!CommandInterface.ServerResonses.PossitiveCompletionReply.GENERAL
				.equals(response[0]))
		{
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().strangeResponse() + response);
		}
	}

}
