package com.pixelgriffin.empires.exception;

public class EmpiresPlayerExistsException extends Exception {
	
	public EmpiresPlayerExistsException(String _msg) {
		super(_msg);
	}
	
	public EmpiresPlayerExistsException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
