package com.pixelgriffin.empires.exception;

public class EmpiresEmptyTerritoryException extends Exception {
	
	private static final long serialVersionUID = -4564632736048933904L;

	public EmpiresEmptyTerritoryException(String _msg) {
		super(_msg);
	}
	
	public EmpiresEmptyTerritoryException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
