package ru.practicum.main_server.exceptions;

public class EventValidationException extends RuntimeException {
    public EventValidationException(String message) {
        super(message);
    }
}
