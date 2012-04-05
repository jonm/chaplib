package org.chaplib;

/**
 * Exception thrown when a caller attempts an action after
 * the deadline for completing that action has passed.
 */
public class TooLateException extends RuntimeException {

    private static final long serialVersionUID = 9222574916266808804L;

    /**
     * Creates a <code>TooLateException</code> with the given
     * message as explanatory text.
     */
    public TooLateException(String message) {
        super(message);
    }
}
