package com.asksunny.json.parser;

public class JSONParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	int lineNumber;
	int columnNumber;
	Token badToken;
	
	

	public Token getBadToken() {
		return badToken;
	}

	public void setBadToken(Token badToken) {
		this.badToken = badToken;
	}

	public JSONParsingException(Token badToken) {
		super(String.format("Bad token [%s] at line %d column %d", badToken.getValue(), badToken.getLineNumber(), badToken.getColumnNumber()));
		this.badToken = badToken;
	}
	
	public JSONParsingException() {
	}

	public JSONParsingException(String message) {
		super(message);

	}

	public JSONParsingException(Throwable cause) {
		super(cause);

	}

	public JSONParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getColumnNumber() {
		return columnNumber;
	}

	public void setColumnNumber(int columnNumber) {
		this.columnNumber = columnNumber;
	}
	
	

}