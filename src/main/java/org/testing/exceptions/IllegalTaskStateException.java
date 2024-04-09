package org.testing.exceptions;

public class IllegalTaskStateException  extends RuntimeException{
    public IllegalTaskStateException() {
        super();
    }

    public IllegalTaskStateException(String message) {
        super(message);
    }
}
