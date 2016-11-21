package com.redblaster.hsl.exceptions;

/**
 * Custom exception. It is used as a convenient way to notify the top 
 * level layers which error happened on the lower level layers.
 * 
 * @author Ilja Hamalainen
 *
 */
public class DatabaseException extends Exception {
	private static final long serialVersionUID = 1L;
	private static int errorNo;
	
	public DatabaseException(String message) {
		super(message);
	}

	public DatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseException(String message, int intErrorNo) {
		super(message);
		errorNo = intErrorNo;
	}
	
	public DatabaseException(String message, Throwable cause, int intErrorNo) {
		super(message, cause);
		errorNo = intErrorNo;
	}
	
	public int getErrorCode() {
		return errorNo;
	}
}