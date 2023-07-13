package ru.practicum.main_server.exceptions;

public class RequestAlreadyConfirmedException extends RuntimeException {
    public RequestAlreadyConfirmedException(String message) {
        super(message);
    }
}
