package com.pixelgriffin.empires.exception;

public class EmpiresJoinableDoesNotExistException extends Exception {
	
	private static final long serialVersionUID = 2968320851946745307L;

	public EmpiresJoinableDoesNotExistException(String _msg) {
		super(_msg);
	}
	
	public EmpiresJoinableDoesNotExistException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
