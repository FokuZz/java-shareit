package ru.practicum.shareit.exception;

public class ValidateDuplicateException extends RuntimeException {
    public ValidateDuplicateException() {
    }

    public ValidateDuplicateException(String message) {
        super(message);
    }

    public ValidateDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidateDuplicateException(Throwable cause) {
        super(cause);
    }
}
