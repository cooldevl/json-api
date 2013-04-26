package com.asksunny.json.parser;

public interface JSONParserConstants {
	public final static char ESCAPE_SLASH = '\\';
	public final static char TAB_CHAR = '\t';
	public final static char RETURN_CHAR = '\r';
	public final static char BACKSPACE_CHAR = '\b';
	public final static char FORMFEED_CHAR = '\f';
	public final static char NEWLINE_CHAR = '\n';
	public final static char FORWARD_SLASH_CHAR = '/';
	public final static int STATE_MASK =  1;
	public final static int QUOTE = '"';	
	public final static int CURLY_BRACKET_OPEN = '{';
	public final static int CURLY_BRACKET_CLOSE = '}';
	public final static int SQUARE_BRACKET_OPEN = '[';
	public final static int SQUARE_BRACKET_CLOSE = ']';	
	
	public final static int COMMA = ',';	
	public final static int COLON = ':';
	
	public final static int TOKEN_TYPE_NVPSEP = 1000001;
	public final static int TOKEN_TYPE_NUMBER = Integer.MAX_VALUE - 1000;
	public final static int TOKEN_TYPE_STRING = Integer.MAX_VALUE - 1001;
	public final static int TOKEN_TYPE_IDENTIFIER = Integer.MAX_VALUE - 1002;
	public final static int TOKEN_TYPE_NULL = Integer.MAX_VALUE - 1003;
	public final static int TOKEN_TYPE_BOOLEAN = Integer.MAX_VALUE - 1004;
}
