package ru.practicum.shareit.exception;

public class AlreadyExist extends RuntimeException {
    public AlreadyExist() {
    }

    public AlreadyExist(String message) {
        super(message);
    }

    public AlreadyExist(String message, Throwable cause) {
        super(message, cause);
    }

    public AlreadyExist(Throwable cause) {
        super(cause);
    }
}