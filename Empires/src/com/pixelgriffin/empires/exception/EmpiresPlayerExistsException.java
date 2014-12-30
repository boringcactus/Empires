package com.pixelgriffin.empires.exception;

public class EmpiresPlayerExistsException extends Exception {
	
	private static final long serialVersionUID = -2184592585571643176L;

	public EmpiresPlayerExistsException(String _msg) {
		super(_msg);
	}
	
	public EmpiresPlayerExistsException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
