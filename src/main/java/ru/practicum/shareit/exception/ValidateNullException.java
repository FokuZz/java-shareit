package ru.practicum.shareit.exception;

public class ValidateNullException extends RuntimeException {
    public ValidateNullException() {
    }

    public ValidateNullException(String message) {
        super(message);
    }

    public ValidateNullException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateNullException(Throwable cause) {
        super(cause);
    }
}
