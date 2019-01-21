package com.gajos.ftp.commands;

import java.util.ArrayList;
import java.util.List;

import com.gajos.ftp.CharTokenizer;
import com.gajos.ftp.FTPException;
import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.FTPMessageType;
import com.gajos.ftp.FTPScanner;
import com.gajos.ftp.FTPStatus;

/**
 * Command for list directory content.
 * @author P.Gajewski
 *
 */
public class ListDirectoryCommand implements CommandInterface {

	List<String> content = new ArrayList<>();
	
	public ListDirectoryCommand()
	{
		
	}
	
	@Override
	public void execute(FTPLexer lexer) {
		// TODO Auto-generated method stub
		//Send command.
	    EnterPassiveModeCommand temp = new EnterPassiveModeCommand();
	    temp.execute(lexer);
	    
    	lexer.send(CommandInterface.Commands.GET_FILES_LIST);
    	//lexer.analize(new FTPMessageType[] {FTPMessageType.CodeResponse});
    	String[] response;// = lexer.getResponse();
	    
	    //if(!CommandInterface.ServerResonses.PossitiveCompletionReply.REQUESTED_OK.equals(response[0]))
	    //	throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotPrintDirectoryPath());
	    
	    FTPLexer tempLexer = new FTPLexer(new FTPScanner(new CharTokenizer(temp.getIpAddress(),temp.getPort())));
	    
	    List<String> content = new ArrayList<>();
	    //Get directory content.
	    while(!tempLexer.isEndReached())
	    {
	    	lexer.analize(new FTPMessageType[] {FTPMessageType.FilePath});
	    	content.add(lexer.getResponse()[0]);
	    }
	    
    	lexer.analize(new FTPMessageType[] {FTPMessageType.CodeResponse});
    	response = lexer.getResponse();
	    
	    if(!CommandInterface.ServerResonses.PossitiveCompletionReply.CLOSING_DATA_CONNECTION.equals(response[0]))
	    	throw new FTPException(FTPStatus.ERROR, lc.getLanguage().cannotPrintDirectoryPath());
	}
	
	public List<String> getContent()
	{
		return content;
	}

	
	
}
