package com.pixelgriffin.empires.exception;

/**
 * 
 * @author Nathan
 *
 */
public class EmpiresNoFundsException extends Exception {
	
	private static final long serialVersionUID = 5085847188150278507L;

	public EmpiresNoFundsException(String _msg) {
		super(_msg);
	}
	
	public EmpiresNoFundsException(String _msg, Throwable _throwable) {
		super(_msg, _throwable);
	}
}
