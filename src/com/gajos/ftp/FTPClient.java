package com.gajos.ftp;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.gajos.ftp.commands.*;

/**
 * Main class of FTP client.
 * @author P.Gajewski
 *
 */
public class FTPClient implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Language controller.
	 */
	private final static LanguageController lc = LanguageController.getInstance();
	
	/**
	 * Server commands
	 * @author P.Gajewski
	 *
	 */

	public static final String DEFAUL_LOGIN = "anonymous";
	public static final String DEFAUL_PASSWORD = "anonymous@example.com";
	public static final long DEFAULT_TIMEOUT = 1000*100;
	
	private class DisconnectTask extends TimerTask implements Serializable
	{

		/**
		 * Version UID for serialization.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void run() {
			FTPClient.this.lexer.close();
		}
	}
	
	
	/**
	 * IP address of FTP server.
	 */
	private InetAddress ipAddress;
	
	/**
	 * IP protocol Port.
	 */
	private int port;
	
	/**
	 * Name of the server in project.
	 */
	private String hostname;
	
	/**
	 * FTP Tokenizer.
	 */
	private FTPLexer lexer;
	
	/**
	 * User login.
	 */
	private String login;
	
	/**
	 * User password.
	 */
	private String password;
	
	/**
	 * Method to check connection status.
	 * @return Connection state
	 */
	public boolean isConnected()
	{
		return lexer != null;
	}
	
	private FTPFileManager fileManager;
	
	/**
	 * Public constructor.
	 */
	public FTPClient()
	{
	}
	
	/**
	 * Method to set server IP address.
	 * @param ipAddress
	 * @throws FTPException
	 */
	public void setIpAddress(String ipAddress) throws FTPException
	{
		try {
			this.ipAddress = InetAddress.getByName(ipAddress);
		} catch (UnknownHostException e) {
			// Incorrect IP address.
        	throw new FTPException(FTPStatus.ERROR, lc.getLanguage().incorrectIPAddress());
		}
	}
	
	/**
	 * Method to set server port.
	 * @param port
	 * @throws FTPException
	 */
	public void setPort(int port) throws FTPException
	{
		//Check suitable port.
		if (port > 0 && port <= 49151)
		{
			this.port = port;	
		}
		else
		{
			throw new FTPException(FTPStatus.ERROR, lc.getLanguage().incorrectPort());
		}
	}
	
	/**
	 * Method to set user login.
	 * @param login
	 */
	public void setLogin(String login)
	{
		this.login = login;
	}
	
	/**
	 * Method to set user password.
	 * @param password
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}

	
	/**
	 * Method to set new hostname.
	 * @param hostname
	 */
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	/**************************************Getters*************************************/
	/**
	 * IP address getter.
	 * @return IP address.
	 */
	public InetAddress getIpAddress()
	{
		return this.ipAddress;
	}
	
	/**
	 * IP protocol port getter.
	 * @return port
	 */
	public int getPort()
	{
		return this.port;
	}
	
	/**
	 * Project hostname getter.
	 * @return hostname.
	 */
	public String getHostname()
	{
		return this.hostname;
	}
	
	/**
	 * Login client getter.
	 * @return
	 */
	public String getLogin()
	{
		return this.login;
	}
	
	/**
	 * Password client getter.
	 * @return
	 */
	public String getPassword()
	{
		return this.password;
	}
	
	/**
	 * Method to connect with user.
	 * @throws FTPException
	 */
	public synchronized void connect() throws FTPException
	{
		connect(login,password);
	}
	
	/**
	 * Method to connect with server by login and password.
	 * @param login
	 * @param password
	 */
	public synchronized void connect(String login, String password) throws FTPException
	{
		//Check that tokenizer exist.
		if (this.lexer != null)
		{
			throw new FTPException(FTPStatus.OK, lc.getLanguage()
					.alreadyConnected());
		}
		
		//Catch port.
		this.lexer = new FTPLexer(new FTPScanner(new CharTokenizer(ipAddress, port)));
		
	    try
	    {
	    	//Connect
	    	new ConnectCommand().execute(lexer);
	    	
	    	//Login.
	    	new LoginCommand(login, password).execute(lexer);
	    	
		}
		catch(FTPException e)
		{
			//Close connection if needed.
			if(FTPStatus.ERROR.equals(e.getStatus()))
			{
				lexer.close();
				lexer = null;
				throw e;
			}
		}
	}
	
	/**
	 * Method to disconnect with server.
	 * @throws FTPException
	 */
	public synchronized void disconnect() throws FTPException
	{
		//Check that tokenizer exist.
		if (this.lexer == null)
		{
			throw new FTPException(FTPStatus.OK, lc.getLanguage()
					.notConnected());
		}
		
		//Disconnect
		new DisconnectCommand().execute(lexer);
		this.lexer = null;
	}
	
	/**
	 * Method to receive file from FTP server.
	 * @throws FTPException
	 */
	public synchronized void receiveFile(String path, File destinationFile) throws FTPException
	{
		checkConnection();

    	//Analyze starting connections.
		new RetrieveCommand(path).execute(lexer);
		
    	//Enter passive mode.
    	fileManager = new FTPFileManager(new FTPByteStream(enterPassiveMode()));
    	
		//Receive file.
    	fileManager.downloadFile(destinationFile);
    	
	    //Closing.
    	new CloseDataConnectionCommand().execute(lexer);
	}
	
	public synchronized long getSize(String path)
	{
		//Check that tokenizer exist.
		if (this.lexer == null)
		{
			throw new FTPException(FTPStatus.OK, lc.getLanguage().notConnected());
		}
		
		//Send command.
		GetSizeCommand command = new GetSizeCommand(path);
		command.execute(lexer);
		
		return command.getSize();
	}
	

	public synchronized List<String> listDirectoryContent() throws FTPException
	{
		//Check that tokenizer exist.
		if (this.lexer == null)
		{
			throw new FTPException(FTPStatus.OK, lc.getLanguage().notConnected());
		}
		ListDirectoryCommand command = new ListDirectoryCommand();
		command.execute(lexer);
		
		return command.getContent();
	}
	
	/**
	 * Method to change actual directory.
	 * @param name
	 * @throws FTPException
	 */
	public synchronized void changeDirectory(String path) throws FTPException
	{
		checkConnection();
		
		new ChangeDirectoryCommand(path).execute(lexer);
	}
	
	public synchronized String showActualDirectory() throws FTPException
	{
		checkConnection();
		
		ShowDirectoryCommand command = new ShowDirectoryCommand();
		command.execute(lexer);
		return command.getName();
	}
	
	/**
	 * Create directory on FTP server.
	 * @param name
	 * @throws FTPException
	 */
	public synchronized void makeDirectory(String name) throws FTPException
	{
		checkConnection();

		new MakeDirectoryCommand(name).execute(lexer);
		
	}
	
	/**
	 * Method to send file to FTP server.
	 * @param name
	 * @throws FTPException
	 */
	public void sendFile(File sourceFile, String serverPath) throws FTPException
	{
		checkConnection();
		
		if(sourceFile.isDirectory())
		{
			//Make directory.
			makeDirectory(serverPath);
			
			//Change directory.
			changeDirectory(serverPath);
			
			//Send directory recursive.
			Arrays.stream(sourceFile.listFiles())
				.forEach((f)-> {
						this.sendFile(f, serverPath + '/' + f.getName());});
		}
		else
		{
			//Send command.
			new StoreFileCommand(serverPath).execute(lexer);
			
	    	//Enter passive mode.
	    	FTPFileManager fileManager = new FTPFileManager(new FTPByteStream(enterPassiveMode()));
	    	
			//Send file.
	    	fileManager.uploadFile(sourceFile);
	    	
		    //Closing.
	    	new CloseDataConnectionCommand().execute(lexer);
		}	
	}
	
	/*******************Special timeout functional interfaces*********************/
	/**
	 * Public function to run BiConsumer operation with timeout.
	 * @param function Action
	 * @param arg1 First argument
	 * @param arg2 Second argument
	 * @param timeout Action timeout
	 */
	public <T,R> void operiationWithTimeout(BiConsumer<T, R> function, T arg1, R arg2, long timeout)
	{
		//Init task timer.
		Timer timer = new Timer(true);
		
		lexer.setTimeout((int) timeout);
		timer.schedule(new DisconnectTask() , timeout);
		
		function.accept(arg1, arg2);
		
		//Cancel timer if task was finished.
		timer.cancel();
	}
	
	/**
	 * Public function to run Consumer operation with timeout
	 * @param function Action
	 * @param arg Argument
	 * @param timeout Action timeout
	 */
	public <T> void operiationWithTimeout(Consumer<T> function, T arg, long timeout)
	{
		//Init task timer.
		Timer timer = new Timer(true);
		
		lexer.setTimeout((int) timeout);
		timer.schedule(new DisconnectTask() , timeout);
		
		function.accept(arg);
		
		//Cancel timer if task was finished.
		timer.cancel();
	}
	
	/**
	 * Public function to run Supplier operation with timeout
	 * @param function Action
	 * @param timeout Action timeout
	 * @return Action result
	 */
	public <T> T operiationWithTimeout(Supplier<T> function, long timeout)
	{
		//Init task timer.
		Timer timer = new Timer(true);
		if(this.lexer != null)
			lexer.setTimeout((int) timeout);
		timer.schedule(new DisconnectTask() , timeout);
		
		T temp = function.get();
		
		//Cancel timer if task was finished.
		timer.cancel();
		return temp;
	}
	
	/***************************Private methods********************************/
	
	/**
	 * Check connection
	 */
	private void checkConnection()
	{
		//Check that tokenizer exist.
		if (this.lexer == null)
		{
			throw new FTPException(FTPStatus.OK, lc.getLanguage().notConnected());
		}
	}
	
	/**
	 * Enter binary mode for sending binary files.
	 */
	private synchronized void enterBinaryMode() throws FTPException {

    	new BinaryModeCommand().execute(lexer);
	}

	/**
	 * Enter ASCII mode for sending text files. This is usually the default mode.
	 * Make sure you use binary mode if you are sending images or other binary
	 * data, as ASCII mode is likely to corrupt them.
	 */
	private synchronized void enterAsciiMode() throws FTPException {

		new ASCIIModeCommand().execute(lexer);
	}
	
	private synchronized CharTokenizer enterPassiveMode() throws FTPException
	{
		EnterPassiveModeCommand command = new EnterPassiveModeCommand();
		command.execute(lexer);
		
    	return new CharTokenizer(command.getIpAddress(), command.getPort());
	}

	public void stopDataTransfer() {
		// TODO Auto-generated method stub
		if(fileManager != null)
			fileManager.close();
	}
}
