package org.chaplib;

public class TooLateException extends RuntimeException {

    private static final long serialVersionUID = 9222574916266808804L;

    public TooLateException(String message) {
        super(message);
    }
}
