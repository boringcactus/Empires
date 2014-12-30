package com.pixelgriffin.empires.exception;

/**
 * 
 * @author Nathan
 *
 */
public class EmpiresJoinableInvalidCharacterException extends Exception {
	
	private static final long serialVersionUID = -3261519246464578310L;

	public EmpiresJoinableInvalidCharacterException(String _msg) {
		super(_msg);
	}
	
	public EmpiresJoinableInvalidCharacterException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
