package com.sllibrary.util.exceptions;

public class IllegalReturnException extends RuntimeException {

    public IllegalReturnException(String message) {
        super(message);
    }

    public IllegalReturnException() {
        super();
    }

    public IllegalReturnException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalReturnException(Throwable cause) {
        super(cause);
    }
}
