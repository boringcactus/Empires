package com.pixelgriffin.empires.exception;

public class EmpiresJoinableExistsException extends Exception {
	
	private static final long serialVersionUID = 3377981415777026736L;

	public EmpiresJoinableExistsException(String _msg) {
		super(_msg);
	}
	
	public EmpiresJoinableExistsException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
