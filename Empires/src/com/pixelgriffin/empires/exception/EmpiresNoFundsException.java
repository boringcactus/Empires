package com.pixelgriffin.empires.exception;

public class EmpiresNoFundsException extends Exception {
	
	public EmpiresNoFundsException(String _msg) {
		super(_msg);
	}
	
	public EmpiresNoFundsException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
