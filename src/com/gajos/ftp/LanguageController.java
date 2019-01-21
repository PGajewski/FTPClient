package com.gajos.ftp;

/**
 * Singleton for language controller.
 * @author P.Gajewski
 *
 */
public class LanguageController {

	private final static LanguageController instance = new LanguageController();
	private final static Language DEFAULT_LANGUAGE = new English();
	private static boolean initialized = false;
	private Language language;
	
	/*****************Inner classes for languages*************************/
	/**
	 * Interface for languages.
	 * @author P.Gajewski
	 *
	 */
	
	public interface Language
	{
		public String serializationError();
		public String deserializationError();
		public String incompatibleFile();
		public String incorrectIPAddress();
		public String incorrectPort();
		public String portInUse();
		public String disconnectError();
		public String alreadyConnected();
		public String readLineError();
		public String strangeResponse();
		public String invalidLoginOrPassword();
		public String sendingError();
		public String fileNotFound();
		public String invalidSocketFromServer();
		public String cannotCreateDirectory();
		public String cannotChangeDirectory();
		public String cannotPrintDirectoryPath();
		public String openFile();
		public String saveFileAs();
		public String savingConfirm();
		public String savingAsk();
		public String savingAlert();
		public String readCharError();
		public String notCodeType();
		public String notFilePath();
		public String invalidType();
		public String messageTooShort();
		public String messageTooLong();
		public String cannotCreateFile();
		public String notIPAddress();
		public String tokenTooLong();
		public String notPortNumber();
		public String notConnected();
		public String hostname();
		public String serverAddress();
		public String serverPort();
		public String add();
		public String clear();
		public String addServer();
		public String error();
		public String warning();
		public String ok();
		public String projectPath();
		public String notNumber();
		public String size();
		public String chooseServerFirst();
		public String connect();
		public String change();
		public String filePath();
		public String connectError();
		public String cancel();
		public String downloadFile();
		public String uploadFile();
		public String username();
		public String password();
		
	}
	/**
	 * Polish
	 * @author P.Gajewski
	 *
	 */
	public static class Polish implements Language 
	{
		public String serializationError()
		{
			return new String("B³¹d w trakcie zapisywania.");
		}
		
		public String deserializationError()
		{
			return new String("B³¹d odczytu z pliku.");
		}
		
		public String incompatibleFile()
		{
			return new String("Nieprawid³owy plik!");
		}
		
		public String incorrectIPAddress()
		{
			return new String("B³êdny adres IP!");
		}
		
		public String incorrectPort()
		{
			return new String("Nieprawid³owy port. Ustaw port z zakresu 1-49151.");
		}
		
		public String portInUse()
		{
			return new String("Port w u¿yciu.");			
		}
		
		public String disconnectError()
		{
			return new String("B³¹d przy roz³¹czaniu.");
		}
		
		public String alreadyConnected()
		{
			return new String("Ju¿ po³¹czony.");			
		}
		
		public String readLineError()
		{
			return new String("B³¹d odczytu linii.");
		}
		
		public String strangeResponse()
		{
			return new String("Dziwna odpowiedz servera: ");			
		}
		
		public String invalidLoginOrPassword()
		{
			return new String("Nieprawid³owy login lub has³o");
		}

		public String sendingError()
		{
			return new String("B³¹d podczas wysy³ania pliku: ");
		}
		
		public String fileNotFound()
		{
			return new String("Nie znaleziono pliku: ");
		}
		
		public String invalidSocketFromServer()
		{
			return new String("Nieprawid³owy format gniazda z serwera: ");
		}
		
		public String cannotCreateDirectory()
		{
			return new String("Nie mo¿na utworzyæ katalogu: ");
		}
		
		public String cannotChangeDirectory()
		{
			return new String("Nie mo¿na zmieniæ katalogu: ");

		}

		public String cannotPrintDirectoryPath()
		{
			return new String("Nie mo¿na odczytaæ œcie¿ki katalogu.");
		}
		
		public String openFile()
		{
			return new String("Otwórz plik:");
		}
		
		public String saveFileAs()
		{
			return new String("Zapisz plik jako:");
		}
		
		public String savingConfirm()
		{
			return new String("Zapisywanie projektu");
		}
		
		public String savingAsk()
		{
			return new String("Czy chcesz zapisaæ poprzedni projekt?");
		}
		
		public String savingAlert()
		{
			return new String("Wszystkie niezapisane zmiany zostan¹ cofniête.");
		}
		
		public String readCharError()
		{
			return new String("Bl¹d odczytu znaku.");
		}
		
		public String notCodeType()
		{
			return new String("Element nie jest kodem.");
		}
		
		public String notFilePath()
		{
			return new String("Element nie jest œcie¿k¹.");
		}
		
		public String invalidType()
		{
			return new String("Niepoprawny typ.");
		}
		
		public String messageTooShort()
		{
			return new String("Wiadomoœæ za krótka.");			
		}
		
		public String messageTooLong()
		{
			return new String("Wiadomoœæ za d³uga.");			
		}
		
		public String cannotCreateFile()
		{
			return new String("Nie mo¿na utworzyæ pliku.");		
		}
		
		public String notIPAddress()
		{
			return new String("Nie rozpoznano adresu IP.");
		}
		
		public String tokenTooLong()
		{
			return new String("Zbyt d³ugi token dla danego typu.");
		}
		
		public String notPortNumber()
		{
			return new String("Nie rozpoznano numeru portu.");
		}
		
		public String notConnected()
		{
			return new String("Nie po³¹czono.");
		}
		
		public String hostname()
		{
			return new String("nazwa hosta");
		}
		
		public String serverAddress()
		{
			return new String("adres");
		}
		
		public String serverPort()
		{
			return new String("port");
		}
		
		public String add()
		{
			return new String("dodaj");			
		}
		
		public String change()
		{
			return new String("zmieñ");			
		}
		
		public String clear()
		{
			return new String("wyczyœæ");			
		}
		
		public String addServer()
		{
			return new String("Dodaj server");			
		}
		
		public String error()
		{
			return new String("[B£¥D]");			
		}
		
		public String warning()
		{
			return new String("[OSTRZE¯ENIE]");			
		}
		
		public String ok()
		{
			return new String("[OK]");				
		}
		
		public String projectPath()
		{
			return new String("Œcie¿ka projektu: ");
		}
		
		public String notNumber()
		{
			return new String("To nie jest numer!");
		}
		
		public String size()
		{
			return new String("Rozmiar: ");
		}
		
		public String chooseServerFirst()
		{
			return new String("Wybierz server.");			
		}
		
		public String connect()
		{
			return new String("Po³¹cz z serverem");			
		}
		
		public String filePath()
		{
			return new String("Œcie¿ka pliku: ");			
		}
		
		public String connectError()
		{
			return new String("Nie mo¿na polaczyæ.");			
		}
		
		public String cancel()
		{
			return new String("Anuluj");			
		}
		
		public String downloadFile() {
			return new String("Œci¹gnij");						
		}
		
		public String uploadFile() {
			return new String("Wyœlij");			
		}
		
		public String username()
		{
			return new String("Login");						
		}
		
		public String password()
		{
			return new String("Has³o");						
		}
	}
	
	/**
	 * English
	 * @author P.Gajewski
	 *
	 */
	private static class English implements Language
	{
		public String serializationError()
		{
			return new String("Error in serialization.");
		}
		
		public String deserializationError()
		{
			return new String("Cannot read file.");
		}
		
		public String incompatibleFile()
		{
			return new String("Incompatible file!");
		}
		
		public String incorrectIPAddress()
		{
			return new String("Bad IP address!");
		}
		
		public String incorrectPort()
		{
			return new String("Incorrect port. Set value from range 1-49151.");
		}
		
		public String portInUse()
		{
			return new String("Port in use.");			
		}
		
		public String disconnectError()
		{
			return new String("Cannot disconnect connection.");
		}
		
		public String alreadyConnected()
		{
			return new String("Already connected.");			
		}
		
		public String readLineError()
		{
			return new String("Read line error.");
		}
		
		public String strangeResponse()
		{
			return new String("Strange server response: ");			
		}
		
		public String invalidLoginOrPassword()
		{
			return new String("Invalid login or password.");
		}
		
		public String sendingError()
		{
			return new String("Error on sending file: ");
		}
		
		public String fileNotFound()
		{
			return new String("File not found: ");
		}
		
		public String invalidSocketFromServer()
		{
			return new String("Invalid socket format: ");
		}
		
		public String cannotCreateDirectory()
		{
			return new String("Cannot create a directory: ");
		}
		
		public String cannotChangeDirectory()
		{
			return new String("Cannot change a directory.");

		}
		
		public String cannotPrintDirectoryPath()
		{
			return new String("Cannot read a directory path.");
		}
		
		public String openFile()
		{
			return new String("Open file:");
		}
		
		public String saveFileAs()
		{
			return new String("Save file as:");
		}
		
		public String savingConfirm()
		{
			return new String("Save project");
		}
		
		public String savingAsk()
		{
			return new String("Do you want save previous project?");
		}
		
		public String savingAlert()
		{
			return new String("All unsaved changed will be erised.");
		}
		
		public String readCharError()
		{
			return new String("Read char error.");
		}
		
		public String notCodeType()
		{
			return new String("Token is not a code.");
		}
		
		public String notFilePath()
		{
			return new String("Token is not a path.");
		}
		
		public String invalidType()
		{
			return new String("Invalid type.)");
		}
		
		public String messageTooShort()
		{
			return new String("Message too short.");			
		}
		
		public String messageTooLong()
		{
			return new String("Message too long.");			
		}
		
		public String cannotCreateFile()
		{
			return new String("Cannot create a file.");		
		}
		
		public String notIPAddress()
		{
			return new String("Not recognized IP address.");
		}
		
		public String tokenTooLong()
		{
			return new String("Token too long for this type.");
		}
		
		public String notPortNumber()
		{
			return new String("Not recognized port number.");
		}
		
		public String notConnected()
		{
			return new String("Not connected.");
		}
		
		public String hostname()
		{
			return new String("hostname");
		}
		
		public String serverAddress()
		{
			return new String("address");
		}
		
		public String serverPort()
		{
			return new String("port");
		}
		
		public String add()
		{
			return new String("add");			
		}
		
		public String change()
		{
			return new String("change");			
		}
		
		public String clear()
		{
			return new String("clear");			
		}
		
		public String addServer()
		{
			return new String("Add server");			
		}
		
		public String error()
		{
			return new String("[ERROR]");			
		}
		
		public String warning()
		{
			return new String("[WARNING]");			
		}
		
		public String ok()
		{
			return new String("[OK]");				
		}
		
		public String projectPath()
		{
			return new String("Project path: ");
		}
		
		public String notNumber()
		{
			return new String("Not a number!");
		}
		
		public String size()
		{
			return new String("Size: ");
		}
		
		public String chooseServerFirst()
		{
			return new String("Choose the server first.");			
		}
		
		public String connect()
		{
			return new String("Connect with a server");			
		}
		
		public String filePath()
		{
			return new String("File path: ");			
		}
		
		public String connectError()
		{
			return new String("Cannot connect.");			
		}
		
		public String cancel()
		{
			return new String("Cancel");			
		}
		
		public String downloadFile() {
			return new String("Download");						
		}
		
		public String uploadFile() {
			return new String("Upload");			
		}
		
		public String username()
		{
			return new String("Login");						
		}
		
		public String password()
		{
			return new String("Password");						
		}
	}
	
	/**
	 * Private constructor.
	 */
	private LanguageController() {
		
	}
	
	/**
	 * Private init method.
	 */
	private void init()
	{
		//Initialize by default language.
		this.language = DEFAULT_LANGUAGE;
		
	}
	
	public static synchronized LanguageController getInstance()
	{
		if (initialized) return instance;
		instance.init();
		return instance;
		
	}
	
	public synchronized void setLanguage(String language)
	{
		switch(language)
		{
			case "polish" : this.language = new Polish();
			case "english" : this.language = new English();
			//Reset to default language.
			default: this.language = DEFAULT_LANGUAGE;
		}
	}
	
	public synchronized Language getLanguage()
	{
		return this.language;
	}
}
