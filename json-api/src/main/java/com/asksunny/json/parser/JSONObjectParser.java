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
import java.util.Stack;

import com.asksunny.json.JSONArray;
import com.asksunny.json.JSONObject;

public class JSONObjectParser implements JSONStreamHandler {

	JSONStreamHandler jsonStreamHandler = null;;
	Reader reader = null;
	protected int objectState = 0;
	protected int arrayState = 0;
	protected List<Token> tokens = new ArrayList<Token>();

	public JSONStreamHandler getJsonStreamHandler() {
		return jsonStreamHandler;
	}

	public void setJsonStreamHandler(JSONStreamHandler jsonStreamHandler) {
		this.jsonStreamHandler = jsonStreamHandler;
	}

	public JSONObjectParser(Reader reader) throws IOException {
		this.reader = reader;
	}

	public JSONObjectParser(Reader reader, JSONStreamHandler jsonStreamHandler)
			throws IOException {
		this(reader);
		this.jsonStreamHandler = jsonStreamHandler;
	}

	public JSONObjectParser(String text) throws IOException {
		this(new StringReader(text));
	}

	public JSONObjectParser(String text, JSONStreamHandler jsonStreamHandler)
			throws IOException {
		this(new StringReader(text), jsonStreamHandler);
	}

	public JSONObjectParser(File f) throws IOException {
		this(new FileReader(f));
	}

	public JSONObjectParser(File f, JSONStreamHandler jsonStreamHandler)
			throws IOException {
		this(new FileReader(f), jsonStreamHandler);
	}

	public JSONObjectParser(InputStream in) throws IOException {
		this(new InputStreamReader(in));
	}

	public JSONObjectParser(InputStream in, JSONStreamHandler jsonStreamHandler)
			throws IOException {
		this(new InputStreamReader(in), jsonStreamHandler);
	}

	public JSONObjectParser(InputStream in, String charSet) throws IOException {
		this(new InputStreamReader(in, charSet));
	}

	public JSONObjectParser(InputStream in, String charSet,
			JSONStreamHandler jsonStreamHandler) throws IOException {
		this(new InputStreamReader(in, charSet), jsonStreamHandler);
	}

	JSONObject rootObject = null;
	JSONObject currObject = null;
	String currIdentifier = null;
	Stack<JSONObject> jsonObjs = new Stack<JSONObject>();

	public JSONObjectParser() {
	}

	public void streamStart() {
	}

	public JSONObject parse() throws IOException {

		if (reader == null)
			throw new IllegalArgumentException(
					"JSON source was not intialized.");
		if (this.jsonStreamHandler == null) {
			this.jsonStreamHandler = this;
		}
		JSONInputSource input = new JSONInputSource(this.reader,
				this.jsonStreamHandler);
		try {
			input.parseInput();
		} finally {
			if (this.reader != null) {
				this.reader.close();
				this.reader = null;
			}
		}
		return analysis();

	}

	protected JSONObject analysis() {
		JSONObject ret = null;
		int cnt = this.tokens.size();
		for (int i = 0; i < cnt; i++) {
			Token t = this.tokens.get(i);
		
			switch (t.getTokenType()) {
			case JSONParserConstants.CURLY_BRACKET_OPEN:
				//System.out.println("Curly Open");
				JSONObject jsonObj = new JSONObject();
				i = readJSONObject(jsonObj, i + 1);
				ret = jsonObj;
				break;
			case JSONParserConstants.SQUARE_BRACKET_OPEN:
				//System.out.println("square Open");
				JSONArray jsonArray = new JSONArray();
				i = readJSONArray(jsonArray, i + 1);
				ret = jsonArray;
				break;			
			default:
				throw  new JSONParsingException(t);					
			}

		}
		
		return ret;
	}

	protected int readJSONObject(JSONObject jsonObj, int startIdx) {
		int cnt = this.tokens.size();
		int i = startIdx;
		for (i = startIdx; i < cnt; i++) {
			Token t = this.tokens.get(i);
			//System.out.println("in curly open:" + t.getValue());
			switch (t.getTokenType()) {				
			case JSONParserConstants.TOKEN_TYPE_IDENTIFIER:
				if(i+2>cnt-1) throw  new JSONParsingException(t);	
				Token lookAhead1 =  this.tokens.get(i+1);
				if(lookAhead1.getTokenType()!=JSONParserConstants.COLON)  throw  new JSONParsingException(t);	
				Token lookAhead2 =  this.tokens.get(i+2);
				switch(lookAhead2.getTokenType())
				{
					case JSONParserConstants.TOKEN_TYPE_NUMBER:
						if(lookAhead2.isDecimal()){
							jsonObj.setAttribute(t.getValue(), Double.valueOf(lookAhead2.getValue()));
						}else{
							jsonObj.setAttribute(t.getValue(), Long.valueOf(lookAhead2.getValue()));
						}				
						i = i+2;
						break;
					case JSONParserConstants.TOKEN_TYPE_STRING:
						jsonObj.setAttribute(t.getValue(), lookAhead2.getValue());
						i = i+2;
						break;
					case JSONParserConstants.TOKEN_TYPE_BOOLEAN:
						jsonObj.setAttribute(t.getValue(), Boolean.valueOf(lookAhead2.getValue()));
						i = i+2;
						break;
					case JSONParserConstants.TOKEN_TYPE_NULL:
						jsonObj.setAttribute(t.getValue(), lookAhead2.getValue());
						i = i+2;
						break;
					case JSONParserConstants.CURLY_BRACKET_OPEN:
						JSONObject jsonObj1 = new JSONObject();
						i = readJSONObject( jsonObj1, i+3);
						jsonObj.setAttribute(t.getValue(), jsonObj1);
						break;
					case JSONParserConstants.SQUARE_BRACKET_OPEN:
						JSONArray jsonArray = new JSONArray();
						i = readJSONArray(jsonArray, i + 3);
						jsonObj.setAttribute(t.getValue(), jsonArray);
						break;
					default:
						 throw  new JSONParsingException(lookAhead2);	
				}				
				break;	
			case JSONParserConstants.COMMA:
				
				break;				
			case JSONParserConstants.CURLY_BRACKET_CLOSE:
				return i;
			default:
				throw new JSONParsingException(t);				
			}
		}
		return startIdx;
	}

	protected int readJSONArray(JSONArray jsonArray, int startIdx) {
		int cnt = this.tokens.size();
		int i = startIdx;
		for (i = startIdx; i < cnt; i++) {
			Token t = this.tokens.get(i);
			//System.out.println("In sq open:" +  t.getValue());			
			switch (t.getTokenType()) {
			case JSONParserConstants.CURLY_BRACKET_OPEN:
				JSONObject jsonObj = new JSONObject();
				i = readJSONObject(jsonObj, i + 1);
				jsonArray.add(jsonObj);
				break;
			case JSONParserConstants.TOKEN_TYPE_NUMBER:
				if(t.isDecimal()){
					jsonArray.add(Double.valueOf(t.getValue()));
				}else{
					jsonArray.add(Long.valueOf(t.getValue()));
				}				
				break;
			case JSONParserConstants.TOKEN_TYPE_STRING:
				jsonArray.add(t.getValue());
				break;
			case JSONParserConstants.TOKEN_TYPE_BOOLEAN:
				jsonArray.add(Boolean.valueOf(t.getValue()));
				break;
			case JSONParserConstants.TOKEN_TYPE_NULL:
				jsonArray.add(t.getValue());
				break;
			case JSONParserConstants.SQUARE_BRACKET_CLOSE:
				return i;				
			}
		}
		return startIdx;
	}

	public void arrayStart(Token arrayStartToken) {
		this.tokens.add(arrayStartToken);
	}

	public void objectStart(Token objStartToken) {
		this.tokens.add(objStartToken);

	}

	public void objectEnd(Token objStartToken) {
		this.tokens.add(objStartToken);
	}

	public void arrayEnd(Token arrayEndToken) {
		this.tokens.add(arrayEndToken);
	}

	public boolean isInObject() {
		return (objectState & JSONParserConstants.STATE_MASK) > 0;
	}

	public boolean isInArray() {
		return (arrayState & JSONParserConstants.STATE_MASK) > 0;
	}

	public void identifier(Token identifierToken) {
		this.tokens.add(identifierToken);
	}

	public void stringValue(Token stringValueToken) {
		this.tokens.add(stringValueToken);
	}

	public void numberValue(Token numberValueToken) {

		this.tokens.add(numberValueToken);
	}

	public void nullValue(Token nullValueToken) {
		this.tokens.add(nullValueToken);
	}

	public void booleanValue(Token booleanValueToken) {
		this.tokens.add(booleanValueToken);
	}

	public void nameValueSeparator(Token nameValueSeparatorToken) {
		this.tokens.add(nameValueSeparatorToken);
	}

	public void listSeparator(Token listSeparatorToken) {
		this.tokens.add(listSeparatorToken);
	}

	public void streamEnd() {

	}

	public static void main(String[] args) throws Exception {
		String tests = "[{\"fname\":\"XIAO\tCong\",\n \"Hobbies\":[\"Hacking\", \"Cooking\",\n \"Chess\"],\"lname\":\"Liu\", \"Address\":{\"City\":\"College Point\", \"Zip\":\"11356\"}, \"age\":18, \"weight\":128.5}, {\"fname\":\"Bill\",\"lname\":\"Liu\", \"Address\":{\"City\":\"College Point\", \n\"Zip\":\"11356\"}, \"age\":10, \"weight\":63.5}]";
		// String tests =
		// "[{\"fname\":\"Sunny\",\"lname\":\"Liu\", \"Address\":{\"City\":\"College Point\", \"Zip\":\"11356\"}, \"age\":18, \"weight\":128.5, , \"Hobbies\":[\"Hacking\", \"Cooking\", \"Chess\"]}]";
		System.out.println(tests);
		JSONObjectParser parser = new JSONObjectParser(tests);
		JSONObject obj = parser.parse();
		System.out.println(obj);
		JSONArray a = (JSONArray) obj;
		System.out.println(((JSONObject)a.get(0)).getAttribute("fname"));

	}

}
