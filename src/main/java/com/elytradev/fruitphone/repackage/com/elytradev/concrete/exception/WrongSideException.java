package com.elytradev.fruitphone.repackage.com.elytradev.concrete.exception;

public class WrongSideException extends RuntimeException {
	private static final long serialVersionUID = 7985887528709688970L;

	public WrongSideException() {
		super();
	}

	public WrongSideException(String message, Throwable cause) {
		super(message, cause);
	}

	public WrongSideException(String message) {
		super(message);
	}

	public WrongSideException(Throwable cause) {
		super(cause);
	}

}
