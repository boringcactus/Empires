package com.pixelgriffin.empires.exception;

public class EmpiresJoinableDoesNotExistException extends Exception {
	
	public EmpiresJoinableDoesNotExistException(String _msg) {
		super(_msg);
	}
	
	public EmpiresJoinableDoesNotExistException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
