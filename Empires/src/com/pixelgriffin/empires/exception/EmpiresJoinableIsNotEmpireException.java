package com.pixelgriffin.empires.exception;

public class EmpiresJoinableIsNotEmpireException extends Exception {
	
	public EmpiresJoinableIsNotEmpireException(String _msg) {
		super(_msg);
	}
	
	public EmpiresJoinableIsNotEmpireException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
