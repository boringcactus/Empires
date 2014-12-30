package com.pixelgriffin.empires.exception;

public class EmpiresJoinableIsEmpireException extends Exception {
	
	private static final long serialVersionUID = -7112684725544611508L;

	public EmpiresJoinableIsEmpireException(String _msg) {
		super(_msg);
	}
	
	public EmpiresJoinableIsEmpireException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
