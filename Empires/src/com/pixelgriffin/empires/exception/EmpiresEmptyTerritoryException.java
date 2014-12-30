package com.pixelgriffin.empires.exception;

public class EmpiresEmptyTerritoryException extends Exception {
	
	public EmpiresEmptyTerritoryException(String _msg) {
		super(_msg);
	}
	
	public EmpiresEmptyTerritoryException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
