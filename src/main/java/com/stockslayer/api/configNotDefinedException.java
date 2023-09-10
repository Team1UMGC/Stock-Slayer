package com.stockslayer.api;

public class configNotDefinedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public configNotDefinedException(String statement) {
		super(statement);
	}
}