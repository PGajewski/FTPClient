package com.gajos.ftp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

public class FTPScanner implements DataTokenizer{
	
	/**
	 * Language controller.
	 */
	private static LanguageController lc = LanguageController.getInstance();
	
	public static final Character[][] VALID_CHARS = {
			{'0','1','2','3','4','5','6','7','8','9'},
			{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','W','X','Y','Z'},
			{'0','1','2','3','4','5','6','7','8','9',',',',', '(',},
			{'0','1','2','3','4','5','6','7','8','9', ')'},
			{'0','1','2','3','4','5','6','7','8','9','+','-'}
	};
	
	public static final Character[][] INVALID_CHARS = {
			null,
			{'/',':','?','|','<','>'},
			null,
			null,
			null
	};
	
	public static final Character[][] END_CHARS = {
			{' ', 10},
			{' ', 10},
			{' ', 10},
			{' ', 10},
			{' ', 10}
	};
	
	public static final int MAX_TAG_SIZE = 255;
	
	public static final int MAX_TAG_SIZES[] = {
			3,
			MAX_TAG_SIZE,
			MAX_TAG_SIZE,
			20,
			MAX_TAG_SIZE
	};
	
	private CharTokenizer charTokenizer;
	
	private String tagBuffer;
		
	private StringBuffer buffer = new StringBuffer();
	
	private boolean isEnd;
	
	public FTPScanner() {
		buffer = new StringBuffer();
	}

	private static boolean checkCodeResponse(char c, int pos)
	{
		//Check valid chars.
		if(Arrays.stream(VALID_CHARS[FTPMessageType.CodeResponse.getValue()])
				.noneMatch(p -> {
					return p.charValue() == c;}))
		{
			return false;
		}
		return true;
	}
	
	private boolean checkFilePath(char c, int pos)
	{
		//Check first char is root sign.
		if(pos == 0 && (c != '/'))
		{
			return false;
		}
		
		//Check any of invalid chars.
		if(Arrays.stream(INVALID_CHARS[FTPMessageType.FilePath.getValue()])
				.anyMatch(p -> {return p.charValue() == c;}))
		{
			return false;
		}
		return true;
	}
	
	private boolean checkPortNumber(char c, int pos)
	{		
		if(c == ')')
		{
			parsePortNumber();
			return true;
		}
		
		//Check valid chars.
		if(Arrays.stream(VALID_CHARS[FTPMessageType.PortNumber.getValue()])
				.noneMatch(p -> {return p.charValue() == c;}))
		{
			return false;
		}
		return true;
	}
	
	private void parseIPAddress()
	{
		//Break parsing.
		isEnd = true;
		
		//Change all commas into dots and delete (
		for(int i = 0; i < buffer.length();)
		{
			if(buffer.charAt(i) == ',')
			{
				buffer.deleteCharAt(i);
				buffer.insert(i, '.');
				++i;
			} else if(buffer.charAt(i) == '(')
			{
				buffer.deleteCharAt(i);
			} else {
				++i;
			}
		}
	}
	
	private void parsePortNumber()
	{
		isEnd = true;
		int comaPos = buffer.indexOf(",");
		String tempString = buffer.toString();
		int temp1 = Integer.parseInt(String.valueOf(tempString.substring(0, comaPos)));
		int temp2 = Integer.parseInt(String.valueOf(tempString.substring(comaPos+1)));
		clearBuffer();
		buffer.append(Integer.toString(temp1 * 256 + temp2));
	}
	
	private int commaCounter;
	
	private boolean checkIPAddress(char c, int pos)
	{
		if(pos == 0)
		{
			commaCounter = 0;
		} else if(c == ',')
		{
			if(commaCounter == 3)
			{
				parseIPAddress();
				return true;
			} else {
				++commaCounter;
			}
		}
		
		//Check character pass to IP address.
		if(Arrays.stream(VALID_CHARS[FTPMessageType.IPAddress.getValue()])
				.noneMatch(p -> {return p.charValue() == c;}))
		{
			return false;
		}
		return true;
	}
	
	private boolean checkNumber(char c, int pos)
	{
		//Check character pass to IP address.
		if(Arrays.stream(VALID_CHARS[FTPMessageType.Number.getValue()])
				.noneMatch(p -> {return p.charValue() == c;}))
		{
			return false;
		}
		return true;
	}
	
	private List<BiPredicate<Character,Integer>> checkChar = new ArrayList<>();
	
	private List<Supplier<String>> errorThrowers = new ArrayList<>();
	
	/**
	 * Initialize function lists.
	 */
	{
		checkChar.add((c,i)->{return checkCodeResponse(c,i);});
		checkChar.add((c,i)->{return checkFilePath(c,i);});
		checkChar.add((c,i)->{return checkIPAddress(c,i);});
		checkChar.add((c,i)->{return checkPortNumber(c,i);});
		checkChar.add((c,i)->{return checkNumber(c,i);});

		errorThrowers.add(()->{return lc.getLanguage().notCodeType();});
		errorThrowers.add(()->{return lc.getLanguage().notFilePath();});
		errorThrowers.add(()->{return lc.getLanguage().notIPAddress();});
		errorThrowers.add(()->{return lc.getLanguage().notPortNumber();});
		errorThrowers.add(()->{return lc.getLanguage().notNumber();});

	}
	public FTPScanner(CharTokenizer tokenizer)
	{
		this.charTokenizer = tokenizer;
	}
	
	public Object get()
	{
		return this.tagBuffer;
	}
	
	private void clearBuffer()
	{
		if(buffer != null)
		{
			buffer.delete(0, tagBuffer.length());
			buffer.setLength(0);
		}
	}
	
	
	public synchronized void next(FTPMessageType type) throws FTPException
	{
		int charCounter = 0;
		isEnd = false;
		
		try {
			while(true)
			{
				charTokenizer.next(null);
				final char temp = ((Character)(charTokenizer.get())).charValue();

				//Char tokenizer reached end
				if(charTokenizer.isEndReached())
				{
					break;
				}
				
				//Check special end char.
				if(Arrays.stream(END_CHARS[type.getValue()])
						.anyMatch(p -> {return p.charValue() == temp;}))
				{
					break;
				}
				
				//Check size of token.
				if(MAX_TAG_SIZES[type.getValue()] < charCounter)
				{
					throw new FTPException(FTPStatus.ERROR,lc.getLanguage().tokenTooLong());
				}
				
				//Analyze message depends of type.
				if(!checkChar.get(type.getValue()).test(temp, charCounter))
				{
					//throw new FTPException(FTPStatus.ERROR, errorThrowers.get(type.getValue()).get());
				}
				
				if(isEnd)
				{
					break;
				}
				
				//All correct, add char.
				++charCounter;
				buffer.append(temp);
			}
			//Check
			tagBuffer = buffer.toString();
			System.out.println(type.getValue() + ": " + tagBuffer);
		} finally {
			clearBuffer();
			isEnd = true;
		}
		
	}
	
	public boolean isEndReached()
	{
		return this.isEnd;
	}
	
	public void send(String message) throws FTPException
	{
		charTokenizer.send(message);
	}
	
	public void close() throws FTPException
	{
		charTokenizer.close();
	}
	
	public void connect() throws FTPException
	{
		charTokenizer.connect();
	}
	
	public void sendStream(InputStream stream) throws FTPException
	{
		charTokenizer.sendStream(stream);
	}
	
	public void clean()
	{
		charTokenizer.clean();
	}

	@Override
	public void setTimeout(int time) {
		charTokenizer.setTimeout(time);
		
	}
}
