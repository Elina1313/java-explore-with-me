package ru.practicum.main_server.exceptions;

public class RequestNotExistException extends RuntimeException {
    public RequestNotExistException(String message) {
        super(message);
    }
}
