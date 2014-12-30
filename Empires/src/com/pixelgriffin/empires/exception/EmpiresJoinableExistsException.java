package com.pixelgriffin.empires.exception;

public class EmpiresJoinableExistsException extends Exception {
	
	public EmpiresJoinableExistsException(String _msg) {
		super(_msg);
	}
	
	public EmpiresJoinableExistsException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
