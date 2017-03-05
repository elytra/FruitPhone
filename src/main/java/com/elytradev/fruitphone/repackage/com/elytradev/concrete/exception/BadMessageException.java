package com.elytradev.fruitphone.repackage.com.elytradev.concrete.exception;

public class BadMessageException extends RuntimeException {
	private static final long serialVersionUID = 7985887528709688970L;

	public BadMessageException() {
		super();
	}

	public BadMessageException(String message, Throwable cause) {
		super(message, cause);
	}

	public BadMessageException(String message) {
		super(message);
	}

	public BadMessageException(Throwable cause) {
		super(cause);
	}

}
