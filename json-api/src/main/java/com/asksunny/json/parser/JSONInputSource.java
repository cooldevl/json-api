package com.asksunny.json.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class JSONInputSource {

	Reader reader = null;
	int lineNumber = 1;
	int columnNumber = 0;
	// only support 31 level nesting for now.
	protected int objectState = 0;
	protected int arrayState = 0;
	protected List<Token> tokens = new ArrayList<Token>();
	protected int pos = 0;
	JSONStreamHandler jsonStreamHandler = null;
	Token prevToken = null;
	Token currToken = null;

	String tokenValue = null;
	boolean hasDecimal = false;

	public Token nextToken() {
		if (pos < tokens.size()) {
			return tokens.get(pos++);
		} else {
			return null;
		}
	}

	protected Token getLastToken() {
		return (jsonStreamHandler==null)?( (tokens.size() == 0) ? null : tokens.get(tokens.size() - 1)):this.currToken;
	}
	
	
	public JSONInputSource(Reader reader)  {
		this.reader = reader;		
	}
	
	public JSONInputSource(Reader reader, JSONStreamHandler jsonStreamHandler) throws IOException 
	{
		this(reader);
		this.jsonStreamHandler = jsonStreamHandler;
	}

	
	
	public JSONInputSource(String text) throws IOException {
		this(new StringReader(text));
	}
	
	public JSONInputSource(String text, JSONStreamHandler jsonStreamHandler) throws IOException {
		this(new StringReader(text), jsonStreamHandler);
	}

	public JSONInputSource(File f) throws IOException {
		this(new FileReader(f));
	}

	public JSONInputSource(File f, JSONStreamHandler jsonStreamHandler) throws IOException {
		this(new FileReader(f), jsonStreamHandler);
	}
	
	
	public JSONInputSource(InputStream in) throws IOException {
		this(new InputStreamReader(in));
	}

	public JSONInputSource(InputStream in, JSONStreamHandler jsonStreamHandler) throws IOException {
		this(new InputStreamReader(in), jsonStreamHandler);
	}
	
	public JSONInputSource(InputStream in, String charSet) throws IOException {
		this(new InputStreamReader(in, charSet));
	}
	
	public JSONInputSource(InputStream in, String charSet, JSONStreamHandler jsonStreamHandler) throws IOException {
		this(new InputStreamReader(in, charSet), jsonStreamHandler);
	}
	
	

	public void parseInput() throws IOException {
		int c = -1;
		StringBuilder buf = new StringBuilder();
		boolean inQuote = false;		
		while ((c = reader.read()) != -1) {
			// System.out.println((char)c);
			this.columnNumber++;
			switch (c) {
			case JSONParserConstants.CURLY_BRACKET_OPEN:
				if (!inQuote) {
					objectState = (objectState << 1) | (1 << 1);
					Token t = new Token(this.lineNumber, this.columnNumber,
							JSONParserConstants.CURLY_BRACKET_OPEN, "{");
					this.emmit(t);
				} else {
					buf.append((char) c);
				}
				break;
			case JSONParserConstants.CURLY_BRACKET_CLOSE:
				if (!inQuote) {
					if (getLastToken() == null) {
						throwError(JSONParserConstants.CURLY_BRACKET_CLOSE);						
					} else if (getLastToken().getTokenType() == JSONParserConstants.COLON) {
						if (tokenValue != null) {
							Token tv = new Token(this.lineNumber,
									this.columnNumber - tokenValue.length(),
									JSONParserConstants.TOKEN_TYPE_STRING,
									tokenValue);							
							this.emmit(tv);
							tokenValue = null;
						} else if (buf.length() > 0) {
							String val = buf.toString().trim();
							buf.setLength(0);
							Token tv = new Token(
									this.lineNumber,
									this.columnNumber - buf.length(),
									(val.equalsIgnoreCase("null")) ? JSONParserConstants.TOKEN_TYPE_NULL
											: ((val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false") ) ? JSONParserConstants.TOKEN_TYPE_BOOLEAN
													: JSONParserConstants.TOKEN_TYPE_NUMBER),
									val);
							this.emmit(tv);
						} else {
							throwError(JSONParserConstants.CURLY_BRACKET_CLOSE);
						}
					}
					objectState = objectState >> 1;
					Token t1 = new Token(this.lineNumber, this.columnNumber,
							JSONParserConstants.CURLY_BRACKET_CLOSE, "}");
					this.emmit(t1);					
				} else {
					buf.append((char) c);
				}
				break;
			case JSONParserConstants.SQUARE_BRACKET_OPEN:
				arrayState = (arrayState << 1) | (1 << 1);
				Token t2 = new Token(this.lineNumber, this.columnNumber,
						JSONParserConstants.SQUARE_BRACKET_OPEN, "[");
				this.emmit(t2);			
				break;
			case JSONParserConstants.SQUARE_BRACKET_CLOSE:
				
				if (getLastToken() == null) {
					throwError(JSONParserConstants.SQUARE_BRACKET_CLOSE);
				} else if (getLastToken().getTokenType() == JSONParserConstants.COMMA
						|| getLastToken().getTokenType() == JSONParserConstants.SQUARE_BRACKET_OPEN) {
					if (tokenValue != null) {
						Token tv = new Token(this.lineNumber,
								this.columnNumber - tokenValue.length(),
								JSONParserConstants.TOKEN_TYPE_STRING,
								tokenValue);
						this.emmit(tv);
						tokenValue = null;
					} else if (buf.length() > 0) {
						String val = buf.toString().trim();
						buf.setLength(0);
						Token tv = new Token(
								this.lineNumber,
								this.columnNumber - buf.length(),
								(val.equalsIgnoreCase("null")) ? JSONParserConstants.TOKEN_TYPE_NULL
										: ((val.equalsIgnoreCase("true") || val.equalsIgnoreCase("false") ) ? JSONParserConstants.TOKEN_TYPE_BOOLEAN
												: JSONParserConstants.TOKEN_TYPE_NUMBER),
								val);
						this.emmit(tv);
					}
				}
				arrayState = arrayState >> 1;
				Token t3 = new Token(this.lineNumber, this.columnNumber,
						JSONParserConstants.SQUARE_BRACKET_CLOSE, "]");
				this.emmit(t3);				
				break;
			case JSONParserConstants.QUOTE:
				buf.setLength(0);
				while ((c = reader.read()) != -1 ){
					this.columnNumber++;
					if(c!=JSONParserConstants.QUOTE){
						switch(c)
						{
							case JSONParserConstants.ESCAPE_SLASH:
								int next = reader.read();							
								this.columnNumber++;
								if (next == -1) {
									throwError(JSONParserConstants.ESCAPE_SLASH);
								}							
								switch (next) {
								case '"':
									buf.append(next);
									break;
								case '\\':
									buf.append(next);
									break;
								case '/':
									buf.append(next);
									break;
								case 'b':
									buf.append('\b');
									break;
								case 'f':
									buf.append('\f');
									break;
								case 'n':
									buf.append('\n');
									break;
								case 'r':
									buf.append('\r');
									break;
								case 't':
									buf.append('\t');
									break;
								case 'u':
									char[] hex = new char[4];
									int readc = reader.read(hex);
									if (readc != 4) {
										JSONParsingException pex = new JSONParsingException(
												"Unicode escape needs four hex digit after \\u");
										pex.setLineNumber(this.lineNumber);
										pex.setColumnNumber(this.columnNumber);
										throw pex;
									}
									buf.append("\\u").append(hex);
									break;
								default:
									throwError((char)next);
									break;
								}
								break;
							default:
								buf.append((char)c);
								break;
						}
					}else{
						this.tokenValue = buf.toString();
						buf.setLength(0);
						break;
					}
				}
				break;
			case JSONParserConstants.COLON:				
				if (tokenValue == null) {
					throwError(JSONParserConstants.COLON);
				}
				Token tn = new Token(this.lineNumber, this.columnNumber,
						JSONParserConstants.TOKEN_TYPE_IDENTIFIER, tokenValue);
				this.emmit(tn);
				tokenValue = null;
				Token t4 = new Token(this.lineNumber, this.columnNumber,
						JSONParserConstants.COLON, ":");
				
				this.emmit(t4);
				
				break;
			case JSONParserConstants.COMMA:				
				String val = buf.toString().trim();				
				if (getLastToken() == null) {
					throwError(JSONParserConstants.COMMA);
				} else if (getLastToken().getTokenType() == JSONParserConstants.COLON) {					
					if (tokenValue != null) {
						Token tv = new Token(this.lineNumber,
								this.columnNumber - tokenValue.length(),
								JSONParserConstants.TOKEN_TYPE_STRING,
								tokenValue);
						this.emmit(tv);
						tokenValue = null;
					} else if (val.length() > 0) {						
						
						buf.setLength(0);						
						Token tv = new Token(
								this.lineNumber,
								this.columnNumber - buf.length(),
								(val.equalsIgnoreCase("null")) ? JSONParserConstants.TOKEN_TYPE_NULL
										: JSONParserConstants.TOKEN_TYPE_NUMBER,
								val);
						this.emmit(tv);
					} else {
						throwError(JSONParserConstants.COMMA);
					}
				} else if (getLastToken().getTokenType() == JSONParserConstants.SQUARE_BRACKET_OPEN 
						|| getLastToken().getTokenType() == JSONParserConstants.COMMA) {									
					//System.out.println(">>>>>>>>>>>>>>>>>>" + buf.toString());
					//System.out.println("$$$$$$$$$$$$$$$$$$" + tokenValue);
					if (tokenValue != null) {						
						Token tv = new Token(this.lineNumber,
								this.columnNumber - tokenValue.length(),
								JSONParserConstants.TOKEN_TYPE_STRING,
								tokenValue);
						this.emmit(tv);
						tokenValue = null;
					} else {						
						buf.setLength(0);
						if(val.length()>0){
							Token tv = new Token(
									this.lineNumber,
									this.columnNumber - buf.length(),
									(val.equalsIgnoreCase("null")) ? JSONParserConstants.TOKEN_TYPE_NULL
											: JSONParserConstants.TOKEN_TYPE_NUMBER,
									val);
							this.emmit(tv);
						}else{
							throwError(JSONParserConstants.COMMA);
						}
					}
				}
				Token t5 = new Token(this.lineNumber, this.columnNumber,
						JSONParserConstants.COMMA);
				this.emmit(t5);
			
				break;			
			case JSONParserConstants.NEWLINE_CHAR:
				this.columnNumber = 0;
				this.lineNumber++;
				break;
			default:
				buf.append((char) c);
				if(c=='.') hasDecimal = true;
				break;

			}

		}

	}
	
	
	
	protected void throwError(String tokenValue){
		
	}
	
	protected void throwError(int tokenValue){
		
	}
	
	protected void throwError(char tokenValue){
		
	}
	
	protected void emmit(Token token) {
		token.setDecimal(hasDecimal);
		hasDecimal = false;		
		if (jsonStreamHandler == null) {
			this.tokens.add(token);
		} else {
			this.prevToken = currToken;
			this.currToken = token;
			
			switch (token.getTokenType()) {
			case JSONParserConstants.CURLY_BRACKET_OPEN:
				jsonStreamHandler.objectStart(token);
				break;
			case JSONParserConstants.CURLY_BRACKET_CLOSE:
				jsonStreamHandler.objectEnd(token);
				break;
			case JSONParserConstants.SQUARE_BRACKET_OPEN:
				jsonStreamHandler.arrayStart(token);
				break;
			case JSONParserConstants.SQUARE_BRACKET_CLOSE:
				jsonStreamHandler.arrayEnd(token);
				break;			
			case JSONParserConstants.COLON:
				jsonStreamHandler.nameValueSeparator(token);
				break;
			case JSONParserConstants.COMMA:
				jsonStreamHandler.listSeparator(token);
				break;
			case JSONParserConstants.TOKEN_TYPE_NUMBER:
				jsonStreamHandler.numberValue(token);
				break;
			case JSONParserConstants.TOKEN_TYPE_STRING:
				jsonStreamHandler.stringValue(token);
				break;
			case JSONParserConstants.TOKEN_TYPE_BOOLEAN:
				jsonStreamHandler.booleanValue(token);
				break;
			case JSONParserConstants.TOKEN_TYPE_IDENTIFIER:
				jsonStreamHandler.identifier(token);
				break;
			case JSONParserConstants.TOKEN_TYPE_NULL:
				jsonStreamHandler.nullValue(token);
				break;
			}

		}
	}

	public JSONStreamHandler getJsonStreamHandler() {
		return jsonStreamHandler;
	}

	public void setJsonStreamHandler(JSONStreamHandler jsonStreamHandler) {
		this.jsonStreamHandler = jsonStreamHandler;
	}

	public Token getPrevToken() {
		return prevToken;
	}

	public void setPrevToken(Token prevToken) {
		this.prevToken = prevToken;
	}

	public Token getCurrToken() {
		return currToken;
	}

	public void setCurrToken(Token currToken) {
		this.currToken = currToken;
	}

	public boolean isInObject() {
		return (objectState & JSONParserConstants.STATE_MASK) > 0;
	}

	public boolean isInArray() {
		return (arrayState & JSONParserConstants.STATE_MASK) > 0;
	}

	public static void main(String[] args) throws Exception {
		String test = "[{\"fname\":\"Sunny\",\"lname\":\"Liu\", \"Address\":{\"City\":\"College Point\", \"Zip\":\"11356\"}, \"age\":18, \"weight\":128.5, \"Hobbies\":[\"Hacking\", \"Cooking\", \"Chess\"]}]";

		JSONInputSource input = new JSONInputSource(test);
		input.parseInput();
		Token t = null;

		while ((t = input.nextToken()) != null) {
			System.out.println(((t.getTokenType() < 10000) ? ((char) t
					.getTokenType()) : 'A') + " " + t.getValue());
		}
	}

}
