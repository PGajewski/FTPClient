package com.gajos.ftp.commands;

import com.gajos.ftp.FTPLexer;
import com.gajos.ftp.LanguageController;

/**
 * Interface for all command classes.
 * @author P.Gajewski
 *
 */
public interface CommandInterface {

	public static class Commands
	{
		public static final String ABORT = "ABOR";
		public static final String ACCOUNT_INFO = "ACCT";
		public static final String AUTHENTICATION_DATA = "ADAT";
		public static final String ALLOCATE_DISK_SPACE = "ALLO";
		public static final String APPEND = "APPE";
		public static final String AUTHENTICATION_MECHANISM = "AUTH";
		public static final String GET_AVAILABLE_SPACE = "AVBL";
		public static final String CLEAR_COMMAND_CHANNEL = "CCC";
		public static final String CHANGE_TO_PARENT_DIRECTORY = "CDUP";
		public static final String CONFIDENTIALITY_PROTECTION_COMMAND = "CONF";
		public static final String SERVER_IDENTIFICATION = "CSID";
		public static final String CHANGE_WORKING_DIRECTORY = "CWD";
		public static final String DELETE_FILE = "DELE";
		public static final String GET_DIRECTORY_SIZE = "DSIZ";
		public static final String PRIVACY_PROTECTED = "ENC";
		public static final String SPECIFY_ADDRESS_AND_PORT = "EPRT";
		public static final String ENTER_EXTENDED_PASSIVE_MODE = "EPSV";
		public static final String GET_FEATURE_LIST = "FEAT";
		public static final String HELP = "HELP";
		public static final String GET_HOST_BY_NAME = "HOST";
		public static final String LANGUAGE_NEGOTIATION = "LANG";
		public static final String GET_FILES_LIST = "LIST";
		public static final String SPECIFY_LONGADDRESS_AND_PORT = "LPRT";
		public static final String ENTER_LONG_PASSIVE_MODE = "LPSV";
		public static final String GET_LAST_MODIFICATION_TIME = "MDTM";
		public static final String MODIFY_CREATION_TIME = "MFCT";
		public static final String MODIFY_FACT = "MFF";
		public static final String MODIFY_MODYFICATION_TIME = "MFMT";
		public static final String INTEGRITY_PROTECTION = "MIC";
		public static final String MAKE_DIRECTORY = "MKD";
		public static final String LIST_A_CONTENT = "MLSD";
		public static final String PROVIDES_DATA = "MLST";
		public static final String SET_TRANSFER_MODE = "MODE";
		public static final String LIST_OF_FILE_NAMES = "NLST";
		public static final String NO_OPERATION = "NOOP";
		public static final String SELECT_OPTIONS = "OPTS";
		public static final String AUTHENTICATION_PASSWORD = "PASS";
		public static final String ENTER_PASSIVE_MODE = "PASV";
		public static final String PROTECTION_BUFFER_SIZE = "PBSZ";
		public static final String SPECIFY_PORT = "PORT";
		public static final String DATA_CHANNEL_PROTECTION_LEVEL = "PROT";
		public static final String PRINT_WORKING_DIRECTORY = "PWD";
		public static final String DISCONNECT = "QUIT";
		public static final String REINITIALIZE = "REIN";
		public static final String RESTART = "REST";
		public static final String RETRIEVE_A_COPY = "RETR";
		public static final String REMOVE_DIRECTORY = "RMD";
		public static final String REMOVE_DIRECTORY_TREE = "RMDA";
		public static final String RENAME_FROM = "RNFR";
		public static final String RENAME_TO = "RNTO";
		public static final String SITE_SPECIFFIC_COMMAND = "SITE";
		public static final String SIZE_OF_FILE = "SIZE";
		public static final String MOUNT_FILE_STRUCTURE = "SMNT";
		public static final String USE_SINGLE_PORT_PASSIVE_MODE = "SPSV";
		public static final String GET_STATUS = "STAT";
		public static final String ACCEPT_AND_STORE = "STOR";
		public static final String STORE_FILE_UNIQUELY = "STOU";
		public static final String SET_FILE_TRANSFER_STRUCT = "STRU";
		public static final String GET_SYSTEM_TYPE = "SYST";
		public static final String GET_THUMBNAIL = "THMB";
		public static final String SET_TRANSFER_TYPE = "TYPE";
		public static final String AUTHENTICATION_USERNAME = "USER";
	}
	
	/**
	 * Types of transmission.
	 * @author P.Gajewski
	 *
	 */
	public static class TransmissionTypes
	{
		public static final String BINARY = "I";
		public static final String ASCII = "A";
	}

	/**
	 * Inner class for responses from server.
	 * @author P.Gajewski
	 *
	 */
	public static class ServerResonses
	{
		/**
		 * 1xx replay codes.
		 * @author P.Gajewski
		 *
		 */
		public static class PossitivePreliminaryReply
		{
			public static final String GENERAL = "100";
			public static final String RESTART_REPLY = "110";
			public static final String SERVICE_READY = "120";	
			public static final String DATA_CONNECTION_ALREADY_OPENED = "125";
			public static final String FILE_STATUS_OKAY = "150";
		}
		
		/**
		 * 2xx replay codes.
		 * @author P.Gajewski
		 *
		 */
		public static class PossitiveCompletionReply
		{
			public static final String GENERAL = "200";
			public static final String SYSTEM_STATUS = "211";
			public static final String DIRECTORY_STATUS = "212";
			public static final String FILE_STATUS = "213";
			public static final String HELP_MESSAGE = "214";
			public static final String NAME_SYSTEM_TYPE = "215";
			public static final String READY_FOR_NEW_USER = "220";
			public static final String SERVICE_CLOSING_CONTROL_CONNECTION = "221";
			public static final String OPEN_DATA_CONNECTION = "225";			
			public static final String CLOSING_DATA_CONNECTION = "226";
			public static final String PASSIVE_MODE = "227";
			public static final String LONG_PASSIVE_MODE = "228";
			public static final String EXTENDED_PASSIVE_MODE = "229";
			public static final String USER_LOG_IN = "230";
			public static final String USER_LOG_OUT = "231";
			public static final String LOGOUT_NOTED = "232";
			public static final String REQUESTED_OK = "250";
			public static final String PATHNAME_CREATED = "257";			
		}
		
		/**
		 * 3xx replay codes.
		 * @author P.Gajewski
		 *
		 */
		public static class PositiveIntermediateReply
		{
			public static final String GENERAL = "300";
			public static final String USERNAME_OK_PASSWORD_NEEDED = "331";
			public static final String NEED_ACCOUNT_FOR_LOGIN = "332";
			public static final String REQUESTED_FILE_ACTION = "350";
		}
		
		/**
		 * 4xx replay codes.
		 * @author P.Gajewski
		 *
		 */
		public static class TransientNegativeCompletionReply
		{
			public static final String GENERAL = "400";
			public static final String SERVICE_NOT_AVAILABLE = "421";
			public static final String CANT_OPEN_DATA_CONNECTION = "425";
			public static final String CONNECTION_CLOSED = "426";
			public static final String INVALID_USERNAME_OR_PASSWORD = "430";
			public static final String REQUESTED_HOST_UNAVAILABLE = "434";
			public static final String REQUESTED_FILE_ACTION_NOT_TAKEN = "450";
			public static final String LOCAL_ERROR = "451";
			public static final String FILE_BUSY = "452";	
		}
		
		/**
		 * 5xx replay codes.
		 * @author P.Gajewski
		 *
		 */
		public static class PermamentNegativeCompletionReply
		{
			public static final String GENERAL = "500";
			public static final String SYNTAX_ERROR = "501";
			public static final String COMMAND_NOT_IMPLEMENTED = "502";
			public static final String BAD_SEQUENCE_OF_COMMANDS = "503";
			public static final String COMMAND_NOT_IMPLEMENTED_FOR_THAT_PARAMETER = "504";
			public static final String NOT_LOGGED_IN = "530";
			public static final String NEED_ACCOUNT_FOR_STORING_FILES = "532";
			public static final String POLICY_REQUIRES_SSL = "534";
			public static final String FILE_NOT_FOUND = "550";
			public static final String PAGE_TYPE_UNKNOWN = "551";			
			public static final String EXCEEDED_STORAGE_ALLOCATION = "552";
			public static final String FILE_NAME_NOT_ALLOWED = "553";		
		}
		
		/**
		 * 6xx replay codes.
		 * @author P.Gajewski
		 *
		 */
		public static class ProtectedReply
		{
			public static final String GENERAL = "600";
			public static final String INTEGRITY_PROTECTED_REPLY = "631";
			public static final String CONFIDENTIALITY_AND_INTEGRITY_PROTECTED_REPLY = "632";			
			public static final String CONFIDENTIALITY_PROTECTED_REPLY = "633";			
		}
	}
	
	/**
	 * Language controller.
	 */
	public final static LanguageController lc = LanguageController.getInstance();
	
	public void execute(FTPLexer lexer);
}
