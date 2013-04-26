
package com.asksunny.json.parser;

public class Token {

	private int tokenType;
	private String value;
	private int lineNumber;
	private int columnNumber;
	private String lineText;
	private boolean decimal = false;
	
	
	public Token()
	{
		
	}
	
	
	
	public boolean isDecimal() {
		return decimal;
	}



	public void setDecimal(boolean decimal) {
		this.decimal = decimal;
	}


	public Token(int lineNumber, int columnNumber) {
		super();
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;		
	}

	
	public Token(int lineNumber, int columnNumber, int tokenType) {
		super();
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.tokenType = tokenType;		
	}

	
	public Token(int lineNumber, int columnNumber, int tokenType, String value) {
		super();
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.tokenType = tokenType;
		this.value = value;		
	}

	
	
	public Token(int lineNumber, int columnNumber, int tokenType, String value,
			String lineText) {
		super();
		this.lineNumber = lineNumber;
		this.columnNumber = columnNumber;
		this.tokenType = tokenType;
		this.value = value;
		this.lineText = lineText;
	}

	public int getTokenType() {
		return tokenType;
	}
	public void setTokenType(int tokenType) {
		this.tokenType = tokenType;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	public String getLineText() {
		return lineText;
	}
	public void setLineText(String lineText) {
		this.lineText = lineText;
	}
		
	
}
