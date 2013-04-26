
package com.asksunny.json.parser;

public interface JSONStreamHandler 
{
	public void streamStart();
	public void objectStart(Token objStartToken);
	public void objectEnd(Token objStartToken);
	public void arrayStart(Token arrayStartToken);
	public void arrayEnd(Token arrayEndToken);
	public void identifier(Token identifierToken);
	public void stringValue(Token stringValueToken);
	public void numberValue(Token stringValueToken);
	public void nullValue(Token nullValueToken);
	public void booleanValue(Token booleanValueToken) ;
	public void nameValueSeparator(Token nameValueSeparatorToken);
	public void listSeparator(Token listSeparatorToken);
	public void streamEnd();
}