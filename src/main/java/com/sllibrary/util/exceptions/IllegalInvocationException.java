package com.sllibrary.util.exceptions;

public class IllegalInvocationException extends RuntimeException {

    public IllegalInvocationException(String message) {
        super(message);
    }

    public IllegalInvocationException() {
        super();
    }

    public IllegalInvocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalInvocationException(Throwable cause) {
        super(cause);
    }
}
