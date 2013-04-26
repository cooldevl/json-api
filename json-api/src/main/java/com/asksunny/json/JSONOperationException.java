package com.asksunny.json;

public class JSONOperationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JSONOperationException() {		
	}

	public JSONOperationException(String message) {
		super(message);
		
	}

	public JSONOperationException(Throwable cause) {
		super(cause);
		
	}

	public JSONOperationException(String message, Throwable cause) {
		super(message, cause);		
	}

}
